package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.TipoTicket;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo TipoTicket
 * Created by Mauricio Cerón on 23/02/2016.
 */
public class TipoTicketDAO {
    private SQLiteService sqLiteService;

    public TipoTicketDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<TipoTicket> getAll() {
        return sqLiteService.tipoTicket.select("");
    }
    /**
     * Obtener registro por Id de TipoTicket
     * @param value
     * @return
     */
    public TipoTicket getByIdTipoTicket(int value) {
        List<TipoTicket> items = sqLiteService.tipoTicket.select("WHERE id_tipo_ticket = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(TipoTicket item){
        return sqLiteService.tipoTicket.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.tipoTicket.getExecutionTime();
    }
}

