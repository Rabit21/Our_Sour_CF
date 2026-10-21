package cl.oursour.pedidos.dto;

import cl.oursour.pedidos.model.LineaPedido;

public record LineaPedidoDto(Long productoId, String nombre, Long precioUnitario, Integer cantidad, Long subtotal) {
    public static LineaPedidoDto from(LineaPedido linea) {
        return new LineaPedidoDto(linea.getProductoId(), linea.getNombreProducto(), linea.getPrecioUnitario(),
                linea.getCantidad(), linea.getSubtotal());
    }
}
