package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.UnidadesGranel;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo UnidadesGranel
 * Created by Alejandro Gomez on 16/12/2015.
 */

public class UnidadesGranelDAO {
    private SQLiteService sqLiteService;

    public UnidadesGranelDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<UnidadesGranel> getAll() {
        return sqLiteService.unidades.select("");
    }
    /**
     * Obtener registro por Id de Unidad
     * @param value
     * @return
     */
    public UnidadesGranel getByIdUnidad(int value) {
        List<UnidadesGranel> items = sqLiteService.unidades.select("WHERE id_unidad = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por nombre
     * @param value
     * @return
     */
    public UnidadesGranel getByNombre(String value) {
        List<UnidadesGranel> items = sqLiteService.unidades.select("WHERE desc_unidad = '" + value +"' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por nombre
     * @param value
     * @return
     */
    public List<UnidadesGranel> getSearchNombre(String value) {
        return sqLiteService.unidades.select("WHERE desc_unidad LIKE '%" + value + "%'");
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(UnidadesGranel item){
        return sqLiteService.unidades.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.unidades.getExecutionTime();
    }
}
