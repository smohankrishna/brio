package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Inventario;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Inventario.
 * Created by Mauricio Cerón on 03/02/2016.
 */
public class InventarioTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Inventario"; // TODO
    private final String key = "id_inventario"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public InventarioTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(Inventario inventario){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = inventario.getIdInventario();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoInventario());

        stmt.bindLong(1, _id);
        stmt.bindLong(2, inventario.getIdArticulo()); // ANY TEXT
        stmt.bindDouble(3, inventario.getExistencias());
        stmt.bindLong(4, inventario.getFechaModificacion());
        stmt.bindLong(5, inventario.getIdSesion());

        Long rowId;

        /* SYNC */ // TODO
        String syncWhere = "WHERE " + key + "=" + _id;
        String syncQry = "SELECT * FROM " + TABLE + " " + syncWhere;

        if(isNew){
            rowId = stmt.executeInsert();
            Log.d(TAG, "executeInsert(): id -> " + rowId);
            //addToSync("INSERT", syncQry, TABLE, syncWhere); // TODO
        }else{
            rowId = ((long)stmt.executeUpdateDelete() > 0)? _id : 0l;
            Log.d(TAG, "executeUpdateDelete(): id -> " + rowId);
            //addToSync("UPDATE", syncQry, TABLE, syncWhere); // TODO
        }

        stmt.close();
        db.close();
        return rowId;
    }

    public List<Inventario> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<Inventario> inventarios = new ArrayList<>();
        Inventario inventario;

        while(resource.moveToNext()){
            inventario = new Inventario();

            inventario.setIdInventario(resource.getInt(0));
            inventario.setIdArticulo(resource.getInt(1));
            inventario.setExistencias(resource.getDouble(2));
            inventario.setFechaModificacion(resource.getLong(3));
            inventario.setIdSesion(resource.getInt(4));

            inventarios.add(inventario);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (inventarios.size() <= 0){
            return null;
        }

        return inventarios;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}