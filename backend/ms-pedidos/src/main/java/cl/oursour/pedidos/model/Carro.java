package cl.oursour.pedidos.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

/** Un carro por usuario. Solo guarda producto y cantidad; precio y stock se consultan al catálogo. */
@Entity
@Table(name = "pedidos_carros")
public class Carro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Id del usuario de ms-auth (viene del token). Sin clave foránea: es otro servicio. */
    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;

    @OneToMany(mappedBy = "carro", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<LineaCarro> lineas = new ArrayList<>();

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long value) { usuarioId = value; }
    public List<LineaCarro> getLineas() { return lineas; }

    public LineaCarro buscarLinea(Long productoId) {
        return lineas.stream().filter(l -> l.getProductoId().equals(productoId)).findFirst().orElse(null);
    }

    public void agregarLinea(LineaCarro linea) {
        linea.setCarro(this);
        lineas.add(linea);
    }
}
