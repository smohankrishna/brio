package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.TipoTicket;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Tipo Ticket.
 * Created by Mauricio Cerón on 23/02/2016.
 */
public class TipoTicketTemplete extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Tipo_Tickets"; // TODO
    private final String key = "id_tipo_ticket"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public TipoTicketTemplete(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(TipoTicket tipoTicket){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = tipoTicket.getIdTipoTicket();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoTipoTickets());

        stmt.bindLong(1, _id);
        stmt.bindString(2, tipoTicket.getDescripcion());
        stmt.bindString(3, tipoTicket.getMovimiento());


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

    public List<TipoTicket> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<TipoTicket> tipoTickets = new ArrayList<>();
        TipoTicket tipoTicket;

        while(resource.moveToNext()){
            tipoTicket = new TipoTicket();

            tipoTicket.setIdTipoTicket(resource.getInt(0));
            tipoTicket.setDescripcion(resource.getString(1));
            tipoTicket.setMovimiento(resource.getString(2));


            tipoTickets.add(tipoTicket);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (tipoTickets.size() <= 0){
            return null;
        }

        return tipoTickets;
    }

    public long getExecutionTime() {
        return executionTime;
    }

}

