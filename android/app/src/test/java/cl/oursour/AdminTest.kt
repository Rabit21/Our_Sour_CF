package cl.oursour

import cl.oursour.model.Role
import cl.oursour.viewmodel.ShopViewModel
import org.junit.Assert.*
import org.junit.Test

class AdminTest {
    @Test fun roleAndValidationBlockChanges() {
        val shop = ShopViewModel()
        assertNotNull(shop.saveProduct(Role.CLIENTE, 0, "Nuevo", "Descripción demo", 1000, 1))
        assertNotNull(shop.deleteProduct(Role.CLIENTE, 1))
        assertNotNull(shop.saveProduct(Role.ADMIN, 0, "", "Descripción demo", 1000, 1))
        assertNotNull(shop.saveProduct(Role.ADMIN, 0, "Nuevo", "Descripción demo", -1, 1))
        assertEquals(5, shop.products.value.size)
    }
    @Test fun createEditDeleteUsesStableIds() {
        val shop = ShopViewModel()
        assertNull(shop.saveProduct(Role.ADMIN, 0, "Producto demo", "Descripción demo", 1200, 2))
        val id = shop.products.value.last().id
        assertNull(shop.saveProduct(Role.ADMIN, id, "Producto editado", "Descripción demo", 1500, 3))
        assertEquals(1500L, shop.products.value.last().price)
        shop.add(id)
        assertNull(shop.deleteProduct(Role.ADMIN, id))
        assertTrue(shop.cart.value.isEmpty())
        assertNull(shop.saveProduct(Role.ADMIN, 0, "Otro producto", "Descripción demo", 1200, 2))
        assertTrue(shop.products.value.last().id > id)
    }
}
