package cl.oursour.pedidos.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.oursour.pedidos.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioIdOrderByIdDesc(Long usuarioId);

    List<Pedido> findAllByOrderByIdDesc();
}
