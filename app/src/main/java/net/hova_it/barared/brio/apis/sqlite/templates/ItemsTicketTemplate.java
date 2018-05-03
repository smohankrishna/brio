package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.ItemsTicket;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Items Ticket.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class ItemsTicketTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Items_Ticket"; // TODO
    private final String key = "id_item_ticket"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public ItemsTicketTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(ItemsTicket itemsTicket){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();//WritableDatabase se utiliza para escribir en la DB

        int _id = itemsTicket.getIdItemTicket();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoItemsTicket());

        stmt.bindLong(1, _id);
        stmt.bindLong(2, itemsTicket.getIdTicket());
        stmt.bindLong(3, itemsTicket.getIdArticulo());
        stmt.bindString(4, itemsTicket.getCodigoBarras()); // ANY NUMBER
        stmt.bindString(5, itemsTicket.getDescripcion());
        stmt.bindDouble(6, itemsTicket.getCantidad());
        stmt.bindDouble(7, itemsTicket.getImporteUnitario());
        stmt.bindDouble(8, itemsTicket.getImporteTotal());

        if(itemsTicket.getTimestamp()<= 0){
            itemsTicket.setTimestamp(Utils.getCurrentTimestamp());
        }
        stmt.bindLong(9, itemsTicket.getTimestamp());

        //add precio compra
        stmt.bindDouble(10, itemsTicket.getPrecioCompra());

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

    public List<ItemsTicket> select(String where){// WHERE es seteado desde el DAO
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<ItemsTicket> itemsTickets = new ArrayList<>();
        ItemsTicket itemTicket;

        while(resource.moveToNext()){
            itemTicket = new ItemsTicket();

            itemTicket.setIdItemTicket(resource.getInt(0));
            itemTicket.setIdTicket(resource.getInt(1));
            itemTicket.setIdArticulo(resource.getInt(2));
            itemTicket.setCodigoBarras(resource.getString(3));
            itemTicket.setDescripcion(resource.getString(4));
            itemTicket.setCantidad(resource.getDouble(5));
            itemTicket.setImporteUnitario(resource.getDouble(6));
            itemTicket.setImporteTotal(resource.getDouble(7));
            itemTicket.setTimestamp(resource.getLong(8));
            //precio compra add
            itemTicket.setPrecioCompra(resource.getDouble(9));

            itemsTickets.add(itemTicket);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (itemsTickets.size() <= 0){
            return null;
        }

        return itemsTickets;
    }

    public Long delete(String where){
        SQLiteDatabase db = sqLiteService.getWritableDatabase();
        long rowsAffected;
        if (where == null || where.isEmpty()) {
           return -1L;
        }
        rowsAffected = db.delete(TABLE,where,null);
        Log.d("DELETE OUTDATED ITEMS", "delete: " + rowsAffected);
        db.close();
        return rowsAffected;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}