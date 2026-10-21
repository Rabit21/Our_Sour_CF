package cl.oursour.catalogo.dto;

import java.math.BigDecimal;
import cl.oursour.catalogo.model.RecetaIngrediente;

/** Respuesta: ingrediente de una receta con su cantidad y unidad. */
public class DosisDto {
    private Long ingredienteId;
    private String ingrediente;
    private BigDecimal cantidad;
    private String unidad;

    public Long getIngredienteId() { return ingredienteId; }
    public String getIngrediente() { return ingrediente; }
    public BigDecimal getCantidad() { return cantidad; }
    public String getUnidad() { return unidad; }

    public static DosisDto from(RecetaIngrediente linea) {
        DosisDto result = new DosisDto();
        result.ingredienteId = linea.getIngrediente().getId();
        result.ingrediente = linea.getIngrediente().getNombre();
        result.cantidad = linea.getCantidad();
        result.unidad = linea.getUnidad();
        return result;
    }
}
