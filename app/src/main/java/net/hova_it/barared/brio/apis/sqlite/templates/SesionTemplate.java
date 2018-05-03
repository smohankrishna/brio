package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Sesion;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Sesion.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class SesionTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Sesiones"; // TODO
    private final String key = "id_sesion"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public SesionTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(Sesion sesion){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = sesion.getIdSesion();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoSesiones());

        stmt.bindLong(1, _id);
        stmt.bindLong(2, sesion.getIdCaja());
        stmt.bindLong(3, sesion.getIdUsuario());
        stmt.bindLong(4, sesion.getFechaInicio());
        stmt.bindLong(5, sesion.getFechaFin());

        Long rowId;

        /* SYNC */ // TODO
        String syncWhere = "WHERE " + key + "=" + _id;
        String syncQry = "SELECT * FROM " + TABLE + " " + syncWhere;

        if(isNew){
            rowId = stmt.executeInsert();
            //Log.d(TAG, "executeInsert(): id -> " + rowId);
            //addToSync("INSERT", syncQry, TABLE, syncWhere); // TODO
        }else{
            rowId = ((long)stmt.executeUpdateDelete() > 0)? _id : 0l;
            //Log.d(TAG, "executeUpdateDelete(): id -> " + rowId);
            //addToSync("UPDATE", syncQry, TABLE, syncWhere); // TODO
        }

        stmt.close();
        db.close();
        return rowId;
    }

    public List<Sesion> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<Sesion> sesiones = new ArrayList<>();
        Sesion sesion;

        while(resource.moveToNext()){
            sesion = new Sesion();

            sesion.setIdSesion(resource.getInt(0));
            sesion.setIdCaja(resource.getInt(1));
            sesion.setIdUsuario(resource.getInt(2));
            sesion.setFechaInicio(resource.getLong(3));
            sesion.setFechaFin(resource.getLong(4));

            sesiones.add(sesion);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (sesiones.size() <= 0){
            return null;
        }

        return sesiones;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
