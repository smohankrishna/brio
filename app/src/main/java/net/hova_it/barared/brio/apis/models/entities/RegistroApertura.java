package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo RegistroApertura
 * Created by Alejandro Gomez on 30/11/2015.
 */

public class RegistroApertura {
    private int idRegistroApertura;
    private int idRegistroCierre;
    private int idCaja;
    private int idUsuario;
    private long fechaApertura;
    private double importeReal;
    private double importeContable;
    private long timestamp;

    public RegistroApertura() {}

    public RegistroApertura(int idRegistroCierre, int idCaja, int idUsuario, long fechaApertura, double importeReal, double importeContable, long timestamp) {
        this.idRegistroCierre = idRegistroCierre;
        this.idCaja = idCaja;
        this.idUsuario = idUsuario;
        this.fechaApertura = fechaApertura;
        this.importeReal = importeReal;
        this.importeContable = importeContable;
        this.timestamp = timestamp;
    }

    public int getIdRegistroApertura() {
        return idRegistroApertura;
    }

    public void setIdRegistroApertura(int idRegistroApertura) {
        this.idRegistroApertura = idRegistroApertura;
    }

    public int getIdRegistroCierre() {
        return idRegistroCierre;
    }

    public void setIdRegistroCierre(int idRegistroCierre) {
        this.idRegistroCierre = idRegistroCierre;
    }

    public int getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(int idCaja) {
        this.idCaja = idCaja;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public long getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(long fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public double getImporteReal() {
        return importeReal;
    }

    public void setImporteReal(double importeReal) {
        this.importeReal = importeReal;
    }

    public double getImporteContable() {
        return importeContable;
    }

    public void setImporteContable(double importeContable) {
        this.importeContable = importeContable;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
