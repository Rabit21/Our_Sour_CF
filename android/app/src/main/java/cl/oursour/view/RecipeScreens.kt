package cl.oursour.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cl.oursour.model.Product
import cl.oursour.repository.RecipeRepository

@Composable
fun IngredientsScreen(selected: Set<Int>, onToggle: (Int) -> Unit, onSearch: () -> Unit, onBack: () -> Unit, frequentIds: Set<Int>, onSave: (Int) -> Unit, onRemove: (Int) -> Unit, error: String?) {
    Page("Tus ingredientes", onBack) {
        Text("Marca lo que tienes para encontrar recetas y ver lo que falta.", style = MaterialTheme.typography.bodyLarge)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        SectionTitle("Frecuentes guardados")
        if (frequentIds.isEmpty()) Text("Aún no tienes ingredientes frecuentes. Usa Guardar en la lista de abajo.")
        frequentIds.sorted().forEach { id ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(RecipeRepository.ingredientName(id), Modifier.weight(1f))
                TextButton(onClick = { onRemove(id) }) { Text("Quitar ${RecipeRepository.ingredientName(id)}") }
            }
        }
        SectionTitle("Todos los ingredientes")
        RecipeRepository.byCategory().forEach { (category, items) ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(vertical = 12.dp)) {
                    Text(category.label.uppercase(), Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.tertiary)
                    items.forEach { ingredient ->
                        // Toda la fila marca o desmarca: se puede tocar el nombre, no solo la casilla
                        Row(Modifier.fillMaxWidth()
                            .toggleable(value = ingredient.id in selected, role = Role.Checkbox, onValueChange = { onToggle(ingredient.id) })
                            .padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = ingredient.id in selected, onCheckedChange = null)
                            Text(ingredient.name, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                            TextButton(onClick = { onSave(ingredient.id) }, enabled = ingredient.id !in frequentIds) {
                                Text(if (ingredient.id in frequentIds) "Guardado" else "Guardar")
                            }
                        }
                    }
                }
            }
        }
        Text("${selected.size} ingredientes seleccionados", style = MaterialTheme.typography.labelLarge)
        Button(onClick = onSearch, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Ver recetas recomendadas") }
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
                    LinearProgressIndicator(progress = { recommendation.ratio.toFloat() }, modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.secondaryContainer)
                    Text(if (recommendation.missing.isEmpty()) "Tienes todos los ingredientes" else "Faltan: " + recommendation.missing.joinToString { RecipeRepository.ingredientName(it) })
                    FilledTonalButton(onClick = { onDetail(recommendation.recipe.id) }) { Text("Ver ${recommendation.recipe.name}") }
                }
            }
        }
    }
}

@Composable
fun RecipeDetailScreen(id: Int, selected: Set<Int>, products: List<Product>, onAdd: (Int) -> String?, onProduct: (Int) -> Unit, onCart: () -> Unit, onBack: () -> Unit) {
    val recipe = RecipeRepository.recipes.firstOrNull { it.id == id }
    var message by rememberSaveable(id) { mutableStateOf<String?>(null) }
    Page(recipe?.name ?: "Receta no encontrada", onBack) {
        if (recipe != null) {
            SectionTitle("Ingredientes para una porción")
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    recipe.ingredients.forEach { item ->
                        val available = item.ingredientId in selected
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("${RecipeRepository.ingredientName(item.ingredientId)}: ${item.quantity}", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                            Text(if (available) "Disponible" else "Falta", style = MaterialTheme.typography.labelLarge,
                                color = if (available) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }
            SectionTitle("Preparación")
            Text(recipe.preparation, style = MaterialTheme.typography.bodyLarge)
            val missingProducts = RecipeRepository.productsForMissing(recipe, selected, products)
            if (missingProducts.isNotEmpty()) {
                SectionTitle("Completa lo que falta")
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        missingProducts.forEach { product ->
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("${product.name} · ${pesos(product.price)}", Modifier.weight(1f))
                                FilledTonalButton(onClick = { message = onAdd(product.id) ?: "${product.name} añadido al carro." }, enabled = product.stock > 0) {
                                    Text(if (product.stock > 0) "Agregar ${product.name}" else "Sin stock")
                                }
                            }
                        }
                    }
                }
            }
            val suggested = products.firstOrNull { it.id == recipe.productId }
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Producto Our Sour sugerido", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    if (suggested == null) Text("El producto asociado ya no está en el catálogo.")
                    else {
                        Text("${suggested.name} · ${pesos(suggested.price)}")
                        OutlinedButton(onClick = { onProduct(suggested.id) }) { Text("Ver producto sugerido") }
                    }
                }
            }
            message?.let { Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge) }
            OutlinedButton(onClick = onCart, modifier = Modifier.fillMaxWidth()) { Text("Ver carro") }
        }
    }
}
