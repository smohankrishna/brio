package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.TransaccionTarjeta;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**Manejo de metodos para el acceso a la informacion del modelo TransaccionesTarjeta
 * Created by Mauricio Cerón on 12/05/2016.
 */
public class TransaccionesTarjetaDAO {

    private SQLiteService sqLiteService;

    public TransaccionesTarjetaDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }

    /**
     * Obtener registros de transacciones con tarjeta en un determinado rango de fechas
     * @param ini
     * @param fin
     * @return
     */
    public List<TransaccionTarjeta> getForRange(long ini, long fin) {
        return sqLiteService.transaccionTarjeta.select(ini,fin);
    }


}
