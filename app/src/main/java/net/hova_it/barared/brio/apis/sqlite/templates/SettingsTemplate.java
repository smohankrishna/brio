package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Settings.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class SettingsTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Settings"; // TODO
    private final String key = "id_settings"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public SettingsTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public boolean update(String nombre, String valor){
        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        Cursor resource = db.rawQuery("UPDATE " + TABLE + " SET valor = '" + valor + "' WHERE nombre = '" + nombre + "' COLLATE NOCASE", null);

        boolean response = resource.moveToFirst();
        resource.close();

        return response;
    }

    public Long insert(Settings settings){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = settings.getIdSetting();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoSettings());

        stmt.bindLong(1, _id);
        stmt.bindString(2, settings.getNombre()); // ANY TEXT
        stmt.bindString(3, settings.getValor());

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

    public List<Settings> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();

        Cursor resource = db.rawQuery("SELECT * FROM Settings " + where, null);

        List<Settings> settings = new ArrayList<>();
        Settings setting;

        while(resource.moveToNext()){
            setting = new Settings();

            setting.setIdSetting(resource.getInt(0));
            setting.setNombre(resource.getString(1));
            setting.setValor(resource.getString(2));

            settings.add(setting);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (settings.size() <= 0){
            return null;
        }

        return settings;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}