package cl.oursour.pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dirección y resultado del pago simulado. No incluye precios ni totales:
 * el servidor los calcula con el catálogo para no confiar en el cliente.
 */
public class CheckoutRequest {
    @NotBlank
    @Size(min = 2, max = 60)
    private String destinatario;

    @NotBlank
    @Size(min = 5, max = 120)
    private String calle;

    @NotBlank
    private String comuna;

    /** Pago sandbox: true simula aprobado y false rechazado. No se procesan tarjetas. */
    @NotNull
    private Boolean pagoAprobado;

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String value) { destinatario = value == null ? null : value.trim(); }
    public String getCalle() { return calle; }
    public void setCalle(String value) { calle = value == null ? null : value.trim(); }
    public String getComuna() { return comuna; }
    public void setComuna(String value) { comuna = value == null ? null : value.trim(); }
    public Boolean getPagoAprobado() { return pagoAprobado; }
    public void setPagoAprobado(Boolean value) { pagoAprobado = value; }
}
