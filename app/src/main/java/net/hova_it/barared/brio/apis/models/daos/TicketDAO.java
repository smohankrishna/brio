package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo Ticket
 * Created by Alejandro Gomez on 17/12/2015.
 */


public class TicketDAO {
    private SQLiteService sqLiteService;

    public TicketDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */

    public List<Ticket> getAll() {
        return sqLiteService.tickets.select("");
    }

    public Ticket getByIdTicket(int value) {
        List<Ticket> items = sqLiteService.tickets.select("WHERE id_ticket = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por Id de Cliente
     * @param value
     * @return
     */
    public List<Ticket> getByIdCliente(int value) {
        return sqLiteService.tickets.select("WHERE id_cliente = " + value);
    }

    /**
     * Obtener registros por Id de Comercio
     * @param value
     * @return
     */
    public List<Ticket> getByIdComercio(int value) {
        return sqLiteService.tickets.select("WHERE id_comercio = " + value);
    }
    /**
     * Obtener registros por Id de Usuario
     * @param value
     * @return
     */
    public List<Ticket> getByIdUsuario(int value) {
        return sqLiteService.tickets.select("WHERE id_usuario = " + value);
    }
    /**
     * Obtener registros por Id de Caja
     * @param value
     * @return
     */
    public List<Ticket> getByIdCaja(int value) {
        return sqLiteService.tickets.select("WHERE id_caja = " + value);
    }

    /**
     * Obtener registros en un rango de fecha determinado
     * @param ini
     * @param fin
     * @return
     */
    public List<Ticket> getPosByFechaTicket(long ini, long fin) {
        return sqLiteService.tickets.select(
                "WHERE timestamp " +
                        "BETWEEN " + ini + " AND " + fin +
                        " ORDER BY id_tipo_ticket ASC ");
    }

    /**
     * Obtener registros por Id de Transaccion
     * @param idTransaccion
     * @return
     */
    public Ticket getByIdTransaccion(int idTransaccion) {
        List<Ticket> items = sqLiteService.tickets.select("WHERE descripcion LIKE = '%" + idTransaccion + "%'");
        return (items == null) ? null : items.get(0);
    }

    /**
     * Obtener registros de ventas de POS en un rango de fechas determinado
     * @param ini
     * @param fin
     * @return
     */
    public List<Ticket> getVentasPosByFechaTicket(long ini, long fin) {
        return sqLiteService.tickets.select(
                "WHERE timestamp " +
                        "BETWEEN " + ini + " AND " + fin +
                        " AND id_tipo_ticket = 1 ");
    }

    /**
     * Obtener registros de transacciones autorizadas en un rango de fechas
     * @param ini
     * @param fin
     * @param idTipoTicket
     * @return
     */
    public List<Ticket> getAutorizadosByRangoFechaTicket(long ini, long fin, String idTipoTicket) {
        return sqLiteService.tickets.selectServiciosAutorizados(ini, fin, idTipoTicket);
    }

    /**
     * Obtener registros de movimientos en caja en un determinado rango de fecha
     * @param ini
     * @param fin
     * @return
     */
    public List<Ticket> getTicketsEntradaSalida(long ini, long fin) {
        return sqLiteService.tickets.select(
                " WHERE id_tipo_ticket IN (2,3) " +
                        " AND timestamp BETWEEN " + ini + " AND " + fin);
    }
    /**
     * Obtener registros por Id de Transactions
     * @param value
     * @return
     */
    public List<Ticket> getByIdTransactions(String value) {
        return sqLiteService.tickets.select("WHERE descripcion IN ("+value+") ");
    }
    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(Ticket item){
        return sqLiteService.tickets.insert(item);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.tickets.getExecutionTime();
    }

    public Long getCountOutdatedTickets(){
        return sqLiteService.tickets.count("where datetime(timestamp ,'unixepoch', 'localtime') < datetime('now', '-2 month')");
    }

    public Long deleteOutdatedTickets(){
        return sqLiteService.tickets.delete(" id_ticket in (select id_ticket from Tickets where datetime(timestamp ,'unixepoch', 'localtime') < datetime('now', '-2 month'))");
    }
}
