package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Comercio;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Comercio
 * Created by Alejandro Gomez on 16/12/2015.
 */

public class ComercioDAO {
    private SQLiteService sqLiteService;

    public ComercioDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Comercio> getAll() {
        return sqLiteService.comercios.select("");
    }
    /**
     * Obtener registro por Id de Comercio
     * @param value
     * @return
     */
    public Comercio getByIdComercio(int value) {
        List<Comercio> items = sqLiteService.comercios.select("WHERE id_comercio = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Comercio item){
        return sqLiteService.comercios.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return id de registro creado
     */
    public long getExecutionTime() {
        return sqLiteService.comercios.getExecutionTime();
    }

}
