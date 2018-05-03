package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo TransaccionTarjeta
 * Created by Mauricio Cerón on 03/05/2016.
 */
public class TransaccionTarjeta {

    private int idTicket;
    private long timestamp;
    private double monto;

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }


}
