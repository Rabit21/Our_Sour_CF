package cl.oursour.catalogo.repository;

import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import cl.oursour.catalogo.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    Optional<Producto> findByNombreIgnoreCase(String nombre);

    /** Bloquea la fila hasta terminar la transacción: dos pedidos simultáneos no venden el mismo stock. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> bloquearPorId(@Param("id") Long id);
}
