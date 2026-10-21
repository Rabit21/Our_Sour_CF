package cl.oursour.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.oursour.catalogo.model.Receta;

public interface RecetaRepository extends JpaRepository<Receta, Long> {
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
