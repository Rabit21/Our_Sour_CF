package cl.oursour

import cl.oursour.model.*
import cl.oursour.viewmodel.ShopViewModel
import org.junit.Assert.*
import org.junit.Test

class CheckoutTest {
    @Test fun validatesAddressAndShippingThreshold() {
        assertNotNull(CheckoutRules.validate(DeliveryAddress("", "", "Santiago")))
        assertNotNull(CheckoutRules.validate(DeliveryAddress("Demo", "Calle 123", "Desconocida")))
        assertEquals(3500L, CheckoutRules.shipping(29999))
        assertEquals(0L, CheckoutRules.shipping(30000))
    }
    @Test fun rejectionKeepsCartApprovalCreatesSnapshotAndReducesStock() {
        val shop = ShopViewModel()
        shop.add(1)
        assertNotNull(shop.checkout(true))
        assertNull(shop.saveAddress(DeliveryAddress("Persona Demo", "Calle Ficticia 123", "Santiago")))
        assertNotNull(shop.checkout(false))
        assertEquals(1, shop.cart.value.size)
        assertTrue(shop.orders.value.isEmpty())
        assertNull(shop.checkout(true))
        assertEquals(5300L, shop.orders.value.single().total)
        assertEquals(19, shop.products.value.first().stock)
        assertTrue(shop.cart.value.isEmpty())
        assertNotNull(shop.checkout(true))
        assertEquals(1, shop.orders.value.size)
        shop.clearSession()
        assertTrue(shop.orders.value.isEmpty())
        assertNull(shop.address.value)
    }
}
