package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.views.ViewArticulo;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo ViewArticulo
 * Created by Mauricio Cerón on 18/12/2015.
 */
public class ViewArticuloDAO {
    private SQLiteService sqLiteService;

    public ViewArticuloDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<ViewArticulo> getAll() {
        return sqLiteService.viewArticulo.select("", 1);
    }
    /**
     * Obtener registro por Id de Articulo
     * @param value
     * @return
     */
    public ViewArticulo getByIdArticulo(int value) {
        List<ViewArticulo> items = sqLiteService.viewArticulo.select("WHERE id_articulo = " + value, 1);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por Id de Central
     * @param value
     * @return
     */
    public ViewArticulo getByIdCentral(int value) {
        List<ViewArticulo> items = sqLiteService.viewArticulo.select("WHERE id_central = " + value, 1);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por Id de CodigoBarras
     * @param value
     * @return
     */
    public ViewArticulo getByCodigoBarras(String value) {
        List<ViewArticulo> items = sqLiteService.viewArticulo.select("WHERE codigo_barras = '" + value + "'", 1);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por Id de descripcion de articulo
     * @param value
     * @return
     */
    public List<ViewArticulo> getByDescArticulo(String value) {
        return sqLiteService.viewArticulo.select("WHERE desc_articulo LIKE '%" + value + "%'", 1);
    }
    /**
     * Obtener registros de articulos a granel
     * @return
     */
    public List<ViewArticulo> getAllGranel() {
        return sqLiteService.viewArticulo.select("WHERE granel = '1' ORDER BY nombre ASC", 1);
    }

    /**
     * Obtiene los registros de articulos a granel en orden descendente de a cuerdo a la cantidad
     * de articulos a granel vendidos
     * @return
     */
    public List<ViewArticulo> getTopGranel(){
        return sqLiteService.viewArticulo.select("", 2);
    }
    /**
     * Obtiene los registros de articulos en orden descendente de a cuerdo a la cantidad de
     * articulos vendidos
     * @return
     */
    public List<ViewArticulo> getTopAll(){
        return sqLiteService.viewArticulo .select("", 3);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */

    public long getExecutionTime() {
        return sqLiteService.viewArticulo.getExecutionTime();
    }
}
