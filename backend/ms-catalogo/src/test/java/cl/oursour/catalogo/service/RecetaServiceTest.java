package cl.oursour.catalogo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.catalogo.dto.DosisRequest;
import cl.oursour.catalogo.dto.RecetaRequest;
import cl.oursour.catalogo.model.Producto;
import cl.oursour.catalogo.repository.IngredienteRepository;
import cl.oursour.catalogo.repository.ProductoRepository;
import cl.oursour.catalogo.repository.RecetaRepository;

class RecetaServiceTest {
    private final RecetaRepository recetas = mock(RecetaRepository.class);
    private final IngredienteRepository ingredientes = mock(IngredienteRepository.class);
    private final ProductoRepository productos = mock(ProductoRepository.class);
    private final RecetaService service = new RecetaService(recetas, ingredientes, productos);

    private static DosisRequest dosis(long ingredienteId) {
        DosisRequest result = new DosisRequest();
        result.setIngredienteId(ingredienteId);
        result.setCantidad(new BigDecimal("30"));
        result.setUnidad("ml");
        return result;
    }

    private static RecetaRequest request(DosisRequest... items) {
        RecetaRequest result = new RecetaRequest();
        result.setNombre("Receta de prueba");
        result.setPreparacion("Mezclar y servir frío.");
        result.setProductoId(1L);
        result.setIngredientes(new ArrayList<>(List.of(items)));
        return result;
    }

    @Test
    void repeatedIngredientIsRejected() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.save(null, request(dosis(1), dosis(1))));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(recetas, never()).saveAndFlush(any());
    }

    @Test
    void unknownIngredientIsRejected() {
        when(productos.findById(1L)).thenReturn(Optional.of(new Producto()));
        when(ingredientes.findById(77L)).thenReturn(Optional.empty());
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.save(null, request(dosis(77))));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(recetas, never()).saveAndFlush(any());
    }

    @Test
    void tooManySelectedIngredientsIsRejected() {
        List<Long> seleccion = Collections.nCopies(RecetaService.MAX_SELECCION + 1, 1L);
        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service.recomendar(seleccion));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void missingRecipeReturnsNotFound() {
        when(recetas.findById(9L)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class, () -> service.get(9L)).getStatusCode());
    }

    @Test
    void unknownProductIsRejected() {
        when(productos.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.save(null, request(dosis(1))));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(recetas, never()).saveAndFlush(any());
    }
}
