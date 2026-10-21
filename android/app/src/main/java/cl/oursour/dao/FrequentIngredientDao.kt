package cl.oursour.dao

import androidx.room.*
import cl.oursour.model.FrequentIngredient
import kotlinx.coroutines.flow.Flow

@Dao
interface FrequentIngredientDao {
    @Query("SELECT * FROM frequent_ingredients ORDER BY ingredientId")
    fun observeAll(): Flow<List<FrequentIngredient>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ingredient: FrequentIngredient)
    @Query("DELETE FROM frequent_ingredients WHERE ingredientId = :id")
    suspend fun delete(id: Int)
}
