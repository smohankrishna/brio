package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.hova_it.barared.brio.apis.models.entities.TransaccionTarjeta;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja el metodos Select en Sqlite para extraer la informacion correspondiente a los pagos con tarjeta.
 * Created by Mauricio Cerón on 12/05/2016.
 */
public class TransaccionTarjetaTemplate {
    private final String TAG = "BRIO_DB";

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;


    public TransaccionTarjetaTemplate(SQLiteService sqLiteService){
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public List<TransaccionTarjeta> select(long ini, long fin) {
        long startTime = System.currentTimeMillis(); //todo: reeplazar System.currentTimeMillis() por SystemClock.uptimeMillis()

        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resourse = db.rawQuery("SELECT t.id_ticket, t.timestamp,  ttp.monto " +
                "       FROM Tickets AS t INNER JOIN Ticket_tipos_pago AS ttp ON t.id_ticket = ttp.id_ticket  " +
                "       WHERE ttp.id_tipo_pago = 2 AND t.timestamp BETWEEN "+ini+" AND "+fin, null);

        List<TransaccionTarjeta> transaccionesTarjeta = new ArrayList<>();
        TransaccionTarjeta transaccionTarjeta ;


        while(resourse.moveToNext()){
            transaccionTarjeta = new TransaccionTarjeta();

            transaccionTarjeta.setIdTicket(resourse.getInt(0));
            transaccionTarjeta.setTimestamp(resourse.getLong(1));
            transaccionTarjeta.setMonto(resourse.getDouble(2));

            transaccionesTarjeta.add(transaccionTarjeta);
        }

        resourse.close();
        db.close();

        executionTime =  System.currentTimeMillis() - startTime;

        if (transaccionesTarjeta.size() <= 0){
            return null;
        }

        return transaccionesTarjeta;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
