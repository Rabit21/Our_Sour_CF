package cl.oursour.repository

import cl.oursour.model.*

object RecipeRepository {
    val ingredients = listOf(
        Ingredient(1, "Limón", IngredientCategory.FRUTA, productId = 1),
        Ingredient(2, "Pisco", IngredientCategory.DESTILADO, productId = 2),
        Ingredient(3, "Azúcar", IngredientCategory.OTRO, productId = 3),
        Ingredient(4, "Hielo", IngredientCategory.OTRO),
        Ingredient(5, "Jengibre", IngredientCategory.FRUTA, productId = 4),
        Ingredient(6, "Agua", IngredientCategory.MIXER),
        Ingredient(7, "Mix sour Our Sour", IngredientCategory.OUR_SOUR, productId = 6)
    )
    val recipes = listOf(
        Recipe(1, "Sour clásico", listOf(RecipeIngredient(1, "30 ml de jugo"), RecipeIngredient(2, "60 ml"), RecipeIngredient(3, "15 g"), RecipeIngredient(4, "4 cubos")), "Disolver el azúcar en el jugo de limón. Agregar pisco y hielo, agitar y servir. Una porción. Solo para mayores de edad.", productId = 2),
        Recipe(2, "Limonada fresca", listOf(RecipeIngredient(1, "30 ml de jugo"), RecipeIngredient(3, "10 g"), RecipeIngredient(4, "4 cubos"), RecipeIngredient(6, "200 ml")), "Mezclar agua, limón y azúcar. Agregar hielo y servir. Una porción sin alcohol.", productId = 1),
        Recipe(3, "Limonada de jengibre", listOf(RecipeIngredient(1, "30 ml de jugo"), RecipeIngredient(5, "5 g rallado"), RecipeIngredient(6, "200 ml")), "Mezclar el jengibre con agua y limón. Reposar cinco minutos y colar. Una porción sin alcohol.", productId = 4),
        Recipe(4, "Sour express", listOf(RecipeIngredient(7, "90 ml"), RecipeIngredient(2, "60 ml"), RecipeIngredient(4, "4 cubos")), "Agitar el mix sour con pisco y hielo durante diez segundos y servir. Una porción. Solo para mayores de edad.", productId = 6)
    )
    fun ingredient(id: Int): Ingredient? = ingredients.firstOrNull { it.id == id }
    fun ingredientName(id: Int): String = ingredient(id)?.name ?: "Ingrediente desconocido"
    /** Ingredientes agrupados por categoría, en el orden del enum. */
    fun byCategory(): Map<IngredientCategory, List<Ingredient>> =
        IngredientCategory.values().associateWith { category -> ingredients.filter { it.category == category } }.filterValues { it.isNotEmpty() }
    fun recommend(selected: Set<Int>): List<Recommendation> {
        if (selected.isEmpty()) return emptyList()
        return recipes.map { recipe ->
            val missing = recipe.ingredients.map { it.ingredientId }.filterNot { it in selected }
            Recommendation(recipe, recipe.ingredients.size - missing.size, missing)
        }.filter { it.available > 0 }.sortedWith(compareByDescending<Recommendation> { it.ratio }.thenBy { it.recipe.id })
    }
    /** Productos del catálogo actual que cubren los ingredientes faltantes de la receta (sin repetir). */
    fun productsForMissing(recipe: Recipe, selected: Set<Int>, products: List<Product>): List<Product> =
        recipe.ingredients.map { it.ingredientId }.filterNot { it in selected }
            .mapNotNull { id -> ingredient(id)?.productId }.distinct()
            .mapNotNull { productId -> products.firstOrNull { it.id == productId } }
}
