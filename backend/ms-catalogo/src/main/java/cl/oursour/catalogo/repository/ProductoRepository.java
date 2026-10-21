package cl.oursour.catalogo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.oursour.catalogo.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    Optional<Producto> findByNombreIgnoreCase(String nombre);
}
