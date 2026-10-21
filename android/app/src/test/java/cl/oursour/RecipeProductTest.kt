package cl.oursour

import cl.oursour.model.IngredientCategory
import cl.oursour.repository.ProductRepository
import cl.oursour.repository.RecipeRepository
import org.junit.Assert.*
import org.junit.Test

class RecipeProductTest {
    private val products = ProductRepository.initialProducts

    @Test fun missingIngredientsMapToCatalogProducts() {
        val sour = RecipeRepository.recipes.first { it.id == 1 }
        // Tiene limón y hielo: faltan pisco (producto 2) y azúcar (producto 3)
        assertEquals(listOf(2, 3), RecipeRepository.productsForMissing(sour, setOf(1, 4), products).map { it.id })
    }
    @Test fun ingredientsWithoutProductOrAlreadySelectedAreSkipped() {
        val limonada = RecipeRepository.recipes.first { it.id == 2 }
        // Falta agua, que no se vende: no hay producto que sugerir
        assertTrue(RecipeRepository.productsForMissing(limonada, setOf(1, 3, 4), products).isEmpty())
    }
    @Test fun deletedProductIsNotSuggested() {
        val sour = RecipeRepository.recipes.first { it.id == 1 }
        val withoutPisco = products.filterNot { it.id == 2 }
        assertEquals(listOf(3), RecipeRepository.productsForMissing(sour, setOf(1, 4), withoutPisco).map { it.id })
    }
    @Test fun everyRecipeHasAnExistingProductAndEveryIngredientACategory() {
        RecipeRepository.recipes.forEach { recipe -> assertNotNull(products.firstOrNull { it.id == recipe.productId }) }
        val groups = RecipeRepository.byCategory()
        assertEquals(RecipeRepository.ingredients.size, groups.values.sumOf { it.size })
        assertEquals(listOf(7), groups.getValue(IngredientCategory.OUR_SOUR).map { it.id })
    }
}
