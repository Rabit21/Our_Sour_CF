package cl.oursour.catalogo.controller;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.oursour.catalogo.dto.DescuentoStockLote;
import cl.oursour.catalogo.dto.ProductoDto;
import cl.oursour.catalogo.service.ProductoService;
@RestController
@RequestMapping("/api/catalogo/productos")
public class ProductoController {
    private final ProductoService service;
    public ProductoController(ProductoService service) { this.service=service; }
    @GetMapping public List<ProductoDto> list() { return service.list(); }
    @GetMapping("/{id}") public ProductoDto get(@PathVariable Long id) { return service.get(id); }
    @PostMapping public ResponseEntity<ProductoDto> create(@Valid @RequestBody ProductoDto input) {
        ProductoDto result=service.save(null,input);
        return ResponseEntity.created(URI.create("/api/catalogo/productos/"+result.getId())).body(result);
    }
    @PutMapping("/{id}") public ProductoDto update(@PathVariable Long id,@Valid @RequestBody ProductoDto input) { return service.save(id,input); }
    /** Uso interno de ms-pedidos (token con rol SERVICIO). 204, 404 o 409. */
    @PostMapping("/stock/descontar") public ResponseEntity<Void> descontarStock(@Valid @RequestBody DescuentoStockLote input) {
        service.descontarStock(input.getLineas());
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
