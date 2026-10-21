package cl.oursour.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import cl.oursour.auth.dto.LoginRequest;
import cl.oursour.auth.dto.RegistroRequest;
import cl.oursour.auth.dto.TokenDto;
import cl.oursour.auth.dto.UsuarioDto;
import cl.oursour.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto registrar(@Valid @RequestBody RegistroRequest input) {
        return service.registrar(input);
    }

    @PostMapping("/login")
    public TokenDto login(@Valid @RequestBody LoginRequest input) {
        return service.login(input);
    }

    /** Requiere token: el id se toma del token, no de la URL. */
    @GetMapping("/perfil")
    public UsuarioDto perfil(@AuthenticationPrincipal Jwt token) {
        return service.perfil(Long.valueOf(token.getSubject()));
    }
}
