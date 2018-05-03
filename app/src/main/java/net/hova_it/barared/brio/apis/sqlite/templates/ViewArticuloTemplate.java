package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.hova_it.barared.brio.apis.models.views.ViewArticulo;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja el metodo Select en Sqlite para el modelo View Articulo.
 * Created by Mauricio Cerón on 18/12/2015.
 */
public class ViewArticuloTemplate {
    private final String TAG = "BRIO_DB";

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;


    public ViewArticuloTemplate(SQLiteService sqLiteService){
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public List<ViewArticulo> select(String where, int tipoConsulta) {
        long startTime = System.currentTimeMillis(); //todo: reeplazar System.currentTimeMillis() por SystemClock.uptimeMillis()

        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resourse = db.rawQuery("", null);  //FIXME

        switch (tipoConsulta) {

            case 1:
                resourse = db.rawQuery("SELECT * FROM ViewArticulo " + where, null);
                break;

            case 2:
                resourse = db.rawQuery("SELECT * , SUM(IT.cantidad) AS Vendidos " +
                                        "FROM ViewArticulo AS V LEFT JOIN Items_Ticket AS IT ON  V.id_articulo = IT.id_articulo " +
                                        "WHERE granel = 1 " +
                                        "GROUP BY V.id_articulo " +
                                        "ORDER BY Vendidos DESC, V.nombre ASC", null);
                break;

            case 3:
                resourse = db.rawQuery("SELECT * , SUM(IT.cantidad) AS Vendidos " +
                                        "FROM ViewArticulo AS V LEFT JOIN Items_Ticket AS IT ON  V.id_articulo = IT.id_articulo " +
                                        "GROUP BY V.id_articulo " +
                                        "ORDER BY Vendidos DESC, V.nombre ASC", null);
                break;


        }

        List<ViewArticulo> viewArticulos = new ArrayList<>();
        ViewArticulo viewArticulo ;


        while(resourse.moveToNext()){
            viewArticulo = new ViewArticulo();

            viewArticulo.setIdArticulo(resourse.getInt(0));
            viewArticulo.setIdCentral(resourse.getInt(1));
            viewArticulo.setPrecioBase(resourse.getDouble(2));
            viewArticulo.setPrecioCompra(resourse.getDouble(3));
            viewArticulo.setCodigoBarras(resourse.getString(4));
            viewArticulo.setNombreArticulo(resourse.getString(5));
            viewArticulo.setNombreMarca(resourse.getString(6));
            viewArticulo.setPresentacion(resourse.getString(7));
            viewArticulo.setContenido(resourse.getInt(8));
            viewArticulo.setUnidad(resourse.getString(9));
            viewArticulo.setGranel(resourse.getInt(10) != 0);

            viewArticulos.add(viewArticulo);
        }

        resourse.close();
        db.close();

        executionTime =  System.currentTimeMillis() - startTime;

        if (viewArticulos.size() <= 0){
            return null;
        }

        return viewArticulos;
    }

    public long getExecutionTime() {
        return executionTime;
    }

}

