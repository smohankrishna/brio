package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Balance;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Balance
 * Created by Mauricio Cerón on 02/03/2016.
 */
public class BalanceDAO {

    private SQLiteService sqLiteService;

    public BalanceDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }

    /**
     * Obtener informacion de balance
     * @param caja
     * @return Resultado
     */
    public Balance getBalance(int caja) {
        Balance balance = sqLiteService.balance.select(caja);
        return balance;
    }
}

