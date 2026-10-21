package cl.oursour.auth.dto;

import jakarta.validation.constraints.*;

public class RegistroRequest {
    @NotBlank
    @Size(min = 2, max = 60)
    private String nombre;

    @NotBlank
    @Email
    @Size(max = 120)
    private String email;

    /** BCrypt solo usa los primeros 72 bytes, por eso el máximo. */
    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    public String getNombre() { return nombre; }
    public void setNombre(String value) { nombre = value == null ? null : value.trim(); }
    public String getEmail() { return email; }
    public void setEmail(String value) { email = value == null ? null : value.trim().toLowerCase(); }
    public String getPassword() { return password; }
    public void setPassword(String value) { password = value; }
}
