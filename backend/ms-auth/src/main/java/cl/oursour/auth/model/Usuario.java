package cl.oursour.auth.model;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /** Hash BCrypt; la contraseña en texto plano nunca se guarda. */
    @Column(name = "password_hash", nullable = false, length = 72)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Rol rol;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { nombre = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { email = value; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String value) { passwordHash = value; }
    public Rol getRol() { return rol; }
    public void setRol(Rol value) { rol = value; }
}
