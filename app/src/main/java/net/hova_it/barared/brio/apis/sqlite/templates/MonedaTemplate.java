package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Moneda;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Moneda.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class MonedaTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Monedas"; // TODO
    private final String key = "id_moneda"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public MonedaTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(Moneda moneda){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = moneda.getIdMoneda();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoMonedas());

        stmt.bindLong(1, _id);
        stmt.bindString(2, moneda.getDescMoneda());
        stmt.bindLong(3, moneda.getDefecto()?1:0);
        stmt.bindDouble(4, moneda.getTipoCambio());

        Long rowId;

        /* SYNC */ // TODO
        String syncWhere = "WHERE " + key + "=" + _id;
        String syncQry = "SELECT * FROM " + TABLE + " " + syncWhere;

        if(isNew){
            rowId = stmt.executeInsert();
            Log.d(TAG, "executeInsert(): id -> " + rowId);
            //addToSync("INSERT", syncQry, TABLE, syncWhere); // TODO IF IN SYNC
        }else{
            rowId = ((long)stmt.executeUpdateDelete() > 0)? _id : 0l;
            Log.d(TAG, "executeUpdateDelete(): id -> " + rowId);
            //addToSync("UPDATE", syncQry, TABLE, syncWhere); // TODO IF IN SYNC
        }

        stmt.close();
        db.close();
        return rowId;
    }

    public List<Moneda> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<Moneda> monedas = new ArrayList<>();
        Moneda moneda;

        while(resource.moveToNext()){
            moneda = new Moneda();

            moneda.setIdMoneda(resource.getInt(0));
            moneda.setDescMoneda(resource.getString(1));
            moneda.setDefecto(resource.getInt(2)!=0);
            moneda.setTipoCambio(resource.getDouble(3));
            moneda.setTimestamp(resource.getLong(4));

            monedas.add(moneda);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (monedas.size() <= 0){
            return null;
        }

        return monedas;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}