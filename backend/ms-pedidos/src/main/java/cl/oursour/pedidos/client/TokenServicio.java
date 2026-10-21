package cl.oursour.pedidos.client;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

/**
 * Token corto (60 s) con rol SERVICIO para que ms-pedidos descuente stock en ms-catalogo.
 * Ningún usuario recibe este rol: ms-auth solo emite CLIENTE o ADMIN.
 */
@Component
public class TokenServicio {
    private final JwtEncoder encoder;

    public TokenServicio(@Value("${jwt.secret}") String secret) {
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")));
    }

    public String generar() {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject("ms-pedidos")
                .claim("rol", "SERVICIO")
                .issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(60))
                .build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
}
