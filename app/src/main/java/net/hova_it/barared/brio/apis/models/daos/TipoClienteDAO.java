package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.TipoCliente;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo TipoCliente
 * Created by Alejandro Gomez on 16/12/2015.
 */

public class TipoClienteDAO {
    private SQLiteService sqLiteService;

    public TipoClienteDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<TipoCliente> getAll() {
        return sqLiteService.tiposCliente.select("");
    }
    /**
     * Obtener registro por Id de TipoCliente
     * @param value
     * @return
     */

    public TipoCliente getByIdTipoCliente(int value) {
        List<TipoCliente> items = sqLiteService.tiposCliente.select("WHERE id_tipo_cliente = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(TipoCliente item){
        return sqLiteService.tiposCliente.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.tiposCliente.getExecutionTime();
    }

}
