package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Ticket.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class TicketTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Tickets"; // TODO
    private final String key = "id_ticket"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public TicketTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(Ticket ticket){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = ticket.getIdTicket();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoTickets());

        stmt.bindLong(1, _id);
        stmt.bindDouble(2, ticket.getImporteBruto());
        stmt.bindDouble(3, ticket.getImporteNeto());
        stmt.bindDouble(4, ticket.getImpuestos());
        stmt.bindDouble(5, ticket.getDescuento());
        stmt.bindLong(6, ticket.getIdMoneda());
        stmt.bindLong(7, ticket.getIdCliente());
        stmt.bindLong(8, ticket.getIdComercio());
        stmt.bindLong(9, ticket.getIdUsuario());
        stmt.bindLong(10, ticket.getIdCaja());
        stmt.bindDouble(11, ticket.getCambio());
        stmt.bindLong(12, ticket.getIdTipoTicket());
        stmt.bindString(13, ticket.getDescripcion());

        if(ticket.getTimestamp()<= 0){
            ticket.setTimestamp(Utils.getCurrentTimestamp());
        }
        stmt.bindLong(14, ticket.getTimestamp());// <= 0 ? null : ticket.getTimestamp());
        Log.w(TAG, Utils.pojoToString(ticket));


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

    public List<Ticket> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket;

        while(resource.moveToNext()) {
            ticket = new Ticket();

            ticket.setIdTicket(resource.getInt(0));
            ticket.setImporteBruto(resource.getDouble(1));
            ticket.setImporteNeto(resource.getDouble(2));
            ticket.setImpuestos(resource.getDouble(3));
            ticket.setDescuento(resource.getDouble(4));
            ticket.setIdMoneda(resource.getInt(5));
            ticket.setIdCliente(resource.getInt(6));
            ticket.setIdComercio(resource.getInt(7));
            ticket.setIdUsuario(resource.getInt(8));
            ticket.setIdCaja(resource.getInt(9));
            ticket.setCambio(resource.getDouble(10));
            ticket.setIdTipoTicket(resource.getInt(11));
            ticket.setDescripcion(resource.getString(12));
            ticket.setTimestamp(resource.getLong(13));

            tickets.add(ticket);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (tickets.size() <= 0){
            return null;
        }

        return tickets;
    }

    public List<Ticket> selectServiciosAutorizados(long finicio, long ffin, String idTipoTicket){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT t.* " +
                " FROM items_ticket AS it INNER JOIN tickets AS t ON it.id_ticket = t.id_ticket " +
                " WHERE  it.descripcion LIKE '%Autoriza%' AND t.id_tipo_ticket IN ("+ idTipoTicket +") AND t.timestamp BETWEEN " + finicio + " AND " + ffin +
                " GROUP BY it.id_ticket " +
                " ORDER BY it.id_ticket ASC", null);

        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket;

        while(resource.moveToNext()) {
            ticket = new Ticket();

            ticket.setIdTicket(resource.getInt(0));
            ticket.setImporteBruto(resource.getDouble(1));
            ticket.setImporteNeto(resource.getDouble(2));
            ticket.setImpuestos(resource.getDouble(3));
            ticket.setDescuento(resource.getDouble(4));
            ticket.setIdMoneda(resource.getInt(5));
            ticket.setIdCliente(resource.getInt(6));
            ticket.setIdComercio(resource.getInt(7));
            ticket.setIdUsuario(resource.getInt(8));
            ticket.setIdCaja(resource.getInt(9));
            ticket.setCambio(resource.getDouble(10));
            ticket.setIdTipoTicket(resource.getInt(11));
            ticket.setDescripcion(resource.getString(12));
            ticket.setTimestamp(resource.getLong(13));

            tickets.add(ticket);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (tickets.size() <= 0){
            return null;
        }

        return tickets;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public long delete(String where){
        SQLiteDatabase db = sqLiteService.getWritableDatabase();
        long rowsAffected;
        if (where == null || where.isEmpty()) {
            return -1L;
        }
        rowsAffected = db.delete(TABLE,where,null);
        Log.d("DELETE OUTDATED TICKETS", "delete: " + rowsAffected);
        db.close();
        return rowsAffected;

    }

    public long count(String where){
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor cursor;
        if (where == null || where.isEmpty()){
            return -1L;
        }
        cursor = db.rawQuery("select count(*) from " + TABLE + " "+ where,null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        db.close();
        Log.d("COUNT TICKETS", "count: " + count);
        return count;
    }
}
