package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.RegistroCierre;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo RegistroCierre
 * Created by Alejandro Gomez on 21/12/2015.
 */
public class RegistroCierreDAO {
    private SQLiteService sqLiteService;

    public RegistroCierreDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<RegistroCierre> getAll() {
        return sqLiteService.registrosCierre.select("");
    }
    /**
     * Obtener registro por Id de RegistroCierre
     * @param value
     * @return
     */
    public RegistroCierre getByIdRegistroCierre(String value) {
        List<RegistroCierre> items = sqLiteService.registrosCierre.select("WHERE id_registro_cierre = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener ultimo registro
     * @return
     */
    public RegistroCierre getLast() {

        List<RegistroCierre> items = sqLiteService.registrosCierre.
                select("WHERE id_registro_cierre = (SELECT MAX(id_registro_cierre) FROM Registro_cierre)" );
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por Id de Caja
     * @param value
     * @return
     */
    public List<RegistroCierre> getByIdCaja(int value) {
        return sqLiteService.registrosCierre.select("WHERE id_caja = " + value);
    }
    /**
     * Obtener registros por Id de Usuario
     * @param value
     * @return
     */
    public List<RegistroCierre> getByIdUsuario(int value) {
        return sqLiteService.registrosCierre.select("WHERE id_usuario = " + value);
    }
    /**
     * Guardar registro
     * @return
     */
    public Long save(RegistroCierre item){
        return sqLiteService.registrosCierre.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return id de registro creado
     */
    public long getExecutionTime() {
        return sqLiteService.registrosCierre.getExecutionTime();
    }

    public List<RegistroCierre> select(String s) {
        return null;
    }
}