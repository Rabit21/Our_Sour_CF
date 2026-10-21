package cl.oursour.pedidos.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.oursour.pedidos.model.Carro;

public interface CarroRepository extends JpaRepository<Carro, Long> {
    Optional<Carro> findByUsuarioId(Long usuarioId);
}
