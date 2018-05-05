package net.hova_it.barared.brio.apis.models.daos;


import net.hova_it.barared.brio.apis.models.entities.Presentacion;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Presentacion
 * Created by Mauricio Cerón on 21/12/2015.
 */
public class PresentacionDAO {
    private SQLiteService sqLiteService;

    public PresentacionDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Presentacion> getAll() {
        return sqLiteService.presentacion.select("");
    }
    /**
     * Obtener registro por Id de Presentacion
     * @param value
     * @return
     */
    public Presentacion getByIdPresentacion(int value) {
        List<Presentacion> items = sqLiteService.presentacion.select("WHERE id_presentacion = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por nombre
     * @param value
     * @return
     */
    public Presentacion getByNombre(String value) {
        List<Presentacion> items = sqLiteService.presentacion.select("WHERE nombre = '" + value +"' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por nombre
     * @param value
     * @return
     */
    public List<Presentacion> getSearchNombre(String value) {
        return sqLiteService.presentacion.select("WHERE nombre LIKE '%" + value + "%'");
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Presentacion item){
        return sqLiteService.presentacion.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.presentacion.getExecutionTime();
    }
}
