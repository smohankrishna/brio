package net.hova_it.barared.brio.apis.models;

import android.database.Cursor;
import android.os.Parcel;

import net.hova_it.barared.brio.apis.models.daos.CorreosClientesDB;

import lat.brio.core.BrioGlobales;

/**
 * Created by guillermo.ortiz on 07/03/18.
 */

public class DLCorreosClientes {
    
    private int id;
    private String id_remoto;
    private String email = "";
    private String fecha_creacion = "";
    private String fecha_modificacion = "";
    
    public DLCorreosClientes () {
        // TODO Auto-generated constructor stub
    }
    
    public DLCorreosClientes (Cursor cursor) {
        super ();
        try {
            
            this.id = cursor.getInt (cursor.getColumnIndex (CorreosClientesDB.KEY_ID));
            this.id_remoto = cursor.getString (cursor.getColumnIndex (CorreosClientesDB.KEY_ID_REMOTO));
            this.email = cursor.getString (cursor.getColumnIndex (CorreosClientesDB.KEY_EMAIL));
            this.fecha_creacion = cursor.getString (cursor.getColumnIndex (CorreosClientesDB.KEY_FECHA_CREACION));
            this.fecha_modificacion = cursor.getString (cursor.getColumnIndex (CorreosClientesDB.KEY_FECHA_MODIFICACION));
            
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.writeLog ("DLCorreosClientes", "DLCorreosClientes()", e.getMessage ());
        }
    }
    
    
    public void setId (int id) {
        this.id = id;
    }
    
    public void setEmail (String email) {
        this.email = email;
    }
    
    public int getId () {
        return this.id;
    }
    
    public String getEmail () {
        return this.email;
    }
    
    
    public String getId_remoto () {
        return id_remoto;
    }
    
    public void setId_remoto (String id_remoto) {
        this.id_remoto = id_remoto;
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
    
    @Override
    public String toString () {
        return this.email;
    }
    
}