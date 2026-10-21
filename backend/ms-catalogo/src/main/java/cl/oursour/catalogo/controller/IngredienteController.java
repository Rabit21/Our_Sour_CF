package cl.oursour.catalogo.controller;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.oursour.catalogo.dto.IngredienteDto;
import cl.oursour.catalogo.service.IngredienteService;
@RestController
@RequestMapping("/api/catalogo/ingredientes")
public class IngredienteController {
    private final IngredienteService service;
    public IngredienteController(IngredienteService service) { this.service=service; }
    @GetMapping public List<IngredienteDto> list() { return service.list(); }
    @GetMapping("/{id}") public IngredienteDto get(@PathVariable Long id) { return service.get(id); }
    @PostMapping public ResponseEntity<IngredienteDto> create(@Valid @RequestBody IngredienteDto input) {
        IngredienteDto result=service.save(null,input);
        return ResponseEntity.created(URI.create("/api/catalogo/ingredientes/"+result.getId())).body(result);
    }
    @PutMapping("/{id}") public IngredienteDto update(@PathVariable Long id,@Valid @RequestBody IngredienteDto input) { return service.save(id,input); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
