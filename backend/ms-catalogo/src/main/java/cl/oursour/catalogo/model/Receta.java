package cl.oursour.catalogo.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "catalogo_recetas")
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(nullable = false, length = 2000)
    private String preparacion;

    /** Producto Our Sour asociado (misma base del servicio de catálogo). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    /** Dosificación: cada línea une la receta con un ingrediente y su cantidad. */
    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<RecetaIngrediente> ingredientes = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { nombre = value; }
    public String getPreparacion() { return preparacion; }
    public void setPreparacion(String value) { preparacion = value; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto value) { producto = value; }
    public List<RecetaIngrediente> getIngredientes() { return ingredientes; }

    public void agregarIngrediente(RecetaIngrediente linea) {
        linea.setReceta(this);
        ingredientes.add(linea);
    }
}
