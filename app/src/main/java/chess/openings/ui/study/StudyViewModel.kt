package chess.openings.ui.study

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import chess.openings.data.Opening
import chess.openings.data.OpeningRepository
import chess.openings.model.ChessPosition
import chess.openings.ui.requireOpening
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StudyUiState(val opening: Opening, val moveIndex: Int = 0) {
    val position: ChessPosition = ChessPosition.after(opening.moves.take(moveIndex))
    val lastMove: String? get() = opening.moves.getOrNull(moveIndex - 1)
    val canGoBack: Boolean get() = moveIndex > 0
    val canGoForward: Boolean get() = moveIndex < opening.moves.size
}

class StudyViewModel(savedStateHandle: SavedStateHandle, repository: OpeningRepository) : ViewModel() {
    private val opening = repository.requireOpening(savedStateHandle)
    private val _uiState = MutableStateFlow(StudyUiState(opening))
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    fun goToStart() = goTo(0)

    fun goBack() = goTo(_uiState.value.moveIndex - 1)

    fun goForward() = goTo(_uiState.value.moveIndex + 1)

    fun goToEnd() = goTo(opening.moves.size)

    fun goTo(moveIndex: Int) {
        _uiState.update { it.copy(moveIndex = moveIndex.coerceIn(0, opening.moves.size)) }
    }
}
