package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.EstadoPos;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;


/**
 * Manejo de metodos para el acceso a la informacion del modelo EstadoPos
 * Created by Mauricio Cerón on 21/04/2016.
 */
public class EstadoPosDAO {
    private SQLiteService sqLiteService;

    public EstadoPosDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }

    /**
     * Obtener informacion del estado de POS entre las fechas que se soliciten
     * @param fechaInicio
     * @param fechaFin
     * @return Resultado
     */
    public EstadoPos getEstadoPos(Long fechaInicio, Long fechaFin) {
        return sqLiteService.estadoPos.select(fechaInicio,fechaFin);
    }
}
