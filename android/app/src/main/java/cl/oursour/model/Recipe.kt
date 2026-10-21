package cl.oursour.model

/** Clasificación del ingrediente según el caso Our Sour. */
enum class IngredientCategory(val label: String) {
    OUR_SOUR("Productos Our Sour"),
    DESTILADO("Destilados y licores"),
    FRUTA("Frutas y hierbas"),
    MIXER("Jugos y mixers"),
    OTRO("Otros")
}

/** productId: producto del catálogo que permite comprar este ingrediente (null si no se vende). */
data class Ingredient(val id: Int, val name: String, val category: IngredientCategory, val productId: Int? = null)
data class RecipeIngredient(val ingredientId: Int, val quantity: String)
/** productId: producto Our Sour asociado a la receta. */
data class Recipe(val id: Int, val name: String, val ingredients: List<RecipeIngredient>, val preparation: String, val productId: Int)
data class Recommendation(val recipe: Recipe, val available: Int, val missing: List<Int>) {
    val ratio: Double get() = available.toDouble() / recipe.ingredients.size
}
