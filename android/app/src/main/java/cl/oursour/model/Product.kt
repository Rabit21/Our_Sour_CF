package cl.oursour.model

data class Product(val id: Int, val name: String, val description: String, val price: Long, val stock: Int)
data class CartLine(val product: Product, val quantity: Int) {
    val subtotal: Long get() = product.price * quantity
}
