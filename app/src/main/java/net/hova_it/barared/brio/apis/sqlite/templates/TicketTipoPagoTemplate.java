package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Ticket Tipo Pago.
 * Created by Mauricio Cerón on 08/02/2016.
 */
public class TicketTipoPagoTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Ticket_tipos_pago"; // TODO
    private final String key = "id_Ticket_tipos_pago"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public TicketTipoPagoTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(TicketTipoPago ticketTipoPago){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = ticketTipoPago.getIdTicketTiposPago();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoTicketTiposPagos());

        stmt.bindLong(1, _id);
        stmt.bindLong(2, ticketTipoPago.getIdTicket()); // ANY TEXT
        stmt.bindLong(3, ticketTipoPago.getIdTipoPago());
        stmt.bindDouble(4, ticketTipoPago.getMonto());
        stmt.bindString(5, ticketTipoPago.getDescripcion());
        stmt.bindLong(6, ticketTipoPago.getIdMoneda());

        Long rowId;

        /* SYNC */ // TODO
        String syncWhere = "WHERE " + key + "=" + _id;
        String syncQry = "SELECT * FROM " + TABLE + " " + syncWhere;

        if(isNew){
            rowId = stmt.executeInsert();
            Log.d(TAG, "executeInsert(): id -> " + rowId);
            addToSync("INSERT", syncQry, TABLE, syncWhere); // TODO
        }else{
            rowId = ((long)stmt.executeUpdateDelete() > 0)? _id : 0l;
            Log.d(TAG, "executeUpdateDelete(): id -> " + rowId);
            addToSync("UPDATE", syncQry, TABLE, syncWhere); // TODO
        }

        stmt.close();
        db.close();
        return rowId;
    }

    public List<TicketTipoPago> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<TicketTipoPago> ticketsTipoPago = new ArrayList<>();
        TicketTipoPago ticketTipoPago;

        while(resource.moveToNext()){
            ticketTipoPago = new TicketTipoPago();

            ticketTipoPago.setIdTicketTiposPago(resource.getInt(0));
            ticketTipoPago.setIdTicket(resource.getInt(1));
            ticketTipoPago.setIdTipoPago(resource.getInt(2));
            ticketTipoPago.setMonto(resource.getDouble(3));
            ticketTipoPago.setDescripcion(resource.getString(4));
            ticketTipoPago.setIdMoneda(resource.getInt(5));


            ticketsTipoPago.add(ticketTipoPago);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (ticketsTipoPago.size() <= 0){
            return null;
        }

        return ticketsTipoPago;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public Long delete(String where){
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        long rowsAffected = -1;
        if (where == null || where.isEmpty()){
            return rowsAffected;
        }
        rowsAffected = db.delete(TABLE,where,null);

        db.close();
        return rowsAffected;

    }
}
