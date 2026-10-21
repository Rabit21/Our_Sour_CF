package cl.oursour.catalogo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Una línea de pedido confirmada: producto y unidades a descontar. Solo la usa ms-pedidos. */
public class DescuentoStockRequest {
    @NotNull
    private Long productoId;

    @NotNull
    @Min(1)
    @Max(99)
    private Integer cantidad;

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long value) { productoId = value; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer value) { cantidad = value; }
}
