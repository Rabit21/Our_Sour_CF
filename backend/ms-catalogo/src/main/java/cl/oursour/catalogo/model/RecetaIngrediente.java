package cl.oursour.catalogo.model;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "catalogo_receta_ingredientes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"receta_id", "ingrediente_id"}))
public class RecetaIngrediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id")
    private Receta receta;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ingrediente_id")
    private Ingrediente ingrediente;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal cantidad;

    @Column(nullable = false, length = 20)
    private String unidad;

    public RecetaIngrediente() { }

    public RecetaIngrediente(Ingrediente ingrediente, BigDecimal cantidad, String unidad) {
        this.ingrediente = ingrediente;
        this.cantidad = cantidad;
        this.unidad = unidad;
    }

    public Long getId() { return id; }
    public Receta getReceta() { return receta; }
    public void setReceta(Receta value) { receta = value; }
    public Ingrediente getIngrediente() { return ingrediente; }
    public BigDecimal getCantidad() { return cantidad; }
    public String getUnidad() { return unidad; }
}
