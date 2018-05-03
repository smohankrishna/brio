package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Cliente;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Cliente.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class ClienteTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Cliente"; // TODO
    private final String key = "id_cliente"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public ClienteTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(Cliente cliente){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");
        ///////////////////////////////////////////

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = cliente.getIdCliente();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoCliente());

        stmt.bindLong(1, cliente.getIdCliente());
        stmt.bindString(2, cliente.getNumeroTarjeta());
        stmt.bindString(3, cliente.getNombreCliente());
        stmt.bindLong(4, cliente.getIdTipoCliente());

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

    public List<Cliente> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<Cliente> clientes = new ArrayList<>();
        Cliente cliente;

        while(resource.moveToNext()){
            cliente = new Cliente();

            cliente.setIdCliente(resource.getInt(0));
            cliente.setNumeroTarjeta(resource.getString(1));
            cliente.setNombreCliente(resource.getString(2));
            cliente.setIdTipoCliente(resource.getInt(3));
            clientes.add(cliente);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (clientes.size() <= 0){
            return null;
        }

        return clientes;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
