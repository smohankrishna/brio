package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.RegistroCierre;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Registro Cierre.
 * Created by Alejandro Gomez on 21/12/2015.
 */
public class RegistroCierreTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Registro_cierre"; // TODO
    private final String key = "id_registro_cierre"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public RegistroCierreTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(RegistroCierre registroCierre){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = registroCierre.getIdRegistroCierre();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoRegistroCierre());

        stmt.bindLong(1, _id);
        stmt.bindLong(2, registroCierre.getIdCaja());
        stmt.bindLong(3, registroCierre.getIdUsuario());
        stmt.bindLong(4, registroCierre.getFechaCierre());
        stmt.bindDouble(5, registroCierre.getImporteReal());
        stmt.bindDouble(6, registroCierre.getImporteContable());
        stmt.bindDouble(7, registroCierre.getImporteRemanente());

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

    public List<RegistroCierre> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);
Log.w(TAG,"Registro cierre"+ (db==null)+" "+(resource==null));
        List<RegistroCierre> registroCierres = new ArrayList<>();
        RegistroCierre registroCierre;

        while(resource.moveToNext()){
            registroCierre = new RegistroCierre();

            registroCierre.setIdRegistroCierre(resource.getInt(0));
            registroCierre.setIdCaja(resource.getInt(1));
            registroCierre.setIdUsuario(resource.getInt(2));
            registroCierre.setFechaCierre(resource.getLong(3));
            registroCierre.setImporteReal(resource.getDouble(4));
            registroCierre.setImporteContable(resource.getDouble(5));
            registroCierre.setImporteRemanente(resource.getDouble(6));
            registroCierre.setTimestamp(resource.getLong(7));

            registroCierres.add(registroCierre);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (registroCierres.size() <= 0){
            return null;
        }

        return registroCierres;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
