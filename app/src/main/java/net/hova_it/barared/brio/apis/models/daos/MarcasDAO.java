package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Marca;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Marcas
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class MarcasDAO {
    private SQLiteService sqLiteService;

    public MarcasDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Marca> getAll() {
        return sqLiteService.marcas.select("");
    }
    /**
     * Obtener registro por Id de marca
     * @param value
     * @return
     */
    public Marca getByIdMarca(int value) {
        List<Marca> items = sqLiteService.marcas.select("WHERE id_marca = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por nombre
     * @param value
     * @return
     */
    public Marca getByNombre(String value) {
        List<Marca> items = sqLiteService.marcas.select("WHERE nombre = '" + value +"' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por nombre
     * @param value
     * @return
     */
    public List<Marca> getSearchNombre(String value) {
        return sqLiteService.marcas.select("WHERE nombre LIKE '%" + value + "%'");
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Marca item){
        return sqLiteService.marcas.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.marcas.getExecutionTime();
    }
}
