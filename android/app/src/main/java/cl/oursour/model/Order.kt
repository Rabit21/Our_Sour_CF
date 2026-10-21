package cl.oursour.model

data class DeliveryAddress(val recipient: String, val street: String, val commune: String)
data class Order(val id: Int, val lines: List<CartLine>, val address: DeliveryAddress, val shipping: Long) {
    val subtotal: Long get() = lines.sumOf { it.subtotal }
    val total: Long get() = subtotal + shipping
}

object CheckoutRules {
    val communes = listOf("Santiago", "Providencia", "Maipú")
    fun validate(address: DeliveryAddress): String? = when {
        address.recipient.trim().length !in 2..60 -> "Escribe un destinatario de 2 a 60 caracteres."
        address.street.trim().length !in 5..120 -> "Escribe una dirección de 5 a 120 caracteres."
        address.commune !in communes -> "Elige una comuna disponible."
        else -> null
    }
    fun shipping(subtotal: Long): Long = if (subtotal >= 30000) 0 else 3500
}
