package chess.openings.ui.list

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
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
                FilterRow(selected = state.filter, onSelect = viewModel::onFilterSelected)
            }
            items(state.items, key = { it.opening.id }) { item ->
                OpeningCard(
                    opening = item.opening,
                    progress = item.progress,
                    onClick = { onOpeningClick(item.opening.id) },
                )
            }
        }
    }
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
