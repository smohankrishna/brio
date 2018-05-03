package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Familia
 * Created by Mauricio Cerón on 10/12/2015.
 */
/*
PK  id_familia INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NULL,
    id_categoria INTEGER,
 */

public class Familia {
    private int idFamilia;
    private String descFamilia;
    private int idCategoria;


    public Familia(){}

    public Familia(String descFamilia,int idCategoria) {
        this.idCategoria = idCategoria;
        this.descFamilia = descFamilia;
    }

    public int getIdFamilia() {
        return idFamilia;
    }

    public void setIdFamilia(int idFamilia) {
        this.idFamilia = idFamilia;
    }

    public String getDescFamilia() {
        return descFamilia;
    }

    public void setDescFamilia(String descFamilia) {
        this.descFamilia = descFamilia;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    @Override
    public String toString() {
        return this.descFamilia;
    }
}