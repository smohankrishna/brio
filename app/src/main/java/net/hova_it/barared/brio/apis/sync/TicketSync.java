package net.hova_it.barared.brio.apis.sync;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import net.hova_it.barared.brio.apis.models.entities.ItemsTicket;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.models.entities.SyncTicketInfo;
import net.hova_it.barared.brio.apis.models.entities.SyncTicketInfoContainer;
import net.hova_it.barared.brio.apis.models.entities.SyncTicketLocal;
import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.payment.PaymentService;
import net.hova_it.barared.brio.apis.pos.api.ItemsTicketController;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;
import lat.brio.core.bussines.BrCommerce;

/**
 * Clase para realizar la sincronización de tickets de servicios
 * entre el servidor y la aplicación Brio.
 * <p>
 * Esta clase se basa en consultar un servicio web el cual regresa todos
 * los tickets de servicios del comercio a partir de una fecha determinada
 * por lastTransaction. Esta clase se encarga entonces de inyectar a la base
 * de datos local los tickets faltantes o datos faltantes sobre los tickets
 * ya existentes.
 * <p>
 * Created by Herman Peralta on 06/05/2016.
 */
public class TicketSync extends RestSyncService {

    private String lastTransaction;
    private String idComercio;

    int autorizados = 0, creados = 0, transaccionado = 0;

    private final static String url = "/session/api/tickets?idComercio=?0&lastTransaction=?1";


    private BrCommerce brCommerce;

    public TicketSync(Context context, RestSyncListener restSyncListener) {
        super(context, restSyncListener,
                ("http://" + BrioGlobales.URL_ZERO_BRIO + url)
                        .replace("?0", "")
                        .replace("?1", ""),
                Request.Method.GET, 0);

        brCommerce=BrCommerce.getInstance(context,BrioGlobales.getAccess());

    }

    @Override
    protected String getJsonRequest() {
        idComercio = brCommerce.getByNombre("ID_COMERCIO").getValor();

        Settings last_transaction = brCommerce.getByNombre("LAST_TRANSACTION");
        lastTransaction = last_transaction == null ? "0" : last_transaction.getValor();
        if (last_transaction == null) {
            last_transaction = new Settings();
            last_transaction.setNombre("LAST_TRANSACTION");
            last_transaction.setValor("0");

            brCommerce.updateSetting(last_transaction);
        }

        //Fixme, regresar el string del json
        serviceUrl = ("http://" + BrioGlobales.URL_ZERO_BRIO + url)
                .replace("?0", idComercio)
                .replace("?1", lastTransaction);


        SyncTicketRequest request = new SyncTicketRequest();
        request.setIdComercio(Long.parseLong(idComercio));
        request.setLastTransaction(Long.parseLong(lastTransaction));

        //Verificar que se forme bien el json
        Gson gson = new Gson();


        return gson.toJson(request);
    }

    /**
     * Procesar los tickets obtenidos desde el servidor.
     *
     * @param response - La respuesta obtenida del web service.
     * @return
     */
    @Override
    protected boolean processResponse(JSONObject response) {

        boolean synched = false;
        SyncTicketInfoContainer syncTicketInfoContainer = null;

        try {
            Gson gson = new Gson();
            syncTicketInfoContainer = gson.fromJson(response.toString(1), SyncTicketInfoContainer.class);


            if (syncTicketInfoContainer.getLastTransaction() == -1) {

                //"ERROR: No se recibieron datos... abortando"
                return false;
            }

            if (syncTicketInfoContainer.getIdComercio() !=
                    Integer.valueOf( idComercio)) {

                //Los datos obtenidos no corresponden a este comercio, abortar

                return false;
            }

            if(lastTransaction==null)
                lastTransaction="0";

            if (syncTicketInfoContainer.getLastTransaction() > Integer.parseInt(lastTransaction)) {
                for (int i = 0; i < syncTicketInfoContainer.getSyncTicketInfos().size(); i++) {

                    SyncTicketInfo remoto = syncTicketInfoContainer.getSyncTicketInfos().get(i);


                    if (ticketTypeMapping.get(remoto.getTtype()) == 7) {
                        continue;
                    }
                    if (remoto.getAuthorization().length() != 0) {
                        processRemoteTicket(remoto);

                    }
                }
            } else {
                //"termina sincronizado, no hay nada que hacer");
            }

            synched = true;
        } catch (Exception e) {
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "TicketSync->processResponse" + e.getMessage());
            e.printStackTrace();
        } finally {

            if (synched) {
                brCommerce.updateSetting(new Settings("LAST_TRANSACTION",String.valueOf(syncTicketInfoContainer.getLastTransaction())));
                DebugLog.log(getClass(), "TicketSync", "updated LAST_TRANSACTION: " + syncTicketInfoContainer.getLastTransaction());
            }

        }

