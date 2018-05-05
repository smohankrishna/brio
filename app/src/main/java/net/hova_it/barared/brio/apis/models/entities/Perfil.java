package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Perfil
 * Created by Alejandro Gomez on 30/11/2015.
 */
/*
PK  id_perfil INTEGER PRIMARY KEY AUTOINCREMENT
    perfil TEXT  NOT NULL
    modulos TEXT  NULL

 */
public class Perfil {
    private int idPerfil;
    private String perfil;
    private String modulos;

    public Perfil(){}

    public Perfil(String perfil) {
        this.perfil = perfil;
    }

    public Perfil(String perfil,String modulos) {
        this.perfil = perfil;
        this.modulos = modulos;
    }

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getModulos() {
        return modulos;
    }

    public void setModulos(String modulos) {
        this.modulos = modulos;
    }


    @Override
    public String toString() {
        return perfil;
    }
}
