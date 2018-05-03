package net.hova_it.barared.brio.apis.models.entities;

import java.util.List;

/**
 * Estructura del modelo SyncTicketInfoContainer
 * Created by Herman Peralta on 09/05/2016.
 */
public class SyncTicketInfoContainer {

    private int idComercio;
    private int lastTransaction;
    private String message;
    private List<SyncTicketInfo> syncTicketInfos;

    public int getLastTransaction() {
        return lastTransaction;
    }

    public void setLastTransaction(int lastTransaction) {
        this.lastTransaction = lastTransaction;
    }

    public int getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(int idComercio) {
        this.idComercio = idComercio;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SyncTicketInfo> getSyncTicketInfos() {
        return syncTicketInfos;
    }

    public void setSyncTicketInfos(List<SyncTicketInfo> syncTicketInfos) {
        this.syncTicketInfos = syncTicketInfos;
    }
}
