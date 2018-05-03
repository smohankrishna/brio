package net.hova_it.barared.brio.apis.models;

import android.database.Cursor;

import net.hova_it.barared.brio.apis.models.daos.CorreosClientesDB;
import net.hova_it.barared.brio.apis.models.daos.TicketsEnviadosClientesDB;

import lat.brio.core.BrioGlobales;

/**
 * Created by guillermo.ortiz on 08/03/18.
 */

public class DLTicketsEnviadosClientes {
    
    private int id;
    private int id_ticket;
    private String id_remoto="";
    private String email = "";
    private String fecha_creacion = "";
    private String fecha_modificacion = "";
    
    public DLTicketsEnviadosClientes () {
    }
    
    public DLTicketsEnviadosClientes (Cursor cursor) {
        super ();
        try {
            
            this.id = cursor.getInt (cursor.getColumnIndex (TicketsEnviadosClientesDB.KEY_ID));
            this.id_ticket = cursor.getInt (cursor.getColumnIndex (TicketsEnviadosClientesDB.KEY_ID_TICKETS));
            this.id_remoto = cursor.getString (cursor.getColumnIndex (TicketsEnviadosClientesDB.KEY_ID_REMOTO));
            this.email = cursor.getString (cursor.getColumnIndex (TicketsEnviadosClientesDB.KEY_EMAIL));
            this.fecha_creacion = cursor.getString (cursor.getColumnIndex (TicketsEnviadosClientesDB.KEY_FECHA_CREACION));
            this.fecha_modificacion = cursor.getString (cursor.getColumnIndex (TicketsEnviadosClientesDB.KEY_FECHA_MODIFICACION));
            
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.writeLog ("DLTicketsEnviadosClientes", "DLTicketsEnviadosClientes()", e.getMessage ());
        }
    }
    
    public int getId () {
        return id;
    }
    
    public void setId (int id) {
        this.id = id;
    }
    
    public int getId_ticket () {
        return id_ticket;
    }
    
    public void setId_ticket (int id_ticket) {
        this.id_ticket = id_ticket;
    }
    
    public String getId_remoto () {
        return id_remoto;
    }
    
    public void setId_remoto (String id_remoto) {
        this.id_remoto = id_remoto;
    }
    
    public String getEmail () {
        return email;
    }
    
    public void setEmail (String email) {
        this.email = email;
    }
    
    public String getFecha_creacion () {
        return fecha_creacion;
    }
    
    public void setFecha_creacion (String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
    
    public String getFecha_modificacion () {
        return fecha_modificacion;
    }
    
    public void setFecha_modificacion (String fecha_modificacion) {
        this.fecha_modificacion = fecha_modificacion;
    }
}
