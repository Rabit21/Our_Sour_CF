package cl.oursour.catalogo.dto;

import java.util.List;
import cl.oursour.catalogo.model.Receta;

public class RecetaDto {
    private Long id;
    private String nombre;
    private String preparacion;
    private Long productoId;
    private String producto;
    private List<DosisDto> ingredientes;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getPreparacion() { return preparacion; }
    public Long getProductoId() { return productoId; }
    public String getProducto() { return producto; }
    public List<DosisDto> getIngredientes() { return ingredientes; }

    public static RecetaDto from(Receta entity) {
        RecetaDto result = new RecetaDto();
        result.id = entity.getId();
        result.nombre = entity.getNombre();
        result.preparacion = entity.getPreparacion();
        if (entity.getProducto() != null) {
            result.productoId = entity.getProducto().getId();
            result.producto = entity.getProducto().getNombre();
        }
        result.ingredientes = entity.getIngredientes().stream().map(DosisDto::from).toList();
        return result;
    }
}
