package mobg.g58093.weather_app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat



private val ColorScheme = darkColorScheme(
    primary = Black,
    secondary = White,
    tertiary = Grey,
    onPrimary = Black,
    primaryContainer = Black,
    onPrimaryContainer = Black,
    onSecondary = White,
    secondaryContainer = White,
    onSecondaryContainer = White,
    onTertiary = Grey,
    tertiaryContainer = Grey,
    onTertiaryContainer = Grey,
)

@Composable
fun Weather_appTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}
