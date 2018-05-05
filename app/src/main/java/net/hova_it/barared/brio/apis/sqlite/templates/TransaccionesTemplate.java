package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.hova_it.barared.brio.apis.models.entities.Transacciones;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

/**
 * Maneja el metodo Select en Sqlite para extraer la informacion correspondiente a la venta de servicios.
 * Servicios, TAE, Internet, Pagos con tarjeta y movimientos Bancarios.
 * Created by Mauricio Cerón on 25/04/2016.
 */
public class TransaccionesTemplate {

    private final String TAG = "BRIO_DB";

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;


    public TransaccionesTemplate(SQLiteService sqLiteService){
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }


    public Transacciones select(long fInicio, long fFin){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        //TODO: definir el calculo del total, manejo de
        Cursor resourse1 = db.rawQuery("SELECT venta_cantidad, venta_total,SUM(venta_cantidad * 7) AS Comision , (SUM(venta_cantidad * 7) * 0.7) AS Ganancia " +
                "                FROM  " +
                "                 (SELECT IFNULL(COUNT(*) ,0) AS venta_cantidad, IFNULL(SUM(t.importe_neto),0) AS venta_total  " +
                "                                 FROM Tickets AS t  INNER JOIN Items_Ticket AS it  ON  t.id_ticket = it.id_ticket " +
                "                                 WHERE t.id_tipo_ticket = 4 AND t.timestamp BETWEEN  "+ fInicio + " AND " + fFin +
                "                                 AND it.descripcion LIKE '%Autoriza%' ) AS Cantidad"
                ,null);

        Cursor resourse2 = db.rawQuery("SELECT IFNULL(COUNT(*) ,0) AS TAE_cantidad, IFNULL(SUM(t.importe_neto),0) AS TAE_total  " +
                " FROM items_ticket AS it INNER JOIN tickets AS t ON it.id_ticket = t.id_ticket " +
                " WHERE it.descripcion LIKE '%Autoriza%' AND t.id_tipo_ticket = 5 AND t.timestamp BETWEEN "+ fInicio + " AND " + fFin
                ,null);

        Cursor resourse3 = db.rawQuery("SELECT IFNULL(COUNT(*) ,0) AS internet_cantidad, IFNULL(SUM(t.importe_neto),0) AS internet_total " +
                " FROM items_ticket AS it INNER JOIN tickets AS t ON it.id_ticket = t.id_ticket " +
                " WHERE it.descripcion LIKE '%Autoriza%' AND t.id_tipo_ticket = 6 AND t.timestamp BETWEEN "+ fInicio +" AND "+ fFin
                ,null);

        Cursor resourse4 = db.rawQuery("SELECT IFNULL(COUNT(*) ,0) AS Cantidad_pagos_tarjeta , IFNULL(SUM (Ttp.monto),0) AS Monto_pagos_tarjeta " +
                                        "from Tickets AS T INNER JOIN Ticket_tipos_pago AS Ttp ON T.id_ticket = Ttp.id_ticket  " +
                                        "WHERE TtP.id_tipo_pago = 2 AND timestamp BETWEEN "+ fInicio +" AND "+ fFin,null);

        Cursor resourse5 = db.rawQuery("SELECT COUNT (*), SUM(importe_neto),SUM(Comision),SUM(Ganancia) " +
                "   FROM " +
                "   (SELECT t.id_ticket ,importe_neto, 15.00 AS Comision, 10.50 AS Ganancia , it.descripcion AS descrip, t.descripcion AS desct " +
                "   FROM items_ticket AS it INNER JOIN tickets AS t ON it.id_ticket = t.id_ticket " +
                "   WHERE codigo_barras LIKE 'VENT%' AND t.id_tipo_ticket IN(7,8) " +
                "   AND it.timestamp BETWEEN "+ fInicio +" AND "+ fFin+
                "   GROUP BY t.id_ticket " +
                "   UNION " +
                "   SELECT t.id_ticket ,importe_neto, 7.00 AS Comision, 4.90 AS Ganancia, it.descripcion AS descrip ,  t.descripcion AS desct " +
                "   FROM items_ticket AS it INNER JOIN tickets AS t ON it.id_ticket = t.id_ticket  " +
                "   WHERE codigo_barras LIKE 'RECA%' AND t.id_tipo_ticket IN(7,8) " +
                "   AND it.timestamp BETWEEN "+ fInicio +" AND "+ fFin+
                "   GROUP BY t.id_ticket " +
                "   UNION " +
                "   SELECT t.id_ticket,importe_neto, 15.00 AS Comision, 10.50 AS Ganancia, it.descripcion AS descrip ,  t.descripcion AS desct " +
                "   FROM items_ticket AS it INNER JOIN tickets AS t ON it.id_ticket = t.id_ticket  " +
                "   WHERE codigo_barras LIKE 'REPO%' AND t.id_tipo_ticket IN(7,8) " +
                "   AND it.timestamp BETWEEN "+ fInicio +" AND "+ fFin+
                "   GROUP BY t.id_ticket) " +
                "   WHERE desct LIKE 'Trans.:__%'", null);


        Transacciones transacciones = new Transacciones();

        while(resourse1.moveToNext()){

            transacciones.setServicioC(resourse1.getInt(0));
            transacciones.setServicioT(resourse1.getDouble(1));
            transacciones.setServicioComision(resourse1.getDouble(2));
            transacciones.setServicioGanancia(resourse1.getDouble(3));

        }

        resourse1.close();

        while(resourse2.moveToNext()) {

            transacciones.setTaeC(resourse2.getInt(0));
            transacciones.setTaeT(resourse2.getDouble(1));

        }

        resourse2.close();

        while(resourse3.moveToNext()) {

            transacciones.setInternetC(resourse3.getInt(0));
            transacciones.setInternetT(resourse3.getDouble(1));

        }

        resourse3.close();

        while(resourse4.moveToNext()){

            transacciones.setCantidadVentasTarjeta(resourse4.getInt(0));
            transacciones.setTotalVentasTarjeta(resourse4.getDouble(1));
            transacciones.setComisionesTarjeta(0);//TODO:comisiones

        }

        resourse4.close();

        while(resourse5.moveToNext()){

            transacciones.setBancoC(resourse5.getInt(0));
            transacciones.setBancoT(resourse5.getDouble(1));
            transacciones.setBancoComision(resourse5.getDouble(2));
            transacciones.setBancoGanancia(resourse5.getDouble(3));

        }

        resourse5.close();


        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        return transacciones;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}


