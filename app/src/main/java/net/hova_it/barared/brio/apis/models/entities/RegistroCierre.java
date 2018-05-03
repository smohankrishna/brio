package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo RegistroCierre
 * Created by Alejandro Gomez on 30/11/2015.
 */

public class RegistroCierre {
    private int idRegistroCierre;
    private int idCaja;
    private int idUsuario;
    private long fechaCierre;
    private double importeReal;
    private double importeContable;
    private double importeRemanente;
    private long timestamp;




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

    public long getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(long fechaCierre) {
        this.fechaCierre = fechaCierre;
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

    public double getImporteRemanente() {
        return importeRemanente;
    }

    public void setImporteRemanente(double importeRemanente) {
        this.importeRemanente = importeRemanente;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
