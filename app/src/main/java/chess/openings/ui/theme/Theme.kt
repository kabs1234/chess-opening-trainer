package chess.openings.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF4A7332),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCDEBB4),
    onPrimaryContainer = Color(0xFF0E2004),
    secondary = Color(0xFF56624B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDAE7CA),
    onSecondaryContainer = Color(0xFF141E0C),
    tertiary = Color(0xFF7A5900),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDEA6),
    onTertiaryContainer = Color(0xFF261900),
    background = Color(0xFFF9FAEF),
    onBackground = Color(0xFF1A1C16),
    surface = Color(0xFFF9FAEF),
    onSurface = Color(0xFF1A1C16),
    surfaceVariant = Color(0xFFE0E4D6),
    onSurfaceVariant = Color(0xFF44483D),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF3F5E8),
    surfaceContainer = Color(0xFFEDEFE2),
    surfaceContainerHigh = Color(0xFFE7E9DC),
    surfaceContainerHighest = Color(0xFFE2E4D7),
    outline = Color(0xFF75796C),
    outlineVariant = Color(0xFFC5C8BA),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA6D38A),
    onPrimary = Color(0xFF173807),
    primaryContainer = Color(0xFF30521D),
    onPrimaryContainer = Color(0xFFCDEBB4),
    secondary = Color(0xFFBECBAE),
    onSecondary = Color(0xFF29341F),
    secondaryContainer = Color(0xFF3F4A34),
    onSecondaryContainer = Color(0xFFDAE7CA),
    tertiary = Color(0xFFF0BF5E),
    onTertiary = Color(0xFF402D00),
    tertiaryContainer = Color(0xFF5C4200),
    onTertiaryContainer = Color(0xFFFFDEA6),
    background = Color(0xFF12140E),
    onBackground = Color(0xFFE2E4D7),
    surface = Color(0xFF12140E),
    onSurface = Color(0xFFE2E4D7),
    surfaceVariant = Color(0xFF44483D),
    onSurfaceVariant = Color(0xFFC5C8BA),
    surfaceContainerLowest = Color(0xFF0D0F09),
    surfaceContainerLow = Color(0xFF1A1C16),
    surfaceContainer = Color(0xFF1E201A),
    surfaceContainerHigh = Color(0xFF292B24),
    surfaceContainerHighest = Color(0xFF33362E),
    outline = Color(0xFF8F9285),
    outlineVariant = Color(0xFF44483D),
)

@Composable
fun OpeningTrainerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
