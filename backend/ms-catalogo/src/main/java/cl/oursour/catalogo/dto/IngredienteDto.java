package cl.oursour.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import cl.oursour.catalogo.model.Ingrediente;

public class IngredienteDto {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 80)
    private String nombre;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { nombre = value == null ? null : value.trim(); }

    public static IngredienteDto from(Ingrediente entity) {
        IngredienteDto result = new IngredienteDto();
        result.id = entity.getId();
        result.nombre = entity.getNombre();
        return result;
    }
}
