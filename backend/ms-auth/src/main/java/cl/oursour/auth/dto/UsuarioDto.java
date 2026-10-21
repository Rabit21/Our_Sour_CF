package cl.oursour.auth.dto;

import cl.oursour.auth.model.Rol;
import cl.oursour.auth.model.Usuario;

/** Datos públicos del usuario: nunca incluye el hash de la contraseña. */
public class UsuarioDto {
    private Long id;
    private String nombre;
    private String email;
    private Rol rol;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public Rol getRol() { return rol; }

    public static UsuarioDto from(Usuario entity) {
        UsuarioDto result = new UsuarioDto();
        result.id = entity.getId();
        result.nombre = entity.getNombre();
        result.email = entity.getEmail();
        result.rol = entity.getRol();
        return result;
    }
}
