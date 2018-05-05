package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.SyncData;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Sync Data.
 * Created by Mauricio Cerón on 09/03/2016.
 */
public class SyncDataTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Sync_Data"; // TODO
    private final String key = "id_sequence"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public SyncDataTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(SyncData syncData){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        long _id = syncData.getIdSequence();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceTableSyncData());

        stmt.bindLong(1, _id);
        stmt.bindLong(2, syncData.getIdComercio());
        stmt.bindString(3, syncData.getUuid());
        stmt.bindString(4, syncData.getTable());
        stmt.bindString(5, syncData.getOperation());
        stmt.bindString(6, syncData.getData());

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

    public List<SyncData> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<SyncData> syncDatas = new ArrayList<>();
        SyncData syncData;

        while(resource.moveToNext()){
            syncData = new SyncData();

            syncData.setIdSequence(resource.getInt(0));
            syncData.setIdComercio(resource.getInt(1));
            syncData.setUuid(resource.getString(2));
            syncData.setTable(resource.getString(3));
            syncData.setOperation(resource.getString(4));
            syncData.setData(resource.getString(5));
            syncData.setTimestamp(resource.getLong(6));

            syncDatas.add(syncData);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (syncDatas.size() <= 0){
            return null;
        }

        return syncDatas;
    }

    public void delete(String idLastSequence){

        Log.w("SyncDataTemplate", "Ejecutando delete, idLastSequence=" + idLastSequence);

        SQLiteDatabase db = sqLiteService.getWritableDatabase();
        db.execSQL("    DELETE FROM Sync_Data   " +
                "    WHERE id_sequence <= " + idLastSequence, new String[]{});
        //db.delete("")


    }

    public long getExecutionTime() {
        return executionTime;
    }
}

