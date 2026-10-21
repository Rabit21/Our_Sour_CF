package cl.oursour.catalogo.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.catalogo.dto.DosisRequest;
import cl.oursour.catalogo.dto.RecetaDto;
import cl.oursour.catalogo.dto.RecetaRequest;
import cl.oursour.catalogo.dto.RecomendacionDto;
import cl.oursour.catalogo.model.Ingrediente;
import cl.oursour.catalogo.model.Receta;
import cl.oursour.catalogo.model.RecetaIngrediente;
import cl.oursour.catalogo.repository.IngredienteRepository;
import cl.oursour.catalogo.repository.RecetaRepository;

@Service
@Transactional
public class RecetaService {
    public static final int MAX_SELECCION = 50;

    private final RecetaRepository recetas;
    private final IngredienteRepository ingredientes;

    public RecetaService(RecetaRepository recetas, IngredienteRepository ingredientes) {
        this.recetas = recetas;
        this.ingredientes = ingredientes;
    }

    @Transactional(readOnly = true)
    public List<RecetaDto> list() {
        return recetas.findAll(Sort.by("id")).stream().map(RecetaDto::from).toList();
    }

    @Transactional(readOnly = true)
    public RecetaDto get(Long id) {
        return RecetaDto.from(find(id));
    }

    @Transactional(readOnly = true)
    public List<RecomendacionDto> recomendar(List<Long> seleccion) {
        if (seleccion != null && seleccion.size() > MAX_SELECCION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Máximo " + MAX_SELECCION + " ingredientes");
        }
        Set<Long> unicos = seleccion == null ? Set.of() : new HashSet<>(seleccion);
        return MotorRecomendacion.recomendar(recetas.findAll(Sort.by("id")), unicos);
    }

    /** Crea cuando id es null; en otro caso reemplaza nombre, preparación y dosificación. */
    public RecetaDto save(Long id, RecetaRequest input) {
        Receta entity = id == null ? new Receta() : find(id);
        long excludedId = id == null ? -1L : id;
        if (recetas.existsByNombreIgnoreCaseAndIdNot(input.getNombre(), excludedId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nombre duplicado");
        }
        Set<Long> vistos = new HashSet<>();
        for (DosisRequest dosis : input.getIngredientes()) {
            if (!vistos.add(dosis.getIngredienteId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingrediente repetido en la receta");
            }
        }
        entity.setNombre(input.getNombre());
        entity.setPreparacion(input.getPreparacion());
        if (!entity.getIngredientes().isEmpty()) {
            // Se eliminan las líneas anteriores antes de insertar las nuevas (restricción única receta-ingrediente).
            entity.getIngredientes().clear();
            recetas.flush();
        }
        for (DosisRequest dosis : input.getIngredientes()) {
            Ingrediente ingrediente = ingredientes.findById(dosis.getIngredienteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Ingrediente inexistente: " + dosis.getIngredienteId()));
            entity.agregarIngrediente(new RecetaIngrediente(ingrediente, dosis.getCantidad(), dosis.getUnidad()));
        }
        return RecetaDto.from(recetas.saveAndFlush(entity));
    }

    public void delete(Long id) {
        recetas.delete(find(id));
        recetas.flush();
    }

    private Receta find(Long id) {
        return recetas.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso no encontrado"));
    }
}
