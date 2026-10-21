package cl.oursour.pedidos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.pedidos.client.CatalogoClient;
import cl.oursour.pedidos.dto.CheckoutRequest;
import cl.oursour.pedidos.dto.PedidoDto;
import cl.oursour.pedidos.dto.ProductoCatalogo;
import cl.oursour.pedidos.model.Carro;
import cl.oursour.pedidos.model.EstadoPedido;
import cl.oursour.pedidos.model.LineaCarro;
import cl.oursour.pedidos.model.Pedido;
import cl.oursour.pedidos.repository.CarroRepository;
import cl.oursour.pedidos.repository.PedidoRepository;

class PedidoServiceTest {
    private final CarroRepository carros = mock(CarroRepository.class);
    private final PedidoRepository pedidos = mock(PedidoRepository.class);
    private final CatalogoClient catalogo = mock(CatalogoClient.class);
    private final CarroService carroService = new CarroService(carros, catalogo);
    private final PedidoService service = new PedidoService(pedidos, carroService, catalogo);

    private Carro carroCon(long usuarioId, long productoId, int cantidad) {
        Carro carro = new Carro();
        carro.setUsuarioId(usuarioId);
        if (cantidad > 0) {
            LineaCarro linea = new LineaCarro();
            linea.setProductoId(productoId);
            linea.setCantidad(cantidad);
            carro.agregarLinea(linea);
        }
        when(carros.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carro));
        return carro;
    }

    private static CheckoutRequest checkout(boolean aprobado) {
        CheckoutRequest r = new CheckoutRequest();
        r.setDestinatario("Persona Demo");
        r.setCalle("Calle Demo 123");
        r.setComuna("Santiago");
        r.setPagoAprobado(aprobado);
        return r;
    }

    @Test
    void shippingRuleMatchesAndroid() {
        assertEquals(0, Despacho.costo(0));
        assertEquals(3500, Despacho.costo(29999));
        assertEquals(0, Despacho.costo(30000));
    }

    @Test
    void emptyCartAndUnknownCommuneAreRejected() {
        carroCon(1, 0, 0);
        assertEquals(HttpStatus.BAD_REQUEST,
                assertThrows(ResponseStatusException.class, () -> service.checkout(1L, checkout(true))).getStatusCode());
        CheckoutRequest otraComuna = checkout(true);
        otraComuna.setComuna("Arica");
        assertEquals(HttpStatus.BAD_REQUEST,
                assertThrows(ResponseStatusException.class, () -> service.checkout(1L, otraComuna)).getStatusCode());
    }

    @Test
    void rejectedPaymentKeepsCartAndDoesNotTouchStock() {
        Carro carro = carroCon(1, 10, 2);
        when(catalogo.producto(10L)).thenReturn(new ProductoCatalogo(10L, "Limón fresco", 1800L, 20));

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.checkout(1L, checkout(false)));

        assertEquals(HttpStatus.PAYMENT_REQUIRED, error.getStatusCode());
        assertEquals(1, carro.getLineas().size());
        verify(catalogo, never()).descontarStock(anyMap());
        verify(pedidos, never()).save(any());
    }

    @Test
    void approvedPaymentUsesServerPricesDiscountsStockAndEmptiesCart() {
        Carro carro = carroCon(1, 10, 2);
        when(catalogo.producto(10L)).thenReturn(new ProductoCatalogo(10L, "Limón fresco", 1800L, 20));
        when(pedidos.save(any(Pedido.class))).thenAnswer(call -> call.getArgument(0));

        PedidoDto result = service.checkout(1L, checkout(true));

        assertEquals(3600L, result.subtotal());
        assertEquals(3500L, result.despacho());
        assertEquals(7100L, result.total());
        assertEquals(EstadoPedido.SOLICITADO, result.estado());
        assertEquals("Limón fresco", result.lineas().get(0).nombre());
        verify(catalogo).descontarStock(Map.of(10L, 2));
        assertTrue(carro.getLineas().isEmpty());
    }

    @Test
    void insufficientStockIsConflictBeforePayment() {
        carroCon(1, 10, 5);
        when(catalogo.producto(10L)).thenReturn(new ProductoCatalogo(10L, "Limón fresco", 1800L, 3));

        assertEquals(HttpStatus.CONFLICT,
                assertThrows(ResponseStatusException.class, () -> service.checkout(1L, checkout(true))).getStatusCode());
        verify(catalogo, never()).descontarStock(anyMap());
    }

    @Test
    void clientCannotReadAnotherUsersOrderButAdminCan() {
        Pedido ajeno = new Pedido();
        ajeno.setId(5L);
        ajeno.setUsuarioId(2L);
        when(pedidos.findById(5L)).thenReturn(Optional.of(ajeno));

        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class, () -> service.obtener(5L, 1L, false)).getStatusCode());
        assertEquals(5L, service.obtener(5L, 1L, true).id());
    }

    @Test
    void cartQuantityAboveStockIsRejected() {
        when(catalogo.producto(10L)).thenReturn(new ProductoCatalogo(10L, "Limón fresco", 1800L, 3));
        assertEquals(HttpStatus.CONFLICT,
                assertThrows(ResponseStatusException.class, () -> carroService.fijarCantidad(1L, 10L, 4)).getStatusCode());
        verify(carros, never()).save(any());
    }
}
