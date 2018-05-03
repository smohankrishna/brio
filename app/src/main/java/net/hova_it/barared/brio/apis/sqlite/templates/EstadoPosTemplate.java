package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.EstadoPos;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

/**
 * Maneja el metodo Select en Sqlite para extraer la informacion que indica el estado de POS.
 * Created by Mauricio Cerón on 21/04/2016.
 */
public class EstadoPosTemplate {
    private final String TAG = "BRIO_DB";

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public EstadoPosTemplate(SQLiteService sqLiteService){
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }


    public EstadoPos select(long fInicio, long fFin){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        //TODO: definir el calculo del total
        Cursor resourse = db.rawQuery("SELECT *" +
                " FROM " +
                " (SELECT IFNULL(COUNT(*) ,0) AS venta_cantidad, IFNULL(SUM(importe_neto),0) AS venta_total FROM tickets WHERE id_tipo_ticket = 1 " +
                "   AND timestamp BETWEEN " + fInicio + " AND " + fFin +"), " +
                " (SELECT IFNULL(SUM(Productos.Venta - (VA.precio_compra * Productos.Vendidos)),0) AS ventas_ganancia  " +
                "    FROM ViewArticulo AS VA, " +
                "    (Select id_articulo,sum(importe_total) AS Venta, sum(cantidad) AS Vendidos  " +
                "    from items_ticket  " +
                "    WHERE timestamp BETWEEN " + fInicio + " AND " + fFin +
                "    group by  id_articulo  " +
                "    order by Venta DESC) AS Productos  " +
                "    WHERE VA.id_articulo = Productos.id_articulo) AS Ganancia2,  " +
                " (SELECT IFNULL(COUNT (*),0) AS entradas_cantidad, IFNULL(SUM (importe_neto),0) AS entradas_total  " +
                "    FROM Tickets  " +
                "    WHERE id_tipo_ticket = 2 AND timestamp BETWEEN " + fInicio + " AND " + fFin +" ) AS Entradas,  " +
                " (SELECT IFNULL(COUNT (*),0) AS salidas_cantidad, IFNULL(SUM (importe_neto),0) AS salidas_total   " +
                "    FROM Tickets   " +
                "    WHERE id_tipo_ticket = 3 AND timestamp BETWEEN " + fInicio + " AND " + fFin +" ) AS Salidas"
                ,null);

        EstadoPos estadoPos = new EstadoPos();
        if(resourse != null){
            Log.w(TAG, "EstadoPos resourse no null");}
        while(resourse.moveToNext()){

            estadoPos.setCantidadV(resourse.getInt(0));
            estadoPos.setTotalV(resourse.getDouble(1));
            estadoPos.setGananciaV(resourse.getDouble(2));
            estadoPos.setCantidadE(resourse.getInt(3));
            estadoPos.setTotalE(resourse.getDouble(4));
            estadoPos.setGananciaE(0.00);
            estadoPos.setCantidadS(resourse.getInt(5));
            estadoPos.setTotalS(resourse.getDouble(6));
            estadoPos.setGananciaS(0.00);
        }

        resourse.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        return estadoPos;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
