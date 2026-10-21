package cl.oursour.pedidos.service;

import java.util.List;

/** Misma regla de la app Android: $3.500, gratis desde $30.000 en productos, tres comunas demo. */
public final class Despacho {
    public static final long COSTO = 3500;
    public static final long GRATIS_DESDE = 30000;
    public static final List<String> COMUNAS = List.of("Santiago", "Providencia", "Maipú");

    private Despacho() { }

    public static long costo(long subtotal) {
        return subtotal == 0 || subtotal >= GRATIS_DESDE ? 0 : COSTO;
    }
}
