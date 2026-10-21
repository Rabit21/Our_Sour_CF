package cl.oursour.catalogo.dto;

import java.util.List;

/** Resultado del motor: cuántos ingredientes tiene el usuario y cuáles le faltan. */
public class RecomendacionDto {
    private final Long recetaId;
    private final String nombre;
    private final int disponibles;
    private final int total;
    private final double proporcion;
    private final List<DosisDto> faltantes;
    private final Long productoId;

    public RecomendacionDto(Long recetaId, String nombre, int disponibles, int total, List<DosisDto> faltantes, Long productoId) {
        this.recetaId = recetaId;
        this.nombre = nombre;
        this.disponibles = disponibles;
        this.total = total;
        this.proporcion = (double) disponibles / total;
        this.faltantes = faltantes;
        this.productoId = productoId;
    }

    public Long getRecetaId() { return recetaId; }
    public String getNombre() { return nombre; }
    public int getDisponibles() { return disponibles; }
    public int getTotal() { return total; }
    public double getProporcion() { return proporcion; }
    public List<DosisDto> getFaltantes() { return faltantes; }
    public Long getProductoId() { return productoId; }
}
