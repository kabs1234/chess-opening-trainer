package chess.openings.ui.practice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chess.openings.data.Opening
import chess.openings.data.OpeningLine
import chess.openings.data.OpeningRepository
import chess.openings.data.PracticeResult
import chess.openings.data.nextLineToPractice
import chess.openings.model.ChessPosition
import chess.openings.ui.requireOpening
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val OPPONENT_DELAY_MILLIS = 600L

sealed interface MoveFeedback {
    data object BookMove : MoveFeedback
    data class OtherOpening(val openingName: String) : MoveFeedback
    data object NotInBook : MoveFeedback
    data class OpponentSideline(val lineName: String) : MoveFeedback
}

data class PracticeUiState(
    val opening: Opening,
    val line: OpeningLine,
    val moveIndex: Int = 0,
    val mistakes: Int = 0,
    val hintsUsed: Int = 0,
    val feedback: MoveFeedback? = null,
    val wrongSquare: String? = null,
    val hintSquare: String? = null,
    val isOpponentThinking: Boolean = false,
) {
    val position: ChessPosition = ChessPosition.after(line.moves.take(moveIndex))
    val lastMove: String? get() = line.moves.getOrNull(moveIndex - 1)
    val expectedMove: String? get() = line.moves.getOrNull(moveIndex)
    val isFinished: Boolean get() = moveIndex >= line.moves.size
    val canPlayerMove: Boolean get() = !isFinished && !isOpponentThinking && opening.isPlayerMove(moveIndex)
    val sidelineStart: Int? get() = if (line == opening.mainLine) null else line.deviationIndexFrom(opening.mainLine)
}

class PracticeViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: OpeningRepository,
) : ViewModel() {
    private val opening = repository.requireOpening(savedStateHandle)
    private val _uiState = MutableStateFlow(newRun())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()
    private var opponentJob: Job? = null

    init {
        playOpponentMoveIfDue()
    }

    fun onMoveAttempt(from: String, to: String) {
        val state = _uiState.value
        val attempt = from + to
        if (!state.canPlayerMove || !state.position.isLegal(attempt)) return

        if (attempt == state.expectedMove) {
            advance(MoveFeedback.BookMove)
            playOpponentMoveIfDue()
        } else {
            rejectOffBookMove(attempt, to)
        }
    }

    fun onHint() {
        val state = _uiState.value
        val expectedMove = state.expectedMove ?: return
        if (!state.canPlayerMove) return
        _uiState.update { it.copy(hintSquare = expectedMove.take(2), hintsUsed = it.hintsUsed + 1) }
    }

    fun clearMistakeMark() {
        _uiState.update { it.copy(wrongSquare = null) }
    }

    fun restart() {
        opponentJob?.cancel()
        _uiState.value = newRun()
        playOpponentMoveIfDue()
    }

    private fun newRun(): PracticeUiState =
        PracticeUiState(opening, opening.nextLineToPractice(repository.progress.value[opening.id]))

    private fun rejectOffBookMove(attempt: String, to: String) {
        val state = _uiState.value
        val playedMoves = state.line.moves.take(state.moveIndex) + attempt
        val otherOpening = repository.getOpenings().firstOrNull { it.id != opening.id && it.continuesWith(playedMoves) }

        _uiState.update {
            if (otherOpening == null) {
                it.copy(feedback = MoveFeedback.NotInBook, mistakes = it.mistakes + 1, wrongSquare = to, hintSquare = null)
            } else {
                it.copy(feedback = MoveFeedback.OtherOpening(otherOpening.name), wrongSquare = null)
            }
        }
    }

    private fun playOpponentMoveIfDue() {
        val state = _uiState.value
        if (state.isFinished || opening.isPlayerMove(state.moveIndex)) return

        _uiState.update { it.copy(isOpponentThinking = true) }
        opponentJob = viewModelScope.launch {
            delay(OPPONENT_DELAY_MILLIS)
            advance(opponentFeedback(_uiState.value))
        }
    }

    private fun opponentFeedback(state: PracticeUiState): MoveFeedback? =
        if (state.moveIndex == state.sidelineStart) MoveFeedback.OpponentSideline(state.line.name) else state.feedback

    private fun advance(feedback: MoveFeedback?) {
        _uiState.update {
            it.copy(
                moveIndex = it.moveIndex + 1,
                feedback = feedback,
                wrongSquare = null,
                hintSquare = null,
                isOpponentThinking = false,
            )
        }
        if (_uiState.value.isFinished) saveResult()
    }

    private fun saveResult() {
        val state = _uiState.value
        viewModelScope.launch { repository.recordResult(PracticeResult(opening.id, state.line.name, state.mistakes)) }
    }
}
