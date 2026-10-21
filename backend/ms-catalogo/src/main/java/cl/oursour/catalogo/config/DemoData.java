package cl.oursour.catalogo.config;

import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cl.oursour.catalogo.model.Ingrediente;
import cl.oursour.catalogo.model.Producto;
import cl.oursour.catalogo.model.Receta;
import cl.oursour.catalogo.model.RecetaIngrediente;
import cl.oursour.catalogo.repository.IngredienteRepository;
import cl.oursour.catalogo.repository.ProductoRepository;
import cl.oursour.catalogo.repository.RecetaRepository;

/** Datos sintéticos que se cargan solo cuando las tablas están vacías. */
@Configuration
public class DemoData {

    @Bean
    CommandLineRunner seedCatalogo(IngredienteRepository ingredients, ProductoRepository products, RecetaRepository recipes) {
        return args -> {
            if (ingredients.count() == 0) {
                for (String name : new String[] {"Limón", "Pisco", "Azúcar", "Hielo", "Jengibre", "Agua"}) {
                    Ingrediente item = new Ingrediente();
                    item.setNombre(name);
                    ingredients.save(item);
                }
            }
            if (products.count() == 0) {
                Producto product = new Producto();
                product.setNombre("Limón fresco");
                product.setDescripcion("Malla ficticia de 1 kg.");
                product.setPrecio(new BigDecimal("1800"));
                product.setStock(20);
                products.save(product);
            }
            if (recipes.count() == 0) {
                guardar(recipes, receta(ingredients, "Sour clásico",
                        "Disolver el azúcar en el jugo de limón. Agregar pisco y hielo, agitar y servir. Una porción. Solo para mayores de edad.",
                        "Limón", "30", "ml", "Pisco", "60", "ml", "Azúcar", "15", "g", "Hielo", "4", "cubos"));
                guardar(recipes, receta(ingredients, "Limonada fresca",
                        "Mezclar agua, limón y azúcar. Agregar hielo y servir. Una porción sin alcohol.",
                        "Limón", "30", "ml", "Azúcar", "10", "g", "Hielo", "4", "cubos", "Agua", "200", "ml"));
                guardar(recipes, receta(ingredients, "Limonada de jengibre",
                        "Mezclar el jengibre con agua y limón. Reposar cinco minutos y colar. Una porción sin alcohol.",
                        "Limón", "30", "ml", "Jengibre", "5", "g", "Agua", "200", "ml"));
            }
        };
    }

    private static void guardar(RecetaRepository recipes, Receta receta) {
        if (receta != null) {
            recipes.save(receta);
        }
    }

    /** dosis se recibe en grupos de tres: nombre de ingrediente, cantidad y unidad. Devuelve null si falta un ingrediente. */
    private static Receta receta(IngredienteRepository ingredients, String nombre, String preparacion, String... dosis) {
        Receta receta = new Receta();
        receta.setNombre(nombre);
        receta.setPreparacion(preparacion);
        for (int i = 0; i < dosis.length; i += 3) {
            Ingrediente ingrediente = ingredients.findByNombreIgnoreCase(dosis[i]).orElse(null);
            if (ingrediente == null) {
                return null; // si alguien renombró o borró el ingrediente, no se inventa la receta
            }
            receta.agregarIngrediente(new RecetaIngrediente(ingrediente, new BigDecimal(dosis[i + 1]), dosis[i + 2]));
        }
        return receta;
    }
}
