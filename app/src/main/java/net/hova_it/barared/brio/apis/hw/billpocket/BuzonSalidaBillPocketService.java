package net.hova_it.barared.brio.apis.hw.billpocket;

import android.content.Context;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.CardPaymentRequest;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.sync.BillPocketSync;
import net.hova_it.barared.brio.apis.sync.RestSyncService;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;

/**
 * Clase para enviar uno a uno los tickets pagados con BillPocket al servicio
 * web de captura de BillPocket.
 * <p>
 * Dado que el POST al servicio web es asíncrono, se envía ticket por ticket de la siguiente forma:
 * Se obtiene la lista de todos los tickets pagados con BillPocket que no
 * se han enviado al servicio web. Se envía el primer ticket. Cuando termina el envío
 * se envía el siguiente ticket. Este proceso se repite hasta terminar la lista de pendientes
 * por enviar. Si algún ticket no fue recibido por el servicio web, se vuelve a encolar
 * en la cola de pendientes por enviar.
 *
 * @see BillPocketSync
 * <p>
 * Created by Herman Peralta on 24/05/2016.
 */
public class BuzonSalidaBillPocketService implements RestSyncService.RestSyncListener {

    public final static String SETTING_BUZON_BILLPOCKET = "BUZON_BILLPOCKET";

    private static BuzonSalidaBillPocketService daService;

    private Context context;
    private BrioActivityMain activityMain;
    private ModelManager managerModel;

    private ArrayList<Integer> ticketTipoPagosBillPocket;
    private ArrayList<Boolean> pendienteStatus;

    private BillPocketSync billPocketSync;
    private BuzonSalidaBillPocketListener buzonSalidaBillPocketListener;

    public static BuzonSalidaBillPocketService getInstance(Context context) {
        if (daService == null) {
            daService = new BuzonSalidaBillPocketService(context);
        }

        return daService;
    }

    private BuzonSalidaBillPocketService(Context context) {
        this.context = context;
        this.activityMain = (BrioActivityMain) context;
        this.managerModel = new ModelManager(context);

        Settings buzon = managerModel.settings.getByNombre(SETTING_BUZON_BILLPOCKET);
        if (buzon == null) {
            buzon = new Settings();
            buzon.setNombre(SETTING_BUZON_BILLPOCKET);
            buzon.setValor("");

            managerModel.settings.save(buzon);
        }
    }

    /**
     * Iniciar el envío de tickets
     */
    public void sync() {
        billPocketSync = new BillPocketSync(context, this);

        activityMain.splash.show();
        activityMain.splash.publish("Sincronizando datos de BillPocket...");

        ticketTipoPagosBillPocket = readPendingTicketTipoPago();
        pendienteStatus = new ArrayList<>();

        for (int i = 0; i < ticketTipoPagosBillPocket.size(); i++) {
            pendienteStatus.add(false);
        }

        if (ticketTipoPagosBillPocket.size() > 0) {
            //traerme todos los encolados, meterlos en ticketTipoPagosBillPocket
            billPocketSync.sync(0, getCardPaymentRequest(ticketTipoPagosBillPocket.get(0)));
        } else {
            activityMain.splash.dismiss();
            buzonSalidaBillPocketListener.onBuzonSalidaBillPocketFinished();
        }
    }

    /**
     * Encolar una lista de pagos con tarjeta
     *
     * @param ids
     */
    public void queueIdTicketTipoPagos(ArrayList<Integer> ids) {
        String queue = readPendingTicketTipoPagoAsString();
        for (int id : ids) {
            queue += String.format("%d;", id);
        }
        managerModel.settings.update(SETTING_BUZON_BILLPOCKET, queue);
    }

    public void setBuzonSalidaBillPocketListener(BuzonSalidaBillPocketListener listener) {
        this.buzonSalidaBillPocketListener = listener;
    }

    /**
     * Se invoca cuando se ha terminado de enviar un ticket.
     * Si el envio no fue exitoso, se guarda una bandera para saber que no fue enviado y
     * se procede a enviar el siguiente ticket. Este proceso se repite hasta que no haya
     * tickets por enviar.
     *
     * @param taskId  - un identificador de task
     * @param synched - True si se pudo consultar, se obtuvo respuesta válida y se procesó divha respuesta. false en otro caso.
     */
    @Override
    public void onRestSyncFinished(int taskId, boolean synched) {
        pendienteStatus.set(taskId, synched);

        DebugLog.log(BuzonSalidaBillPocketService.this.getClass(), "BRIO_BUZON_BILLPOCKET", "***** BuzonBillPocket Result for taskId: " + taskId + ": " + synched + " *****");

        taskId++;
        if (taskId < ticketTipoPagosBillPocket.size()) {
            billPocketSync.sync(taskId, getCardPaymentRequest(ticketTipoPagosBillPocket.get(taskId)));
        } else {
            queuePending();
        }
    }

