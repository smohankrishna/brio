package net.hova_it.barared.brio.apis.models.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lat.brio.core.DataAccess.BrioAccesoDatos;

/**
 * Created by guillermo.ortiz on 22/03/18.
 */

public class VisitasModulosDB extends BrioAccesoDatos {
    
    
    /**
     * Constante con el nombre de la tabla
     */
    public static final String DATABASE_TABLE = "Visitas_modulos";
    
    public static final String[] columnas = new String[] { "fecha_inicio", "fecha_fin",
            "duracion", "seccion", "fecha_creacion",  "id_remoto","id_comercio","status_mobile"
    };
    
    public static final String KEY_FECHA_INICIO = columnas[0];
    public static final String KEY_FECHA_FIN= columnas[1];
    public static final String KEY_DURACION = columnas[2];
    public static final String KEY_SECCION = columnas[3];
    public static final String KEY_FECHA_CREACION = columnas[4];
    public static final String KEY_ID_REMOTO = columnas[5];
    public static final String KEY_ID_COMERCIO = columnas[6];
    public static final String KEY_STATUS_MOBILE = columnas[7];
    /**
     * Constructor de la clase
     * @param context
     * @param mAccess
     */
    public VisitasModulosDB (Context context, SQLiteDatabase mAccess) {
        super (context, mAccess, DATABASE_TABLE);
    }
}
