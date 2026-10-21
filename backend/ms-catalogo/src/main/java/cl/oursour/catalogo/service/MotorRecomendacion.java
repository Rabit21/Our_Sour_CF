package cl.oursour.catalogo.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import cl.oursour.catalogo.dto.DosisDto;
import cl.oursour.catalogo.dto.RecomendacionDto;
import cl.oursour.catalogo.model.Receta;
import cl.oursour.catalogo.model.RecetaIngrediente;

/**
 * Regla de recomendación (la misma de la app Android):
 * proporción = ingredientes disponibles / ingredientes de la receta.
 * Se descartan recetas sin coincidencias, se ordena de mayor a menor proporción
 * y los empates se resuelven por ID de receta ascendente. No es aprendizaje automático.
 */
public final class MotorRecomendacion {

    private MotorRecomendacion() { }

    public static List<RecomendacionDto> recomendar(List<Receta> recetas, Set<Long> seleccion) {
        List<RecomendacionDto> result = new ArrayList<>();
        if (seleccion == null || seleccion.isEmpty()) {
            return result;
        }
        for (Receta receta : recetas) {
            int total = receta.getIngredientes().size();
            if (total == 0) {
                continue;
            }
            List<DosisDto> faltantes = new ArrayList<>();
            for (RecetaIngrediente linea : receta.getIngredientes()) {
                if (!seleccion.contains(linea.getIngrediente().getId())) {
                    faltantes.add(DosisDto.from(linea));
                }
            }
            int disponibles = total - faltantes.size();
            if (disponibles > 0) {
                result.add(new RecomendacionDto(receta.getId(), receta.getNombre(), disponibles, total, faltantes));
            }
        }
        result.sort(Comparator.comparingDouble(RecomendacionDto::getProporcion).reversed()
                .thenComparing(RecomendacionDto::getRecetaId));
        return result;
    }
}
