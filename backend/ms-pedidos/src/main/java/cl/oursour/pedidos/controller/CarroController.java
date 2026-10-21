package cl.oursour.pedidos.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import cl.oursour.pedidos.dto.CantidadRequest;
import cl.oursour.pedidos.dto.CarroDto;
import cl.oursour.pedidos.service.CarroService;

@RestController
@RequestMapping("/api/pedidos/carro")
public class CarroController {
    private final CarroService service;

    public CarroController(CarroService service) {
        this.service = service;
    }

    @GetMapping
    public CarroDto ver(@AuthenticationPrincipal Jwt token) {
        return service.ver(Usuario.id(token));
    }

    @PutMapping("/lineas/{productoId}")
    public CarroDto fijar(@AuthenticationPrincipal Jwt token, @PathVariable Long productoId, @Valid @RequestBody CantidadRequest input) {
        return service.fijarCantidad(Usuario.id(token), productoId, input.getCantidad());
    }

    @DeleteMapping("/lineas/{productoId}")
    public CarroDto quitar(@AuthenticationPrincipal Jwt token, @PathVariable Long productoId) {
        return service.quitar(Usuario.id(token), productoId);
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciar(@AuthenticationPrincipal Jwt token) {
        service.vaciar(Usuario.id(token));
        return ResponseEntity.noContent().build();
    }
}
