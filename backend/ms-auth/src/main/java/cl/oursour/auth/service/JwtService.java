package cl.oursour.auth.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import cl.oursour.auth.model.Usuario;

/** Genera tokens JWT firmados con HS256. Gateway y servicios validan con el mismo secreto. */
@Service
public class JwtService {
    private final SecretKey key;
    private final long duracionSegundos;

    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.duracion-segundos:3600}") long duracionSegundos) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres");
        }
        this.key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.duracionSegundos = duracionSegundos;
    }

    public long getDuracionSegundos() { return duracionSegundos; }

    /** subject = id del usuario; además se incluyen email, nombre y rol. */
    public String generar(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .claim("email", usuario.getEmail())
                .claim("nombre", usuario.getNombre())
                .claim("rol", usuario.getRol().name())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusSeconds(duracionSegundos)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}
