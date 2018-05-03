package net.hova_it.barared.brio.apis.models;

import android.database.Cursor;

import net.hova_it.barared.brio.apis.models.daos.VisitasModulosDB;

import lat.brio.core.BrioGlobales;

/**
 * Created by guillermo.ortiz on 22/03/18.
 */

public class DlVisitaModulos {
    
   
    private String fecha_inicio;
    private String fecha_fin;
    private int duracion;
    private String seccion;
    private String fecha_creacion;
    private String id_remoto;
    private int id_comercio;
    private String status_mobile;
    
    public DlVisitaModulos () {
    }
    
    public DlVisitaModulos (String fecha_inicio, String fecha_fin, int duracion, String seccion, String fecha_creacion, String id_remoto, int id_comercio, String status_mobile) {
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.duracion = duracion;
        this.seccion = seccion;
        this.fecha_creacion = fecha_creacion;
        this.id_remoto = id_remoto;
        this.id_comercio = id_comercio;
        this.status_mobile = status_mobile;
    }
    
    public DlVisitaModulos (Cursor cursor) {
        try {
    
            this.fecha_inicio = cursor.getString (cursor.getColumnIndex (VisitasModulosDB.KEY_FECHA_INICIO));
            this.fecha_fin = cursor.getString (cursor.getColumnIndex (VisitasModulosDB.KEY_FECHA_FIN));
            this.duracion = cursor.getInt (cursor.getColumnIndex (VisitasModulosDB.KEY_DURACION));
            this.seccion = cursor.getString (cursor.getColumnIndex (VisitasModulosDB.KEY_SECCION));
            this.fecha_creacion = cursor.getString (cursor.getColumnIndex (VisitasModulosDB.KEY_FECHA_CREACION));
            this.id_remoto = cursor.getString (cursor.getColumnIndex (VisitasModulosDB.KEY_ID_REMOTO));
            this.id_comercio = cursor.getInt (cursor.getColumnIndex (VisitasModulosDB.KEY_ID_COMERCIO));
            this.status_mobile = cursor.getString (cursor.getColumnIndex (VisitasModulosDB.KEY_STATUS_MOBILE));
            
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.writeLog ("DLCorreosClientes", "DLCorreosClientes()", e.getMessage ());
        }
    }
    
    public String getFechaInicio () {
        return fecha_inicio;
    }
    
    public void setFechaInicio (String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }
    
    public String getFechaFin () {
        return fecha_fin;
    }
    
    public void setFechaFin (String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }
    
    public int getDuracion () {
        return duracion;
    }
    
    public void setDuracion (int duracion) {
        this.duracion = duracion;
    }
    
    public String getSeccion () {
        return seccion;
    }
    
    public void setSeccion (String seccion) {
        this.seccion = seccion;
    }
    
    public String getFechaCreacion () {
        return fecha_creacion;
    }
    
    public void setFechaCreacion (String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
    
    public String getIdRemoto () {
        return id_remoto;
    }
    
    public void setIdRemoto (String id_remoto) {
        this.id_remoto = id_remoto;
    }
    
    public int getIdComercio () {
        return id_comercio;
    }
    
    public void setIdComercio (int id_comercio) {
        this.id_comercio = id_comercio;
    }
    
    public String getStatusMobile () {
        return status_mobile;
    }
    
    public void setStatusMobile (String status_mobile) {
        this.status_mobile = status_mobile;
    }
}
