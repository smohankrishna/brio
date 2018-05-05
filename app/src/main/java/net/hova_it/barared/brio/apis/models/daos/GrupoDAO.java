package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Grupo;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Grupo
 * Created by Alejandro Gomez on 16/12/2015.
 */

public class GrupoDAO {
    private SQLiteService sqLiteService;

    public GrupoDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Grupo> getAll() {
        return sqLiteService.grupos.select("");
    }
    /**
     * Obtener registro por Id de Grupo
     * @param value
     * @return
     */
    public Grupo getByIdGrupo(int value) {
        List<Grupo> items = sqLiteService.grupos.select("WHERE id_grupo = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por descripcion de Grupo
     * @param value
     * @return
     */
    public List<Grupo> getSearchDescGrupo(String value) {
        return sqLiteService.grupos.select("WHERE desc_grupo LIKE '%" + value + "%'");
    }
    /**
     * Obtener registro por descripcion de Grupo
     * @param value
     * @return
     */
    public Grupo getByDescGrupo (String value) {
        List<Grupo> items = sqLiteService.grupos.select("WHERE desc_grupo = '" + value +"' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Grupo item){
        return sqLiteService.grupos.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.grupos.getExecutionTime();
    }

}
