package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.hova_it.barared.brio.apis.models.views.ViewInventario;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo View Inventario.
 * Created by Mauricio Cerón on 04/02/2016.
 */
public class ViewInventarioTemplate {
    private final String TAG = "BRIO_DB";

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;


    public ViewInventarioTemplate(SQLiteService sqLiteService){
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public List<ViewInventario> select(String where) {
        long startTime = System.currentTimeMillis(); //todo: reeplazar System.currentTimeMillis() por SystemClock.uptimeMillis()

        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resourse = db.rawQuery("SELECT * FROM ViewInventario "+ where, null);

        List<ViewInventario> viewInventarios = new ArrayList<>();
        ViewInventario viewInventario ;


        while(resourse.moveToNext()){
            viewInventario = new ViewInventario();

            viewInventario.setIdArticulo(resourse.getInt(0));
            viewInventario.setIdInventario(resourse.getInt(1));
            viewInventario.setNombreArticulo(resourse.getString(2));
            viewInventario.setNombreMarca(resourse.getString(3));
            viewInventario.setContenido(resourse.getDouble(4));
            viewInventario.setUnidad(resourse.getString(5));
            viewInventario.setPresentacion(resourse.getString(6));
            viewInventario.setCodigoBarras(resourse.getString(7));
            viewInventario.setPrecioCompra(resourse.getDouble(8));
            viewInventario.setPrecioVenta(resourse.getDouble(9));
            viewInventario.setExistencias(resourse.getDouble(10));

            viewInventarios.add(viewInventario);
        }

        resourse.close();
        //db.close();  // FIXME

        executionTime =  System.currentTimeMillis() - startTime;

        if (viewInventarios.size() <= 0){
            return null;
        }

        return viewInventarios;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
