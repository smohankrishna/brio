package net.hova_it.barared.brio.apis.sqlite.templates;

/**
 * Created by guillermo.ortiz on 02/03/18.
 */

import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.TelefonoUsers;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edith Maldonado on 16/11/2017.
 */

public class TelefonoUsersTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Telefono_Users"; // TODO
    private final String key = "id_telefono"; // TODO
    
    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;
    
    
    public TelefonoUsersTemplate (SQLiteService sqLiteService) {
        super (sqLiteService);
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit ();
    }
    
    public Long insert (TelefonoUsers telefonoUsers) {
        ///////////////////////////////////////////
        Log.i (TAG, "NEW INSERT TelefonoUser/UPDATE #########");
        
        SQLiteDatabase db = sqLiteService.getWritableDatabase ();
        
        int _id = telefonoUsers.getIdNumTel ();
        boolean isNew = true;
        
        if (_id > 0) {
            isNew = false;
        } else {
            _id = sqLiteService.getLastId (TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////
        
        SQLiteStatement stmt = db.compileStatement (query.replaceIntoTelefonoUsers ());
        
        stmt.bindLong (1, _id);
        stmt.bindString (2, telefonoUsers.getNumTelefonico ()); // ANY TEXT
        
        Long rowId;

        /* SYNC */ // TODO
        String syncWhere = "WHERE " + key + "=" + _id;
        String syncQry = "SELECT * FROM " + TABLE + " " + syncWhere;
        
        if (isNew) {
            rowId = stmt.executeInsert ();
            Log.d (TAG, "executeInsert(): id -> " + rowId);
            //addToSync("INSERT", syncQry, TABLE, syncWhere); // TODO IF IN SYNC
        } else {
            rowId = ((long) stmt.executeUpdateDelete () > 0) ? _id : 0l;
            Log.d (TAG, "executeUpdateDelete(): id -> " + rowId);
            //addToSync("UPDATE", syncQry, TABLE, syncWhere); // TODO IF IN SYNC
        }
        
        stmt.close ();
        db.close ();
        return rowId;
    }
    
    public List<TelefonoUsers> select (String where) {
        long startTime = System.currentTimeMillis ();
        SQLiteDatabase db = sqLiteService.getReadableDatabase ();//getWritableDatabase();
        Cursor resource = db.rawQuery ("SELECT * FROM " + TABLE + " " + where, null);
        
        List<TelefonoUsers> telefonoUsers = new ArrayList<> ();
        TelefonoUsers telefonos;
        
        while (resource.moveToNext ()) {
            telefonos = new TelefonoUsers ();
            
            telefonos.setIdNumTel (resource.getInt (0));
            telefonos.setNumTelefonico (resource.getString (1));
            
            telefonoUsers.add (telefonos);
        }
        resource.close ();
        db.close ();
        
        executionTime = System.currentTimeMillis () - startTime;
        
        if (telefonoUsers.size () <= 0) {
            return null;
        }
        
        return telefonoUsers;
    }
    
    public long getExecutionTime () {
        return executionTime;
    }
}

