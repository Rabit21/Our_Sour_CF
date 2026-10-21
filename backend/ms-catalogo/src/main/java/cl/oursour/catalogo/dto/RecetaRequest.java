package cl.oursour.catalogo.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class RecetaRequest {
    @NotBlank
    @Size(min = 2, max = 80)
    private String nombre;

    @NotBlank
    @Size(min = 10, max = 2000)
    private String preparacion;

    @NotEmpty
    @Size(max = 20)
    private List<@Valid @NotNull DosisRequest> ingredientes;

    public String getNombre() { return nombre; }
    public void setNombre(String value) { nombre = value == null ? null : value.trim(); }
    public String getPreparacion() { return preparacion; }
    public void setPreparacion(String value) { preparacion = value == null ? null : value.trim(); }
    public List<DosisRequest> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<DosisRequest> value) { ingredientes = value; }
}
