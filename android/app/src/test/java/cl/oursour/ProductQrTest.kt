package cl.oursour

import cl.oursour.model.ProductQr
import org.junit.Assert.*
import org.junit.Test

class ProductQrTest {
    @Test fun acceptsOnlyPositiveProductIdentifiers() {
        assertEquals(1, ProductQr.productId("OURSOUR:PRODUCTO:1"))
        assertEquals(9999, ProductQr.productId("OURSOUR:PRODUCTO:9999"))
        listOf("https://example.test", "OURSOUR:PRODUCTO:0", "OURSOUR:PRODUCTO:-1", "OURSOUR:PRODUCTO:1 ", "OURSOUR:PRODUCTO:99999999999999999999", "OURSOUR:RECETA:1", "").forEach {
            assertNull(ProductQr.productId(it))
        }
    }
}
