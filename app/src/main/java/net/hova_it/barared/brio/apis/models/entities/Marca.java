package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Marca
 * Created by Mauricio Cerón on 11/12/2015.
 */
/*
PK   id_marca INTEGER PRIMARY KEY AUTOINCREMENT,
     nombre TEXT NOT NULL
 */

public class Marca {
    private int idMarca;
    private String nombre;

    public Marca(){}

    public Marca(String nombre) {
        this.nombre = nombre;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}