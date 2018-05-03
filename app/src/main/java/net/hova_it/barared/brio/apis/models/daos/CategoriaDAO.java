package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Categoria;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Categoria
 * Created by Alejandro Gomez on 17/12/2015.
 */

public class CategoriaDAO {
    private SQLiteService sqLiteService;

    public CategoriaDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Categoria> getAll() {
        return sqLiteService.categorias.select("");
    }
    /**
     * Obtener registro por Id de Categoria
     * @param value
     * @return
     */
    public Categoria getByIdCategoria(int value) {
        List<Categoria> items = sqLiteService.categorias.select("WHERE id_categoria = " + value);
        return (items == null) ? null : items.get(0);
    }

    /**
     * Obtener registro por nombre
     * @param value
     * @return
     */
    public Categoria getByNombre(String value) {
        List<Categoria> items = sqLiteService.categorias.select("WHERE nombre = '" + value + "' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado.
     */
    public Long save(Categoria item){
        return sqLiteService.categorias.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.categorias.getExecutionTime();
    }
}
