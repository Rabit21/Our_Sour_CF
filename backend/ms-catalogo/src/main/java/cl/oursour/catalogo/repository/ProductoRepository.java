package cl.oursour.catalogo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.oursour.catalogo.model.Producto;
public interface ProductoRepository extends JpaRepository<Producto,Long> {
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
