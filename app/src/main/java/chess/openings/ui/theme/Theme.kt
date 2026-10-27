package chess.openings.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Walnut = Color(0xFF7A4E2D)
private val Sand = Color(0xFFE8C9A0)

private val LightColors = lightColorScheme(primary = Walnut, secondary = Walnut)
private val DarkColors = darkColorScheme(primary = Sand, secondary = Sand)

@Composable
fun OpeningTrainerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
