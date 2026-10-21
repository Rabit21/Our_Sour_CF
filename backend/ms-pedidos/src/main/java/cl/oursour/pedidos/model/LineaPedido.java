package cl.oursour.pedidos.model;

import jakarta.persistence.*;

/** Copia histórica: si el producto cambia de nombre o precio, el pedido conserva lo que se compró. */
@Entity
@Table(name = "pedidos_lineas_pedido")
public class LineaPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "nombre_producto", nullable = false, length = 80)
    private String nombreProducto;

    @Column(name = "precio_unitario", nullable = false)
    private Long precioUnitario;

    @Column(nullable = false)
    private Integer cantidad;

    public LineaPedido() { }

    public LineaPedido(Long productoId, String nombreProducto, Long precioUnitario, Integer cantidad) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido value) { pedido = value; }
    public Long getProductoId() { return productoId; }
    public String getNombreProducto() { return nombreProducto; }
    public Long getPrecioUnitario() { return precioUnitario; }
    public Integer getCantidad() { return cantidad; }
    public Long getSubtotal() { return precioUnitario * cantidad; }
}
