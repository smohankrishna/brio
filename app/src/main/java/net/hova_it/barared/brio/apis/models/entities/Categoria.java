package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Categoria
 * Created by Mauricio Cerón on 10/12/2015.
 */
/*
PK  id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
 */

public class Categoria {
    private int idCategoria;
    private String nombre;


    public Categoria(){}

    public Categoria(String nombre) {
        this.nombre = nombre;

    }


    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
