package cl.oursour.pedidos.dto;

import jakarta.validation.constraints.NotNull;
import cl.oursour.pedidos.model.EstadoPedido;

public class EstadoRequest {
    @NotNull
    private EstadoPedido estado;

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido value) { estado = value; }
}
