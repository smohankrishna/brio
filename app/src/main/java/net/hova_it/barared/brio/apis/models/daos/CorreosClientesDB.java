package net.hova_it.barared.brio.apis.models.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.hova_it.barared.brio.apis.models.entities.MailUsers;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

import lat.brio.core.DataAccess.BrioAccesoDatos;

/**
 * Created by guillermo.ortiz on 02/03/18.
 */

public class CorreosClientesDB extends BrioAccesoDatos {
    
    
    
    /**
     * Constante con el nombre de la tabla
     */
    public static final String DATABASE_TABLE = "Correos_Clientes";
    
    public static final String[] columnas = new String[] { "id",
            "email", "fecha_creacion", "fecha_modificacion", "id_remoto","status_mobile"
    };
    
    public static final String KEY_ID = columnas[0];
    public static final String KEY_EMAIL = columnas[1];
    public static final String KEY_FECHA_CREACION = columnas[2];
    public static final String KEY_FECHA_MODIFICACION = columnas[3];
    public static final String KEY_ID_REMOTO = columnas[4];
    public static final String KEY_STATUS_MOBILE = columnas[5];
    
    public CorreosClientesDB (Context context, SQLiteDatabase mAccess) {
        super (context, mAccess, DATABASE_TABLE);
        
    }
    
}
