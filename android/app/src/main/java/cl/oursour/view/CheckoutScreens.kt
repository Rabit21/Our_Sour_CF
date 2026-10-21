package cl.oursour.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.oursour.model.*

@Composable
fun AddressScreen(address: DeliveryAddress?, onContinue: (DeliveryAddress) -> String?, onBack: () -> Unit) {
    var recipient by rememberSaveable { mutableStateOf(address?.recipient ?: "") }
    var street by rememberSaveable { mutableStateOf(address?.street ?: "") }
    var commune by rememberSaveable { mutableStateOf(address?.commune ?: "Santiago") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    Page("Dirección de despacho", onBack) {
        Text("Usa datos ficticios. Cobertura y tarifa de demostración.")
        OutlinedTextField(recipient, { recipient = it }, label = { Text("Destinatario") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(street, { street = it }, label = { Text("Calle y número") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        SectionTitle("Comuna")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CheckoutRules.communes.forEach { name ->
                FilterChip(selected = commune == name, onClick = { commune = name }, label = { Text(name) })
            }
        }
        NoticeCard("Despacho demo: $3.500. Gratis desde $30.000 en productos.")
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = { error = onContinue(DeliveryAddress(recipient, street, commune)) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Revisar compra") }
    }
}

@Composable
fun SummaryScreen(lines: List<CartLine>, address: DeliveryAddress?, onPay: (Boolean) -> String?, onBack: () -> Unit) {
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val subtotal = lines.sumOf { it.subtotal }
    Page("Resumen y pago simulado", onBack) {
        SectionTitle("Productos")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                lines.forEach { Text("${it.quantity} × ${it.product.name}: ${pesos(it.subtotal)}") }
            }
        }
        address?.let {
            SectionTitle("Despacho a")
            Text("${it.recipient}\n${it.street}, ${it.commune}")
        }
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Productos: ${pesos(subtotal)}")
                Text("Despacho: ${pesos(CheckoutRules.shipping(subtotal))}")
                Text("Total: ${pesos(subtotal + CheckoutRules.shipping(subtotal))}", style = MaterialTheme.typography.headlineSmall)
            }
        }
        NoticeCard("Demostración sin cobros. Elige un resultado para probar el flujo.")
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = { error = onPay(true) }, enabled = lines.isNotEmpty() && address != null, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Simular pago aprobado") }
        OutlinedButton(onClick = { error = onPay(false) }, enabled = lines.isNotEmpty() && address != null, modifier = Modifier.fillMaxWidth()) { Text("Simular pago rechazado") }
    }
}

@Composable
fun OrdersScreen(orders: List<Order>, onDetail: (Int) -> Unit, onBack: () -> Unit) {
    Page("Mis pedidos", onBack) {
        Text("Pedidos de esta sesión de demostración. Se borran al cerrar sesión o terminar el proceso.")
        if (orders.isEmpty()) Text("Todavía no tienes pedidos.")
        orders.reversed().forEach { order ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pedido #${order.id} · ${pesos(order.total)}", style = MaterialTheme.typography.titleLarge)
                    val units = order.lines.sumOf { it.quantity }
                    Text("$units ${if (units == 1) "producto" else "productos"} · ${order.address.commune}", style = MaterialTheme.typography.bodyMedium)
                    FilledTonalButton(onClick = { onDetail(order.id) }) { Text("Ver pedido #${order.id}") }
                }
            }
        }
    }
}

@Composable
fun OrderDetailScreen(order: Order?, onBack: () -> Unit, confirmed: Boolean = false) {
    Page(if (confirmed) "Pedido confirmado" else "Detalle del pedido", onBack) {
        if (order == null) Text("Pedido no encontrado en esta sesión.")
        else {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text("Pedido #${order.id} · Pago simulado aprobado", Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
            }
            SectionTitle("Productos")
            order.lines.forEach { Text("${it.quantity} × ${it.product.name}: ${pesos(it.subtotal)}") }
            SectionTitle("Despacho a")
            Text("${order.address.recipient}\n${order.address.street}, ${order.address.commune}")
            Text("Despacho: ${pesos(order.shipping)}")
            Text("Total: ${pesos(order.total)}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            NoticeCard("No se ha realizado ningún cobro ni envío real.")
        }
    }
}
