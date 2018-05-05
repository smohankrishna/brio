package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Articulo;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Articulo
 * Created by Alejandro Gomez on 30/11/2015.
 */
public class ArticulosDAO {
    private SQLiteService sqLiteService;

    public ArticulosDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }

    /**
     * Obtener todos los registros
     * @return Lista de resultados
     */
    public List<Articulo> getAll() {
        return sqLiteService.articulos.select("WHERE fecha_baja = null");
    }

    /**
     * Obtener todos los registros de Articulos sin importar que fueran eliminados
     * * @return Lista de resultados
     */
    public List<Articulo> getTotallyAll() {
        return sqLiteService.articulos.select("");
    }

    /**
     * Obtener registros de Articulos eliminados
     * @returnLista de resultados
     */
    public List<Articulo> getAllDeletes() {
        return sqLiteService.articulos.select("WHERE fecha_baja != null");
    }

    /**
     * Obtener registros de los Articulos guardados de forma expres
     * @return Lista de resultados
     */
    public List<Articulo> getArticulosPendientes() {
        return sqLiteService.articulos.select("WHERE id_marca = 1 AND id_presentacion = 1 AND id_unidad = 1");
    }

    /**
     * Obtener registro por Id de Articulo
     * @param value
     * @return Resultado de la consulta
     */
    public Articulo getByIdArticulo(int value) {
        List<Articulo> items = sqLiteService.articulos.select("WHERE id_articulo = " + value);
        return (items == null) ? null : items.get(0);
    }

    /**
     * Obtener registro por Id Central
     * @param value
     * @return Resultado de la consulta
     */
    public Articulo getByIdCentral(int value) {
        List<Articulo> items = sqLiteService.articulos.select("WHERE id_central = " + value);
        return (items == null) ? null : items.get(0);
    }

    /**
     * Obtener registro por Codigo de barras
     * @param value
     * @return Resultado de la consulta
     */
    public Articulo getByCodigoBarras(String value) {
        List<Articulo> items = sqLiteService.articulos.select("WHERE codigo_barras = '" + value + "' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por Id de marca
     * @param value
     * @return Lista de resultados
     */
    public List<Articulo> getByIdMarca(int value) {
        return sqLiteService.articulos.select("WHERE id_marca = '" + value + "'");
    }
    /**
     * Obtener registro por Id de familia
     * @param value
     * @return Lista de resultados
     */
    public List<Articulo> getByIdFamilia(int value) {
        return sqLiteService.articulos.select("WHERE id_familia = '" + value + "'");
    }
    /**
     * Obtener registro por Id de presentacion
     * @param value
     * @return Lista de resultados
     */
    public List<Articulo> getByIdPresentacion(int value) {
        return sqLiteService.articulos.select("WHERE id_presentacion = '" + value + "'");
    }
    /**
     * Obtener registro por Id de unidad de medida
     * @param value
     * @return Lista de resultados
     */
    public List<Articulo> getByIdUnidadMedida(int value) {
        return sqLiteService.articulos.select("WHERE id_unidad = '" + value + "'");
    }

    /**
     * Obtener registros de Articulos a granel
     * @return Lista de resultados
     */
    public List<Articulo> getAllGranel() {
        return sqLiteService.articulos.select("WHERE granel = '1'");
    }

    /**
     * Obtener registro por descripcion de articulo
     * @param value
     * @return Resultado
     */
    public Articulo getByDescArticulo (String value) {
        List<Articulo> items = sqLiteService.articulos.select("WHERE nombre = '" + value +"' COLLATE NOCASE");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por nombre
     * @param value
     * @return Lista de resultados
     */
    public List<Articulo> getSearchByNombre(String value) {
        return sqLiteService.articulos.select("WHERE nombre LIKE '%" + value + "%'");
            }
    /**
     * Guardar registro
     * @return id de registro creado.
     */
    public Long save(Articulo articulo){
        return sqLiteService.articulos.insert(articulo);
    }
    /**
     * Obtener registro por familia
     * @param value
     * @return Resultado
     */
    public Articulo getByFamilia(String value) {
        List<Articulo> items = sqLiteService.articulos.select("WHERE familia = '" + value + "'");
        return (items == null) ? null : items.get(0);
    }

    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.articulos.getExecutionTime();
    }

}
