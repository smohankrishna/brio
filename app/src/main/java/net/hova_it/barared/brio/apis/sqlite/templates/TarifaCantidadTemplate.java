package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.hova_it.barared.brio.apis.models.views.TarifaCantidad;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Tarifa Cantidad.
 * Created by Mauricio Cerón on 18/12/2015.
 */
public class TarifaCantidadTemplate {
    private final String TAG = "BRIO_DB";

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;


    public TarifaCantidadTemplate(SQLiteService sqLiteService){
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public double getPrecioBase(int id){

        SQLiteDatabase db = sqLiteService.getWritableDatabase();
        Cursor resource = db.rawQuery("select id_articulo, min(id_tarifa), importe_unitario " +
                "from Tarifa_articulo " +
                "where id_articulo= " + id +
                " limit 1 ", null);
            double p =0f;
        if(resource.moveToFirst()){
            p=(resource.getDouble(2));
        }
        resource.close();
        db.close();
        return p;
    }

    public List<TarifaCantidad> select(int idArticulo){
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();

        /*Cursor resourse = db.rawQuery("SELECT C.cantidad_inferior, C.cantidad_superior, C.precio_unitario "+
                "FROM Articulos AS A "+
                "INNER JOIN Tarifa_articulo AS T  ON A.id_articulo = T.id_articulo "+
                "INNER JOIN Tarifa_articulo_cantidad AS C ON C.id_tarifa_articulo = T.id_tarifa_articulo "+
                "WHERE A.id_articulo= " + idArticulo + " ORDER BY C.cantidad_inferior ", null);*/
        Cursor resource = db.rawQuery("SELECT C.cantidad_mayoreo,C.precio_unitario "+
                "FROM Articulos AS A "+
                "INNER JOIN Tarifa_articulo AS T  ON A.id_articulo = T.id_articulo "+
                "INNER JOIN Tarifa_articulo_cantidad AS C ON C.id_tarifa_articulo = T.id_tarifa_articulo "+
                "WHERE A.id_articulo= " + idArticulo + " ORDER BY C.cantidad_mayoreo ", null);

        List<TarifaCantidad> tarifasCantidad = new ArrayList<>();
        TarifaCantidad tarifaCantidad;

        while(resource.moveToNext()){
            tarifaCantidad = new TarifaCantidad();

            tarifaCantidad.setCantidadMayoreo(resource.getInt(0));
            tarifaCantidad.setPrecio(resource.getDouble(1));
			
            tarifasCantidad.add(tarifaCantidad);
        }
        resource.close();
        db.close();

        if (tarifasCantidad.size() <= 0){
            return null;
        }

        return tarifasCantidad;
    }

}
