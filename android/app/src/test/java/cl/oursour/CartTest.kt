package cl.oursour

import cl.oursour.viewmodel.ShopViewModel
import org.junit.Assert.*
import org.junit.Test

class CartTest {
    @Test fun addUpdateRemoveAndSubtotal() {
        val shop = ShopViewModel()
        assertNull(shop.add(1))
        assertNull(shop.add(1))
        assertNull(shop.add(3))
        assertEquals(2, shop.cart.value.size)
        assertEquals(5000L, shop.cart.value.sumOf { it.subtotal })
        assertNull(shop.setQuantity(1, 3))
        assertEquals(6800L, shop.cart.value.sumOf { it.subtotal })
        shop.remove(1)
        assertEquals(1400L, shop.cart.value.sumOf { it.subtotal })
        shop.clearCart()
        assertTrue(shop.cart.value.isEmpty())
    }
    @Test fun invalidQuantitiesStockAndUnknownDoNotChangeCart() {
        val shop = ShopViewModel()
        assertNotNull(shop.setQuantity(1, 0))
        assertNotNull(shop.setQuantity(1, -1))
        assertNotNull(shop.setQuantity(1, 100))
        assertNotNull(shop.setQuantity(1, 21))
        assertNotNull(shop.add(5))
        assertNotNull(shop.add(999))
        assertTrue(shop.cart.value.isEmpty())
    }
}
