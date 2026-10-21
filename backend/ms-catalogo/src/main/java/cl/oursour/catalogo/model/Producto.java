package cl.oursour.catalogo.model;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "catalogo_productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { nombre = value; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { descripcion = value; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal value) { precio = value; }
    public Integer getStock() { return stock; }
    public void setStock(Integer value) { stock = value; }
}
