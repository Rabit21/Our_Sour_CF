package cl.oursour.catalogo.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import cl.oursour.catalogo.model.Producto;

public class ProductoDto {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 80)
    private String nombre;

    @NotBlank
    @Size(min = 5, max = 500)
    private String descripcion;

    @NotNull
    @DecimalMin("1")
    @DecimalMax("10000000")
    @Digits(integer = 8, fraction = 0)
    private BigDecimal precio;

    @NotNull
    @Min(0)
    @Max(9999)
    private Integer stock;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { nombre = value == null ? null : value.trim(); }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { descripcion = value == null ? null : value.trim(); }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal value) { precio = value; }
    public Integer getStock() { return stock; }
    public void setStock(Integer value) { stock = value; }

    public static ProductoDto from(Producto entity) {
        ProductoDto result = new ProductoDto();
        result.id = entity.getId();
        result.nombre = entity.getNombre();
        result.descripcion = entity.getDescripcion();
        result.precio = entity.getPrecio();
        result.stock = entity.getStock();
        return result;
    }
}
