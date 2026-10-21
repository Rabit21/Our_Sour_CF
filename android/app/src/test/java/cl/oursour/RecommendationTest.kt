package cl.oursour

import cl.oursour.repository.RecipeRepository
import org.junit.Assert.*
import org.junit.Test

class RecommendationTest {
    @Test fun emptySelectionReturnsNoResults() { assertTrue(RecipeRepository.recommend(emptySet()).isEmpty()) }
    @Test fun fullMatchHasNoMissingAndComesFirst() {
        val first = RecipeRepository.recommend(setOf(1, 2, 3, 4)).first()
        assertEquals(1, first.recipe.id)
        assertEquals(1.0, first.ratio, 0.001)
        assertTrue(first.missing.isEmpty())
    }
    @Test fun partialMatchesAreSortedAndTieUsesId() {
        val results = RecipeRepository.recommend(setOf(1))
        assertEquals(listOf(3, 1, 2), results.map { it.recipe.id })
        assertEquals(listOf(5, 6), results.first().missing)
        assertEquals(1.0 / 3, results.first().ratio, 0.001)
    }
    @Test fun unknownIngredientDoesNotMatch() { assertTrue(RecipeRepository.recommend(setOf(999)).isEmpty()) }
}
