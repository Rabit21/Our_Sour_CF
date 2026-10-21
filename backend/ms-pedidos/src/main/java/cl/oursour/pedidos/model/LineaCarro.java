package cl.oursour.pedidos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pedidos_lineas_carro",
        uniqueConstraints = @UniqueConstraint(columnNames = {"carro_id", "producto_id"}))
public class LineaCarro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "carro_id")
    private Carro carro;

    /** Id del producto en ms-catalogo (sin clave foránea entre servicios). */
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private Integer cantidad;

    public Long getId() { return id; }
    public Carro getCarro() { return carro; }
    public void setCarro(Carro value) { carro = value; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long value) { productoId = value; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer value) { cantidad = value; }
}
