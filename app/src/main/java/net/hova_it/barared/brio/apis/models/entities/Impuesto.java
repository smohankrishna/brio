package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Impuesto
 * Created by Mauricio Cerón on 10/12/2015.
 */
/*
PK  id_impuestos INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    impuesto INTEGER NOT NULL,
    prioridad INTEGER NULL,
 */

public class Impuesto {
    private int idImpuesto;
    private String descImpuesto;
    private double impuesto;
    private int prioridad;


    public Impuesto(){}

    public Impuesto(String descImpuesto,int impuesto,int prioridad) {
        this.descImpuesto = descImpuesto;
        this.impuesto = impuesto;
        this.prioridad = prioridad;
    }

    public int getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(int idImpuesto) {
        this.idImpuesto = idImpuesto;
    }

    public String getDescImpuesto() {
        return descImpuesto;
    }

    public void setDescImpuesto(String DescImpuesto) {
        this.descImpuesto = DescImpuesto;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(double Impuesto) {
        this.impuesto = Impuesto;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    @Override
    public String toString() {
        return this.descImpuesto;
    }
}
