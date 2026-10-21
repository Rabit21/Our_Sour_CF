package cl.oursour.catalogo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.oursour.catalogo.model.Ingrediente;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    Optional<Ingrediente> findByNombreIgnoreCase(String nombre);
}
