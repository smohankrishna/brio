package net.hova_it.barared.brio.apis.models.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lat.brio.core.DataAccess.BrioAccesoDatos;

public class ViewArticuloDB extends BrioAccesoDatos {


    /**
     * Constante con el nombre de la tabla
     --PRAGMA table_info('ViewInventario')
     */
    public static final String DATABASE_TABLE = "ViewArticulo";

    public static final String[] columnas = new String[] {
            "id_articulo",
            "id_central",
            "precio_venta",
            "precio_compra",
            "codigo_barras",
            "nombre",
            "marca",
            "presentacion",
            "contenido",
            "Unidad",
            "granel"
    };

    public static final String KEY_ID_ARTICULO = columnas[0];
    public static final String KEY_ID_CENTRAL = columnas[1];
    public static final String KEY_PRECIO_VENTA = columnas[2];
    public static final String KEY_PRECIO_COMPRA = columnas[3];
    public static final String KEY_CODE_BAR = columnas[4];
    public static final String KEY_NOMBRE = columnas[5];
    public static final String KEY_MARCA = columnas[6];
    public static final String KEY_PRESENTACION = columnas[7];
    public static final String KEY_CONTENIDO = columnas[8];
    public static final String KEY_UNIDAD = columnas[9];
    public static final String KEY_granel = columnas[10];

    public ViewArticuloDB(Context context, SQLiteDatabase mAccess) {
        super (context, mAccess, DATABASE_TABLE);
        
    }
    
}
