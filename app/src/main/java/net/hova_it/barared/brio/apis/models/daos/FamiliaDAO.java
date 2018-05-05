package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Familia;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Familia
 * Created by Alejandro Gomez on 17/12/2015.
 */

public class   FamiliaDAO {
    private SQLiteService sqLiteService;

    public FamiliaDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Familia> getAll() {
        return sqLiteService.familias.select("");
    }
    /**
     * Obtener registro por Id de Familia
     * @param value
     * @return
     */
    public Familia getByIdFamilia(int value) {
        List<Familia> items = sqLiteService.familias.select("WHERE id_familia = " + value);
        return (items == null) ? null : items.get(0);
    }

    /**
     * Obtener registro por descripcion de familia
     * @param value
     * @return
     */
    public Familia getByDescFamilia(String value) {
        List<Familia> items = sqLiteService.familias.select("WHERE nombre = '" + value + "' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por Id de categoria
     * @param value
     * @return
     */
    public Familia getByIdCategoria(int value) {
        List<Familia> items = sqLiteService.familias.select("WHERE id_categoria = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por nombre de familia
     * @param value
     * @return
     */
    public List<Familia> getByNombre (String value) {
        return sqLiteService.familias.select("WHERE nombre LIKE '%" + value + "%'");
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Familia item){
        return sqLiteService.familias.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.familias.getExecutionTime();
    }
}
