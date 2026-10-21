package cl.oursour.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import cl.oursour.auth.model.Rol;
import cl.oursour.auth.model.Usuario;
import cl.oursour.auth.repository.UsuarioRepository;

/**
 * Crea el administrador ficticio solo si se definen ADMIN_EMAIL y ADMIN_PASSWORD
 * en el entorno local. Así no queda ninguna contraseña escrita en el código.
 */
@Configuration
public class AdminSeed {

    @Bean
    CommandLineRunner seedAdmin(UsuarioRepository usuarios, PasswordEncoder encoder,
                                @Value("${ADMIN_EMAIL:}") String email, @Value("${ADMIN_PASSWORD:}") String password) {
        return args -> {
            if (email.isBlank() || password.length() < 8 || usuarios.existsByEmailIgnoreCase(email)) {
                return;
            }
            Usuario admin = new Usuario();
            admin.setNombre("Administrador demo");
            admin.setEmail(email.trim().toLowerCase());
            admin.setPasswordHash(encoder.encode(password));
            admin.setRol(Rol.ADMIN);
            usuarios.save(admin);
        };
    }
}
