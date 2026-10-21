package cl.oursour.viewmodel

import androidx.lifecycle.ViewModel
import cl.oursour.model.*
import cl.oursour.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShopViewModel : ViewModel() {
    private val _products = MutableStateFlow(ProductRepository.initialProducts)
    val products = _products.asStateFlow()
    private val _cart = MutableStateFlow<List<CartLine>>(emptyList())
    val cart = _cart.asStateFlow()

    private val _address = MutableStateFlow<DeliveryAddress?>(null)
    val address = _address.asStateFlow()
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()
    private var nextOrderId = 1
    private var nextProductId = 6

    fun saveProduct(role: Role?, id: Int, name: String, description: String, price: Long, stock: Int): String? {
        if (role != Role.ADMIN) return "Solo el administrador demo puede editar productos."
        if (name.trim().length !in 2..80) return "El nombre debe tener entre 2 y 80 caracteres."
        if (description.trim().length !in 5..500) return "La descripción debe tener entre 5 y 500 caracteres."
        if (price !in 1..10000000) return "El precio debe estar entre 1 y 10.000.000 pesos."
        if (stock !in 0..9999) return "El stock debe estar entre 0 y 9999."
        if (_products.value.any { it.id != id && it.name.equals(name.trim(), ignoreCase = true) }) return "Ya existe un producto con ese nombre."
        if (id != 0 && _products.value.none { it.id == id }) return "Producto no encontrado."
        val product = Product(if (id == 0) nextProductId++ else id, name.trim(), description.trim(), price, stock)
        _products.value = if (id == 0) _products.value + product else _products.value.map { if (it.id == id) product else it }
        return null
    }
    fun deleteProduct(role: Role?, id: Int): String? {
        if (role != Role.ADMIN) return "Solo el administrador demo puede eliminar productos."
        if (_products.value.none { it.id == id }) return "Producto no encontrado."
        _products.value = _products.value.filterNot { it.id == id }
        remove(id)
        return null
    }

    fun saveAddress(value: DeliveryAddress): String? {
        val error = CheckoutRules.validate(value)
        if (error != null) return error
        _address.value = value.copy(recipient = value.recipient.trim(), street = value.street.trim())
        return null
    }
    fun checkout(approved: Boolean): String? {
        if (_cart.value.isEmpty()) return "El carro está vacío."
        val delivery = _address.value ?: return "Completa la dirección de despacho."
        CheckoutRules.validate(delivery)?.let { return it }
        val currentLines = _cart.value.map { line ->
            val product = _products.value.firstOrNull { it.id == line.product.id } ?: return "Un producto ya no está disponible."
            if (line.quantity !in 1..99 || line.quantity > product.stock) return "No hay stock suficiente para ${product.name}."
            if (product.price != line.product.price) return "Cambió un precio. Actualiza el carro antes de pagar."
            CartLine(product, line.quantity)
        }
        if (!approved) return "Pago simulado rechazado. Puedes intentarlo nuevamente."
        val subtotal = currentLines.sumOf { it.subtotal }
        _orders.value = _orders.value + Order(nextOrderId++, currentLines, delivery, CheckoutRules.shipping(subtotal))
        _products.value = _products.value.map { product ->
            product.copy(stock = product.stock - (currentLines.firstOrNull { it.product.id == product.id }?.quantity ?: 0))
        }
        clearCart()
        return null
    }
    fun clearSession() {
        clearCart()
        _address.value = null
        _orders.value = emptyList()
    }
    fun add(id: Int): String? {
        val count = _cart.value.firstOrNull { it.product.id == id }?.quantity ?: 0
        return setQuantity(id, count + 1)
    }
    fun setQuantity(id: Int, quantity: Int): String? {
        val product = _products.value.firstOrNull { it.id == id } ?: return "Producto no encontrado."
        if (quantity !in 1..99) return "La cantidad debe estar entre 1 y 99."
        if (quantity > product.stock) return "No hay stock suficiente."
        _cart.value = if (_cart.value.any { it.product.id == id })
            _cart.value.map { if (it.product.id == id) CartLine(product, quantity) else it }
        else _cart.value + CartLine(product, quantity)
        return null
    }
    fun remove(id: Int) { _cart.value = _cart.value.filterNot { it.product.id == id } }
    fun clearCart() { _cart.value = emptyList() }
}
