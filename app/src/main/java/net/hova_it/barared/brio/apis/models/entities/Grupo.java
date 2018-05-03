package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Grupo
 * Created by Mauricio Cerón on 10/12/2015.
 */
/*
PK  id_grupo INTEGER PRIMARY KEY AUTOINCREMENT
    desc_grupo TEXT  NOT NULL
 */

public class Grupo {
    private int idGrupo;
    private String descGrupo;


    public Grupo(){}

    public Grupo(String descGrupo) {
        this.descGrupo = descGrupo;

    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getDescGrupo() {
        return descGrupo;
    }

    public void setDescGrupo(String descGrupo) {
        this.descGrupo = descGrupo;
    }

}