package cl.oursour.catalogo.controller;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.oursour.catalogo.dto.RecetaDto;
import cl.oursour.catalogo.dto.RecetaRequest;
import cl.oursour.catalogo.dto.RecomendacionDto;
import cl.oursour.catalogo.service.RecetaService;

@RestController
@RequestMapping("/api/catalogo/recetas")
public class RecetaController {
    private final RecetaService service;

    public RecetaController(RecetaService service) {
        this.service = service;
    }

    @GetMapping
    public List<RecetaDto> list() {
        return service.list();
    }

    /** Ejemplo: GET /api/catalogo/recetas/recomendaciones?ingredientes=1,2,3 */
    @GetMapping("/recomendaciones")
    public List<RecomendacionDto> recomendar(@RequestParam(name = "ingredientes", required = false) List<Long> ingredientes) {
        return service.recomendar(ingredientes);
    }

    @GetMapping("/{id}")
    public RecetaDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<RecetaDto> create(@Valid @RequestBody RecetaRequest input) {
        RecetaDto result = service.save(null, input);
        return ResponseEntity.created(URI.create("/api/catalogo/recetas/" + result.getId())).body(result);
    }

    @PutMapping("/{id}")
    public RecetaDto update(@PathVariable Long id, @Valid @RequestBody RecetaRequest input) {
        return service.save(id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
