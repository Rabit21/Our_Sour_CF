package cl.oursour.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.auth.dto.LoginRequest;
import cl.oursour.auth.dto.RegistroRequest;
import cl.oursour.auth.dto.TokenDto;
import cl.oursour.auth.dto.UsuarioDto;
import cl.oursour.auth.model.Rol;
import cl.oursour.auth.model.Usuario;
import cl.oursour.auth.repository.UsuarioRepository;

@Service
@Transactional
public class AuthService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    /** El registro público siempre crea un CLIENTE; el rol no se acepta desde la solicitud. */
    public UsuarioDto registrar(RegistroRequest input) {
        if (usuarios.existsByEmailIgnoreCase(input.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(input.getNombre());
        usuario.setEmail(input.getEmail());
        usuario.setPasswordHash(encoder.encode(input.getPassword()));
        usuario.setRol(Rol.CLIENTE);
        return UsuarioDto.from(usuarios.saveAndFlush(usuario));
    }

    /** Mismo mensaje para correo inexistente o contraseña incorrecta, para no revelar qué correos existen. */
    @Transactional(readOnly = true)
    public TokenDto login(LoginRequest input) {
        Usuario usuario = usuarios.findByEmailIgnoreCase(input.getEmail())
                .filter(u -> encoder.matches(input.getPassword(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        return new TokenDto(jwt.generar(usuario), jwt.getDuracionSegundos(), UsuarioDto.from(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioDto perfil(Long id) {
        return usuarios.findById(id).map(UsuarioDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
