package cl.oursour.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.oursour.R
import cl.oursour.model.DemoUser
import cl.oursour.model.Role
import cl.oursour.ui.theme.OurSourPalette

/** Ancho máximo del contenido: en tablet el texto no se estira de borde a borde. */
private val ContentWidth = 680.dp

/** Estructura común de las pantallas: encabezado verde con título serif y contenido desplazable. */
@Composable
fun Page(title: String, onBack: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().imePadding()) {
            PageHeader(title, onBack)
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Column(
                    Modifier.widthIn(max = ContentWidth).fillMaxWidth().verticalScroll(rememberScrollState())
                        .navigationBarsPadding().padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) { content() }
            }
        }
    }
}

@Composable
private fun PageHeader(title: String, onBack: (() -> Unit)?) {
    Surface(color = OurSourPalette.Bosque, contentColor = OurSourPalette.CremaTexto) {
        Column {
            Box(Modifier.fillMaxWidth().statusBarsPadding().displayCutoutPadding(), contentAlignment = Alignment.TopCenter) {
                Column(Modifier.widthIn(max = ContentWidth).fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (onBack != null) {
                        TextButton(onClick = onBack, contentPadding = PaddingValues(horizontal = 0.dp),
                            colors = ButtonDefaults.textButtonColors(contentColor = OurSourPalette.Limon)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Volver")
                        }
                    }
                    Text("OUR SOUR", style = MaterialTheme.typography.labelLarge, color = OurSourPalette.Limon)
                    Text(title, style = MaterialTheme.typography.headlineMedium)
                }
            }
            // Línea de acento limón bajo el encabezado
            Box(Modifier.fillMaxWidth().height(3.dp).background(OurSourPalette.Limon))
        }
    }
}

/** Subtítulo de sección usado dentro de las pantallas. */
@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
}

/** Aviso destacado con fondo limón suave. */
@Composable
fun NoticeCard(text: String) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Text(text, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun LoginScreen(onLogin: (Role) -> Unit, onRegister: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Lado a lado en tablet horizontal o ventanas casi cuadradas, donde la imagen arriba quedaría muy baja
        val wide = maxWidth >= 480.dp && maxWidth > maxHeight * 0.9f
        val portraitImageHeight = imageHeight(maxWidth, maxHeight)
        if (wide) {
            // Tablet horizontal: imagen a la izquierda y acceso a la derecha
            Row(Modifier.fillMaxSize()) {
                WelcomeImage(Modifier.weight(1f).fillMaxHeight())
                Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    Column(Modifier.widthIn(max = 480.dp).verticalScroll(rememberScrollState()).safeDrawingPadding().padding(32.dp)) {
                        WelcomePanel(onLogin, onRegister)
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                WelcomeImage(Modifier.fillMaxWidth().height(portraitImageHeight))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    Column(Modifier.widthIn(max = 520.dp).navigationBarsPadding().padding(24.dp)) {
                        WelcomePanel(onLogin, onRegister)
                    }
                }
            }
        }
    }
}

/**
 * Alto de la imagen en vertical. Con Crop y ancho completo, el nombre "Our Sour" (parte de la imagen)
 * termina cerca del 34 % de su alto, es decir, a 0,6 veces el ancho: nunca se baja de 0,64 veces el ancho.
 * Hasta 55 % de la pantalla y como máximo el 62 % superior de la imagen (logo y copas).
 */
private fun imageHeight(width: Dp, height: Dp): Dp {
    val fullHeight = width * (1672f / 941f)
    return maxOf(minOf(fullHeight * 0.62f, height * 0.55f), width * 0.64f)
}

@Composable
private fun WelcomeImage(modifier: Modifier) {
    // Crop recorta sin deformar; TopCenter conserva visible el nombre que forma parte de la imagen
    Image(
        painter = painterResource(R.drawable.our_sour_bienvenida),
        contentDescription = "Cócteles sour con limón sobre una mesa de mármol",
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter,
        modifier = modifier.background(OurSourPalette.Bosque)
    )
}

@Composable
private fun WelcomePanel(onLogin: (Role) -> Unit, onRegister: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Bienvenido", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text("Descubre qué puedes preparar con tus ingredientes.", style = MaterialTheme.typography.bodyLarge)
        NoticeCard("Acceso de demostración local. No autentica cuentas reales ni guarda contraseñas.")
        Button(onClick = { onLogin(Role.CLIENTE) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Entrar como cliente demo") }
        OutlinedButton(onClick = { onLogin(Role.ADMIN) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Entrar como administrador demo") }
        TextButton(onClick = onRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Crear perfil de demostración") }
    }
}

@Composable
fun RegisterScreen(onBack: () -> Unit, onRegister: (String, String) -> String?) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    Page("Crear perfil demo", onBack) {
        Text("Usa datos ficticios. El perfil existe solo durante esta sesión y tiene rol de cliente.")
        OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(email, { email = it }, label = { Text("Correo ficticio") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = { error = onRegister(name, email) }, modifier = Modifier.fillMaxWidth()) { Text("Crear perfil") }
    }
}

/** Opción del inicio: tarjeta completa pulsable con título serif y una línea de ayuda. */
private data class HomeAction(val title: String, val description: String, val highlighted: Boolean, val onClick: () -> Unit)

@Composable
fun HomeScreen(user: DemoUser, onRecipes: () -> Unit, onCatalog: () -> Unit, onOrders: () -> Unit, onAdmin: () -> Unit, onQr: () -> Unit, onLogout: () -> Unit) {
    val actions = buildList {
        add(HomeAction("Buscar recetas", "Marca tus ingredientes y descubre qué preparar.", true, onRecipes))
        add(HomeAction("Explorar catálogo", "Productos Our Sour con precio y stock.", true, onCatalog))
        add(HomeAction("Mis pedidos", "Compras simuladas de esta sesión.", false, onOrders))
        add(HomeAction("Leer QR", "Abre un producto con la cámara.", false, onQr))
        if (user.role == Role.ADMIN) add(HomeAction("Administrar productos", "Crear, editar y eliminar del catálogo.", false, onAdmin))
    }
    Page("Hola, ${user.name}") {
        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.tertiaryContainer) {
            Text("Rol: ${user.role.name} · Sesión de demostración", Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
        Text("Tu espacio para descubrir recetas y preparar tu próximo sour.", style = MaterialTheme.typography.bodyLarge)
        BoxWithConstraints {
            val columns = if (maxWidth >= 520.dp) 2 else 1
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                actions.chunked(columns).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { action -> HomeActionCard(action, Modifier.weight(1f)) }
                        repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
    }
}

@Composable
private fun HomeActionCard(action: HomeAction, modifier: Modifier) {
    val colors = if (action.highlighted)
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
    else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    Card(onClick = action.onClick, modifier = modifier.heightIn(min = 112.dp), colors = colors) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(action.title, style = MaterialTheme.typography.titleLarge)
            Text(action.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
