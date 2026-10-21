package cl.oursour.pedidos.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.oursour.pedidos.client.CatalogoClient;
import cl.oursour.pedidos.dto.CarroDto;
import cl.oursour.pedidos.dto.LineaCarroDto;
import cl.oursour.pedidos.dto.ProductoCatalogo;
import cl.oursour.pedidos.model.Carro;
import cl.oursour.pedidos.model.LineaCarro;
import cl.oursour.pedidos.repository.CarroRepository;

@Service
@Transactional
public class CarroService {
    private final CarroRepository carros;
    private final CatalogoClient catalogo;

    public CarroService(CarroRepository carros, CatalogoClient catalogo) {
        this.carros = carros;
        this.catalogo = catalogo;
    }

    public CarroDto ver(Long usuarioId) {
        return calcular(obtener(usuarioId));
    }

    /** Agrega o reemplaza la cantidad de un producto. 404 si no existe, 409 si supera el stock. */
    public CarroDto fijarCantidad(Long usuarioId, Long productoId, int cantidad) {
        ProductoCatalogo producto = catalogo.producto(productoId);
        if (cantidad > producto.stock()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay stock suficiente de " + producto.nombre());
        }
        Carro carro = obtener(usuarioId);
        LineaCarro linea = carro.buscarLinea(productoId);
        if (linea == null) {
            linea = new LineaCarro();
            linea.setProductoId(productoId);
            carro.agregarLinea(linea);
        }
        linea.setCantidad(cantidad);
        return calcular(carros.save(carro));
    }

    public CarroDto quitar(Long usuarioId, Long productoId) {
        Carro carro = obtener(usuarioId);
        carro.getLineas().removeIf(l -> l.getProductoId().equals(productoId));
        return calcular(carros.save(carro));
    }

    public void vaciar(Long usuarioId) {
        obtener(usuarioId).getLineas().clear();
    }

    /** Crea el carro vacío la primera vez que el usuario lo usa. */
    Carro obtener(Long usuarioId) {
        return carros.findByUsuarioId(usuarioId).orElseGet(() -> {
            Carro nuevo = new Carro();
            nuevo.setUsuarioId(usuarioId);
            return carros.save(nuevo);
        });
    }

    /** Precios actuales del catálogo; las líneas de productos eliminados quedan como no disponibles. */
    CarroDto calcular(Carro carro) {
        List<LineaCarroDto> lineas = new ArrayList<>();
        long subtotal = 0;
        for (LineaCarro linea : carro.getLineas()) {
            ProductoCatalogo producto;
            try {
                producto = catalogo.producto(linea.getProductoId());
            } catch (ResponseStatusException e) {
                if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                    throw e;
                }
                lineas.add(new LineaCarroDto(linea.getProductoId(), "Producto no disponible", 0L, linea.getCantidad(), 0L, false));
                continue;
            }
            boolean disponible = linea.getCantidad() <= producto.stock();
            long lineaSubtotal = producto.precio() * linea.getCantidad();
            subtotal += lineaSubtotal;
            lineas.add(new LineaCarroDto(producto.id(), producto.nombre(), producto.precio(), linea.getCantidad(), lineaSubtotal, disponible));
        }
        long despacho = Despacho.costo(subtotal);
        return new CarroDto(lineas, subtotal, despacho, subtotal + despacho);
    }
}
