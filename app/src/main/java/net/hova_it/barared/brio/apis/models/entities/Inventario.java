package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Inventario
 * Created by Mauricio Cerón on 03/02/2016.
 */
/*
PK    id_inventario INTEGER PRIMARY KEY AUTOINCREMENT
      id_articulo INTEGER NOT NULL
      cantidad REAL NOT NULL
      fecha_modificacion INTEGER DEFAULT 0
      id_sesion INTEGER NOT NULL
*/
public class Inventario {
    private int idInventario;
    private int idArticulo;
    private double existencias;
    private long fechaModificacion;
    private int idSesion;




    public Inventario(){}

    public Inventario(int idArticulo, double existencias,long fechaModificacion,int idSesion) {
        this.idArticulo = idArticulo;
        this.existencias = existencias;
        this.fechaModificacion = fechaModificacion;
        this.idSesion = idSesion;

    }


    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public double getExistencias() {
        return existencias;
    }

    public void setExistencias(double existencias) {
        this.existencias = existencias;
    }

    public long getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(long fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public int getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }

}
