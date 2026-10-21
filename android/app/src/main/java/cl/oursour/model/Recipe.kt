package cl.oursour.model

data class Ingredient(val id: Int, val name: String)
data class RecipeIngredient(val ingredientId: Int, val quantity: String)
data class Recipe(val id: Int, val name: String, val ingredients: List<RecipeIngredient>, val preparation: String)
data class Recommendation(val recipe: Recipe, val available: Int, val missing: List<Int>) {
    val ratio: Double get() = available.toDouble() / recipe.ingredients.size
}
