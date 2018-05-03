package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Cliente
 * Created by Alejandro Gomez on 27/11/2015.
 */

/*
PK  id_cliente INTEGER PRIMARY KEY
    numero_tarjeta TEXT  NOT NULL
    nombre_cliente TEXT  NOT NULL
    id_tipo_cliente INTEGER
*/

public class Cliente {
    private int idCliente;
    private String numeroTarjeta;
    private String nombreCliente;
    private int idTipoCliente;

    public Cliente(){}

    public Cliente(int idCliente, String numeroTarjeta, String nombreCliente, int idTipoCliente) {

        this.idCliente = idCliente;
        this.numeroTarjeta = numeroTarjeta;
        this.nombreCliente = nombreCliente;
        this.idTipoCliente = idTipoCliente;

    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }


    public int getIdTipoCliente() {
        return idTipoCliente;
    }

    public void setIdTipoCliente(int idTipoCliente) {
        this.idTipoCliente = idTipoCliente;
    }

}
