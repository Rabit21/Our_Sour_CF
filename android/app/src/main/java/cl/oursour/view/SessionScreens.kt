package cl.oursour.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.oursour.model.DemoUser
import cl.oursour.model.Role

@Composable
fun Page(title: String, onBack: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.safeDrawingPadding().imePadding(), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = 600.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (onBack != null) TextButton(onClick = onBack) { Text("Volver") }
                Text("OUR SOUR", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(title, style = MaterialTheme.typography.headlineLarge)
                content()
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (Role) -> Unit, onRegister: () -> Unit) {
    Page("Bienvenido") {
        Text("Descubre qué puedes preparar con tus ingredientes.")
        Card { Text("Acceso de demostración local. No autentica cuentas reales ni guarda contraseñas.", Modifier.padding(16.dp)) }
        Button(onClick = { onLogin(Role.CLIENTE) }, modifier = Modifier.fillMaxWidth()) { Text("Entrar como cliente demo") }
        OutlinedButton(onClick = { onLogin(Role.ADMIN) }, modifier = Modifier.fillMaxWidth()) { Text("Entrar como administrador demo") }
        TextButton(onClick = onRegister) { Text("Crear perfil de demostración") }
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
        Button(onClick = { error = onRegister(name, email) }) { Text("Crear perfil") }
    }
}

@Composable
fun HomeScreen(user: DemoUser, onRecipes: () -> Unit, onCatalog: () -> Unit, onLogout: () -> Unit) {
    Page("Hola, ${user.name}") {
        Text("Rol: ${user.role.name} · Sesión de demostración")
        Text("Tu espacio para descubrir recetas y preparar tu próximo sour.")
        Button(onClick = onRecipes) { Text("Buscar recetas") }
        Button(onClick = onCatalog) { Text("Explorar catálogo") }
        OutlinedButton(onClick = onLogout) { Text("Cerrar sesión") }
    }
}
