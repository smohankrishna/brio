package net.hova_it.barared.brio.apis.models.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lat.brio.core.DataAccess.BrioAccesoDatos;

/**
 * Created by guillermo.ortiz on 27/03/18.
 */

public class RegistroCierreDB extends BrioAccesoDatos {
    
    
    /** Constante con el nombre de la tabla*/
    public static final String DATABASE_TABLE = "Registro_cierre";
    
    /** Array de String en el que se almacenan los nombres de las columnas de la tabla*/
    public static final String[] columnas = new String[] { "id_registro_cierre",
            "id_caja", "id_usuario", "fecha_cierre", "importe_real","importe_contable","importe_remanente","timestamp"
    };
    /** Variables asociadas a los nombres de las columnas de la tabla*/
    public static final String KEY_ID_REGISTRO = columnas[0];
    public static final String KEY_ID_CAJA = columnas[1];
    public static final String KEY_ID_USUARIO = columnas[2];
    public static final String KEY_FECHA_CIERRE = columnas[3];
    public static final String KEY_IMPORTE_REAL = columnas[4];
    public static final String KEY_CONTABLE = columnas[5];
    public static final String KEY_REMANENTE = columnas[6];
    public static final String KEY_TIMESTAMP = columnas[7];
    
    /**
     * Constructor de la clase
     * @param context
     * @param mAccess
     */
    public RegistroCierreDB (Context context, SQLiteDatabase mAccess  ) {
        super (context, mAccess, DATABASE_TABLE);
    }
}
