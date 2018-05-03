package net.hova_it.barared.brio.apis.models.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lat.brio.core.DataAccess.BrioAccesoDatos;

public class SettingsDB extends BrioAccesoDatos {


    /**
     * Constante con el nombre de la tabla
     */
    public static final String DATABASE_TABLE = "Settings";

    public static final String[] columnas = new String[]{
            "id_settings",
            "nombre",
            "valor"
    };


    public static final String KEY_ID_SETTINGS = columnas[0];
    public static final String KEY_NOMBRE = columnas[1];
    public static final String KEY_VALOR = columnas[2];

    public SettingsDB(Context context, SQLiteDatabase mAccess) {
        super(context, mAccess, DATABASE_TABLE);
    }
}
