package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.UnidadesGranel;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Unidades Granel.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class UnidadesGranelTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Unidades_granel"; // TODO
    private final String key = "id_unidad"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public UnidadesGranelTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(UnidadesGranel unidades){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = unidades.getIdUnidad();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoUnidadesGranel());

        stmt.bindLong(1, _id);
        stmt.bindString(2, unidades.getDescUnidad());

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

    public List<UnidadesGranel> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<UnidadesGranel> unidades = new ArrayList<>();
        UnidadesGranel unidad;

        while(resource.moveToNext()){
            unidad = new UnidadesGranel();

            unidad.setIdUnidad(resource.getInt(0));
            unidad.setDescUnidad(resource.getString(1));

            unidades.add(unidad);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (unidades.size() <= 0){
            return null;
        }

        return unidades;
    }

    public long getExecutionTime() {
        return executionTime;
    }

}
