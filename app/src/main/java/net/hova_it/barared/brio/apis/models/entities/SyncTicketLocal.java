package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo SyncTicketLocal
 * Created by Herman Peralta on 09/05/2016.
 */
public class SyncTicketLocal {

    int idComercio, idTicket, idTipoTicket;
    String descripcionTicket, descripcionItemTicket;
    double importeNeto;
    long timestamp;

    public int getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(int idComercio) {
        this.idComercio = idComercio;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public int getIdTipoTicket() {
        return idTipoTicket;
    }

    public void setIdTipoTicket(int idTipoTicket) {
        this.idTipoTicket = idTipoTicket;
    }

    public String getDescripcionTicket() {
        return descripcionTicket;
    }

    public void setDescripcionTicket(String descripcionTicket) {
        this.descripcionTicket = descripcionTicket;
    }

    public String getDescripcionItemTicket() {
        return descripcionItemTicket;
    }

    public void setDescripcionItemTicket(String descripcionItemTicket) {
        this.descripcionItemTicket = descripcionItemTicket;
    }

    public double getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(double importeNeto) {
        this.importeNeto = importeNeto;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
