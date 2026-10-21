package cl.oursour.catalogo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "catalogo_ingredientes")
public class Ingrediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    /** Se guarda como texto (OUR_SOUR, DESTILADO...) para que la base sea legible. */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CategoriaIngrediente categoria;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { nombre = value; }
    public CategoriaIngrediente getCategoria() { return categoria; }
    public void setCategoria(CategoriaIngrediente value) { categoria = value; }
}
