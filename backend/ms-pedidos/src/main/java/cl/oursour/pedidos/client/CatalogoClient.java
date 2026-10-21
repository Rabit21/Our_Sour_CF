package cl.oursour.pedidos.client;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.pedidos.dto.ProductoCatalogo;

/**
 * Comunicación REST con ms-catalogo. Pedidos no tiene tabla de productos:
 * precio y stock siempre se consultan aquí.
 */
@Component
public class CatalogoClient {
    private final RestClient http;
    private final TokenServicio token;

    public CatalogoClient(RestClient.Builder builder, TokenServicio token) {
        this.http = builder.baseUrl("http://ms-catalogo").build();
        this.token = token;
    }

    /** 404 si el producto no existe; 503 si el catálogo no responde. */
    public ProductoCatalogo producto(Long id) {
        try {
            return http.get().uri("/api/catalogo/productos/{id}", id).retrieve().body(ProductoCatalogo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto inexistente: " + id);
        } catch (RuntimeException e) {
            throw noDisponible();
        }
    }

    /** Descuenta stock de todas las líneas en una sola transacción del catálogo; 409 si no alcanza. */
    public void descontarStock(Map<Long, Integer> cantidades) {
        List<Map<String, Object>> lineas = cantidades.entrySet().stream()
                .map(e -> Map.<String, Object>of("productoId", e.getKey(), "cantidad", e.getValue())).toList();
        try {
            http.post().uri("/api/catalogo/productos/stock/descontar")
                    .header("Authorization", "Bearer " + token.generar())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("lineas", lineas))
                    .retrieve().toBodilessEntity();
        } catch (HttpClientErrorException.Conflict | HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente o producto no disponible");
        } catch (RuntimeException e) {
            throw noDisponible();
        }
    }

    private static ResponseStatusException noDisponible() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Catálogo no disponible, intenta más tarde");
    }
}
