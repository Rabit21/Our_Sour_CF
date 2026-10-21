package cl.oursour.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import cl.oursour.view.*
import cl.oursour.viewmodel.SessionViewModel

@Composable
fun OurSourApp(session: SessionViewModel = viewModel()) {
    val nav = rememberNavController()
    val user by session.user.collectAsState()
    fun enterHome() { nav.navigate("home") { popUpTo("login") { inclusive = true }; launchSingleTop = true } }
    NavHost(nav, startDestination = "login") {
        composable("login") {
            LoginScreen(onLogin = { session.login(it); enterHome() }, onRegister = { nav.navigate("register") })
        }
        composable("register") {
            RegisterScreen(onBack = { nav.popBackStack() }, onRegister = { name, email ->
                val error = session.register(name, email)
                if (error == null) enterHome()
                error
            })
        }
        composable("home") {
            val current = user
            if (current != null) HomeScreen(current, onLogout = {
                session.logout()
                nav.navigate("login") { popUpTo("home") { inclusive = true }; launchSingleTop = true }
            }) else LaunchedEffect(Unit) { nav.navigate("login") { popUpTo("home") { inclusive = true } } }
        }
    }
}
