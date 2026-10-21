package cl.oursour.pedidos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "pedidos_pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 60)
    private String destinatario;

    @Column(nullable = false, length = 120)
    private String calle;

    @Column(nullable = false, length = 40)
    private String comuna;

    /** Montos en pesos enteros, calculados por el servidor. */
    @Column(nullable = false)
    private Long subtotal;

    @Column(nullable = false)
    private Long despacho;

    @Column(nullable = false)
    private Long total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<LineaPedido> lineas = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long value) { usuarioId = value; }
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String value) { destinatario = value; }
    public String getCalle() { return calle; }
    public void setCalle(String value) { calle = value; }
    public String getComuna() { return comuna; }
    public void setComuna(String value) { comuna = value; }
    public Long getSubtotal() { return subtotal; }
    public void setSubtotal(Long value) { subtotal = value; }
    public Long getDespacho() { return despacho; }
    public void setDespacho(Long value) { despacho = value; }
    public Long getTotal() { return total; }
    public void setTotal(Long value) { total = value; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido value) { estado = value; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime value) { creadoEn = value; }
    public List<LineaPedido> getLineas() { return lineas; }

    public void agregarLinea(LineaPedido linea) {
        linea.setPedido(this);
        lineas.add(linea);
    }
}
