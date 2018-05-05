package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Caja;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Caja
 * Created by Alejandro Gomez on 16/12/2015.
 */

public class CajaDAO {
    private SQLiteService sqLiteService;

    public CajaDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Caja> getAll() {
        return sqLiteService.cajas.select("");
    }
    /**
     * Obtener registro por Id de Caja
     * @param value
     * @return
     */
    public Caja getByIdCaja(int value) {
        List<Caja> items = sqLiteService.cajas.select("WHERE id_caja = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado.
     */
    public Long save(Caja item){
        return sqLiteService.cajas.insert(item);
    }

    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.cajas.getExecutionTime();
    }
}
