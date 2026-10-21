package cl.oursour.pedidos.dto;

import java.time.LocalDateTime;
import java.util.List;
import cl.oursour.pedidos.model.EstadoPedido;
import cl.oursour.pedidos.model.Pedido;

public record PedidoDto(Long id, Long usuarioId, String destinatario, String calle, String comuna,
                        List<LineaPedidoDto> lineas, Long subtotal, Long despacho, Long total,
                        EstadoPedido estado, LocalDateTime creadoEn) {
    public static PedidoDto from(Pedido p) {
        return new PedidoDto(p.getId(), p.getUsuarioId(), p.getDestinatario(), p.getCalle(), p.getComuna(),
                p.getLineas().stream().map(LineaPedidoDto::from).toList(),
                p.getSubtotal(), p.getDespacho(), p.getTotal(), p.getEstado(), p.getCreadoEn());
    }
}
