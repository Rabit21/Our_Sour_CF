package cl.oursour.repository

import cl.oursour.model.Product

object ProductRepository {
    val initialProducts = listOf(
        Product(1, "Limón fresco", "Malla de 1 kg para recetas y bebidas.", 1800, 20),
        Product(2, "Pisco de demostración", "Botella ficticia de 700 ml. Solo para mayores de edad.", 8900, 8),
        Product(3, "Azúcar", "Bolsa de 1 kg.", 1400, 15),
        Product(4, "Jengibre", "Porción de 100 g.", 1200, 12),
        Product(5, "Vaso de cóctel", "Vaso de 350 ml, producto de demostración.", 3500, 0),
        Product(6, "Mix sour Our Sour", "Mezcla ficticia de limón y almíbar, 700 ml. Producto de demostración.", 5990, 10)
    )
}
