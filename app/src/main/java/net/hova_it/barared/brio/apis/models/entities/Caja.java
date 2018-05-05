package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Caja
 * Created by Alejandro Gomez on 30/11/2015.
 */

/*

PK  id_caja INTEGER PRIMARY KEY AUTOINCREMENT
    uuid TEXT  NOT NULL
    nombre TEXT NOT NULL
 */
public class Caja {
    private int idCaja;
    private String uuid;
    private String nombre;



    public Caja(){}

    public Caja( String uuid, String nombre) {
        this.uuid = uuid;
        this.nombre = nombre;
    }

    public int getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(int idCaja) {
        this.idCaja = idCaja;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
