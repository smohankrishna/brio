package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.hova_it.barared.brio.apis.models.views.ImpuestosDeArticulo;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja el metodo Select en Sqlite para extraer la informacion de impuestos por articulo .
 * Created by Mauricio Cerón on 06/01/2016.
 */
public class ImpuestosDeArticuloTemplate {
    private final String TAG = "BRIO_DB";

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;


    public ImpuestosDeArticuloTemplate(SQLiteService sqLiteService){
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public List<ImpuestosDeArticulo> select(int idArticulo){
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resourse = db.rawQuery("SELECT I.nombre, I.impuesto, I.prioridad" +
                " FROM (Articulos AS A INNER JOIN Impuestos_por_Articulo AS IpA ON A.id_articulo = IpA.id_articulo) AS AI INNER JOIN Impuestos AS I ON AI.id_impuesto = I.id_impuestos " +
                "WHERE A.id_articulo= " + idArticulo + " ORDER BY I.prioridad", null);

        List<ImpuestosDeArticulo> impuestosDeArticulo = new ArrayList<>();
        ImpuestosDeArticulo impuestoDeArticulo;

        while(resourse.moveToNext()){
            impuestoDeArticulo = new ImpuestosDeArticulo();

            impuestoDeArticulo.setNombre(resourse.getString(0));
            impuestoDeArticulo.setImpuesto(resourse.getDouble(1));
            impuestoDeArticulo.setPrioridad(resourse.getInt(2));

            impuestosDeArticulo.add(impuestoDeArticulo);
        }

        if (impuestosDeArticulo.size() <= 0){
            return null;
        }

        return impuestosDeArticulo;
    }

}
