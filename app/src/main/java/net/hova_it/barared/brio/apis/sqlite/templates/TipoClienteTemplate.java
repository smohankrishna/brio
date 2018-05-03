package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.TipoCliente;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Tipo Cliente.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class TipoClienteTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Tipo_cliente"; // TODO
    private final String key = "id_tipo_cliente"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public TipoClienteTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(TipoCliente tipoCliente){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = tipoCliente.getIdTipoCliente();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoTipoCliente());

        stmt.bindLong(1, _id);
        stmt.bindString(2, tipoCliente.getDescTipoCliente()); // ANY TEXT
        stmt.bindLong(3, tipoCliente.getDescuento());

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

    public List<TipoCliente> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<TipoCliente> tipoClientes = new ArrayList<>();
        TipoCliente tipoCliente;

        while(resource.moveToNext()){
            tipoCliente = new TipoCliente();

            tipoCliente.setIdTipoCliente(resource.getInt(0));
            tipoCliente.setDescTipoCliente(resource.getString(1));
            tipoCliente.setDescuento(resource.getInt(2));

            tipoClientes.add(tipoCliente);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (tipoClientes.size() <= 0){
            return null;
        }

        return tipoClientes;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
