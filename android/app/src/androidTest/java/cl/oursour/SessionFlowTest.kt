package cl.oursour

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class SessionFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun bothRolesCanEnterAndLeave() {
        compose.onNodeWithText("Entrar como cliente demo").performClick()
        compose.onNodeWithText("Hola, Cliente demo").assertIsDisplayed()
        compose.onNodeWithText("Cerrar sesión").performClick()
        compose.onNodeWithText("Entrar como administrador demo").performClick()
        compose.onNodeWithText("Hola, Admin demo").assertIsDisplayed()
        compose.onNodeWithText("Cerrar sesión").performClick()
        compose.onNodeWithText("Bienvenido").assertIsDisplayed()
    }

    @Test fun registrationValidatesAndBackReturnsToLogin() {
        compose.onNodeWithText("Crear perfil de demostración").performClick()
        compose.onNodeWithText("Crear perfil").performClick()
        compose.onNodeWithText("Escribe un nombre de 2 a 60 caracteres.").assertIsDisplayed()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Bienvenido").assertIsDisplayed()
        compose.onNodeWithText("Crear perfil de demostración").performClick()
        compose.onNodeWithText("Nombre").performTextInput("Persona Demo")
        compose.onNodeWithText("Correo ficticio").performTextInput("persona@example.test")
        compose.onNodeWithText("Crear perfil").performScrollTo().performClick()
        compose.onNodeWithText("Hola, Persona Demo").assertIsDisplayed()
    }
}
