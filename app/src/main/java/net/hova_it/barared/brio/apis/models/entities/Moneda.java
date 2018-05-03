package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Moneda
 * Created by Alejandro Gomez on 30/11/2015.
 */

/*
PK  id_moneda INTEGER PRIMARY KEY AUTOINCREMENT
    desc_moneda TEXT  NOT NULL
    defecto INTEGER  NOT NULL
    tipo_cambio INTEGER  NOT NULL
    timestamp TEXT  NOT NULL
 */
public class Moneda {
    private int idMoneda;
    private String descMoneda;
    private boolean defecto;
    private double tipoCambio;
    private long timestamp;
    private static String nombre;





    public Moneda(String descMoneda, boolean defecto, double tipoCambio, long timestamp) {
        this.descMoneda = descMoneda;
        this.defecto = defecto;
        this.tipoCambio = tipoCambio;
        this.timestamp = timestamp;
    }

    public Moneda() {

    }

    public static void setNombre(String nombre) {
        Moneda.nombre = nombre;
    }

    public int getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(int idMoneda) {
        this.idMoneda = idMoneda;
    }

    public String getDescMoneda() {
        return descMoneda;
    }

    public void setDescMoneda(String descMoneda) {
        this.descMoneda = descMoneda;
    }

    public boolean getDefecto() {
        return defecto;
    }

    public void setDefecto(boolean defecto) {
        this.defecto = defecto;
    }

    public double getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(double tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return this.getDescMoneda();
    }
}