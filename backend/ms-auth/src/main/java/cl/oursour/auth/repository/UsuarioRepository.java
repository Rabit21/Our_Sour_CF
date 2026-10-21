package cl.oursour.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.oursour.auth.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
