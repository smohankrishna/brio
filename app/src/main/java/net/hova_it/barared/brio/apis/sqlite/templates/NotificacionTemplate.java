package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Notificacion;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Notificacion.
 * Created by Mauricio Cerón on 21/12/2015.
 */
public class NotificacionTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Notificaciones"; // TODO
    private final String key = "id_notificacion"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public NotificacionTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(Notificacion notificacion){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = notificacion.getIdNotificacion();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoNotificaciones());

        stmt.bindLong(1, _id);
        stmt.bindLong(2, notificacion.getIdArticulo());
        stmt.bindString(3, notificacion.getMensaje()); // ANY TEXT
        stmt.bindLong(4, notificacion.getActivo()?1:0);
        stmt.bindLong(5, notificacion.getEtapa());

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

    public List<Notificacion> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<Notificacion> notificaciones = new ArrayList<>();
        Notificacion notificacion ;

        while(resource.moveToNext()){
            notificacion = new Notificacion();

            notificacion.setIdNotificacion(resource.getInt(0));
            notificacion.setIdArticulo(resource.getInt(1));
            notificacion.setMensaje(resource.getString(2));
            notificacion.setActivo(resource.getInt(3)!=0);
            notificacion.setEtapa(resource.getInt(4));

            notificaciones.add(notificacion);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (notificaciones.size() <= 0){
            return null;
        }

        return notificaciones;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
