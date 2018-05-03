package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Comercio;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Comercio.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class ComercioTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Comercio"; // TODO
    private final String key = "id_comercio"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public ComercioTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(Comercio comercio){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = comercio.getIdComercio();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoComercio());

        stmt.bindLong(1, _id);
        stmt.bindString(2, comercio.getDescComercio());
        stmt.bindLong(3, comercio.getIdGrupo());
        stmt.bindString(4, comercio.getNombreLegal());
        stmt.bindString(5, comercio.getRfc());
        stmt.bindString(6, comercio.getDireccionLegal());
        stmt.bindString(7, comercio.getNumeroExteriorLegal());
        stmt.bindString(8, comercio.getNumeroInteriorLegal());
        stmt.bindString(9, comercio.getColoniaLegal());
        stmt.bindString(10, comercio.getMunicipioLegal());
        stmt.bindString(11, comercio.getEstadoLegal());
        stmt.bindString(12, comercio.getCodigoPostalLegal());
        stmt.bindString(13, comercio.getPaisLegal());
        stmt.bindString(14, comercio.getDireccionFisica());
        stmt.bindString(15, comercio.getNumeroExteriorFisica());
        stmt.bindString(16, comercio.getNumeroInteriorFisica());
        stmt.bindString(17, comercio.getColoniaFisica());
        stmt.bindString(18, comercio.getMunicipioFisica());
        stmt.bindString(19, comercio.getEstadoFisica());
        stmt.bindString(20, comercio.getCodigoPostalFisica());
        stmt.bindString(21, comercio.getPaisFisica());

        Long rowId;

        /* SYNC */ // TODO
        String syncWhere = "WHERE " + key + "=" + _id;
        String syncQry = "SELECT * FROM " + TABLE + " " + syncWhere;

        if(isNew){
            rowId = stmt.executeInsert();
            Log.d(TAG, "executeInsert(): id -> " + rowId);
            //addToSync("INSERT", syncQry, TABLE, syncWhere); // TODO IF IN SYNC
        }else{
            rowId = ((long)stmt.executeUpdateDelete() > 0)? _id : 0l;
            Log.d(TAG, "executeUpdateDelete(): id -> " + rowId);
            //addToSync("UPDATE", syncQry, TABLE, syncWhere); // TODO IF IN SYNC
        }

        stmt.close();
        db.close();
        return rowId;
    }

    public List<Comercio> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<Comercio> comercios = new ArrayList<>();
        Comercio comercio;

        while(resource.moveToNext()){
            comercio = new Comercio();

            comercio.setIdComercio(resource.getInt(0));
            comercio.setDescComercio(resource.getString(1));
            comercio.setIdGrupo(resource.getInt(2));
            comercio.setNombreLegal(resource.getString(3));
            comercio.setRfc(resource.getString(4));
            comercio.setDireccionLegal(resource.getString(5));
            comercio.setNumeroExteriorLegal(resource.getString(6));
            comercio.setNumeroInteriorLegal(resource.getString(7));
            comercio.setColoniaLegal(resource.getString(8));
            comercio.setMunicipioLegal(resource.getString(9));
            comercio.setEstadoLegal(resource.getString(10));
            comercio.setCodigoPostalLegal(resource.getString(11));
            comercio.setPaisLegal(resource.getString(12));
            comercio.setDireccionFisica(resource.getString(13));
            comercio.setNumeroExteriorFisica(resource.getString(14));
            comercio.setNumeroInteriorFisica(resource.getString(15));
            comercio.setColoniaFisica(resource.getString(16));
            comercio.setMunicipioFisica(resource.getString(17));
            comercio.setEstadoFisica(resource.getString(18));
            comercio.setCodigoPostalFisica(resource.getString(19));
            comercio.setPaisFisica(resource.getString(20));
            comercio.setTimestamp(resource.getLong(21));

            comercios.add(comercio);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (comercios.size() <= 0){
            return null;
        }

        return comercios;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
