package cl.oursour.pedidos.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

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

    /** Devuelve el mensaje del servicio (por ejemplo, pago rechazado o stock insuficiente) en JSON. */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> status(ResponseStatusException error) {
        String mensaje = error.getReason() == null ? "Error" : error.getReason();
        return ResponseEntity.status(error.getStatusCode()).body(Map.of("error", mensaje));
    }
}
