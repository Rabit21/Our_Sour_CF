package cl.oursour.catalogo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.catalogo.dto.IngredienteDto;
import cl.oursour.catalogo.model.CategoriaIngrediente;
import cl.oursour.catalogo.model.Ingrediente;
import cl.oursour.catalogo.repository.IngredienteRepository;

class IngredienteServiceTest {
    private final IngredienteRepository repository = mock(IngredienteRepository.class);
    private final IngredienteService service = new IngredienteService(repository);

    private IngredienteDto input(String nombre) {
        IngredienteDto dto = new IngredienteDto();
        dto.setNombre(nombre);
        dto.setCategoria(CategoriaIngrediente.FRUTA);
        return dto;
    }

    @Test
    void duplicateIgnoringCaseIsRejected() {
        when(repository.existsByNombreIgnoreCaseAndIdNot("limón", -1L)).thenReturn(true);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.save(null, input("limón")));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void deleteRemovesExistingIngredient() {
        Ingrediente existing = new Ingrediente();
        existing.setId(5L);
        existing.setNombre("Menta");
        when(repository.findById(5L)).thenReturn(Optional.of(existing));

        service.delete(5L);

        verify(repository).delete(existing);
        verify(repository).flush();
    }

    @Test
    void categoryIsSavedAndReturned() {
        when(repository.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
        IngredienteDto result = service.save(null, input("Menta"));
        assertEquals(CategoriaIngrediente.FRUTA, result.getCategoria());
    }
}
