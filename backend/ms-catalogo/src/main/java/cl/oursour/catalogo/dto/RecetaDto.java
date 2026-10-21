package cl.oursour.catalogo.dto;

import java.util.List;
import cl.oursour.catalogo.model.Receta;

public class RecetaDto {
    private Long id;
    private String nombre;
    private String preparacion;
    private List<DosisDto> ingredientes;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getPreparacion() { return preparacion; }
    public List<DosisDto> getIngredientes() { return ingredientes; }

    public static RecetaDto from(Receta entity) {
        RecetaDto result = new RecetaDto();
        result.id = entity.getId();
        result.nombre = entity.getNombre();
        result.preparacion = entity.getPreparacion();
        result.ingredientes = entity.getIngredientes().stream().map(DosisDto::from).toList();
        return result;
    }
}
