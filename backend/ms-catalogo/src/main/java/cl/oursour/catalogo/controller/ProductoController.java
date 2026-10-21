package cl.oursour.catalogo.controller;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
