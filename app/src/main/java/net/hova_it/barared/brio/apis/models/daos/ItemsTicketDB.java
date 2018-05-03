package net.hova_it.barared.brio.apis.models.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lat.brio.core.DataAccess.BrioAccesoDatos;

public class ItemsTicketDB extends BrioAccesoDatos {



    /**
     * Constante con el nombre de la tabla
     */
    public static final String DATABASE_TABLE = "Items_Ticket";

    public static final String[] columnas = new String[] {
            "id_item_ticket",
            "id_ticket",
            "id_articulo",
            "codigo_barras",
            "descripcion",
            "cantidad",
            "importe_unitario",
            "importe_total",
            "timestamp",
            "precio_compra",
    };


    public static final String KEY_ID_ITEM_TICKET = columnas[0];
    public static final String KEY_ID_TICKET = columnas[1];
    public static final String KEY_ID_ARTICULO = columnas[2];
    public static final String KEY_CODIGO_BARRAS = columnas[3];
    public static final String KEY_DESCRIPCION = columnas[4];
    public static final String KEY_CANTIDAD = columnas[5];
    public static final String KEY_IMPORTE_UNITARIO = columnas[6];
    public static final String KEY_IMPORTE_TOTAL = columnas[7];
    public static final String KEY_TIMESTAMP = columnas[8];
    public static final String KEY_PRECIO_COMPRA = columnas[9];


    public ItemsTicketDB(Context context, SQLiteDatabase mAccess) {
        super(context, mAccess, DATABASE_TABLE);
    }
}
