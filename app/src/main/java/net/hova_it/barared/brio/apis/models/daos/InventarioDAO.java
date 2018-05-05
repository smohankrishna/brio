package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Inventario;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Inventario
 * Created by Mauricio Cerón on 03/02/2016.
 */
public class InventarioDAO {


    private SQLiteService sqLiteService;

    public InventarioDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Inventario> getAll() {
        return sqLiteService.inventario.select("");
    }
    /**
     * Obtener registro por Id de Inventario
     * @param value
     * @return
     */
    public Inventario getByIdInventario(int value) {
        List<Inventario> items = sqLiteService.inventario.select("WHERE id_inventario = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por Id de Articulo
     * @param value
     * @return
     */
    public Inventario getByIdArticulo(int value) {
        List<Inventario> items = sqLiteService.inventario.select("WHERE id_articulo = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por Id de sesion
     * @param value
     * @return
     */
    public Inventario getByIdSesion(int value) {
        List<Inventario> items = sqLiteService.inventario.select("WHERE id_sesion = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por existencias
     * @param value
     * @return
     */
    public Inventario getByExistencias(int value) {
        List<Inventario> items = sqLiteService.inventario.select("WHERE cantidad = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Inventario item){
        return sqLiteService.inventario.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.impuestos.getExecutionTime();
    }

}
