package cl.oursour.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Colores tomados de la imagen de bienvenida: verde oliva profundo, crema, limón y dorado. */
object OurSourPalette {
    val Bosque = Color(0xFF26301A)      // fondo verde de la imagen, usado en el encabezado
    val Oliva = Color(0xFF3D4F22)
    val Crema = Color(0xFFFBF6E9)
    val CremaTexto = Color(0xFFF6EEDB)  // texto claro sobre el verde
    val Limon = Color(0xFFF2D35B)
    val Dorado = Color(0xFF8C6D1F)
}

private val OurSourColors = lightColorScheme(
    primary = OurSourPalette.Oliva,
    onPrimary = Color(0xFFFFF8E7),
    primaryContainer = Color(0xFFDDE5C4),
    onPrimaryContainer = Color(0xFF1D2A08),
    secondary = Color(0xFF6B5E00),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF7E38A),
    onSecondaryContainer = Color(0xFF2B2500),
    tertiary = OurSourPalette.Dorado,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF3DFA6),
    onTertiaryContainer = Color(0xFF2C2100),
    background = OurSourPalette.Crema,
    onBackground = Color(0xFF1F2318),
    surface = OurSourPalette.Crema,
    onSurface = Color(0xFF1F2318),
    surfaceVariant = Color(0xFFECE5D0),
    onSurfaceVariant = Color(0xFF4A4A3A),
    surfaceContainerLowest = Color(0xFFFFFDF6),
    surfaceContainerLow = Color(0xFFF8F1E0),
    surfaceContainer = Color(0xFFF5EDDA),
    surfaceContainerHigh = Color(0xFFF1E8D3),
    surfaceContainerHighest = Color(0xFFEDE3CB),
    outline = Color(0xFF8F8566),
    outlineVariant = Color(0xFFD5CBB0)
)

/** Títulos con letra serif (como el logo); textos de lectura con la letra sans del sistema. */
private val Serif = FontFamily.Serif
private val base = Typography()
private val OurSourTypography = base.copy(
    displayLarge = base.displayLarge.copy(fontFamily = Serif),
    displayMedium = base.displayMedium.copy(fontFamily = Serif),
    displaySmall = base.displaySmall.copy(fontFamily = Serif),
    headlineLarge = base.headlineLarge.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold),
    headlineMedium = base.headlineMedium.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold),
    headlineSmall = base.headlineSmall.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold),
    titleLarge = base.titleLarge.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleSmall = base.titleSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = base.bodyLarge.copy(lineHeight = 26.sp),
    labelLarge = base.labelLarge.copy(letterSpacing = 0.5.sp)
)

private val OurSourShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun OurSourTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = OurSourColors, typography = OurSourTypography, shapes = OurSourShapes, content = content)
}
