package cl.oursour.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.auth.dto.LoginRequest;
import cl.oursour.auth.dto.RegistroRequest;
import cl.oursour.auth.dto.TokenDto;
import cl.oursour.auth.dto.UsuarioDto;
import cl.oursour.auth.model.Rol;
import cl.oursour.auth.model.Usuario;
import cl.oursour.auth.repository.UsuarioRepository;

class AuthServiceTest {
    private static final String SECRETO = "secreto-de-prueba-con-mas-de-32-caracteres";
    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final AuthService service = new AuthService(repository, encoder, new JwtService(SECRETO, 3600));

    private static RegistroRequest registro(String email) {
        RegistroRequest result = new RegistroRequest();
        result.setNombre("Persona Demo");
        result.setEmail(email);
        result.setPassword("clave-demo-123");
        return result;
    }

    private static LoginRequest login(String email, String password) {
        LoginRequest result = new LoginRequest();
        result.setEmail(email);
        result.setPassword(password);
        return result;
    }

    private Usuario guardado(String password) {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNombre("Persona Demo");
        usuario.setEmail("persona@demo.cl");
        usuario.setPasswordHash(encoder.encode(password));
        usuario.setRol(Rol.CLIENTE);
        return usuario;
    }

    @Test
    void registerStoresBcryptHashAndClientRole() {
        when(repository.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));

        UsuarioDto result = service.registrar(registro("Persona@Demo.cl"));

        assertEquals(Rol.CLIENTE, result.getRol());
        assertEquals("persona@demo.cl", result.getEmail());
        verify(repository).saveAndFlush(argThat(u -> !u.getPasswordHash().equals("clave-demo-123")
                && encoder.matches("clave-demo-123", u.getPasswordHash())));
    }

    @Test
    void duplicateEmailIsRejected() {
        when(repository.existsByEmailIgnoreCase("persona@demo.cl")).thenReturn(true);

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.registrar(registro("persona@demo.cl")));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void wrongPasswordAndUnknownEmailReturnUnauthorized() {
        when(repository.findByEmailIgnoreCase("persona@demo.cl")).thenReturn(Optional.of(guardado("clave-demo-123")));
        when(repository.findByEmailIgnoreCase("nadie@demo.cl")).thenReturn(Optional.empty());

        assertEquals(HttpStatus.UNAUTHORIZED,
                assertThrows(ResponseStatusException.class, () -> service.login(login("persona@demo.cl", "otra-clave"))).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED,
                assertThrows(ResponseStatusException.class, () -> service.login(login("nadie@demo.cl", "clave-demo-123"))).getStatusCode());
    }

    @Test
    void correctLoginReturnsBearerToken() {
        when(repository.findByEmailIgnoreCase("persona@demo.cl")).thenReturn(Optional.of(guardado("clave-demo-123")));

        TokenDto result = service.login(login("persona@demo.cl", "clave-demo-123"));

        assertEquals("Bearer", result.getTipo());
        assertEquals(3600, result.getExpiraEnSegundos());
        assertEquals(3, result.getToken().split("\\.").length);
    }
}
