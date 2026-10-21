package cl.oursour.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String value) { email = value == null ? null : value.trim().toLowerCase(); }
    public String getPassword() { return password; }
    public void setPassword(String value) { password = value; }
}
