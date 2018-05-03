package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo TipoTicket
 * Created by Mauricio Cerón on 23/02/2016.
 */
/*
Tipo_Tickets
    id_tipo_ticket INTEGER PRIMARY KEY AUTOINCREMENT
    descripcion TEXT  NOT NULL
*/
public class TipoTicket {
    private int idTipoTicket;
    private String descripcion;
    private String movimiento;


    public TipoTicket(){}

    public TipoTicket(String descripcion, String movimiento) {
        this.descripcion = descripcion;
        this.movimiento = movimiento;
    }

    public int getIdTipoTicket() {
        return idTipoTicket;
    }

    public void setIdTipoTicket(int idTipoTicket) {
        this.idTipoTicket = idTipoTicket;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

}
