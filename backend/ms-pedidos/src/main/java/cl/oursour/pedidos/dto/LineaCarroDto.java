package cl.oursour.pedidos.dto;

/** Línea del carro con datos actuales del catálogo. disponible=false si el producto ya no existe o no alcanza el stock. */
public record LineaCarroDto(Long productoId, String nombre, Long precioUnitario, Integer cantidad, Long subtotal, boolean disponible) { }
