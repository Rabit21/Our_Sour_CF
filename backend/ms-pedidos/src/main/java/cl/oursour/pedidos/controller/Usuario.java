package cl.oursour.pedidos.controller;

import org.springframework.security.oauth2.jwt.Jwt;

/** Lee del token validado quién hace la solicitud; nunca se toma de la URL o del cuerpo. */
final class Usuario {
    private Usuario() { }

    static Long id(Jwt token) {
        return Long.valueOf(token.getSubject());
    }

    static boolean esAdmin(Jwt token) {
        return "ADMIN".equals(token.getClaimAsString("rol"));
    }
}
