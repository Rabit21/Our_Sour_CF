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
