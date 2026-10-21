package cl.oursour

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class RecipeToCartFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun missingIngredientCanBeAddedToCartFromRecipe() {
        compose.onNodeWithText("Entrar como cliente demo").performClick()
        compose.onNodeWithText("Buscar recetas").performClick()
        compose.onNodeWithText("Frutas y hierbas").assertExists()
        // Orden por categoría: Mix sour, Pisco, Limón... se marca Limón (tercera casilla)
        compose.onAllNodes(isToggleable())[2].performScrollTo().performClick()
        compose.onNodeWithText("Ver recetas recomendadas").performScrollTo().performClick()
        compose.onNodeWithText("Ver Sour clásico").performScrollTo().performClick()
        compose.onNodeWithText("Producto Our Sour sugerido").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Agregar Pisco de demostración").performScrollTo().performClick()
        compose.onNodeWithText("Pisco de demostración añadido al carro.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Ver carro").performScrollTo().performClick()
        compose.onNodeWithText("Tu carro").assertIsDisplayed()
        compose.onNodeWithText("Pisco de demostración").assertIsDisplayed()
    }
}
