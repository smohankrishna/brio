package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Moneda;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Moneda
 * Created by Alejandro Gomez on 16/12/2015.
 */
public class MonedaDAO {
    private SQLiteService sqLiteService;

    public MonedaDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Moneda> getAll() {
        return sqLiteService.monedas.select("");
    }
    /**
     * Obtener registro por Id de moneda
     * @param value
     * @return
     */
    public Moneda getByIdMoneda(int value) {
        List<Moneda> items = sqLiteService.monedas.select("WHERE id_moneda = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por defecto
     * @param value
     * @return
     */
    public List<Moneda> getByDefecto(String value) {
        return sqLiteService.monedas.select("WHERE defecto = " + value);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Moneda item){
        return sqLiteService.monedas.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.monedas.getExecutionTime();
    }
}
