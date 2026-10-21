package cl.oursour.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OurSourColors = lightColorScheme(
    primary = Color(0xFF174C3C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7E8D7),
    onPrimaryContainer = Color(0xFF10382B),
    secondary = Color(0xFF596515),
    secondaryContainer = Color(0xFFEBEDAB),
    onSecondaryContainer = Color(0xFF303800),
    background = Color(0xFFFFF9EB),
    onBackground = Color(0xFF202A23),
    surface = Color(0xFFFFF9EB),
    onSurface = Color(0xFF202A23)
)

@Composable
fun OurSourTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = OurSourColors, content = content)
}
