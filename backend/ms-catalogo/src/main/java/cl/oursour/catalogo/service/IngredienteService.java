package cl.oursour.catalogo.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.catalogo.dto.IngredienteDto;
import cl.oursour.catalogo.model.Ingrediente;
import cl.oursour.catalogo.repository.IngredienteRepository;

@Service
@Transactional
public class IngredienteService {
    private final IngredienteRepository repository;

    public IngredienteService(IngredienteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<IngredienteDto> list() {
        return repository.findAll(Sort.by("id")).stream().map(IngredienteDto::from).toList();
    }

    @Transactional(readOnly = true)
    public IngredienteDto get(Long id) {
        return IngredienteDto.from(find(id));
    }

    /** Crea cuando id es null; en otro caso actualiza el registro existente. */
    public IngredienteDto save(Long id, IngredienteDto input) {
        Ingrediente entity = id == null ? new Ingrediente() : find(id);
        long excludedId = id == null ? -1L : id;
        if (repository.existsByNombreIgnoreCaseAndIdNot(input.getNombre(), excludedId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nombre duplicado");
        }
        entity.setNombre(input.getNombre());
        entity.setCategoria(input.getCategoria());
        return IngredienteDto.from(repository.saveAndFlush(entity));
    }

    public void delete(Long id) {
        repository.delete(find(id));
        repository.flush();
    }

    private Ingrediente find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso no encontrado"));
    }
}
