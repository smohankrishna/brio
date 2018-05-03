package net.hova_it.barared.brio.apis.sync;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.models.entities.SyncTicketInfo;
import net.hova_it.barared.brio.apis.models.entities.SyncTicketInfoContainer;
import net.hova_it.barared.brio.apis.payment.PaymentService;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;

/**
 * Consume un servicio web que proporciona la informacion sobre las transacciones de Servicios, TAE
 * e Internet.
 * Created by Herman Peralta on 06/05/2016.
 */
public class Remove0Sync extends RestSyncService {

    private String lastTransaction;
    private String idComercio;

    //public ArrayList<String> transacciones;
    //public ArrayList<Long> timestamps;

    public ArrayList<PatchPojo> pojos;
    public String in = "";

    int autorizados = 0, creados = 0, transaccionado = 0;

    private final static String  url = "/session/api/tickets?idComercio=?0&lastTransaction=?1";

    public Remove0Sync(Context context) {
        super(context, null, ("http://" + BrioGlobales.URL_ZERO_BRIO + url)
                        .replace("?0", "")
                        .replace("?1", ""),
                Request.Method.GET, 0);
    }
    public void setListener( RestSyncListener restSyncListener) {
        super.restSyncListener = restSyncListener;
    }

    /**
     * Obtiene un json con la informacion de las transacciones del comercio a partir del registro
     * guardado en la tabla "Settings" con la descripcion "LAST_TRANSACTION_FIX_TIME"
     * @return
     */
    @Override
    protected String getJsonRequest() {
        String tmp = null;
        try {
            idComercio = managerModel.settings.getByNombre("ID_COMERCIO").getValor();

        Settings last_transaction = managerModel.settings.getByNombre("LAST_TRANSACTION_FIX_TIME");
        lastTransaction = last_transaction == null ? "0" : last_transaction.getValor();
        if(last_transaction == null) {
            last_transaction = new Settings();
            last_transaction.setNombre("LAST_TRANSACTION_FIX_TIME");
            last_transaction.setValor("0");

            managerModel.settings.save(last_transaction);
        }

        //lastTransaction = "0";

        //Fixme, regresar el string del json
        serviceUrl = ("http://" + BrioGlobales.URL_ZERO_BRIO+ url)
                .replace("?0", idComercio)
                .replace("?1", lastTransaction);

        DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "serviceUrl: " + serviceUrl);

        //String json = jsonRequest
        //        .replace("?idComercio", idComercio)
        //        .replace("?lastTransaction", lastTransaction);

        SyncTicketRequest request = new SyncTicketRequest();
        request.setIdComercio(Long.parseLong(idComercio));
        request.setLastTransaction(Long.parseLong(lastTransaction));

        //Verificar que se forme bien el json
        Gson gson = new Gson();
        tmp = gson.toJson(request);
        DebugLog.log(getClass(), "BRIO_SYNC_TICKET", tmp);

        } catch (Exception e) {

            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + getClass() + "->getJsonRequest" + e.getMessage());
        }


        return tmp;
    }

    /**
     * Analiza el contenido del json validando si la transaccion existe en la DB.
     * En caso de no existir se procede a guardar el registro en la DB
     * @param response - La respuesta obtenida del web service.
     * @return
     */
    @Override
    protected boolean processResponse(final JSONObject response) {

        //transacciones = new ArrayList<>();
        //timestamps = new ArrayList<>();

        pojos = new ArrayList<>();
        in = "";


        boolean synched = false;
        SyncTicketInfoContainer syncTicketInfoContainer = null;

        try {
            Gson gson = new Gson();
            syncTicketInfoContainer = gson.fromJson(response.toString(1), SyncTicketInfoContainer.class);

            DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "Response: \n" + response.toString(1) + "\nSize:" + syncTicketInfoContainer.getSyncTicketInfos().size());
            DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "POJO: \n" + Utils.pojoToString(syncTicketInfoContainer));

            if(syncTicketInfoContainer.getLastTransaction() == -1) {
                DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "ERROR: No se recibieron datos... abortando");

                return false;
            }

            //if(syncTicketInfoContainer.getIdComercio() != Integer.valueOf(/*managerModel.settings.getByNombre("ID_COMERCIO").getValor()*/ idComercio)) {
            if (syncTicketInfoContainer.getIdComercio() != Integer.valueOf(idComercio)) {

                //Los datos obtenidos no corresponden a este comercio, abortar
                DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "ERROR: Los datos obtenidos no corresponden a este comercio, abortando");

                return false;
            }

            if(syncTicketInfoContainer.getLastTransaction() > Integer.parseInt(lastTransaction)) {
                DebugLog.log(getClass(), "BRIO_SYNC_TICKET", String.format("Se encontraron %d registros. Iniciando sincronizacion...", syncTicketInfoContainer.getSyncTicketInfos().size()));
                int i=0;
                for(SyncTicketInfo remoto : syncTicketInfoContainer.getSyncTicketInfos()) {
                    DebugLog.log(getClass(), "BRIO_SYNC_TICKET", String.format("remoto %d/%d: POJO:\n%s", i+1, syncTicketInfoContainer.getSyncTicketInfos().size(), Utils.pojoToString(remoto)));
                    if(remoto.getAuthorization() != null) {
                        if (remoto.getAuthorization().length() != 0) {
                            pojos.add(new PatchPojo(String.format("Trans.: %d", remoto.getTrans()), remoto.getGenerated()));
                            //transacciones.add(String.format("Trans.: %d", remoto.getTrans()));
                            //timestamps.add(remoto.getGenerated());

                            //boolean processed = processRemoteTicket(remoto);
                            //DebugLog.log(getClass(), "BRIO_SYNC_TICKET", String.format("remoto %d/%d: %s", i+1, syncTicketInfoContainer.getSyncTicketInfos().size(), processed));
                        }
                    }
                    i++;
                }
            } else {
                DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "Todo esta sincronizado, no hay nada que hacer");
            }

            synched = true;
        } catch (Exception e) {
          //  BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + getClass() + "->processResponse" + e.getMessage());
            DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "Error: " + e.getMessage());

            e.printStackTrace();
        } finally {

            if(synched) {
                managerModel.settings.update("LAST_TRANSACTION_FIX_TIME", String.valueOf(syncTicketInfoContainer.getLastTransaction()));
                DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "updated LAST_TRANSACTION_FIX_TIME: " + syncTicketInfoContainer.getLastTransaction());
            }

            DebugLog.log(getClass(), "BRIO_SYNC_TICKET", String.format("resultados-----------\nautorizados %d\ncreados %d\ntransaccionados %d\ntotal %d", autorizados, creados, transaccionado, (autorizados + creados + transaccionado)));
        }

        if(pojos.size() > 0) {
            for(int i=0; i<pojos.size() ; i++) {
                in += "\""+pojos.get(i).transaccion+"\"";
                if(i < pojos.size() -1) { in += ","; }
            }
        }

        return synched;
    }

    @Override
    protected void processError(VolleyError volleyError) {
        DebugLog.log(getClass(), "BRIO_SYNC_TICKET", "error: " + volleyError.getMessage());
        volleyError.printStackTrace();
        BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + getClass() + "->processResponse" + volleyError.getMessage());
    }

    /**
     * Mapeo de los tipos de ticket de la app web y de android
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

    private class SyncTicketRequest {
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

    public class PatchPojo {
        public String transaccion;
        public Long timestamp;

        public PatchPojo(String transaccion, Long timestamp) {
            this.transaccion = transaccion;
            this.timestamp = timestamp;
        }

    }
}