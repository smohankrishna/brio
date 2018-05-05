package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Marca;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Marca.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class MarcaTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Marcas"; // TODO
    private final String key = "id_marca"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public MarcaTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(Marca marca){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = marca.getIdMarca();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoMarcas());

        stmt.bindLong(1, _id);
        stmt.bindString(2, marca.getNombre());

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

    public List<Marca> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<Marca> marcas = new ArrayList<>();
        Marca marca ;

        while(resource.moveToNext()){
            marca = new Marca();

            marca.setIdMarca(resource.getInt(0));
            marca.setNombre(resource.getString(1));

            marcas.add(marca);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (marcas.size() <= 0){
            return null;
        }

        return marcas;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
