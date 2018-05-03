package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Usuario;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Usuario
 * Created by Alejandro Gomez on 16/12/2015.
 */

public class UsuarioDAO {
    private SQLiteService sqLiteService;

    public UsuarioDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Usuario> getAll() {
        return sqLiteService.usuarios.select("");
    }

    public Usuario getByIdUsuario(int value) {
        List<Usuario> items = sqLiteService.usuarios.select("WHERE id_usuario = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por usuario
     * @param value
     * @return
     */
    public Usuario getByUsuario(String value) {
        List<Usuario> items = sqLiteService.usuarios.select("WHERE usuario = '" + value + "' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por nombre
     * @param value
     * @return
     */
    public Usuario getByNombre(String value) {
        List<Usuario> items = sqLiteService.usuarios.select("WHERE nombre = '" + value + "' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por nombre
     * @param value
     * @return
     */
    public List<Usuario> getSearchByNombre(String value) {
        return sqLiteService.usuarios.select("WHERE nombre LIKE '%" + value + "%'");
    }
    /**
     * Obtener registro por apellidos
     * @param value
     * @return
     */
    public Usuario getByApellidos(String value) {
        List<Usuario> items = sqLiteService.usuarios.select("WHERE apellidos = '" + value + "' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por apellidos
     * @param value
     * @return
     */
    public List<Usuario> getSeachByApellidos(String value) {
        return sqLiteService.usuarios.select("WHERE apellidos LIKE '%" + value + "%'");
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Usuario item){
        return sqLiteService.usuarios.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.usuarios.getExecutionTime();
    }

}
