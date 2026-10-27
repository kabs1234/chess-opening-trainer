package chess.openings.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, onBack: (() -> Unit)? = null) {
    TopAppBar(
        title = { BarTitle(title) },
        navigationIcon = {
            onBack?.let {
                IconButton(onClick = it) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
    )
}

@ExperimentalMaterial3Api
@Composable
fun AppTopBar(title: String, scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(title = { BarTitle(title) }, scrollBehavior = scrollBehavior)
}

@Composable
private fun BarTitle(title: String) {
    Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
}
