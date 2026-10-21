package cl.oursour.pedidos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.pedidos.client.CatalogoClient;
import cl.oursour.pedidos.dto.CarroDto;
import cl.oursour.pedidos.dto.CheckoutRequest;
import cl.oursour.pedidos.dto.LineaCarroDto;
import cl.oursour.pedidos.dto.PedidoDto;
import cl.oursour.pedidos.model.Carro;
import cl.oursour.pedidos.model.EstadoPedido;
import cl.oursour.pedidos.model.LineaPedido;
import cl.oursour.pedidos.model.Pedido;
import cl.oursour.pedidos.repository.PedidoRepository;

@Service
@Transactional
public class PedidoService {
    private final PedidoRepository pedidos;
    private final CarroService carroService;
    private final CatalogoClient catalogo;

    public PedidoService(PedidoRepository pedidos, CarroService carroService, CatalogoClient catalogo) {
        this.pedidos = pedidos;
        this.carroService = carroService;
        this.catalogo = catalogo;
    }

    /**
     * Confirma la compra con pago simulado. Orden: validar carro y dirección, recalcular con el catálogo,
     * aplicar el resultado del pago, descontar stock, guardar el pedido con precios históricos y vaciar el carro.
     */
    public PedidoDto checkout(Long usuarioId, CheckoutRequest input) {
        if (!Despacho.COMUNAS.contains(input.getComuna())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comuna sin despacho disponible");
        }
        Carro carro = carroService.obtener(usuarioId);
        if (carro.getLineas().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carro está vacío");
        }
        CarroDto calculado = carroService.calcular(carro);
        if (calculado.lineas().stream().anyMatch(l -> !l.disponible())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hay productos sin stock o eliminados; revisa el carro");
        }
        if (!input.getPagoAprobado()) {
            // El carro se conserva para reintentar
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Pago simulado rechazado. Puedes intentarlo nuevamente.");
        }
        Map<Long, Integer> cantidades = new TreeMap<>();
        calculado.lineas().forEach(l -> cantidades.put(l.productoId(), l.cantidad()));
        catalogo.descontarStock(cantidades);

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuarioId);
        pedido.setDestinatario(input.getDestinatario());
        pedido.setCalle(input.getCalle());
        pedido.setComuna(input.getComuna());
        pedido.setSubtotal(calculado.subtotal());
        pedido.setDespacho(calculado.despacho());
        pedido.setTotal(calculado.total());
        pedido.setEstado(EstadoPedido.SOLICITADO);
        pedido.setCreadoEn(LocalDateTime.now());
        for (LineaCarroDto linea : calculado.lineas()) {
            pedido.agregarLinea(new LineaPedido(linea.productoId(), linea.nombre(), linea.precioUnitario(), linea.cantidad()));
        }
        Pedido guardado = pedidos.save(pedido);
        carro.getLineas().clear();
        return PedidoDto.from(guardado);
    }

    /** El cliente ve solo sus pedidos; el administrador ve todos. */
    @Transactional(readOnly = true)
    public List<PedidoDto> listar(Long usuarioId, boolean admin) {
        List<Pedido> lista = admin ? pedidos.findAllByOrderByIdDesc() : pedidos.findByUsuarioIdOrderByIdDesc(usuarioId);
        return lista.stream().map(PedidoDto::from).toList();
    }

    @Transactional(readOnly = true)
    public PedidoDto obtener(Long id, Long usuarioId, boolean admin) {
        return PedidoDto.from(buscar(id, usuarioId, admin));
    }

    public PedidoDto cambiarEstado(Long id, EstadoPedido estado) {
        Pedido pedido = buscar(id, null, true);
        pedido.setEstado(estado);
        return PedidoDto.from(pedidos.save(pedido));
    }

    /** Un pedido ajeno responde 404 igual que uno inexistente, para no revelar que existe. */
    private Pedido buscar(Long id, Long usuarioId, boolean admin) {
        return pedidos.findById(id)
                .filter(p -> admin || p.getUsuarioId().equals(usuarioId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }
}
