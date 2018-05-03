package net.hova_it.barared.brio.apis.models.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lat.brio.core.DataAccess.BrioAccesoDatos;

/**
 * Created by guillermo.ortiz on 07/03/18.
 */

public class TMAEVersionDB extends BrioAccesoDatos {
    
    /** Constante con el nombre de la tabla*/
    public static final String DATABASE_TABLE = "TMAEVersion";
    
    /** Array de String en el que se almacenan los nombres de las columnas de la tabla*/
    public static final String [] columnas  = new String[] {"Version"};
    
    /** Variables asociadas a los nombres de las columnas de la tabla*/
    public static final String KEY_VERSION = columnas[0];
    
    /**
     * Constructor de la clase
     * @param context
     * @param mAccess
     */
    public TMAEVersionDB (Context context, SQLiteDatabase mAccess) {
        super(context, mAccess, DATABASE_TABLE);
        
    }
    
}
