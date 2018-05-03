package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.SyncTicketLocal;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;
import net.hova_it.barared.brio.apis.utils.DebugLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja el metodo Select en Sqlite para extraer la informacion de los tickets a sincronizar.
 * Created by Herman Peralta on 09/05/2016.
 */
public class SyncTicketLocalTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public SyncTicketLocalTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }


    public List<SyncTicketLocal> select(String lastTransaccion , double importeNeto, int idTipoTicket, String idTransaccion){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Log.w("SyncTicket","lastTransaccion="+lastTransaccion+" importeNeto="+importeNeto+" idTipoTicket="+idTipoTicket);
        String query ="SELECT t.id_comercio,t.id_ticket , t.descripcion AS descripcion_ticket, it.descripcion AS descripcion_item_ticket, t.id_tipo_ticket, t.importe_neto, (((t.timestamp / 100000) * 100000) + 50000) AS timestamp\n" +
                "        FROM Tickets AS t INNER JOIN Items_Ticket AS it ON t.id_ticket  = it.id_ticket\n" +
                "        WHERE t.id_tipo_ticket = "+ idTipoTicket +"  AND t.importe_neto = "+ importeNeto +"   AND t.id_ticket > IFNULL((SELECT id_ticket FROM Tickets WHERE descripcion LIKE '" + lastTransaccion + "'),0)\n" +
                "        AND it.descripcion NOT LIKE '%Comisi%'\n" +
                "        ORDER BY t.id_ticket";
        Cursor resource = db.rawQuery(query, null);

        DebugLog.log(getClass(), "BRIO_SYNC_TICKET", query);

        List<SyncTicketLocal> syncTicketLocals = new ArrayList<>();
        SyncTicketLocal syncTicketLocal;

        while(resource.moveToNext()) {
            syncTicketLocal = new SyncTicketLocal();

            syncTicketLocal.setIdComercio(resource.getInt(0));
            syncTicketLocal.setIdTicket(resource.getInt(1));
            syncTicketLocal.setDescripcionTicket(resource.getString(2));
            syncTicketLocal.setDescripcionItemTicket(resource.getString(3));
            syncTicketLocal.setIdTipoTicket(resource.getInt(4));
            syncTicketLocal.setImporteNeto(resource.getDouble(5));
            syncTicketLocal.setTimestamp(resource.getLong(6));

            syncTicketLocals.add(syncTicketLocal);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (syncTicketLocals.size() <= 0){
            return null;
        }

        return syncTicketLocals;
    }



    public long getExecutionTime() {
        return executionTime;
    }
}
