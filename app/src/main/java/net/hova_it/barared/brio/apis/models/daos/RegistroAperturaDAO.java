package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.RegistroApertura;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo RegistroApertura
 * Created by Alejandro Gomez on 21/12/2015.
 */
public class RegistroAperturaDAO {
    private SQLiteService sqLiteService;

    public RegistroAperturaDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<RegistroApertura> getAll() {
        return sqLiteService.registrosApertura.select("");
    }
    /**
     * Obtener registro por Id de RegistroApertura
     * @param value
     * @return
     */
    public RegistroApertura getByIdRegistroApertura(int value) {
        List<RegistroApertura> items = sqLiteService.registrosApertura.select("WHERE id_registro_apertura = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por Id de RegistroCierre
     * @param value
     * @return
     */
    public RegistroApertura getByIdRegistroCierre(int value) {
        List<RegistroApertura> items = sqLiteService.registrosApertura.select("WHERE id_registro_cierre = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener el ultimo registro
     * @return
     */
    public RegistroApertura getLast() {
        List<RegistroApertura> items = sqLiteService.registrosApertura.select("WHERE id_registro_apertura = (SELECT MAX(id_registro_apertura) FROM Registro_apertura)" );
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por Id de Caja
     * @param value
     * @return
     */
    public List<RegistroApertura> getByIdCaja(int value) {
        return sqLiteService.registrosApertura.select("WHERE id_caja = " + value);
    }
    /**
     * Obtener registro por Id de Usuario
     * @param value
     * @return
     */
    public List<RegistroApertura> getByIdUsuario(int value) {
        return sqLiteService.registrosApertura.select("WHERE id_usuario = " + value);
    }
    /**
     * Guardar registro
     * @return
     */
    public Long save(RegistroApertura item){
       return sqLiteService.registrosApertura.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return id de registro creado
     */
    public long getExecutionTime() {
        return sqLiteService.registrosApertura.getExecutionTime();
    }

}
