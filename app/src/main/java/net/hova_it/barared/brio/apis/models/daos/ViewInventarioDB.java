package net.hova_it.barared.brio.apis.models.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lat.brio.core.DataAccess.BrioAccesoDatos;

public class ViewInventarioDB extends BrioAccesoDatos {
    
    
    /**
     * Constante con el nombre de la tabla
     --PRAGMA table_info('ViewInventario')
     */
    public static final String DATABASE_TABLE = "ViewInventario";
    
    public static final String[] columnas = new String[] {
            "id_articulo",
            "id_inventario",
            "nombre",
            "marca",
            "contenido",
            "Unidad",
            "presentacion",
            "codigo_barras",
            "precio_compra",
            "precio_venta",
            "existencias"
    };
    
    public static final String KEY_ID_ARTICULO = columnas[0];
    public static final String KEY_ID_INVENTARIO = columnas[1];
    public static final String KEY_NOMBRE = columnas[2];
    public static final String KEY_MARCA = columnas[3];
    public static final String KEY_CONTENIDO = columnas[4];
    public static final String KEY_UNIDAD = columnas[5];
    public static final String KEY_PRESENTACION = columnas[6];
    public static final String KEY_CODE_BAR = columnas[7];
    public static final String KEY_PRECIO_COMPRA = columnas[8];
    public static final String KEY_PRECIO_VENTA = columnas[9];
    public static final String KEY_EXISTENCIAS = columnas[10];
    
    public ViewInventarioDB (Context context, SQLiteDatabase mAccess) {
        super (context, mAccess, DATABASE_TABLE);
        
    }
    
}
