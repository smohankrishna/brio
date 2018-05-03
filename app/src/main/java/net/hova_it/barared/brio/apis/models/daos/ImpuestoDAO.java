package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Impuesto;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Impuesto
 * Created by Alejandro Gomez on 17/12/2015.
 */

public class ImpuestoDAO {
    private SQLiteService sqLiteService;

    public ImpuestoDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Impuesto> getAll() {
        return sqLiteService.impuestos.select("");
    }
    /**
     * Obtener registro por Id de Impuesto
     * @param value
     * @return
     */
    public Impuesto getByIdImpuesto(int value) {
        List<Impuesto> items = sqLiteService.impuestos.select("WHERE id_impuestos = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Impuesto item){
        return sqLiteService.impuestos.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.impuestos.getExecutionTime();
    }
}
