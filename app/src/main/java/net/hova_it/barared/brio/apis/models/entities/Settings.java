package net.hova_it.barared.brio.apis.models.entities;

import android.database.Cursor;

import net.hova_it.barared.brio.apis.models.daos.SettingsDB;

import lat.brio.core.BrioGlobales;

/**
 * Estructura del modelo Settings
 * Created by Mauricio Cerón on 11/12/2015.
 */
/*
PK  id_parametro INTEGER PRIMARY KEY AUTOINCREMENT
    nombre TEXT  NULL
    valor TEXT  NULL
 */

public class Settings {
    private int idSetting;
    private String nombre;
    private String valor;

    public Settings(){}

    public Settings(String nombre, String valor) {
        this.nombre = nombre;
        this.valor = valor;

    }
    public Settings (Cursor cursor) {
        super ();
        try {

            this.idSetting = cursor.getInt (cursor.getColumnIndex (SettingsDB.KEY_ID_SETTINGS));
            this.nombre = cursor.getString (cursor.getColumnIndex (SettingsDB.KEY_NOMBRE));
            this.valor = cursor.getString (cursor.getColumnIndex (SettingsDB.KEY_VALOR));
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.writeLog ("Settings", "DLCorreosClientes()", e.getMessage ());
        }
    }

    public int getIdSetting() {
        return idSetting;
    }

    public void setIdSetting(int idSetting) {
        this.idSetting = idSetting;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}
