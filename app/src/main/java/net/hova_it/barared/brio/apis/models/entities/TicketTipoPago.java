package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo TicketTipoPago
 * Created by Mauricio Cerón on 05/02/2016.
 */
/*

  idTicket_tipos_pago INTEGER PRIMARY KEY AUTOINCREMENT,
  id_ticket INTEGER NOT NULL,
  id_tipo_pago INTEGER NOT NULL,
  monto REAL  NOT NULL,
  descripcion TEXT NOT NULL,
  id_moneda INTEGER NOT NULL

 */

public class TicketTipoPago {

    private int idTicketTiposPago;
    private int idTicket;
    private int idTipoPago;
    private double monto;
    private String descripcion;
    private int idMoneda;

    public TicketTipoPago(){}

    public TicketTipoPago(int idTicketTiposPago, int idTicket, int idTipoPago, double monto, String descripcion, int idMoneda) {
        this.idTicketTiposPago = idTicketTiposPago;
        this.idTicket = idTicket;
        this.idTipoPago = idTipoPago;
        this.monto = monto;
        this.descripcion = descripcion;
        this.idMoneda = idMoneda;

    }

    public int getIdTicketTiposPago() {
        return idTicketTiposPago;
    }

    public void setIdTicketTiposPago(int idTicketTiposPago) {
        this.idTicketTiposPago = idTicketTiposPago;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public int getIdTipoPago() {
        return idTipoPago;
    }

    public void setIdTipoPago(int idTipoPago) {
        this.idTipoPago = idTipoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public int getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(int idmoneda) {
        this.idMoneda = idmoneda;
    }

}
