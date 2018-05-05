package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo TipoCliente
 * Created by Mauricio Cerón on 10/12/2015.
 */
/*
PK  id_tipo_cliente INTEGER PRIMARY KEY AUTOINCREMENT
    desc_tipo_cliente TEXT  NOT NULL
    descuento INTEGER  NULL
 */

public class TipoCliente {
    private int idTipoCliente;
    private String descTipoCliente;
    private int descuento;

    public TipoCliente(){}

    public TipoCliente(String descTipoCliente,int descuento) {
        this.descTipoCliente = descTipoCliente;
        this.descuento = descuento;
    }

    public int getIdTipoCliente() {
        return idTipoCliente;
    }

    public void setIdTipoCliente(int idTipoCliente) {
        this.idTipoCliente = idTipoCliente;
    }

    public String getDescTipoCliente() {
        return descTipoCliente;
    }

    public void setDescTipoCliente(String descTipoCliente) {
        this.descTipoCliente = descTipoCliente;
    }

    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
    }

}