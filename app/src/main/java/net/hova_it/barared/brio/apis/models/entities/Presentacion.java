package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Presentacion
 * Created by Mauricio Cerón on 21/12/2015.
 */
/*
PK  id_presentacion INTEGER PRIMARY KEY AUTOINCREMENT
    nombre TEXT NOT NULL
*/
public class Presentacion {

    private int idPresentacion;
    private String nombre;

    public Presentacion(){}

    public Presentacion(String nombre) {
        this.nombre = nombre;
    }

    public int getIdPresentacion() {
        return idPresentacion;
    }

    public void setIdPresentacion(int idPresentacion) {
        this.idPresentacion = idPresentacion;
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
