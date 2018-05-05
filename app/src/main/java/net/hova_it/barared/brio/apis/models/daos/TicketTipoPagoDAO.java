package net.hova_it.barared.brio.apis.models.daos;

import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;

import java.util.List;

/**
 * Manejo de metodos para el acceso a la informacion del modelo TicketTipoPago
 * Created by Mauricio Cerón on 08/02/2016.
 */
public class TicketTipoPagoDAO {

    private SQLiteService sqLiteService;

    public TicketTipoPagoDAO(SQLiteService sqLiteService) {
        this.sqLiteService = sqLiteService;
    }
    /**
     * Obtener todos los registros
     * @return
     */
    public List<TicketTipoPago> getAll() {
        return sqLiteService.ticketTipoPago.select("");
    }
    /**
     * Obtener registros por Id de TicketTipoPago
     * @param value
     * @return
     */
    public TicketTipoPago getByIdTicketTipoPago(int value) {
        List<TicketTipoPago> items = sqLiteService.ticketTipoPago.select("WHERE id_ticket_tipos_pago = " + value);
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registros por Id de Ticket
     * @param value
     * @return
     */
    public List<TicketTipoPago> getByIdTicket(int value) {
        return sqLiteService.ticketTipoPago.select("WHERE id_ticket = " + value);
    }
    /**
     * Obtener registro por Id de Ticket y si fue pagado con tarjeta
     * @param value
     * @return
     */
    public TicketTipoPago getByIdTicketTarjeta(int value) {
        List<TicketTipoPago> items =
        sqLiteService.ticketTipoPago.select("WHERE id_ticket = "+ value +" AND id_tipo_pago = 2 ");
        return (items == null) ? null : items.get(0);
    }
    /**
     * Obtener registro por Id de TipoPago
     * @param value
     * @return
     */
    public TicketTipoPago getByIdTipoPago(int value) {
        List<TicketTipoPago> items = sqLiteService.ticketTipoPago.select("WHERE id_tipo_pago = " + value);
        return (items == null) ? null : items.get(0);
    }

    /**
     * Guardar registro
     * @return id de registro creado
     */
    public Long save(TicketTipoPago pago){
        return sqLiteService.ticketTipoPago.insert(pago);
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public long getExecutionTime() {
        return sqLiteService.impuestos.getExecutionTime();
    }
    /**
     * Obtener tiempo de duracion de movimientos en la tabla
     * @return
     */
    public Long deleteOutdatedTicketTipoPago(){
        return sqLiteService.ticketTipoPago.delete(" Ticket_tipos_pago.id_ticket in (select Ticket_tipos_pago.id_ticket from Ticket_tipos_pago  \n" +
                "left join Tickets on Tickets.id_ticket =  Ticket_tipos_pago.id_ticket\n" +
                "where datetime(Tickets.timestamp,'unixepoch', 'localtime') < datetime('now', '-2 month'))");
    }

}
