package chess.openings.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chess.openings.data.Opening
import chess.openings.data.OpeningProgress
import chess.openings.data.OpeningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private const val STOP_TIMEOUT_MILLIS = 5_000L
private const val E4_FIRST_MOVE = "e2e4"
private const val D4_FIRST_MOVE = "d2d4"

enum class OpeningFilter(val label: String) {
    ALL("All"),
    E4("1.e4"),
    D4("1.d4"),
    OTHER("Other");

    fun matches(opening: Opening): Boolean = when (this) {
        ALL -> true
        E4 -> opening.firstMove == E4_FIRST_MOVE
        D4 -> opening.firstMove == D4_FIRST_MOVE
        OTHER -> opening.firstMove != E4_FIRST_MOVE && opening.firstMove != D4_FIRST_MOVE
    }
}

data class OpeningListItem(val opening: Opening, val progress: OpeningProgress?)

data class OpeningListUiState(
    val filter: OpeningFilter,
    val items: List<OpeningListItem>,
)

class OpeningListViewModel(private val repository: OpeningRepository) : ViewModel() {
    private val filter = MutableStateFlow(OpeningFilter.ALL)

    val uiState: StateFlow<OpeningListUiState> = combine(filter, repository.progress, ::buildState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = buildState(filter.value, repository.progress.value),
        )

    fun onFilterSelected(selected: OpeningFilter) {
        filter.value = selected
    }

    private fun buildState(selected: OpeningFilter, progress: Map<String, OpeningProgress>) = OpeningListUiState(
        filter = selected,
        items = repository.getOpenings()
            .filter(selected::matches)
            .map { OpeningListItem(it, progress[it.id]) },
    )
}
