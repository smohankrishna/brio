package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.SyncTicketLocal;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo SyncTicketLocal
 * Created by Herman Peralta on 09/05/2016.
 */
public class SyncTicketLocalDAO {

    public SyncTicketLocalDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }

    private SQLiteService sqLiteService;

    /**
     * Obtener registros de tickets que coincidadn con los siguientes parametros:
     * @param lastTransaccion
     * @param importeNeto
     * @param idTipoTicket
     * @param idTransaccion
     * @return
     */
    public List<SyncTicketLocal> getInfoOfTicket(String lastTransaccion, double importeNeto, int idTipoTicket, String idTransaccion) {
        return sqLiteService.syncTicketLocal.select(lastTransaccion, importeNeto, idTipoTicket, idTransaccion);
    }
}
