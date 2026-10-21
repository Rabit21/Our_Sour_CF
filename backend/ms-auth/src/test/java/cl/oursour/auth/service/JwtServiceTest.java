package cl.oursour.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import cl.oursour.auth.model.Rol;
import cl.oursour.auth.model.Usuario;

class JwtServiceTest {
    private static final String SECRETO = "secreto-de-prueba-con-mas-de-32-caracteres";

    private static NimbusJwtDecoder decoder(String secret) {
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")).build();
    }

    @Test
    void tokenContainsIdAndRoleAndIsValidatedWithSameSecret() {
        Usuario usuario = new Usuario();
        usuario.setId(3L);
        usuario.setNombre("Admin");
        usuario.setEmail("admin@demo.cl");
        usuario.setRol(Rol.ADMIN);

        String token = new JwtService(SECRETO, 600).generar(usuario);
        Jwt jwt = decoder(SECRETO).decode(token);

        assertEquals("3", jwt.getSubject());
        assertEquals("ADMIN", jwt.getClaimAsString("rol"));
        assertThrows(JwtException.class, () -> decoder("otro-secreto-distinto-con-mas-de-32-caracteres").decode(token));
    }

    @Test
    void shortSecretIsRejected() {
        assertThrows(IllegalStateException.class, () -> new JwtService("corto", 600));
    }
}
