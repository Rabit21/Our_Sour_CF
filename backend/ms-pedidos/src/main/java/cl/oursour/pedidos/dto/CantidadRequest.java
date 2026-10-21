package cl.oursour.pedidos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CantidadRequest {
    @NotNull
    @Min(1)
    @Max(99)
    private Integer cantidad;

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer value) { cantidad = value; }
}
