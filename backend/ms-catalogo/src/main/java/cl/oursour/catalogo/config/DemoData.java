package cl.oursour.catalogo.config;

import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cl.oursour.catalogo.model.Ingrediente;
import cl.oursour.catalogo.model.Producto;
import cl.oursour.catalogo.repository.IngredienteRepository;
import cl.oursour.catalogo.repository.ProductoRepository;

@Configuration
public class DemoData {

    @Bean
    CommandLineRunner seedCatalogo(IngredienteRepository ingredients, ProductoRepository products) {
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
        };
    }
}
