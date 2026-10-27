package chess.openings.ui.list

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import chess.openings.AppContainer
import chess.openings.ui.components.AppTopBar
import chess.openings.ui.components.OpeningCard

@Composable
fun OpeningListScreen(
    onOpeningClick: (String) -> Unit,
    viewModel: OpeningListViewModel = viewModel { OpeningListViewModel(AppContainer.repository) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { AppTopBar("Opening Trainer") }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                SearchField(state = viewModel.searchState, onClear = viewModel::onClearSearch)
            }
            item {
                FilterRow(selected = state.criteria.filter, onSelect = viewModel::onFilterSelected)
            }
            items(state.items, key = { it.opening.id }) { item ->
                OpeningCard(
                    opening = item.opening,
                    progress = item.progress,
                    onClick = { onOpeningClick(item.opening.id) },
                )
            }
            if (state.items.isEmpty()) {
                item {
                    Text(
                        "No openings match \"${state.criteria.searchText}\" in ${state.criteria.filter.label}.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(state: TextFieldState, onClear: () -> Unit) {
    OutlinedTextField(
        state = state,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Search") },
        placeholder = { Text("Name or move, e.g. Najdorf or Nf3") },
        lineLimits = TextFieldLineLimits.SingleLine,
        trailingIcon = {
            if (state.text.isNotEmpty()) {
                IconButton(onClick = onClear) { Text("✕") }
            }
        },
    )
}

@Composable
private fun FilterRow(selected: OpeningFilter, onSelect: (OpeningFilter) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OpeningFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onSelect(filter) },
                label = { Text(filter.label) },
            )
        }
    }
}
