package cl.oursour.catalogo.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.catalogo.dto.ProductoDto;
import cl.oursour.catalogo.model.Producto;
import cl.oursour.catalogo.repository.ProductoRepository;

@Service
@Transactional
public class ProductoService {
    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> list() {
        return repository.findAll(Sort.by("id")).stream().map(ProductoDto::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductoDto get(Long id) {
        return ProductoDto.from(find(id));
    }

    /** Crea cuando id es null; en otro caso actualiza el registro existente. */
    public ProductoDto save(Long id, ProductoDto input) {
        Producto entity = id == null ? new Producto() : find(id);
        long excludedId = id == null ? -1L : id;
        if (repository.existsByNombreIgnoreCaseAndIdNot(input.getNombre(), excludedId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nombre duplicado");
        }
        entity.setNombre(input.getNombre());
        entity.setDescripcion(input.getDescripcion());
        entity.setPrecio(input.getPrecio());
        entity.setStock(input.getStock());
        return ProductoDto.from(repository.saveAndFlush(entity));
    }

    public void delete(Long id) {
        repository.delete(find(id));
        repository.flush();
    }

    private Producto find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso no encontrado"));
    }
}
