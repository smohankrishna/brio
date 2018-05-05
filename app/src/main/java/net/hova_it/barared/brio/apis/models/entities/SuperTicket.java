package net.hova_it.barared.brio.apis.models.entities;

import net.hova_it.barared.brio.apis.pos.api.ItemsTicketController;
import net.hova_it.barared.brio.apis.utils.DebugLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Estructura del modelo SuperTicket
 * Created by Herman Peralta on 23/02/2016.
 */
public class SuperTicket {

    public Ticket ticket;
    public List<ItemsTicketController> itemsTicket;
    public List<TicketTipoPago> ticketTipoPago;
    public String Voucher = "";
    public String TipoTarjeta;
    public String Emisor;
    public String Tarjeta;
    public String Autorizacion;
    public String Transaccion;
    public String Afiliacion;
    public String Referencia;

    public double montoPagado;
    public String Aid;
    public String Tvr;
    public String Tsi;
    public String Apn;
    public String Arqc;
    public String Firma;
    public String Titular;
    public Boolean Emv = false;
    public String Expira;

    //public List<Politics> list_politics; //todo: checar si se van a autilizar politicas para el ticket

    public SuperTicket() {
        DebugLog.log(getClass(), "POSManager", "nuevo SuperTicket");
        this.ticket = new Ticket();

        itemsTicket = new ArrayList<>();
        ticketTipoPago = new ArrayList<>();
    }

    public Ticket getTicket() {
        return ticket;
    }

}
