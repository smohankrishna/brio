package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Grupo;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Grupo.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class GrupoTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Grupo"; // TODO
    private final String key = "id_grupo"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public GrupoTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(Grupo grupo){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = grupo.getIdGrupo();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoGrupo());

        stmt.bindLong(1, _id);
        stmt.bindString(2, grupo.getDescGrupo()); // ANY TEXT

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

    public List<Grupo> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resourse = db.rawQuery("SELECT * FROM Grupo " + where, null);

        List<Grupo> grupos = new ArrayList<>();
        Grupo grupo;

        while(resourse.moveToNext()){
            grupo = new Grupo();

            grupo.setIdGrupo(resourse.getInt(0));
            grupo.setDescGrupo(resourse.getString(1));

            grupos.add(grupo);
        }
        resourse.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (grupos.size() <= 0){
            return null;
        }

        return grupos;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
