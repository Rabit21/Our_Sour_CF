package cl.oursour.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.oursour.model.*
import java.text.NumberFormat
import java.util.Locale

fun pesos(amount: Long): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(amount)

@Composable
fun CatalogScreen(products: List<Product>, onDetail: (Int) -> Unit, onCart: () -> Unit, onBack: () -> Unit) {
    Page("Catálogo", onBack) {
        Text("Productos y precios ficticios · Pesos chilenos", style = MaterialTheme.typography.bodyLarge)
        OutlinedButton(onClick = onCart, modifier = Modifier.fillMaxWidth()) { Text("Ver carro") }
        if (products.isEmpty()) Text("No hay productos disponibles.")
        products.forEach { product ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(product.name, style = MaterialTheme.typography.titleLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(pesos(product.price), Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        StockLabel(product.stock)
                    }
                    FilledTonalButton(onClick = { onDetail(product.id) }) { Text("Ver ${product.name}") }
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
            Text(product.description, style = MaterialTheme.typography.bodyLarge)
            Text(pesos(product.price), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text(if (product.stock > 0) "Stock disponible: ${product.stock}" else "Sin stock",
                color = if (product.stock > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.tertiary)
            Button(onClick = { message = onAdd(product.id) ?: "Producto añadido al carro." }, enabled = product.stock > 0,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Añadir al carro") }
            message?.let { Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge) }
            OutlinedButton(onClick = onCart, modifier = Modifier.fillMaxWidth()) { Text("Ver carro") }
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
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(onClick = { error = onQuantity(line.product.id, line.quantity - 1) }, enabled = line.quantity > 1) { Text("Menos") }
                        OutlinedButton(onClick = { error = onQuantity(line.product.id, line.quantity + 1) }) { Text("Más") }
                        TextButton(onClick = { onRemove(line.product.id); error = null }) { Text("Eliminar") }
                    }
                }
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Text("Subtotal: ${pesos(lines.sumOf { it.subtotal })}", Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
        }
        Button(onClick = onCheckout, enabled = lines.isNotEmpty(), modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Continuar compra") }
    }
}

/** Stock con color: dorado cuando no hay unidades. */
@Composable
fun StockLabel(stock: Int) {
    Text(if (stock > 0) "Stock: $stock" else "Sin stock", style = MaterialTheme.typography.labelLarge,
        color = if (stock > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.tertiary)
}
