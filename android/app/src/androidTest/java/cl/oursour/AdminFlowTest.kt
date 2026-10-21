package cl.oursour

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class AdminFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun clientCannotSeeAdminAndAdminCanCreateEditDelete() {
        compose.onNodeWithText("Entrar como cliente demo").performClick()
        compose.onNodeWithText("Administrar productos").assertDoesNotExist()
        compose.onNodeWithText("Cerrar sesión").performClick()
        compose.onNodeWithText("Entrar como administrador demo").performClick()
        compose.onNodeWithText("Administrar productos").performClick()
        compose.onNodeWithText("Crear producto").performClick()
        compose.onNodeWithText("Nombre del producto").performTextInput("Producto prueba")
        compose.onNodeWithText("Descripción").performTextInput("Descripción de prueba")
        compose.onNodeWithText("Precio en pesos").performTextInput("2000")
        compose.onNodeWithText("Unidades disponibles").performScrollTo().performTextInput("5")
        compose.onNodeWithText("Guardar producto").performScrollTo().performClick()
        compose.onNodeWithText("Editar Producto prueba").performScrollTo().performClick()
        compose.onNodeWithText("Nombre del producto").performTextReplacement("Producto editado")
        compose.onNodeWithText("Guardar producto").performScrollTo().performClick()
        compose.onNodeWithText("Eliminar Producto editado").performScrollTo().performClick()
        compose.onNodeWithText("Confirmar eliminación").performClick()
        compose.onNodeWithText("Producto editado").assertDoesNotExist()
        compose.onNodeWithText("Ver métricas demo").performScrollTo().performClick()
        compose.onNodeWithText("Métricas de demostración").assertIsDisplayed()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Volver").performScrollTo().performClick()
        compose.onNodeWithText("Hola, Admin demo").assertIsDisplayed()
    }
}
