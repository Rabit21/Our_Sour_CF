package cl.oursour.auth.controller;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validation(MethodArgumentNotValidException error) {
        String campo = error.getBindingResult().getFieldErrors().isEmpty()
                ? "solicitud"
                : error.getBindingResult().getFieldErrors().get(0).getField();
        return Map.of("error", "Datos inválidos en " + campo + ".");
    }

    /** Dos registros simultáneos con el mismo correo: la restricción UNIQUE responde. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflict() {
        return Map.of("error", "El correo ya está registrado.");
    }
}
