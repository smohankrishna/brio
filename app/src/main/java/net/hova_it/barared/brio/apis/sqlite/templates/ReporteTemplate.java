package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Reporte;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja el metodo Select en Sqlite para la extraccion de la informacion del reporte que se solicite.
 * Created by Mauricio Cerón on 18/04/2016.
 */
public class ReporteTemplate {
    private final String TAG = "BRIO_DB";

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;


    public ReporteTemplate(SQLiteService sqLiteService){
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public List<Reporte> selectById(Long fechaInicio, Long fechaFin , int idArticulo) {
        long startTime = System.currentTimeMillis(); //todo: reeplazar System.currentTimeMillis() por SystemClock.uptimeMillis()

        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();

        Cursor resourse = db.rawQuery("SELECT * , (Productos.Venta - (VA.precio_compra * Productos.Vendidos)) AS Ganancia " +
                " FROM ViewArticulo AS VA, " +
                " (Select id_articulo,sum(importe_total) AS Venta,sum(cantidad) AS Vendidos " +
                " from items_ticket " +
                " WHERE timestamp BETWEEN "+ fechaInicio +" AND "+ fechaFin +
                " group by  id_articulo " +
                " order by Vendidos DESC ) AS Productos " +
                " WHERE VA.id_articulo = " + idArticulo , null);

        List<Reporte> reportes = new ArrayList<>();
        Reporte reporte ;


        while(resourse.moveToNext()){
            reporte = new Reporte();

            reporte.setIdArticulo(resourse.getInt(0));
            reporte.setIdCentral(resourse.getInt(1));
            reporte.setPrecioBase(resourse.getDouble(2));
            reporte.setPrecioCompra(resourse.getDouble(3));
            reporte.setCodigoBarras(resourse.getString(4));
            reporte.setNombreArticulo(resourse.getString(5));
            reporte.setNombreMarca(resourse.getString(6));
            reporte.setPresentacion(resourse.getString(7));
            reporte.setContenido(resourse.getInt(8));
            reporte.setUnidad(resourse.getString(9));
            reporte.setGranel(resourse.getInt(10) != 0);
            reporte.setVenta(resourse.getDouble(12));
            reporte.setVendidos(resourse.getDouble(13));
            reporte.setGanancia(resourse.getDouble(14));
            Log.w("REPORTE", Utils.pojoToString(reporte));
            reportes.add(reporte);
        }

        resourse.close();
        db.close();

        executionTime =  System.currentTimeMillis() - startTime;

        if (reportes.size() <= 0){
            return null;
        }

        return reportes;
    }


    public List<Reporte> select(Long fechaInicio, Long fechaFin , int tipoReporte) {
        /*TipoReporte:
                    1= Los 10 mas vendidos
                    2= Los 10 menos vendidos
                    3= ganancias
         */
        long startTime = System.currentTimeMillis(); //todo: reeplazar System.currentTimeMillis() por SystemClock.uptimeMillis()

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        Cursor resourse = db.rawQuery("",null);  // FIXME

        switch(tipoReporte){

            case 1:
                Log.w("Reporte","Los mas vendidos");
                resourse = db.rawQuery("SELECT * , (Productos.Venta - (VA.precio_compra * Productos.Vendidos)) AS Ganancia " +
                        " FROM ViewArticulo AS VA, " +
                        " (Select id_articulo,sum(importe_total) AS Venta,sum(cantidad) AS Vendidos " +
                        " from items_ticket " +
                        " WHERE timestamp BETWEEN "+ fechaInicio +" AND "+ fechaFin +
                        " group by  id_articulo " +
                        " order by Vendidos DESC LIMIT 0,10) AS Productos " +
                        "WHERE VA.id_articulo = Productos.id_articulo", null);
                break;

            case 2:
                Log.w("Reporte","Los menos vendidos");
                resourse = db.rawQuery("SELECT * , (Productos.Venta - (VA.precio_compra * Productos.Vendidos)) AS Ganancia " +
                        " FROM ViewArticulo AS VA, " +
                        " (Select id_articulo,sum(importe_total) AS Venta,sum(cantidad) AS Vendidos " +
                        " from items_ticket " +
                        " WHERE timestamp BETWEEN "+ fechaInicio +" AND "+ fechaFin +
                        " group by  id_articulo " +
                        " order by Vendidos ASC LIMIT 0,10) AS Productos " +
                        "WHERE VA.id_articulo = Productos.id_articulo", null);
                break;

            case 3:

                Log.w("Reporte","Ganancias");

                resourse = db.rawQuery("SELECT *    " +
                        "   FROM ViewArticulo AS VA,    " +
                        "   (SELECT it.id_articulo, SUM(importe_total) AS Venta,SUM(cantidad) AS Vendidos ,SUM((it.importe_total - (it.cantidad * it.precio_compra))) AS Ganancia, it.precio_compra AS PrecioCompra   " +
                        "   FROM Items_Ticket AS it INNER JOIN Tickets AS t ON it.id_ticket = t.id_ticket   " +
                        "   WHERE t.id_tipo_ticket = 1 AND t.timestamp BETWEEN  "+ fechaInicio +" AND "+ fechaFin +
                        "   GROUP BY it.id_articulo , it.precio_compra" +
                        "   ORDER BY Vendidos DESC ) AS Calculo " +
                        "   WHERE VA.id_articulo = Calculo.id_articulo",null);
                /*/////////////////////////////Old reporte ganancias/////////////////////////////////
                resourse = db.rawQuery("SELECT * , (Productos.Venta - (VA.precio_compra * Productos.Vendidos)) AS Ganancia " +
                        " FROM ViewArticulo AS VA, " +
                        " (Select id_articulo,sum(importe_total) AS Venta,sum(cantidad) AS Vendidos " +
                        " from items_ticket " +
                        " WHERE timestamp BETWEEN "+ fechaInicio +" AND "+ fechaFin +
                        " group by  id_articulo " +
                        " order by Vendidos DESC ) AS Productos " +
                        "WHERE VA.id_articulo = Productos.id_articulo", null);
                *////////////////////////////////////////////////////////////////////////////////////
                break;

            case 4:
                Log.w("Reporte","Garnel top mas vendidos");
                resourse = db.rawQuery("SELECT * , (Productos.Venta - (VA.precio_compra * Productos.Vendidos)) AS Ganancia " +
                        " FROM ViewArticulo AS VA, " +
                        " (Select id_articulo,sum(importe_total) AS Venta,sum(cantidad) AS Vendidos " +
                        " from items_ticket " +
                        " group by  id_articulo " +
                        " order by Vendidos ASC ) AS Productos " +
                        " WHERE VA.id_articulo = Productos.id_articulo" +
                        " AND granel = 1", null);
                break;

            case 5:
                Log.w("Reporte", "Cash Closing");
                final String sql = "Select * From\n" +
                        "\n" +
                        "                (Select  strftime('%d-%m-%Y', datetime(a.timestamp, 'unixepoch', 'localtime')) As Fecha,\n" +
                        "\n" +
                        "                a.id_usuario, b.nombre As Nombre_Usuario, 'Saldo Inicial' As Concepto, a.importe_real As Cantidad, strftime('%H:%M:%S', a.fecha_apertura) As Hora, 1 As Orden\n" +
                        "\n" +
                        "                From Registro_apertura a\n" +
                        "\n" +
                        "                Left Join Usuarios b On a.id_usuario = b.id_usuario\n" +
                        "\n" +
                        "                LIMIT 1) a\n" +
                        "\n" +
                        "Union\n" +
                        "\n" +
                        "Select * From\n" +
                        "\n" +
                        "                (Select strftime('%d-%m-%Y', datetime(a.timestamp, 'unixepoch', 'localtime')) As Fecha, a.id_usuario, b.nombre As Nombre_Usuario, 'Entradas de dinero' As Concepto,\n" +
                        "\n" +
                        "                a.importe_neto As Cantidad, 'NA' As Hora, 2 As Orden\n" +
                        "\n" +
                        "                From Tickets a\n" +
                        "\n" +
                        "                Left Join Usuarios b On a.id_usuario = b.id_usuario\n" +
                        "\n" +
                        "                LIMIT 1) b\n" +
                        "\n" +
                        "Union  \n" +
                        "\n" +
                        "Select * From\n" +
                        "\n" +
                        "                (Select strftime('%d-%m-%Y', datetime(a.timestamp, 'unixepoch', 'localtime')) As Fecha, a.id_usuario, b.nombre As Nombre_Usuario, 'Salidas de dinero' As Concepto,\n" +
                        "\n" +
                        "                a.importe_neto As Cantidad, 'NA' As Hora, 3 As Orden\n" +
                        "\n" +
                        "                From Tickets a\n" +
                        "\n" +
                        "                Left Join Usuarios b On a.id_usuario = b.id_usuario\n" +
                        "\n" +
                        "                LIMIT 1) c\n" +
                        "\n" +
                        "Union  \n" +
                        "\n" +
                        "Select * From\n" +
                        "\n" +
                        "                (Select strftime('%d-%m-%Y', datetime(a.timestamp, 'unixepoch', 'localtime')) As Fecha, a.id_usuario, b.nombre As Nombre_Usuario, 'Ventas en efectivo' As Concepto,\n" +
                        "\n" +
                        "                a.importe_neto As Cantidad, 'NA' As Hora, 4 As Orden\n" +
                        "\n" +
                        "                From Tickets a\n" +
                        "\n" +
                        "                Left Join Usuarios b On a.id_usuario = b.id_usuario\n" +
                        "\n" +
                        "                LIMIT 1) d          \n" +
                        "\n" +
                        "Union\n" +
                        "\n" +
                        "Select * From\n" +
                        "\n" +
                        "                (Select  strftime('%d-%m-%Y', datetime(a.fecha_cierre, 'unixepoch', 'localtime')) As Fecha,\n" +
                        "\n" +
                        "                a.id_usuario, b.nombre As Nombre_Usuario, 'Total en caja al cierre' As Concepto, 10.0 As Cantidad, '06:00 PM' As Hora, 5 As Orden\n" +
                        "\n" +
                        "                From Registro_cierre a\n" +
                        "\n" +
                        "                Left Join Usuarios b On a.id_usuario = b.id_usuario\n" +
                        "\n" +
                        "                LIMIT 1) e\n" +
                        "\n" +
                        "Order By Orden asc";

                resourse = db.rawQuery(sql, null);

        }

        List<Reporte> reportes = new ArrayList<>();
        Reporte reporte ;

        if(tipoReporte == 5) {
            while(resourse.moveToNext()){
                reporte = new Reporte();
                reporte.setFecha(resourse.getString(0));
                reporte.setIdUsuario(resourse.getInt(1));
                reporte.setNombreUsuario(resourse.getString(2));
                reporte.setConcepto(resourse.getString(3));
                reporte.setCantidad(resourse.getString(4));
                reporte.setHora(resourse.getString(5));
                reporte.setOrden(resourse.getInt(6));
                Log.w("REPORTE", Utils.pojoToString(reporte));
                reportes.add(reporte);
            }

        } else {
            while(resourse.moveToNext()){
                reporte = new Reporte();

                reporte.setIdArticulo(resourse.getInt(0));
                reporte.setIdCentral(resourse.getInt(1));
                reporte.setPrecioBase(resourse.getDouble(2));
                if(tipoReporte != 3) {
                    reporte.setPrecioCompra(resourse.getDouble(3));
                }else{
                    reporte.setPrecioCompra(resourse.getDouble(15));
                }
                reporte.setCodigoBarras(resourse.getString(4));
                reporte.setNombreArticulo(resourse.getString(5));
                reporte.setNombreMarca(resourse.getString(6));
                reporte.setPresentacion(resourse.getString(7));
                reporte.setContenido(resourse.getInt(8));
                reporte.setUnidad(resourse.getString(9));
                reporte.setGranel(resourse.getInt(10) != 0);
                reporte.setVenta(resourse.getDouble(12));
                reporte.setVendidos(resourse.getDouble(13));
                reporte.setGanancia(resourse.getDouble(14));
                Log.w("REPORTE", Utils.pojoToString(reporte));
                reportes.add(reporte);
            }

        }

        resourse.close();
        db.close();

        executionTime =  System.currentTimeMillis() - startTime;

        if (reportes.size() <= 0){
            return null;
        }

        return reportes;
    }

    public long getExecutionTime() {
        return executionTime;
    }

}
