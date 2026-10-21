package cl.oursour.repository

import cl.oursour.model.*

object RecipeRepository {
    val ingredients = listOf(Ingredient(1, "Limón"), Ingredient(2, "Pisco"), Ingredient(3, "Azúcar"), Ingredient(4, "Hielo"), Ingredient(5, "Jengibre"), Ingredient(6, "Agua"))
    val recipes = listOf(
        Recipe(1, "Sour clásico", listOf(RecipeIngredient(1, "30 ml de jugo"), RecipeIngredient(2, "60 ml"), RecipeIngredient(3, "15 g"), RecipeIngredient(4, "4 cubos")), "Disolver el azúcar en el jugo de limón. Agregar pisco y hielo, agitar y servir. Una porción. Solo para mayores de edad."),
        Recipe(2, "Limonada fresca", listOf(RecipeIngredient(1, "30 ml de jugo"), RecipeIngredient(3, "10 g"), RecipeIngredient(4, "4 cubos"), RecipeIngredient(6, "200 ml")), "Mezclar agua, limón y azúcar. Agregar hielo y servir. Una porción sin alcohol."),
        Recipe(3, "Limonada de jengibre", listOf(RecipeIngredient(1, "30 ml de jugo"), RecipeIngredient(5, "5 g rallado"), RecipeIngredient(6, "200 ml")), "Mezclar el jengibre con agua y limón. Reposar cinco minutos y colar. Una porción sin alcohol.")
    )
    fun ingredientName(id: Int): String = ingredients.firstOrNull { it.id == id }?.name ?: "Ingrediente desconocido"
    fun recommend(selected: Set<Int>): List<Recommendation> {
        if (selected.isEmpty()) return emptyList()
        return recipes.map { recipe ->
            val missing = recipe.ingredients.map { it.ingredientId }.filterNot { it in selected }
            Recommendation(recipe, recipe.ingredients.size - missing.size, missing)
        }.filter { it.available > 0 }.sortedWith(compareByDescending<Recommendation> { it.ratio }.thenBy { it.recipe.id })
    }
}
