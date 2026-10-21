package cl.oursour.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.oursour.model.Product

@Composable
fun AdminScreen(products: List<Product>, onEdit: (Int) -> Unit, onDelete: (Int) -> String?, onMetrics: () -> Unit, onBack: () -> Unit) {
    var deleting by remember { mutableStateOf<Product?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    Page("Administración demo", onBack) {
        Text("Los cambios solo afectan al catálogo local de esta ejecución.")
        Button(onClick = { onEdit(0) }) { Text("Crear producto") }
        OutlinedButton(onClick = onMetrics) { Text("Ver métricas demo") }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        products.forEach { product ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(product.name, style = MaterialTheme.typography.titleLarge)
                    Text("${pesos(product.price)} · Stock ${product.stock}")
                    TextButton(onClick = { onEdit(product.id) }) { Text("Editar ${product.name}") }
                    TextButton(onClick = { deleting = product }) { Text("Eliminar ${product.name}") }
                }
            }
        }
    }
    deleting?.let { product ->
        AlertDialog(onDismissRequest = { deleting = null }, title = { Text("Eliminar producto") },
            text = { Text("¿Eliminar ${product.name} del catálogo demo? También se quitará del carro; los pedidos conservan sus datos históricos.") },
            confirmButton = { TextButton(onClick = { error = onDelete(product.id); deleting = null }) { Text("Confirmar eliminación") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancelar") } })
    }
}

@Composable
fun ProductFormScreen(product: Product?, isNew: Boolean, onSave: (String, String, Long, Int) -> String?, onBack: () -> Unit) {
    var name by rememberSaveable { mutableStateOf(product?.name ?: "") }
    var description by rememberSaveable { mutableStateOf(product?.description ?: "") }
    var price by rememberSaveable { mutableStateOf(product?.price?.toString() ?: "") }
    var stock by rememberSaveable { mutableStateOf(product?.stock?.toString() ?: "") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    Page(if (isNew) "Nuevo producto" else "Editar producto", onBack) {
        if (!isNew && product == null) Text("Producto no encontrado.")
        else {
            OutlinedTextField(name, { name = it }, label = { Text("Nombre del producto") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(description, { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(price, { price = it }, label = { Text("Precio en pesos") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(stock, { stock = it }, label = { Text("Unidades disponibles") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(onClick = { error = onSave(name, description, price.toLongOrNull() ?: -1, stock.toIntOrNull() ?: -1) }) { Text("Guardar producto") }
        }
    }
}

@Composable
fun MetricsScreen(products: List<Product>, onBack: () -> Unit) {
    Page("Métricas de demostración", onBack) {
        Text("Catálogo local actual", style = MaterialTheme.typography.titleMedium)
        Text("Productos: ${products.size}")
        Text("Sin stock: ${products.count { it.stock == 0 }}")
        Text("Ejemplos ficticios, no mediciones reales", style = MaterialTheme.typography.titleMedium)
        Text("Búsquedas de ejemplo: 128")
        Text("Receta destacada de ejemplo: Sour clásico")
        Text("Las búsquedas reales todavía no se registran ni se envían a un servidor.")
    }
}
