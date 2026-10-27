package chess.openings.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import chess.openings.AppContainer
import chess.openings.ui.components.AppTopBar
import chess.openings.ui.components.OpeningCard

private val CARD_MIN_WIDTH = 340.dp
private val SEARCH_MIN_WIDTH = 320.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpeningListScreen(
    onOpeningClick: (String) -> Unit,
    viewModel: OpeningListViewModel = viewModel { OpeningListViewModel(AppContainer.repository) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { AppTopBar("Opening Trainer", scrollBehavior = scrollBehavior) },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(CARD_MIN_WIDTH),
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SearchAndFilters(
                    searchState = viewModel.searchState,
                    selectedFilter = state.criteria.filter,
                    onClearSearch = viewModel::onClearSearch,
                    onFilterSelect = viewModel::onFilterSelected,
                )
            }
            items(state.items, key = { it.opening.id }) { item ->
                OpeningCard(
                    opening = item.opening,
                    progress = item.progress,
                    onClick = { onOpeningClick(item.opening.id) },
                )
            }
            if (state.items.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) { EmptyResult(state.criteria) }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchAndFilters(
    searchState: TextFieldState,
    selectedFilter: OpeningFilter,
    onClearSearch: () -> Unit,
    onFilterSelect: (OpeningFilter) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        SearchField(
            state = searchState,
            onClear = onClearSearch,
            modifier = Modifier.widthIn(min = SEARCH_MIN_WIDTH).weight(1f),
        )
        FilterRow(selected = selectedFilter, onSelect = onFilterSelect)
    }
}

@Composable
private fun SearchField(state: TextFieldState, onClear: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        state = state,
        modifier = modifier,
        placeholder = { Text("Search by name or move, e.g. Nf3") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (state.text.isNotEmpty()) {
                IconButton(onClick = onClear) { Icon(Icons.Filled.Clear, contentDescription = "Clear search") }
            }
        },
        lineLimits = TextFieldLineLimits.SingleLine,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        ),
    )
}

@Composable
private fun FilterRow(selected: OpeningFilter, onSelect: (OpeningFilter) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OpeningFilter.entries.forEach { filter ->
            val isSelected = filter == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(filter) },
                label = { Text(filter.label) },
                leadingIcon = if (isSelected) {
                    { Icon(Icons.Filled.Done, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        }
    }
}

@Composable
private fun EmptyResult(criteria: OpeningCriteria) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text("No openings found", style = MaterialTheme.typography.titleMedium)
        Text(
            "Nothing matches \"${criteria.searchText}\" in ${criteria.filter.label}. Try another name or move, or switch the filter.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
