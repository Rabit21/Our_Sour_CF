package cl.oursour.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.oursour.repository.RecipeRepository

@Composable
fun IngredientsScreen(selected: Set<Int>, onToggle: (Int) -> Unit, onSearch: () -> Unit, onBack: () -> Unit, frequentIds: Set<Int>, onSave: (Int) -> Unit, onRemove: (Int) -> Unit, error: String?) {
    Page("Tus ingredientes", onBack) {
        Text("Marca lo que tienes para encontrar recetas y ver lo que falta.")
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Text("Frecuentes guardados", style = MaterialTheme.typography.titleMedium)
        if (frequentIds.isEmpty()) Text("Aún no tienes ingredientes frecuentes.")
        frequentIds.sorted().forEach { id ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(RecipeRepository.ingredientName(id), Modifier.weight(1f).padding(top = 12.dp))
                TextButton(onClick = { onRemove(id) }) { Text("Quitar ${RecipeRepository.ingredientName(id)}") }
            }
        }
        Text("Todos los ingredientes", style = MaterialTheme.typography.titleMedium)
        RecipeRepository.ingredients.forEach { ingredient ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(ingredient.name, Modifier.weight(1f).padding(top = 12.dp))
                Checkbox(checked = ingredient.id in selected, onCheckedChange = { onToggle(ingredient.id) })
                TextButton(onClick = { onSave(ingredient.id) }, enabled = ingredient.id !in frequentIds) { Text(if (ingredient.id in frequentIds) "Guardado" else "Guardar ${ingredient.name}") }
            }
        }
        Text("${selected.size} ingredientes seleccionados")
        Button(onClick = onSearch) { Text("Ver recetas recomendadas") }
    }
}

@Composable
fun RecommendationsScreen(selected: Set<Int>, onDetail: (Int) -> Unit, onBack: () -> Unit) {
    val recommendations = RecipeRepository.recommend(selected)
    Page("Recetas recomendadas", onBack) {
        if (selected.isEmpty()) Text("Selecciona al menos un ingrediente para buscar recetas.")
        else if (recommendations.isEmpty()) Text("No hay recetas con estos ingredientes.")
        recommendations.forEach { recommendation ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(recommendation.recipe.name, style = MaterialTheme.typography.titleLarge)
                    Text("${recommendation.available} de ${recommendation.recipe.ingredients.size} ingredientes · ${(recommendation.ratio * 100).toInt()}%")
                    Text(if (recommendation.missing.isEmpty()) "Tienes todos los ingredientes" else "Faltan: " + recommendation.missing.joinToString { RecipeRepository.ingredientName(it) })
                    TextButton(onClick = { onDetail(recommendation.recipe.id) }) { Text("Ver ${recommendation.recipe.name}") }
                }
            }
        }
    }
}

@Composable
fun RecipeDetailScreen(id: Int, selected: Set<Int>, onBack: () -> Unit) {
    val recipe = RecipeRepository.recipes.firstOrNull { it.id == id }
    Page(recipe?.name ?: "Receta no encontrada", onBack) {
        if (recipe != null) {
            Text("Ingredientes para una porción", style = MaterialTheme.typography.titleMedium)
            recipe.ingredients.forEach { item ->
                Text("${RecipeRepository.ingredientName(item.ingredientId)}: ${item.quantity} · ${if (item.ingredientId in selected) "Disponible" else "Falta"}")
            }
            Text("Preparación", style = MaterialTheme.typography.titleMedium)
            Text(recipe.preparation)
        }
    }
}
