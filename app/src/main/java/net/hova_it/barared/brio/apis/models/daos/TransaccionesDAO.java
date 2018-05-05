package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Transacciones;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Transacciones
 * Created by Mauricio Cerón on 25/04/2016.
 */
public class TransaccionesDAO {
    private SQLiteService sqLiteService;

    public TransaccionesDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }

    /**
     * Obtener registros de transacciones en un determinado rango de fechas
     * @param fechaInicio
     * @param fechaFin
     * @return
     */
    public Transacciones getTransacciones(Long fechaInicio, Long fechaFin) {
        return sqLiteService.transacciones.select(fechaInicio,fechaFin);
    }
}
