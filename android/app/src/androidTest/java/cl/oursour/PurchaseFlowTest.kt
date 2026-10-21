package cl.oursour

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class PurchaseFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun buyWithRejectionThenApprovalAndFindOrder() {
        compose.onNodeWithText("Entrar como cliente demo").performScrollTo().performClick()
        compose.onNodeWithText("Explorar catálogo").performScrollTo().performClick()
        compose.onNodeWithText("Ver Limón fresco").performClick()
        compose.onNodeWithText("Añadir al carro").performClick()
        compose.onNodeWithText("Ver carro").performClick()
        compose.onNodeWithText("Continuar compra").performScrollTo().performClick()
        compose.onNodeWithText("Revisar compra").performScrollTo().performClick()
        compose.onNodeWithText("Escribe un destinatario de 2 a 60 caracteres.").assertExists()
        compose.onNodeWithText("Destinatario").performScrollTo().performTextInput("Persona Demo")
        compose.onNodeWithText("Calle y número").performScrollTo().performTextInput("Calle Demo 123")
        compose.onNodeWithText("Revisar compra").performScrollTo().performClick()
        compose.onNodeWithText("Simular pago rechazado").performScrollTo().performClick()
        compose.onNodeWithText("Pago simulado rechazado. Puedes intentarlo nuevamente.").assertExists()
        compose.onNodeWithText("Simular pago aprobado").performScrollTo().performClick()
        compose.onNodeWithText("Pedido confirmado").assertIsDisplayed()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Mis pedidos").performScrollTo().performClick()
        compose.onNodeWithText("Ver pedido #1").performClick()
        compose.onNodeWithText("Detalle del pedido").assertIsDisplayed()
    }
}
