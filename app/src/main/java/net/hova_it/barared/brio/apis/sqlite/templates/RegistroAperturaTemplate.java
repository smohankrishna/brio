package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.RegistroApertura;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Registro Apertura.
 * Created by Alejandro Gomez on 21/12/2015.
 */
public class RegistroAperturaTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Registro_Apertura"; // TODO
    private final String key = "id_registro_apertura"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public RegistroAperturaTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(RegistroApertura registroApertura){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = registroApertura.getIdRegistroApertura();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoRegistroApertura());

        stmt.bindLong(1, _id);
        stmt.bindLong(2, registroApertura.getIdRegistroCierre());
        stmt.bindLong(3, registroApertura.getIdCaja());
        stmt.bindLong(4, registroApertura.getIdUsuario());
        stmt.bindLong(5, registroApertura.getFechaApertura());
        stmt.bindDouble(6, registroApertura.getImporteReal());
        stmt.bindDouble(7, registroApertura.getImporteContable());
        //stmt.bindLong(8, registroApertura.getTimestamp());

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

    public List<RegistroApertura> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);
        Log.w(TAG,"Registro apertura"+ (db==null)+" "+(resource==null));
        List<RegistroApertura> registroAperturas = new ArrayList<>();
        RegistroApertura registroApertura;

        while(resource.moveToNext()){
            registroApertura = new RegistroApertura();

            registroApertura.setIdRegistroApertura(resource.getInt(0));
            registroApertura.setIdRegistroCierre(resource.getInt(1));
            registroApertura.setIdCaja(resource.getInt(2));
            registroApertura.setIdUsuario(resource.getInt(3));
            registroApertura.setFechaApertura(resource.getLong(4));
            registroApertura.setImporteReal(resource.getDouble(5));
            registroApertura.setImporteContable(resource.getDouble(6));
            registroApertura.setTimestamp(resource.getLong(7));

            registroAperturas.add(registroApertura);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (registroAperturas.size() <= 0){
            return null;
        }

        return registroAperturas;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
