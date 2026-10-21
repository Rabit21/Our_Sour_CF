package cl.oursour.catalogo.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import cl.oursour.catalogo.dto.RecomendacionDto;
import cl.oursour.catalogo.model.Ingrediente;
import cl.oursour.catalogo.model.Receta;
import cl.oursour.catalogo.model.RecetaIngrediente;

/** Mismos casos que RecommendationTest de Android, con los datos de demostración. */
class MotorRecomendacionTest {
    private static Ingrediente ing(long id, String nombre) {
        Ingrediente result = new Ingrediente();
        result.setId(id);
        result.setNombre(nombre);
        return result;
    }

    private static final Ingrediente LIMON = ing(1, "Limón"), PISCO = ing(2, "Pisco"), AZUCAR = ing(3, "Azúcar"),
            HIELO = ing(4, "Hielo"), JENGIBRE = ing(5, "Jengibre"), AGUA = ing(6, "Agua");

    private static Receta receta(long id, String nombre, Ingrediente... items) {
        Receta result = new Receta();
        result.setId(id);
        result.setNombre(nombre);
        for (Ingrediente item : items) {
            result.agregarIngrediente(new RecetaIngrediente(item, BigDecimal.ONE, "u"));
        }
        return result;
    }

    private final List<Receta> recetas = List.of(
            receta(1, "Sour clásico", LIMON, PISCO, AZUCAR, HIELO),
            receta(2, "Limonada fresca", LIMON, AZUCAR, HIELO, AGUA),
            receta(3, "Limonada de jengibre", LIMON, JENGIBRE, AGUA));

    @Test
    void emptySelectionReturnsNoResults() {
        assertTrue(MotorRecomendacion.recomendar(recetas, Set.of()).isEmpty());
        assertTrue(MotorRecomendacion.recomendar(recetas, null).isEmpty());
    }

    @Test
    void fullMatchHasNoMissingAndComesFirst() {
        RecomendacionDto first = MotorRecomendacion.recomendar(recetas, Set.of(1L, 2L, 3L, 4L)).get(0);
        assertEquals(1L, first.getRecetaId());
        assertEquals(1.0, first.getProporcion(), 0.001);
        assertEquals(4, first.getDisponibles());
        assertTrue(first.getFaltantes().isEmpty());
    }

    @Test
    void partialMatchesAreSortedAndTieUsesId() {
        List<RecomendacionDto> results = MotorRecomendacion.recomendar(recetas, Set.of(1L));
        assertEquals(List.of(3L, 1L, 2L), results.stream().map(RecomendacionDto::getRecetaId).toList());
        assertEquals(List.of(5L, 6L), results.get(0).getFaltantes().stream().map(f -> f.getIngredienteId()).toList());
        assertEquals("Jengibre", results.get(0).getFaltantes().get(0).getIngrediente());
        assertEquals(1.0 / 3, results.get(0).getProporcion(), 0.001);
    }

    @Test
    void unknownIngredientDoesNotMatch() {
        assertTrue(MotorRecomendacion.recomendar(recetas, Set.of(999L)).isEmpty());
    }
}
