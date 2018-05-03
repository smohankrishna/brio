package net.hova_it.barared.brio.apis.hw.billpocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.CardPaymentRequest;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;

import java.util.HashMap;
import java.util.Map;

import lat.brio.core.BrioGlobales;

/**
 * Esta clase permite enviar pagos a la app BillPocket mediante un intent.
 *
 * Verifica que BillPocket esté instalado.
 *
 * Created by Herman Peralta on 15/03/2016.
 */
public class BillPocketService {

    public final static int BP_INTENT_CODE = 666;

    /** El pin por default de Barared */
    public final static String ARG_PIN = "5656";

    private static BillPocketService daService;

    private Context context;

    //private FileLogger fileLogger;

    private Map<String, BillPocketChicken> queue = new HashMap<>();

    public static BillPocketService getInstance(Context context) {
        if(daService == null) {
            daService = new BillPocketService(context);
        }

        return daService;
    }

    private BillPocketService(Context context) {
        this.context = context;
    }

    /**
     * Enviar la información para realizar un pago con BillPocket.
     * La respuesta de BillPocket llega por @see net.hova_it.barared.brio.apis.payment.PaymentService.onActivityResult()
     * @param chicken pojo e los parametros
     */
    public void sendPayment(BillPocketChicken chicken) {
        DebugLog.log(getClass(), "PAYMENT", "recibo el pojo");
        Intent intent = new Intent(BrioGlobales.META_BILLPOCKET_PAYMENT_SERVICE);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            DebugLog.log(getClass(), "PAYMENT", "Billpocket app instalada");

            //Mandatory request
            intent.putExtra("pin", chicken.getPin());
            intent.putExtra("amount", chicken.getAmount());
            intent.putExtra("transaction", chicken.getTransaction());
            intent.putExtra("identifier", chicken.getIdentifier());
            intent.putExtra("reference", chicken.getReference()); //TODO: agregar la referencia (# cuenta, # tel etc).
            intent.putExtra("xpLandscape", chicken.getXpLandscape()); //para poner en horizontal solo pruebas con version "Billpocket 57 Demo_LS.apk"
            intent.putExtra("3pID", "bpfrontdesk"); /* mostrar logo brio */

            DebugLog.log(getClass(), "BILLPOCKET", "Parametros:\n"
                    + "pin=" + chicken.getPin() + "\n"
                    + "amount=" + chicken.getAmount() + "\n"
                    + "transaction=" + chicken.getTransaction() + "\n"
                    + "identifier=" + chicken.getIdentifier() + "\n"
                    + "reference=" + chicken.getReference() + "\n");

            queue.put(chicken.getIdentifier(), chicken);

            DebugLog.log(getClass(), "PAYMENT", "ANTES DE MANDAR INTENT A Billpocket");


           /* try {  Se comento porque se quedaba pasmado en el cobro por tarjeta se comento todo el filelogger
                fileLogger = FileLogger.get(context, FL_KEY_BILLPOCKET);
                fileLogger.log(chicken, String.format("[%s] Enviando a BP BillPocketService.sendPayment", chicken.getIdentifier()));

                //Eliminar la instancia del FileLogger para ahorrar memoria.
                FileLogger.delete(fileLogger);
            }catch (Exception e) {
                fileLogger = null;
            }*/
            ((BrioActivityMain) context).startActivityForResult(intent, BP_INTENT_CODE);
        } else {
            DebugLog.log(getClass(), "PAYMENT", "Billpocket app NO instalada");
            BrioConfirmDialog bcd = new BrioConfirmDialog((AppCompatActivity)context, null, Utils.getString(R.string.payment_billpocket_notfound, context), null, new DialogListener() {
                @Override
                public void onAccept() {
                    Utils.launchPlayStoreForApp((AppCompatActivity)context, BrioGlobales.NAME_PACKAGE_BILLPOCKET);
                }

                @Override
                public void onCancel() {

                }
            });
            bcd.show();
        }
    }

    /**
     * Verifica si la fue aprobado o declinado el pago
     *
     * @param intent se pasan los parametros atraves de un intent
     * @return el Pojo
     */
    public BillPocketChicken managePayment(Intent intent) {
        Bundle extras = intent.getExtras();
        BillPocketChicken chicken = null;

      //  fileLogger = FileLogger.get(context, FL_KEY_BILLPOCKET);

        if (extras != null) {
            String identifier = extras.getString("identifier");

            chicken = queue.get(identifier);
            queue.remove(identifier);

            String result = (String)extras.get("result");
            if(result == null) { result = BillPocketChicken.RESULT_ERROR; }

            switch (result) {
                case BillPocketChicken.RESULT_APPROVED:
                    Object value;
                    int invalido = 0;
                    //Aprobada

                    value = extras.get("reference");
                    invalido += (value == null ? 1 : 0);
                    chicken.setReference(value != null ? (String) value : "error");

                    value = extras.get("transactionid");
                    invalido += (value == null ? 1 : 0);
                    chicken.setTransactionid(value != null ? (String) value : "error");

                    value = extras.get("amount");
                    invalido += (value == null ? 1 : 0);
                    chicken.setAmount(Double.parseDouble(value != null ? (String) value : "0"));
                    if (value == null) { chicken.setReference(chicken.getReference() + ", error_amount " /*+ amount original*/); }

                    value = extras.get("authorization");
                    invalido += (value == null ? 1 : 0);
                    chicken.setAuthorization(value != null ? (String) value : "error");

                    value = extras.get("creditcard");
                    invalido += (value == null ? 1 : 0);
                    chicken.setCreditcard(value != null ? (String) value : "error");

                    value = extras.get("cardtype");
                    invalido += (value == null ? 1 : 0);
                    chicken.setCardtype(value != null ? (String) value : "error");

                    value = extras.get("url");
                    invalido += (value == null ? 1 : 0);
                    chicken.setUrl(value != null ? (String) value : "error");

                    if(invalido>0) {chicken.setResult(BillPocketChicken.RESULT_APPROVED_INVALIDO);}
                    break;

                case BillPocketChicken.RESULT_DECLINED:
                    //Declinada
                    chicken.setStatusinfo((String) extras.get("statusinfo"));
                    break;

                default:
                    //Error
                    chicken.setStatusinfo((String) extras.get("statusinfo"));
                    break;
            }

            //llenar el pojo
            chicken.setResult(result);

            //fileLogger.log(result, String.format("[%s] Recibido de BP BillPocketService.managePayment", chicken.getIdentifier()));
        } else {
            //fileLogger.log("Recibido de BP BillPocketService.managePayment extras era null");
        }

       /* FileLogger.delete(fileLogger);
        fileLogger = null;*/

        return chicken;
    }

    /**
     * Obtener el Pojo que se enviara al servicio de conciliación de pagos
     * Billpocket
     *
     * @param context
     * @param chicken
     * @return
     */
    public static CardPaymentRequest getCardPaymentRequest(Context context, BillPocketChicken chicken) {

        ModelManager managerModel = new ModelManager(context);
        SessionManager managerSession = SessionManager.getInstance(context);

        CardPaymentRequest cardPaymentRequest = new CardPaymentRequest();
        cardPaymentRequest.setIdComercio(Long.parseLong(managerModel.settings.getByNombre("ID_COMERCIO").getValor()));
        cardPaymentRequest.setIdUsuario(managerSession.readInt("idUsuario"));
        cardPaymentRequest.setIdTicket(managerSession.readInt("idTicket"));
        cardPaymentRequest.setTimestamp(Utils.getCurrentTimestamp());
        cardPaymentRequest.setTransactionid(chicken.getTransactionid());
        cardPaymentRequest.setAmount(chicken.getAmount());
        cardPaymentRequest.setReference(chicken.getReference());
        cardPaymentRequest.setAuthorization(chicken.getAuthorization());
        cardPaymentRequest.setCreditcard(chicken.getCreditcard());
        cardPaymentRequest.setCardtype(chicken.getCardtype());
        cardPaymentRequest.setUrl(chicken.getUrl());

        return cardPaymentRequest;
    }
}
