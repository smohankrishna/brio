package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Notificacion
 * Created by Mauricio Cerón on 21/12/2015.
 */
/*
  id_notificacion INTEGER PRIMARY KEY AUTOINCREMENT,
  id_articulo INTEGER NULL ,
  mensaje TEXT NULL ,
  activo INTEGER NOT NULL,
  etapa INTEGER NOT NULL
 */

public class Notificacion {
    private int idNotificacion;
    private int idArticulo;
    private String mensaje;
    private boolean activo;
    private int etapa;


    public Notificacion(){}

    public Notificacion(int idArticulo, String mensaje, boolean activo, int etapa) {
        this.idArticulo = idArticulo;
        this.mensaje = mensaje;
        this.activo = activo;
        this.etapa = etapa;

    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getEtapa() {
        return etapa;
    }

    public void setEtapa(int etapa) {
        this.etapa = etapa;
    }

}
