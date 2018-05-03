package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.TipoPago;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo TipoPago
 * Created by Mauricio Cerón on 29/03/2016.
 */
public class TipoPagoDAO {
    private SQLiteService sqLiteService;

    public TipoPagoDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<TipoPago> getAll() {
        return sqLiteService.tipoPago.select("");
    }
    /**
     * Obtener registro por Id de TipoPago
     * @param value
     * @return
     */
    public TipoPago getByIdTipoPago(int value) {
        List<TipoPago> items = sqLiteService.tipoPago.select("WHERE id_tipo_pago = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(TipoPago item){
        return sqLiteService.tipoPago.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.tipoPago.getExecutionTime();
    }
}

