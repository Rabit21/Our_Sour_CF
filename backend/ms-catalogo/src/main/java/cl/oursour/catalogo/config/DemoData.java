package cl.oursour.catalogo.config;

import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cl.oursour.catalogo.model.CategoriaIngrediente;
import cl.oursour.catalogo.model.Ingrediente;
import cl.oursour.catalogo.model.Producto;
import cl.oursour.catalogo.model.Receta;
import cl.oursour.catalogo.model.RecetaIngrediente;
import cl.oursour.catalogo.repository.IngredienteRepository;
import cl.oursour.catalogo.repository.ProductoRepository;
import cl.oursour.catalogo.repository.RecetaRepository;

/**
 * Datos sintéticos, iguales a los de la app Android. Al arrancar se crea lo que falte
 * (buscando por nombre) y se completan categoría y producto en registros antiguos.
 * No modifica nombres, precios ni stock existentes.
 */
@Configuration
public class DemoData {

    @Bean
    CommandLineRunner seedCatalogo(IngredienteRepository ingredients, ProductoRepository products, RecetaRepository recipes) {
        return args -> {
            ingrediente(ingredients, "Limón", CategoriaIngrediente.FRUTA);
            ingrediente(ingredients, "Pisco", CategoriaIngrediente.DESTILADO);
            ingrediente(ingredients, "Azúcar", CategoriaIngrediente.OTRO);
            ingrediente(ingredients, "Hielo", CategoriaIngrediente.OTRO);
            ingrediente(ingredients, "Jengibre", CategoriaIngrediente.FRUTA);
            ingrediente(ingredients, "Agua", CategoriaIngrediente.MIXER);
            ingrediente(ingredients, "Mix sour Our Sour", CategoriaIngrediente.OUR_SOUR);
            // Ingredientes creados por la API antes de existir la categoría
            ingredients.findAll().stream().filter(i -> i.getCategoria() == null).forEach(i -> {
                i.setCategoria(CategoriaIngrediente.OTRO);
                ingredients.save(i);
            });

            producto(products, "Limón fresco", "Malla ficticia de 1 kg.", "1800", 20);
            producto(products, "Pisco de demostración", "Botella ficticia de 700 ml. Solo para mayores de edad.", "8900", 8);
            producto(products, "Azúcar", "Bolsa de 1 kg.", "1400", 15);
            producto(products, "Jengibre", "Porción de 100 g.", "1200", 12);
            producto(products, "Vaso de cóctel", "Vaso de 350 ml, producto de demostración.", "3500", 0);
            producto(products, "Mix sour Our Sour", "Mezcla ficticia de limón y almíbar, 700 ml.", "5990", 10);

            receta(recipes, ingredients, products, "Sour clásico", "Pisco de demostración",
                    "Disolver el azúcar en el jugo de limón. Agregar pisco y hielo, agitar y servir. Una porción. Solo para mayores de edad.",
                    "Limón", "30", "ml", "Pisco", "60", "ml", "Azúcar", "15", "g", "Hielo", "4", "cubos");
            receta(recipes, ingredients, products, "Limonada fresca", "Limón fresco",
                    "Mezclar agua, limón y azúcar. Agregar hielo y servir. Una porción sin alcohol.",
                    "Limón", "30", "ml", "Azúcar", "10", "g", "Hielo", "4", "cubos", "Agua", "200", "ml");
            receta(recipes, ingredients, products, "Limonada de jengibre", "Jengibre",
                    "Mezclar el jengibre con agua y limón. Reposar cinco minutos y colar. Una porción sin alcohol.",
                    "Limón", "30", "ml", "Jengibre", "5", "g", "Agua", "200", "ml");
            receta(recipes, ingredients, products, "Sour express", "Mix sour Our Sour",
                    "Agitar el mix sour con pisco y hielo durante diez segundos y servir. Una porción. Solo para mayores de edad.",
                    "Mix sour Our Sour", "90", "ml", "Pisco", "60", "ml", "Hielo", "4", "cubos");
        };
    }

    private static void ingrediente(IngredienteRepository repo, String nombre, CategoriaIngrediente categoria) {
        Ingrediente item = repo.findByNombreIgnoreCase(nombre).orElseGet(Ingrediente::new);
        if (item.getId() == null || item.getCategoria() == null) {
            item.setNombre(item.getId() == null ? nombre : item.getNombre());
            item.setCategoria(categoria);
            repo.save(item);
        }
    }

    private static void producto(ProductoRepository repo, String nombre, String descripcion, String precio, int stock) {
        if (repo.findByNombreIgnoreCase(nombre).isEmpty()) {
            Producto item = new Producto();
            item.setNombre(nombre);
            item.setDescripcion(descripcion);
            item.setPrecio(new BigDecimal(precio));
            item.setStock(stock);
            repo.save(item);
        }
    }

    /** dosis en grupos de tres: nombre de ingrediente, cantidad y unidad. Si falta un ingrediente no se crea la receta. */
    private static void receta(RecetaRepository recipes, IngredienteRepository ingredients, ProductoRepository products,
                               String nombre, String producto, String preparacion, String... dosis) {
        Producto asociado = products.findByNombreIgnoreCase(producto).orElse(null);
        Receta existente = recipes.findByNombreIgnoreCase(nombre).orElse(null);
        if (existente != null) {
            if (existente.getProducto() == null && asociado != null) {
                existente.setProducto(asociado);
                recipes.save(existente);
            }
            return;
        }
        Receta receta = new Receta();
        receta.setNombre(nombre);
        receta.setPreparacion(preparacion);
        receta.setProducto(asociado);
        for (int i = 0; i < dosis.length; i += 3) {
            Ingrediente ingrediente = ingredients.findByNombreIgnoreCase(dosis[i]).orElse(null);
            if (ingrediente == null) {
                return;
            }
            receta.agregarIngrediente(new RecetaIngrediente(ingrediente, new BigDecimal(dosis[i + 1]), dosis[i + 2]));
        }
        recipes.save(receta);
    }
}
