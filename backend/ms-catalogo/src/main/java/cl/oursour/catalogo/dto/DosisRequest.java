package cl.oursour.catalogo.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

/** Entrada: ingrediente existente y cantidad para una porción. */
public class DosisRequest {
    @NotNull
    private Long ingredienteId;

    @NotNull
    @DecimalMin("0.01")
    @DecimalMax("9999")
    @Digits(integer = 4, fraction = 2)
    private BigDecimal cantidad;

    @NotBlank
    @Size(max = 20)
    private String unidad;

    public Long getIngredienteId() { return ingredienteId; }
    public void setIngredienteId(Long value) { ingredienteId = value; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal value) { cantidad = value; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String value) { unidad = value == null ? null : value.trim(); }
}
