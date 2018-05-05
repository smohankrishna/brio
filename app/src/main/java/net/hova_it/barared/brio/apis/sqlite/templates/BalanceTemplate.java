package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Balance;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

/**
 * Maneja el metodo Select en Sqlite para la generacion de la informacion de Balance.
 * Created by Mauricio Cerón on 01/03/2016.
 */
public class BalanceTemplate {
    private final String TAG = "BRIO_DB";

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public BalanceTemplate(SQLiteService sqLiteService){
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }


    public Balance select(int caja){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        //TODO: definir el calculo del total
        Cursor resourse = db.rawQuery("SELECT       IFNULL((SELECT importe_real FROM Registro_cierre WHERE id_registro_cierre = (SELECT MAX(id_registro_cierre) FROM Registro_cierre) AND id_caja = 1 ), 0) AS Saldo_inicial,  \n" +
                        "\tIFNULL((SELECT SUM (importe_neto) FROM Tickets WHERE id_caja = "+ caja +"  AND id_tipo_ticket = 2 AND timestamp >= (SELECT timestamp FROM Registro_apertura WHERE  id_registro_apertura = (SELECT MAX(id_registro_apertura) FROM Registro_apertura))), 0) AS Entradas,  \n" +
                        "\tIFNULL((SELECT SUM (importe_neto) FROM Tickets WHERE id_caja = "+ caja +"  AND id_tipo_ticket = 3 AND timestamp >= (SELECT timestamp FROM Registro_apertura WHERE  id_registro_apertura = (SELECT MAX(id_registro_apertura) FROM Registro_apertura))), 0) AS Salidas,  \n" +
                        "\tIFNULL((SELECT SUM (Ttp.monto)  \n" +
                        "\t\tfrom Tickets AS T INNER JOIN Ticket_tipos_pago AS Ttp ON T.id_ticket = Ttp.id_ticket  \n" +
                        "\t\tWHERE TtP.id_tipo_pago = 4 AND id_caja = "+ caja +" AND timestamp >= (SELECT timestamp FROM Registro_apertura WHERE  id_registro_apertura = (SELECT MAX(id_registro_apertura) FROM Registro_apertura))\n" +
                        "\t\tAND T.id_ticket IN (SELECT t.id_ticket AS id_ticket\n" +
                        "\t\t\t\t\t\t\t\t\t\tFROM Tickets AS t INNER JOIN Items_Ticket it ON t.id_ticket = it.id_ticket\n" +
                        "\t\t\t\t\t\t\t\t\t\tWHERE (t.id_tipo_ticket IN (4,5,6,7,8) AND it.descripcion LIKE '%Autoriza%')\n" +
                        "\t\t\t\t\t\t\t\t\t\tOR (t.id_tipo_ticket = 1)\n" +
                        "\t\t\t\t\t\t\t\t\t\tGROUP BY t.id_ticket)) , 0) AS Ventas_Fiado,  \n" +
                        "\tIFNULL((SELECT SUM (Ttp.monto)  \n" +
                        "\t\tfrom Tickets AS T INNER JOIN Ticket_tipos_pago AS Ttp ON T.id_ticket = Ttp.id_ticket  \n" +
                        "\t\tWHERE TtP.id_tipo_pago = 1 AND id_caja = "+ caja +" AND timestamp >= (SELECT timestamp FROM Registro_apertura WHERE  id_registro_apertura = (SELECT MAX(id_registro_apertura) FROM Registro_apertura))\n" +
                        "\t\tAND T.id_ticket IN (SELECT t.id_ticket AS id_ticket\n" +
                        "\t\t\t\t\t\t\t\t\t\tFROM Tickets AS t INNER JOIN Items_Ticket it ON t.id_ticket = it.id_ticket\n" +
                        "\t\t\t\t\t\t\t\t\t\tWHERE (t.id_tipo_ticket IN (4,5,6,7,8) AND it.descripcion LIKE '%Autoriza%')\n" +
                        "\t\t\t\t\t\t\t\t\t\tOR (t.id_tipo_ticket = 1)\n" +
                        "\t\t\t\t\t\t\t\t\t\tGROUP BY t.id_ticket)), 0) AS Ventas_Efectivo,  \n" +
                        "\tIFNULL((SELECT SUM (Ttp.monto)  \n" +
                        "\t\tfrom Tickets AS T INNER JOIN Ticket_tipos_pago AS Ttp ON T.id_ticket = Ttp.id_ticket  \n" +
                        "\t\tWHERE TtP.id_tipo_pago = 2 AND id_caja = "+ caja +" AND timestamp >= (SELECT timestamp FROM Registro_apertura WHERE  id_registro_apertura = (SELECT MAX(id_registro_apertura) FROM Registro_apertura))\n" +
                        "\t\tAND T.id_ticket IN (SELECT t.id_ticket AS id_ticket\n" +
                        "\t\t\t\t\t\t\t\t\t\tFROM Tickets AS t INNER JOIN Items_Ticket it ON t.id_ticket = it.id_ticket\n" +
                        "\t\t\t\t\t\t\t\t\t\tWHERE (t.id_tipo_ticket IN (4,5,6,7,8) AND it.descripcion LIKE '%Autoriza%')\n" +
                        "\t\t\t\t\t\t\t\t\t\tOR (t.id_tipo_ticket = 1)\n" +
                        "\t\t\t\t\t\t\t\t\t\tGROUP BY t.id_ticket)), 0) AS Ventas_Tarjeta,  \n" +
                        "\tIFNULL((SELECT SUM (Ttp.monto)  \n" +
                        "\t\tfrom Tickets AS T INNER JOIN Ticket_tipos_pago AS Ttp ON T.id_ticket = Ttp.id_ticket  \n" +
                        "\t\tWHERE TtP.id_tipo_pago = 3 AND id_caja = "+ caja +" AND timestamp >= (SELECT timestamp FROM Registro_apertura WHERE  id_registro_apertura = (SELECT MAX(id_registro_apertura) FROM Registro_apertura))\n" +
                        "\t\tAND T.id_ticket IN (SELECT t.id_ticket AS id_ticket\n" +
                        "\t\t\t\t\t\t\t\t\t\tFROM Tickets AS t INNER JOIN Items_Ticket it ON t.id_ticket = it.id_ticket\n" +
                        "\t\t\t\t\t\t\t\t\t\tWHERE (t.id_tipo_ticket IN (4,5,6,7,8) AND it.descripcion LIKE '%Autoriza%')\n" +
                        "\t\t\t\t\t\t\t\t\t\tOR (t.id_tipo_ticket = 1)\n" +
                        "\t\t\t\t\t\t\t\t\t\tGROUP BY t.id_ticket)), 0) AS Ventas_Vales",
                null);

        Balance balance = new Balance();
        if(resourse != null){Log.w(TAG, "Balance resourse no null");}
        while(resourse.moveToNext()){

            balance.setSaldoInicial(resourse.getDouble(0));
            balance.setEntradas(resourse.getDouble(1));
            balance.setSalidas(resourse.getDouble(2));
            balance.setVentasFiado(resourse.getDouble(3));
            balance.setVentasEfectivo(resourse.getDouble(4));
            balance.setVentasTarjeta(resourse.getDouble(5));
            balance.setVentasVales(resourse.getDouble(6));
        }

        balance.calcularTotal();
        resourse.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        return balance;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}