    /**
     * Encolar los tickets que no se pudieron enviar.
     */
    private void queuePending() {
        int cont = 0;

        String pending = "";
        for (int i = 0; i < ticketTipoPagosBillPocket.size(); i++) {
            if (pendienteStatus.get(i)) {
                cont++;
            } else {
                pending += String.format("%d;", ticketTipoPagosBillPocket.get(i));
            }
        }
        Toast.makeText(context, "Enviados: " + cont + "/" + pendienteStatus.size(), Toast.LENGTH_SHORT).show();

        //guardo los pendientes
        managerModel.settings.update(SETTING_BUZON_BILLPOCKET, pending);

        //quito el splash
        activityMain.splash.dismiss();

        buzonSalidaBillPocketListener.onBuzonSalidaBillPocketFinished();
    }

    /**
     * convertir el ticket a enviar en un request de pago con tarjeta.
     *
     * @param idTicketTipoPago
     * @return
     */
    private CardPaymentRequest getCardPaymentRequest(int idTicketTipoPago) {

        String msg = "CardPaymentRequest para TicketTipoPago id=" + idTicketTipoPago + " ";
        CardPaymentRequest cardPaymentRequest = null;
        TicketTipoPago ticketTipoPago = managerModel.ticketTipoPago.getByIdTicketTipoPago(idTicketTipoPago);
        if (ticketTipoPago != null) {
            //"{\"amount\":\"10.0\",\"authorization\":\"065040\",\"cardtype\":\"VISA\",\"creditcard\":\"9693\",\"idComercio\":10015,\"idTicket\":0,\"idUsuario\":1,\"reference\":\"prueba\",\"timestamp\":1464125978,\"transactionid\":\"17127\",\"url\":\"369b35ac15709446de9ac5eac1ce9d9c122e33d5\"}"
            cardPaymentRequest = (CardPaymentRequest) Utils.fromJSON(ticketTipoPago.getDescripcion(), CardPaymentRequest.class);
            msg += Utils.toJSON(cardPaymentRequest);
        } else {
            msg += "null";
        }

        DebugLog.log(getClass(), "BRIO_BUZON_BILLPOCKET", msg);

        return cardPaymentRequest;
    }

    /**
     * Lee la cadena de ids de TicketTipoPago pendientes por enviar al servicio, y regresa una lista con sus ids
     *
     * @return
     */
    private ArrayList<Integer> readPendingTicketTipoPago() {

        ArrayList<Integer> pendingTicketTipoPago = new ArrayList<>();
        try {
            String pending = managerModel.settings.getByNombre(SETTING_BUZON_BILLPOCKET).getValor();

            if (pending.length() > 0) {
                String[] ids = pending.split(";");
                for (String id : ids) {

                    DebugLog.log(getClass(), "BRIO_BUZON_BILLPOCKET", "add id ticketTipoPago=" + id);

                    int idTicket = Integer.parseInt(id);
//                    Valida si el ticket existe, sino, no se agrega
                    if (getCardPaymentRequest(idTicket) != null) {
                        pendingTicketTipoPago.add(idTicket);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + getClass() + "->readPendingTicketTipoPago" + e.getMessage());

        }

        return pendingTicketTipoPago;
    }

    /**
     * Obtener un string con los ids de los pagos con tarjeta pendientes por enviar.
     *
     * @return
     */
    private String readPendingTicketTipoPagoAsString() {
        StringBuilder squeue = new StringBuilder("");

        ArrayList<Integer> queue = readPendingTicketTipoPago();
        for (int id : queue) {
            squeue.append(String.format(new Locale("es","MX"),"%d;", id));
        }

        return squeue.toString();
    }

    public interface BuzonSalidaBillPocketListener {
        /**
         * Este metodo se invoca cuando ya no hay pagos con tarjeta por enviar.
         */
        void onBuzonSalidaBillPocketFinished ();
    }
}
