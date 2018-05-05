package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo UnidadesGranel
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class UnidadesGranel {
    private int idUnidad;
    private String descUnidad;


    public UnidadesGranel(){}

    public UnidadesGranel(String descUnidad) {
        this.descUnidad = descUnidad;

    }

    public int getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(int idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getDescUnidad() {
        return descUnidad;
    }

    public void setDescUnidad(String descUnidad) {
        this.descUnidad = descUnidad;
    }

    @Override
    public String toString() {
        return this.descUnidad;
    }
}
