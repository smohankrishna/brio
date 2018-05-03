package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.TelefonoUsers;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Created by guillermo.ortiz on 05/03/18.
 */

public class TelefonoUsersDAO {
    
    private SQLiteService sqLiteService;
    
    public TelefonoUsersDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    
    /** Obtener todos los registros
     * @return
     */
    public List<TelefonoUsers> getAll() {
        return sqLiteService.telefonosUsers.select("");
    }
    /**
     * Obtener registro por Id de TelefonoUsers
     * @param value
     * @return
     */
    public TelefonoUsers getByIdTelefonoUsers(int value) {
        List<TelefonoUsers> items = sqLiteService.telefonosUsers.select("WHERE id_telefono = " + value);
        return (items == null) ? null : items.get(0);
    }
    
    /**
     * Obtener registro por telefonos
     * @param value
     * @return Lista de resultados
     */
    public List<TelefonoUsers> getSearchByTelefono(String value) {
        return sqLiteService.telefonosUsers.select("WHERE num_telefono_user LIKE '%" + value + "%'");
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(TelefonoUsers item){
        return sqLiteService.telefonosUsers.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.telefonosUsers.getExecutionTime();
    }
}