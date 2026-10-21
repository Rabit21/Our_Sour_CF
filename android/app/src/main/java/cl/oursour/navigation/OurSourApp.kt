package cl.oursour.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import cl.oursour.view.*
import cl.oursour.model.Role
import cl.oursour.viewmodel.SessionViewModel
import cl.oursour.viewmodel.RecipeViewModel
import cl.oursour.viewmodel.ShopViewModel
import cl.oursour.viewmodel.FrequentIngredientViewModel

@Composable
fun OurSourApp(session: SessionViewModel = viewModel()) {
    val nav = rememberNavController()
    val user by session.user.collectAsState()
    val shop: ShopViewModel = viewModel()
    val products by shop.products.collectAsState()
    val cart by shop.cart.collectAsState()
    val address by shop.address.collectAsState()
    val orders by shop.orders.collectAsState()
    val recipes: RecipeViewModel = viewModel()
    val selected by recipes.selected.collectAsState()
    val frequent: FrequentIngredientViewModel = viewModel()
    val saved by frequent.ingredients.collectAsState()
    val frequentError by frequent.error.collectAsState()
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
        composable("ingredients") { IngredientsScreen(selected, recipes::toggle, { nav.navigate("recommendations") }, { nav.popBackStack() }, saved.map { it.ingredientId }.toSet(), frequent::save, frequent::remove, frequentError) }
        composable("recommendations") { RecommendationsScreen(selected, { nav.navigate("recipe/$it") }, { nav.popBackStack() }) }
        composable("recipe/{id}") { entry -> RecipeDetailScreen(entry.arguments?.getString("id")?.toIntOrNull() ?: -1, selected, { nav.popBackStack() }) }
        composable("catalog") { CatalogScreen(products, { nav.navigate("product/$it") }, { nav.navigate("cart") }, { nav.popBackStack() }) }
        composable("product/{id}") { entry ->
            val id = entry.arguments?.getString("id")?.toIntOrNull()
            ProductDetailScreen(products.firstOrNull { it.id == id }, shop::add, { nav.navigate("cart") }, { nav.popBackStack() })
        }
        composable("cart") { CartScreen(cart, shop::setQuantity, shop::remove, { nav.popBackStack() }, { nav.navigate("address") }) }
        composable("address") { AddressScreen(address, { value ->
            val error = shop.saveAddress(value)
            if (error == null) nav.navigate("summary")
            error
        }, { nav.popBackStack() }) }
        composable("summary") { SummaryScreen(cart, address, { approved ->
            val error = shop.checkout(approved)
            if (error == null) nav.navigate("confirmation/${shop.orders.value.last().id}") { popUpTo("home") }
            error
        }, { nav.popBackStack() }) }
        composable("confirmation/{id}") { entry ->
            OrderDetailScreen(orders.firstOrNull { it.id == entry.arguments?.getString("id")?.toIntOrNull() }, { nav.popBackStack("home", false) }, true)
        }
        composable("orders") { OrdersScreen(orders, { nav.navigate("order/$it") }, { nav.popBackStack() }) }
        composable("order/{id}") { entry -> OrderDetailScreen(orders.firstOrNull { it.id == entry.arguments?.getString("id")?.toIntOrNull() }, { nav.popBackStack() }) }
        composable("admin") {
            if (user?.role == Role.ADMIN) AdminScreen(products, { nav.navigate("edit/$it") }, { shop.deleteProduct(user?.role, it) }, { nav.navigate("metrics") }, { nav.popBackStack() })
            else Page("Acceso no disponible", { nav.popBackStack() }) { androidx.compose.material3.Text("Esta pantalla requiere el perfil administrador demo.") }
        }
        composable("edit/{id}") { entry ->
            val id = entry.arguments?.getString("id")?.toIntOrNull() ?: -1
            if (user?.role == Role.ADMIN) ProductFormScreen(products.firstOrNull { it.id == id }, id == 0, { name, description, price, stock ->
                val error = shop.saveProduct(user?.role, id, name, description, price, stock)
                if (error == null) nav.popBackStack()
                error
            }, { nav.popBackStack() })
            else Page("Acceso no disponible", { nav.popBackStack() }) { androidx.compose.material3.Text("Esta pantalla requiere el perfil administrador demo.") }
        }
        composable("metrics") {
            if (user?.role == Role.ADMIN) MetricsScreen(products, { nav.popBackStack() })
            else Page("Acceso no disponible", { nav.popBackStack() }) { androidx.compose.material3.Text("Esta pantalla requiere el perfil administrador demo.") }
        }
        composable("qr") { QrScreen(products, { nav.navigate("product/$it") { launchSingleTop = true } }, { nav.popBackStack() }) }
        composable("home") {
            val current = user
            if (current != null) HomeScreen(current, onRecipes = { nav.navigate("ingredients") }, onCatalog = { nav.navigate("catalog") }, onOrders = { nav.navigate("orders") }, onAdmin = { nav.navigate("admin") }, onQr = { nav.navigate("qr") }, onLogout = {
                session.logout()
                recipes.clear()
                shop.clearSession()
                nav.navigate("login") { popUpTo("home") { inclusive = true }; launchSingleTop = true }
            }) else LaunchedEffect(Unit) { nav.navigate("login") { popUpTo("home") { inclusive = true } } }
        }
    }
}
