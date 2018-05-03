package net.hova_it.barared.brio.apis.models.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Super Pojo usado para la sincronizacion via servicio REST
 * entre Brío android y el Back End.
 *
 * Esta clase es la misma en android y en Back End
 *
 * @version 1.0
 *
 * Created by Herman Peralta on 27/07/2016.
 */
public class SuperSyncData {
    private long idComercio;
    private long idCaja;
    private List<SyncData> transacciones;
    private long lastSequence;

    public SuperSyncData() {
        transacciones = new ArrayList<>();
    }

    public long getLastSequence() {
        return lastSequence;
    }

    public void setLastSequence(long lastSequence) {
        this.lastSequence = lastSequence;
    }

    public long getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(long idComercio) {
        this.idComercio = idComercio;
    }

    public long getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(long idCaja) {
        this.idCaja = idCaja;
    }

    public List<SyncData> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<SyncData> transacciones) {
        this.transacciones = transacciones;
    }
}
