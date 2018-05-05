package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Sesion
 * Created by Alejandro Gomez on 30/11/2015.
 */

/*
PK  id_sesion INTEGER PRIMARY KEY AUTOINCREMENT
    id_caja INTEGER  NOT NULL
    id_usuario INTEGER  NOT NULL
    f_inicio TEXT  NOT NULL
    f_fin TEXT  NULL
    FOREIGN KEY (id_caja)REFERENCES Cajas (id_caja)
    FOREIGN KEY (id_usuario)REFERENCES Usuarios (id_usuario)
 */
public class Sesion {
    private int idSesion;
    private int idCaja;
    private int idUsuario;
    private long fechaInicio;
    private long fechaFin;

    public Sesion(){}

    public Sesion( int idCaja, int idUsuario, long fechaInicio, long fechaFin) {

        this.idCaja = idCaja;
        this.idUsuario = idUsuario;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }

    public int getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(int idCaja) {
        this.idCaja = idCaja;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public long getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(long fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public long getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(long fechaFin) {
        this.fechaFin = fechaFin;
    }

}
