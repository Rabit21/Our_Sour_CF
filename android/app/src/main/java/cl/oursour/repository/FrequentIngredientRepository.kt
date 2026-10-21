package cl.oursour.repository

import cl.oursour.dao.FrequentIngredientDao
import cl.oursour.model.FrequentIngredient

class FrequentIngredientRepository(private val dao: FrequentIngredientDao) {
    val ingredients = dao.observeAll()
    suspend fun save(id: Int) {
        require(RecipeRepository.ingredients.any { it.id == id }) { "Ingrediente desconocido" }
        dao.insert(FrequentIngredient(id))
    }
    suspend fun remove(id: Int) = dao.delete(id)
}
