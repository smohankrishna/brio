package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Articulo;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Articulo.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class ArticuloTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Articulos"; // TODO
    private final String key = "id_articulo"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public ArticuloTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(Articulo articulo){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = articulo.getIdArticulo();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoArticulos());

        stmt.bindLong(1, _id);
        stmt.bindLong(2, articulo.getIdCentral());
        stmt.bindString(3, articulo.getCodigoBarras());
        stmt.bindString(4, articulo.getNombre());
        stmt.bindLong(5, articulo.getIdMarca());
        stmt.bindLong(6, articulo.getIdPresentacion());
        stmt.bindLong(7, articulo.getIdUnidad());
        stmt.bindDouble(8, articulo.getContenido());
        stmt.bindDouble(9, articulo.getPrecioVenta());
        stmt.bindDouble(10, articulo.getPrecioCompra());
        stmt.bindLong(11, articulo.getIdFamilia());
        stmt.bindLong(12, articulo.getGranel()?1:0);
        stmt.bindString(13, ""/*articulo.getImagen()*/);
        stmt.bindLong(14, articulo.getIdUsuario());
        stmt.bindLong(15, articulo.getFechaBaja());

        Long rowId;

        /* SYNC */ // TODO
        String syncWhere = "WHERE " + key + "=" + _id;
        String syncQry = "SELECT * FROM " + TABLE + " " + syncWhere;

        if(isNew){
            rowId = stmt.executeInsert();
            Log.d(TAG, "executeInsert(): id -> " + rowId);
            //todo: descomentar
            addToSync("INSERT", syncQry, TABLE, syncWhere); // TODO
        }else{
            rowId = ((long)stmt.executeUpdateDelete() > 0)? _id : 0l;
            Log.d(TAG, "executeUpdateDelete(): id -> " + rowId);
            //addToSync("UPDATE", syncQry, TABLE, syncWhere); // TODO
        }

        stmt.close();
        db.close();
        return rowId;

    }

    public List<Articulo> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//WritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<Articulo> articulos = new ArrayList<>();
        Articulo articulo;

        while(resource.moveToNext()){
            articulo = new Articulo();

            articulo.setIdArticulo(resource.getInt(0));
            articulo.setIdCentral(resource.getInt(1));
            articulo.setCodigoBarras(resource.getString(2));
            articulo.setNombre(resource.getString(3));
            articulo.setIdMarca(resource.getInt(4));
            articulo.setIdPresentacion(resource.getInt(5));
            articulo.setIdUnidad(resource.getInt(6));
            articulo.setContenido(resource.getDouble(7));
            articulo.setPrecioVenta(resource.getDouble(8));
            articulo.setPrecioCompra(resource.getDouble(9));
            articulo.setIdFamilia(resource.getInt(10));
            articulo.setGranel(resource.getInt(11)!=0);
            articulo.setImagen(resource.getString(12));
            articulo.setTimestamp(resource.getLong(13));
            articulo.setIdUsuario(resource.getInt(14));
            articulo.setFechaBaja(resource.getInt(15));

            articulos.add(articulo);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        resource.close();
        db.close();

        if (articulos.size() <= 0){
            return null;
        }

        return articulos;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
