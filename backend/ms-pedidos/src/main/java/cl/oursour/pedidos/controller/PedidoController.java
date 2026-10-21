package cl.oursour.pedidos.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import cl.oursour.pedidos.dto.CheckoutRequest;
import cl.oursour.pedidos.dto.EstadoRequest;
import cl.oursour.pedidos.dto.PedidoDto;
import cl.oursour.pedidos.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    /** Pago simulado: 201 con el pedido si se aprueba; 402 si se rechaza (el carro se conserva). */
    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoDto checkout(@AuthenticationPrincipal Jwt token, @Valid @RequestBody CheckoutRequest input) {
        return service.checkout(Usuario.id(token), input);
    }

    @GetMapping
    public List<PedidoDto> listar(@AuthenticationPrincipal Jwt token) {
        return service.listar(Usuario.id(token), Usuario.esAdmin(token));
    }

    @GetMapping("/{id}")
    public PedidoDto obtener(@AuthenticationPrincipal Jwt token, @PathVariable Long id) {
        return service.obtener(id, Usuario.id(token), Usuario.esAdmin(token));
    }

    /** Solo ADMIN (regla en SecurityConfig). */
    @PatchMapping("/{id}/estado")
    public PedidoDto cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest input) {
        return service.cambiarEstado(id, input.getEstado());
    }
}
