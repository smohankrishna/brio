package net.hova_it.barared.brio.apis.sync;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.utils.Utils;

/**
 * Clase que permite concentrar las operaciones que
 * se realizan sobre la base de datos local.
 *
 *
 *
 * Created by Alejandro Gomez on 14/03/2016.
 */
public class Synchronizable {
    private static final String pattern = "[^0-9a-zA-Z áéíóúÁÉÍÓÚñÑüÜ]";

    private final String TAG = "BRIO_SYNC";
    private SQLiteService sqLiteService;
    private SQLiteDatabase db;
    private SQLiteInit sqLiteInit;


    public Synchronizable(SQLiteService sqLiteService){
        this.sqLiteService = sqLiteService;
        this.sqLiteInit = new SQLiteInit();
    }

    /**
     *
     * @param operation - la operacion realizada en la base de datos INSERT/ UPDATE
     * @param query - El query realizado la operación
     * @param TABLE - La tabla afectada por el query
     * @param WHERE - La claúsula where de la operación realizadsa
     *
     * @return el id del registro de los datos de sincronización en la base de datos
     */
    protected long addToSync(String operation, String query, String TABLE, String WHERE){
        long executionTime = System.currentTimeMillis();
        this.db = sqLiteService.getReadableDatabase();
        long idComercio = getIdComercio(this.db);
        Cursor resourse = db.rawQuery(query, null);
        String cols = "";
        String vals = "";
        String qry = "";
        int colCount;

        if(resourse.getCount() > 0){
            switch (operation){
                case "INSERT":
                    resourse.moveToFirst();
                    colCount = resourse.getColumnCount();
                    qry = "INSERT INTO " + TABLE + "(";
                    for(int i = 0; i < colCount; i++){
                        if(!resourse.getColumnName(i).equals("id_comercio")) {
                            if(i>0){
                                cols += ",";
                                vals += ",";
                            }
                            switch (resourse.getColumnName(i)){
                                case "timestamp":
                                    cols += "registro";
                                    vals += "'" + Utils.getBrioDate(Long.valueOf(resourse.getString(i))) + "'";
                                    break;
                                default:
                                    cols += resourse.getColumnName(i);
                                    if (resourse.getType(i) > 2) {
                                        String tmp = resourse.getString(i).replaceAll(pattern, " ");
                                        if(tmp.length() > 90) { tmp = tmp.substring(0, 90); }
                                        vals += "'" + tmp + "'"; //
                                    } else {
                                        vals += resourse.getString(i);
                                    }
                                    break;
                            }
                        }
                    }
                    qry += cols + ",id_comercio) VALUES (" + vals + "," + idComercio + ");";
                    break;
                case "UPDATE":
                    resourse.moveToFirst();
                    colCount = resourse.getColumnCount();
                    qry = "UPDATE " + TABLE + " SET ";
                    for(int i = 0; i < colCount; i++){
                        if(!resourse.getColumnName(i).equals("id_comercio")) {
                            if(i>0){
                                qry += ",";
                            }
                            switch (resourse.getColumnName(i)){
                                case "timestamp":
                                    qry += "registro=" + "'" + Utils.getBrioDate(Long.valueOf(resourse.getString(i))) + "'";
                                    break;
                                default:
                                    qry += resourse.getColumnName(i) + "=";
                                    if (resourse.getType(i) > 2) {
                                        String tmp = resourse.getString(i).replaceAll(pattern, " ");
                                        if(tmp.length() > 90) { tmp = tmp.substring(0, 90); }
                                        qry += "'" + tmp + "'"; //
                                    } else {
                                        qry += resourse.getString(i);
                                    }
                                    break;
                            }
                        }
                    }
                    qry += " " + WHERE + " AND id_comercio=" + idComercio + ";";
                    break;
            }

        }

        qry = qry.toUpperCase();

        /* SAVE QUERIES */
        SQLiteStatement stmt = db.compileStatement(sqLiteInit.replaceTableSyncData());
        long _id = sqLiteService.getLastId("Sync_Data", "id_sequence", db) + 1;

        stmt.bindLong(1, _id);
        stmt.bindLong(2, idComercio);
        stmt.bindString(3, Utils.getUUID(sqLiteService.getContext()));
        stmt.bindString(4, TABLE);
        stmt.bindString(5, operation);
        stmt.bindString(6, qry);

        stmt.executeInsert();

        resourse.close();
        db.close();

        //Log.d(TAG, "<<" + (System.currentTimeMillis() - executionTime) + ">>" + qry);

        Log.d(TAG, qry);

        return _id;
    }

    private long getIdComercio(SQLiteDatabase db) {
        Cursor resource = db.rawQuery("SELECT valor FROM Settings WHERE nombre = 'ID_COMERCIO' COLLATE NOCASE", null);
        long idComercio = 0L;
        if(resource.moveToFirst()){
            idComercio = Long.valueOf(resource.getString(0));
        }
        resource.close();

        return idComercio;
    }
}