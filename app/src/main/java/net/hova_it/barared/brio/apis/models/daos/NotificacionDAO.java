package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Notificacion;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Notificacion
 * Created by Mauricio Cerón on 21/12/2015.
 */
public class NotificacionDAO {
    private SQLiteService sqLiteService;

    public NotificacionDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<Notificacion> getAll() {
        return sqLiteService.notificacion.select("");
    }
    /**
     * Obtener registro por Id de Notificacion
     * @param value
     * @return
     */
    public Notificacion getByIdNotificacion(int value) {
        List<Notificacion> items = sqLiteService.notificacion.select("WHERE id_notificacion = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por Id de Articulo
     * @param value
     * @return
     */
    public Notificacion getByIdArticulo(int value) {
        List<Notificacion> items = sqLiteService.notificacion.select("WHERE id_articulo = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Notificacion item){
        return sqLiteService.notificacion.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.notificacion.getExecutionTime();
    }
}
