package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.views.ViewInventario;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo ViewInventario
 * Created by Mauricio Cerón on 04/02/2016.
 */
public class ViewInventarioDAO {

    private SQLiteService sqLiteService;

    public ViewInventarioDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<ViewInventario> getAll() {
        return sqLiteService.viewInventario.select("");
    }
    /**
     * Obtener registro por Id de Articulo
     * @param value
     * @return
     */
    public ViewInventario getByIdArticulo(int value) {
        List<ViewInventario> items = sqLiteService.viewInventario.select("WHERE id_articulo = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por nombre
     * @param value
     * @return
     */
    public ViewInventario getByNombre(String value) {
        List<ViewInventario> items = sqLiteService.viewInventario.select("WHERE nombre = " + value);
        return (items == null) ? null : items.get(0);
    }

    /**
     * Obtener registro por marca
     * @param value
     * @return
     */
    public ViewInventario getByMarca(String value) {
        List<ViewInventario> items = sqLiteService.viewInventario.select("WHERE marca = " + value);
        return (items == null) ? null : items.get(0);
    }

    /**
     * Obtener registro por contenido
     * @param value
     * @return
     */
    public ViewInventario getByContenido(String value) {
        List<ViewInventario> items = sqLiteService.viewInventario.select("WHERE contenido = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por presentacion
     * @param value
     * @return
     */
    public ViewInventario getByPresentacion(String value) {
        List<ViewInventario> items = sqLiteService.viewInventario.select("WHERE presentacion = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por categoria
     * @param value
     * @return
     */
    public ViewInventario getByCategoria(String value) {
        List<ViewInventario> items = sqLiteService.viewInventario.select("WHERE categoria = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por existencias
     * @param value
     * @return
     */
    public ViewInventario getByExistencias(int value) {
        List<ViewInventario> items = sqLiteService.viewInventario.select("WHERE existencias = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.viewArticulo.getExecutionTime();
    }
}
