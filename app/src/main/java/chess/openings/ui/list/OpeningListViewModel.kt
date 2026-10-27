package chess.openings.ui.list

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
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

data class OpeningCriteria(val filter: OpeningFilter = OpeningFilter.ALL, val searchText: String = "") {
    fun matches(opening: Opening): Boolean = filter.matches(opening) && matchesSearch(opening)

    private fun matchesSearch(opening: Opening): Boolean =
        searchText.isBlank() ||
            opening.name.contains(searchText, ignoreCase = true) ||
            opening.lines.any { it.san.contains(searchText, ignoreCase = true) }
}

data class OpeningListItem(val opening: Opening, val progress: OpeningProgress?)

data class OpeningListUiState(
    val criteria: OpeningCriteria,
    val items: List<OpeningListItem>,
)

class OpeningListViewModel(private val repository: OpeningRepository) : ViewModel() {
    val searchState = TextFieldState()
    private val filter = MutableStateFlow(OpeningFilter.ALL)
    private val searchText = snapshotFlow { searchState.text.toString().trim() }

    val uiState: StateFlow<OpeningListUiState> =
        combine(filter, searchText, repository.progress) { selected, text, progress ->
            buildState(OpeningCriteria(selected, text), progress)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = buildState(OpeningCriteria(), repository.progress.value),
        )

    fun onFilterSelected(selected: OpeningFilter) {
        filter.value = selected
    }

    fun onClearSearch() {
        searchState.clearText()
    }

    private fun buildState(criteria: OpeningCriteria, progress: Map<String, OpeningProgress>) = OpeningListUiState(
        criteria = criteria,
        items = repository.getOpenings()
            .filter(criteria::matches)
            .map { OpeningListItem(it, progress[it.id]) },
    )
}
