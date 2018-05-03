package net.hova_it.barared.brio.apis.models.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lat.brio.core.DataAccess.BrioAccesoDatos;

/**
 * Created by guillermo.ortiz on 27/03/18.
 */

public class UsuarioDB extends BrioAccesoDatos {
    
    
    /**
     * Constante con el nombre de la tabla
     */
    public static final String DATABASE_TABLE = "Usuarios";
    
    /**
     * Array de String en el que se almacenan los nombres de las columnas de la tabla
     */
    public static final String[] columnas = new String[] { "id_usuario",
            "usuario", "password", "id_perfil", "nombre", "apellidos", "pregunta1"
            , "respuesta1", "activo", "bloqueado", "timestamp"
    };
    
    public static final String KEY_ID_USUARIO = columnas[0];
    public static final String KEY_USUARIO = columnas[1];
    public static final String KEY_PASSWORD = columnas[2];
    public static final String KEY_ID_PERFIL = columnas[3];
    public static final String KEY_NOMBRE = columnas[4];
    public static final String KEY_APELLIDOS = columnas[5];
    public static final String KEY_PREGUNTA1 = columnas[6];
    public static final String KEY_RESPUESTA1 = columnas[7];
    public static final String KEY_ACTIVO = columnas[8];
    public static final String KEY_BLOQUEADO = columnas[9];
    public static final String KEY_TIMESTAMP = columnas[10];
    
    /**
     * Constructor de la clase
     * @param context
     * @param mAccess
     */
    public UsuarioDB (Context context, SQLiteDatabase mAccess  ) {
        super (context, mAccess, DATABASE_TABLE);
    }
    
}
