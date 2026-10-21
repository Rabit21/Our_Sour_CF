package cl.oursour.pedidos.dto;

/** Lo que ms-pedidos lee de GET /api/catalogo/productos/{id}. */
public record ProductoCatalogo(Long id, String nombre, Long precio, Integer stock) { }
