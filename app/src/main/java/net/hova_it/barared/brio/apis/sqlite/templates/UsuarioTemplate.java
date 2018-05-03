package net.hova_it.barared.brio.apis.sqlite.templates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.Usuario;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja los metodos Insert y Select en Sqlite para el modelo Usuario.
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class UsuarioTemplate extends Synchronizable {
    private final String TAG = "BRIO_DB";
    private final String TABLE = "Usuarios"; // TODO
    private final String key = "id_usuario"; // TODO

    private SQLiteService sqLiteService;
    private SQLiteInit query;
    private long executionTime;

    public UsuarioTemplate(SQLiteService sqLiteService){
        super(sqLiteService); // TODO
        this.sqLiteService = sqLiteService;
        this.query = new SQLiteInit();
    }

    public Long insert(Usuario usuario){
        ///////////////////////////////////////////
        Log.i(TAG, "NEW INSERT/UPDATE #########");

        SQLiteDatabase db = sqLiteService.getWritableDatabase();

        int _id = usuario.getIdUsuario();
        boolean isNew = true;

        if(_id > 0){
            isNew = false;
        }else{
            _id = sqLiteService.getLastId(TABLE, key, db) + 1;
        }
        ///////////////////////////////////////////

        SQLiteStatement stmt = db.compileStatement(query.replaceIntoUsuarios());

        stmt.bindLong(1, _id);
        stmt.bindString(2, usuario.getUsuario());
        stmt.bindString(3, usuario.getPassword());
        stmt.bindLong(4, usuario.getIdPerfil());
        stmt.bindString(5, usuario.getNombre());
        stmt.bindString(6, usuario.getApellidos());
        stmt.bindLong(7, usuario.getPregunta1());
        stmt.bindString(8, usuario.getRespuesta1());
        stmt.bindLong(9, usuario.getActivo() ? 1 : 0);
        stmt.bindLong(10, usuario.getBloqueado()?1:0);

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
    public List<Usuario> select(String where){
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = sqLiteService.getReadableDatabase();//getWritableDatabase();
        Cursor resource = db.rawQuery("SELECT * FROM " + TABLE + " " + where, null);

        List<Usuario> usuarios = new ArrayList<>();
        Usuario usuario;

        while(resource.moveToNext()){
            usuario = new Usuario();

            usuario.setIdUsuario(resource.getInt(0));
            usuario.setUsuario(resource.getString(1));
            usuario.setPassword(resource.getString(2));
            usuario.setIdPerfil(resource.getInt(3));
            usuario.setNombre(resource.getString(4));
            usuario.setApellidos(resource.getString(5));
            usuario.setPregunta1(resource.getInt(6));
            usuario.setRespuesta1(resource.getString(7));
            usuario.setActivo(resource.getInt(8)!=0);
            usuario.setBloqueado(resource.getInt(9)!=0);
            usuario.setTimestamp(resource.getLong(10));

            usuarios.add(usuario);
        }
        resource.close();
        db.close();

        executionTime = System.currentTimeMillis() - startTime;

        if (usuarios.size() <= 0){
            return null;
        }

        return usuarios;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
