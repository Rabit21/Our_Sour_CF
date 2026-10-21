package cl.oursour.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "frequent_ingredients")
data class FrequentIngredient(@PrimaryKey val ingredientId: Int)
