package cl.oursour.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.oursour.model.*
import java.text.NumberFormat
import java.util.Locale

fun pesos(amount: Long): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(amount)

@Composable
fun CatalogScreen(products: List<Product>, onDetail: (Int) -> Unit, onCart: () -> Unit, onBack: () -> Unit) {
    Page("Catálogo", onBack) {
        Text("Productos y precios ficticios · Pesos chilenos")
        OutlinedButton(onClick = onCart) { Text("Ver carro") }
        if (products.isEmpty()) Text("No hay productos disponibles.")
        products.forEach { product ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(product.name, style = MaterialTheme.typography.titleLarge)
                    Text("${pesos(product.price)} · Stock: ${product.stock}")
                    TextButton(onClick = { onDetail(product.id) }) { Text("Ver ${product.name}") }
                }
            }
        }
    }
}

@Composable
fun ProductDetailScreen(product: Product?, onAdd: (Int) -> String?, onCart: () -> Unit, onBack: () -> Unit) {
    var message by rememberSaveable(product?.id) { mutableStateOf<String?>(null) }
    Page(product?.name ?: "Producto no encontrado", onBack) {
        if (product != null) {
            Text(product.description)
            Text(pesos(product.price), style = MaterialTheme.typography.headlineMedium)
            Text(if (product.stock > 0) "Stock disponible: ${product.stock}" else "Sin stock")
            Button(onClick = { message = onAdd(product.id) ?: "Producto añadido al carro." }, enabled = product.stock > 0) { Text("Añadir al carro") }
            message?.let { Text(it) }
            OutlinedButton(onClick = onCart) { Text("Ver carro") }
        }
    }
}

@Composable
fun CartScreen(lines: List<CartLine>, onQuantity: (Int, Int) -> String?, onRemove: (Int) -> Unit, onBack: () -> Unit, onCheckout: () -> Unit) {
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    Page("Tu carro", onBack) {
        if (lines.isEmpty()) Text("Tu carro está vacío. Explora el catálogo para agregar productos.")
        lines.forEach { line ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(line.product.name, style = MaterialTheme.typography.titleLarge)
                    Text("${pesos(line.product.price)} × ${line.quantity} = ${pesos(line.subtotal)}")
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { error = onQuantity(line.product.id, line.quantity - 1) }, enabled = line.quantity > 1) { Text("Menos") }
                        OutlinedButton(onClick = { error = onQuantity(line.product.id, line.quantity + 1) }) { Text("Más") }
                        TextButton(onClick = { onRemove(line.product.id); error = null }) { Text("Eliminar") }
                    }
                }
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Text("Subtotal: ${pesos(lines.sumOf { it.subtotal })}", style = MaterialTheme.typography.titleLarge)
        Button(onClick = onCheckout, enabled = lines.isNotEmpty()) { Text("Continuar compra") }
    }
}
