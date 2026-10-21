package cl.oursour.catalogo.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.catalogo.dto.DescuentoStockRequest;
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

    /**
     * Descuenta el stock de un pedido confirmado. Si un producto no existe (404) o no alcanza
     * el stock (409), la excepción revierte la transacción y no se descuenta nada.
     */
    public void descontarStock(List<DescuentoStockRequest> lineas) {
        Map<Long, Integer> porProducto = new TreeMap<>(); // ordenado por id: los bloqueos siempre en el mismo orden
        for (DescuentoStockRequest linea : lineas) {
            porProducto.merge(linea.getProductoId(), linea.getCantidad(), Integer::sum);
        }
        for (Map.Entry<Long, Integer> item : porProducto.entrySet()) {
            Producto producto = repository.bloquearPorId(item.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto inexistente: " + item.getKey()));
            if (producto.getStock() < item.getValue()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para " + producto.getNombre());
            }
            producto.setStock(producto.getStock() - item.getValue());
        }
        repository.flush();
    }

    private Producto find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso no encontrado"));
    }
}
