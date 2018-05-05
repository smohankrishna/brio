package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo TipoPago
 * Created by Mauricio Cerón on 25/02/2016.
 */
/*
Tipo_pago
     id_tipo_pago INTEGER PRIMARY KEY AUTOINCREMENT
     descripcion TEXT  NOT NULL
*/
public class TipoPago {
    private int idTipoPago;
    private String descripcion;


    public TipoPago(){}

    public TipoPago(String descripcion) {
        this.descripcion = descripcion;

    }

    public int getIdTipoPago() {
        return idTipoPago;
    }

    public void setIdTipoPago(int idTipoPago) {
        this.idTipoPago = idTipoPago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}