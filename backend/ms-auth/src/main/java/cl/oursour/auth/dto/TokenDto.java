package cl.oursour.auth.dto;

public class TokenDto {
    private final String token;
    private final String tipo = "Bearer";
    private final long expiraEnSegundos;
    private final UsuarioDto usuario;

    public TokenDto(String token, long expiraEnSegundos, UsuarioDto usuario) {
        this.token = token;
        this.expiraEnSegundos = expiraEnSegundos;
        this.usuario = usuario;
    }

    public String getToken() { return token; }
    public String getTipo() { return tipo; }
    public long getExpiraEnSegundos() { return expiraEnSegundos; }
    public UsuarioDto getUsuario() { return usuario; }
}
