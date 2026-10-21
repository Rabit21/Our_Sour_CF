package cl.oursour.pedidos.dto;

import java.util.List;

public record CarroDto(List<LineaCarroDto> lineas, Long subtotal, Long despacho, Long total) { }