        return synched;
    }

    @Override
    protected void processError(VolleyError volleyError) {
        DebugLog.log(getClass(), "TicketSync", "error: " + volleyError.getMessage());
        volleyError.printStackTrace();
    }

    /**
     * Busca un Ticket remoto en BD, y lo inyecta en los siguientes casos:
     * 1) Si encuentra el ticket pero no tiene la autorizacion, se le inyecta la autorización
     * 2) Si no encuentra el ticket local, se inyecta el SuperTicket completo
     *
     * @param remoto - La información del ticket remoto
     * @return 0 si no esta en la base, > 0 si esta en la base
     */
    private void processRemoteTicket(SyncTicketInfo remoto) {
        boolean processed = false;

        float comiTmp = (float) (remoto.getCommi() / 0.7);
        List<SyncTicketLocal> locales;
        if (ticketTypeMapping.get(remoto.getTtype()) == 7 && comiTmp == 15.0) {//15.0 en comision significa compra de tarjeta bankaool
            //en caso de detectar una compra de tarjeta, al monto se le suma 30.00
            remoto.setAmount(remoto.getAmount() + 30.00);
        }

        locales = managerModel.syncTicketLocal.getInfoOfTicket(lastTransaction,
                remoto.getAmount(),
                ticketTypeMapping.get(remoto.getTtype()),
                String.valueOf(remoto.getTrans()));

        if (locales == null) {

            injectTicket(remoto);
            processed = true;

        } else {
            //(1) validar si alguno tiene la autorizacion y si la tiene solo inyectarle la transaccion, si ninguno la tiene, buscar los datos
            ////////////////////////////////////////////////////////////////////////////////////////
            boolean hasDaAuth = false;
            SyncTicketLocal local = null;
            int position = 0;
            for (; position < locales.size(); position++) {

                int offset = 0;
                local = locales.get(position);
                SyncTicketLocal tmp;
                String descripcion_item_ticket = "";

                // recorro todos los locales del mismo ticket y concateno las descripciones de los itemsTicket en un solo string
                do {
                    tmp = locales.get(position + offset);
                    descripcion_item_ticket += tmp.getDescripcionItemTicket() + " ";
                    offset++;
                }
                while ((position + offset) < locales.size() && tmp.getIdTicket() == local.getIdTicket());

                if (descripcion_item_ticket.contains("n (" + remoto.getAuthorization() + ")")) {
                    hasDaAuth = true;
                    break;
                }

                position += (offset - 1);
            }


            if (hasDaAuth) {
                if (!local.getDescripcionTicket().contains("Trans.: ")) {
                    injectTransaction(local.getIdTicket(), remoto.getTrans());

                    transaccionado++;
                }
                processed = true;

            } else {
                ////////////////////////////////////////////////////////////////////////////////////////
                //2) comparo datos, si coinciden, hay que inmsertar auth y trans y sino, inyectar completo
                position = 0;
                for (; position < locales.size(); position++) {

                    int offset = 0;
                    local = locales.get(position);
                    SyncTicketLocal tmp;
                    String descripcion_item_ticket = "";

                    // recorro todos los locales del mismo ticket y concateno las descripciones de los itemsTicket en un solo string
                    do {
                        tmp = locales.get(position + offset);
                        descripcion_item_ticket += tmp.getDescripcionItemTicket() + " ";
                        offset++;
                    }
                    while ((position + offset) < locales.size() && tmp.getIdTicket() == local.getIdTicket());

                    DebugLog.log(getClass(), "BRIO_SYNC_TICKET", String.format("\nlocal: %s\ndescripcion_item_ticket='%s'", Utils.pojoToString(local), descripcion_item_ticket));
                    //  || descripcion_item_ticket.contains("a (undefined)")
                    if ((descripcion_item_ticket.contains("(" + remoto.getReference() + ")")) &&
                            !descripcion_item_ticket.toUpperCase().contains("AUTORIZA")) {
                        //Agregarle item ticket con la autorizacion
                        //ticket fantasma ver 14
                        ItemsTicket itemAutorizacion = new ItemsTicket();

                        itemAutorizacion.setIdTicket(locales.get(position).getIdTicket());
                        itemAutorizacion.setDescripcion(String.format("Autorización (%s)", remoto.getAuthorization()));
                        itemAutorizacion.setCodigoBarras("");
                        itemAutorizacion.setCantidad(1.0);
                        itemAutorizacion.setImporteUnitario(0.0);
                        itemAutorizacion.setImporteTotal(0.0);
                        itemAutorizacion.setTimestamp(remoto.getGenerated());

                        managerModel.itemsTicket.save(itemAutorizacion);

                        DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "Inyectando autorizacion en ticket '" + local.getIdTipoTicket() + "'\n" + Utils.pojoToString(itemAutorizacion));

                        injectTransaction(local.getIdTicket(), remoto.getTrans());

                        autorizados++;
                        processed = true;

                        break;
                    }

                    position += (offset - 1); // saltar a los items del siguiente ticket en siguiente iteracion
                } //For

                if (!processed) {
                    injectTicket(remoto);
                }
            }
        }

    }

    /**
     * Funcion que se invoca para inyectar un ticket completo que no se encuetra
     * localmente, pero que si se encuentra en el servidor.
     *
     * @param remoto
     */
    private void injectTicket(SyncTicketInfo remoto) {
        try {
            //crear uno nuevo vaciando el remoto, dado que no hay registro local
            Ticket ticket = new Ticket();
            String desc = "Trans.: " + String.valueOf(remoto.getTrans());
            DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "nuevo desc: '" + desc + "'");
            ticket.setDescripcion(desc);
            ticket.setImporteBruto(remoto.getAmount());
            ticket.setImporteNeto(remoto.getAmount());
            ticket.setIdComercio(((Long) remoto.getComm()).intValue());
            ticket.setIdTipoTicket(ticketTypeMapping.get(remoto.getTtype()));
            ticket.setTimestamp(remoto.getGenerated());
            ticket.setCambio(0.0);

            //----------------------
            ItemsTicket itemServicio = new ItemsTicket();
            itemServicio.setDescripcion(remoto.getServ() + " a (" + remoto.getReference() + ")");
            itemServicio.setCantidad(1.0);
            double amount = remoto.getAmount() - comisionTypeMapping.get(remoto.getTtype());
            double comision = comisionTypeMapping.get(remoto.getTtype());
            String barcode = "";
            //if(ticketTypeMapping.get(remoto.getTtype())==7 && remoto.getCommi()==10.5) {
            if (ticketTypeMapping.get(remoto.getTtype()) == 7) {
                if (remoto.getCommi() == 10.5) {
                    amount = remoto.getAmount() - 30.0;
                    comision = 30.0;
                    barcode = "VENTATARJETA";
                }
                if (remoto.getCommi() == 4.9) {
                    barcode = "RECARGATARJETA";
                }
            }
            itemServicio.setImporteUnitario(amount);
            itemServicio.setImporteTotal(amount);
            itemServicio.setCodigoBarras(barcode);
            itemServicio.setTimestamp(remoto.getGenerated());

            //----------------------
            ItemsTicket itemComision = new ItemsTicket();
            itemComision.setDescripcion("Comisión por operación");
            itemComision.setCantidad(1.0);
            itemComision.setImporteUnitario(comision); //FIXME mapear la comision
            itemComision.setImporteTotal(comision);
            itemComision.setCodigoBarras("");
            itemComision.setTimestamp(remoto.getGenerated());

            //----------------------
            ItemsTicket itemAutorizacion = new ItemsTicket();
            itemAutorizacion.setDescripcion("Autorización (" + remoto.getAuthorization() + ")");
            itemAutorizacion.setCantidad(1.0);
            itemAutorizacion.setImporteUnitario(0.0);
            itemAutorizacion.setImporteTotal(0.0);
            itemAutorizacion.setCodigoBarras("");
            itemAutorizacion.setTimestamp(remoto.getGenerated());

            //----------------------
            TicketTipoPago pago = new TicketTipoPago();
            pago.setIdTipoPago(PaymentService.TIPO_PAGO_EFECTIVO);
            pago.setMonto(remoto.getAmount());

            //Generacion de superTicket
            SuperTicket superTicket = new SuperTicket();
            superTicket.ticket = ticket;
            superTicket.itemsTicket.add(new ItemsTicketController(itemServicio));
            superTicket.itemsTicket.add(new ItemsTicketController(itemComision));
            superTicket.itemsTicket.add(new ItemsTicketController(itemAutorizacion));
            superTicket.ticketTipoPago.add(pago);

            PaymentService.saveSuperTicket(context, superTicket);

            DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "Inyectando ticket completo \n" + Utils.pojoToString(superTicket) + "\ntimestamp: " + ticket.getDescripcion());

            creados++;

        } catch (Exception e) {
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "TicketSync->injectTicket" + e.getMessage());
            DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "Error: " + e.getMessage());

            e.printStackTrace();
        }
    }

    /**
     * Inyectar los datos de transaccíon faltantes en un ticket local existente.
     *
     * @param idTicket
     * @param transaction
     */
    private void injectTransaction(int idTicket, long transaction) {
        Ticket ticket = managerModel.tickets.getByIdTicket(idTicket);

        String desc = "Trans.: " + String.valueOf(transaction);

        DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "injectTransaction tenia '" + ticket.getDescripcion() + "', ahora '" + desc + "'");

        ticket.setDescripcion(desc);
        Long id = managerModel.tickets.save(ticket);

        Ticket tmpticket = managerModel.tickets.getByIdTicket(idTicket);
        DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "injectTransaction id: " + id + " tiene: " + Utils.pojoToString(tmpticket));

    }

    /**
     * Mapeo de los tipos de ticket de la app web (servidor) y de android
     */
    private static Map<Integer, Integer> ticketTypeMapping = new HashMap<>();
    private static Map<Integer, Double> comisionTypeMapping = new HashMap<>();

    static {
        ticketTypeMapping.put(1, PaymentService.TIPO_TICKET_TAE);
        ticketTypeMapping.put(2, PaymentService.TIPO_TICKET_SERVICIO);
        ticketTypeMapping.put(6, PaymentService.TIPO_TICKET_INTERNET);
        ticketTypeMapping.put(7, PaymentService.TIPO_TICKET_BANCO_ENTRADA);

        comisionTypeMapping.put(1, 0.0);
        comisionTypeMapping.put(2, 9.0);
        comisionTypeMapping.put(6, 0.0);
        comisionTypeMapping.put(7, 0.0);
    }

    /**
     * Pojo para mantener los datos del request
     * al servicio web.
     */
    public class SyncTicketRequest {
        private Long idComercio;
        private Long lastTransaction;

        public Long getLastTransaction() {
            return lastTransaction;
        }

        public void setLastTransaction(Long lastTransaction) {
            this.lastTransaction = lastTransaction;
        }

        public Long getIdComercio() {
            return idComercio;
        }

        public void setIdComercio(Long idComercio) {
            this.idComercio = idComercio;
        }
    }
}