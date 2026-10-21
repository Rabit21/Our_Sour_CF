package cl.oursour.catalogo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.catalogo.dto.ProductoDto;
import cl.oursour.catalogo.model.Producto;
import cl.oursour.catalogo.repository.ProductoRepository;

class ProductoServiceTest {
    private final ProductoRepository repository = mock(ProductoRepository.class);
    private final ProductoService service = new ProductoService(repository);

    private ProductoDto input(String nombre) {
        ProductoDto dto = new ProductoDto();
        dto.setNombre(nombre);
        dto.setDescripcion("Producto sintético");
        dto.setPrecio(new BigDecimal("2500"));
        dto.setStock(4);
        return dto;
    }

    @Test
    void createSavesAllFieldsAndReturnsGeneratedId() {
        when(repository.existsByNombreIgnoreCaseAndIdNot("Pomelo", -1L)).thenReturn(false);
        when(repository.saveAndFlush(any(Producto.class))).thenAnswer(call -> {
            Producto saved = call.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        ProductoDto result = service.save(null, input("  Pomelo "));

        assertEquals(10L, result.getId());
        assertEquals("Pomelo", result.getNombre());
        assertEquals(new BigDecimal("2500"), result.getPrecio());
        assertEquals(4, result.getStock());
    }

    @Test
    void duplicateNameIsRejectedWithConflict() {
        when(repository.existsByNombreIgnoreCaseAndIdNot("Limón fresco", -1L)).thenReturn(true);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.save(null, input("Limón fresco")));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void updatingOwnNameIsAllowed() {
        Producto existing = new Producto();
        existing.setId(3L);
        existing.setNombre("Pomelo");
        when(repository.findById(3L)).thenReturn(Optional.of(existing));
        when(repository.existsByNombreIgnoreCaseAndIdNot("Pomelo", 3L)).thenReturn(false);
        when(repository.saveAndFlush(existing)).thenReturn(existing);

        ProductoDto result = service.save(3L, input("Pomelo"));

        assertEquals(3L, result.getId());
        assertEquals(4, result.getStock());
    }

    @Test
    void missingProductReturnsNotFoundOnGetUpdateAndDelete() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class, () -> service.get(99L)).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class, () -> service.save(99L, input("Pomelo"))).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class, () -> service.delete(99L)).getStatusCode());
        verify(repository, never()).delete(any());
    }
}
