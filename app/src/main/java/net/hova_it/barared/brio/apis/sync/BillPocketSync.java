package net.hova_it.barared.brio.apis.sync;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import net.hova_it.barared.brio.apis.models.entities.CardPaymentRequest;
import net.hova_it.barared.brio.apis.models.entities.CardPaymentResponse;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;

import org.json.JSONObject;

import lat.brio.core.BrioGlobales;

/**
 * Enviar un ticket pagado con BillPocket
 * al servicio web de manejo de pagos de BillPocket.
 *
 * @see RestSyncService
 *
 * Created by Herman Peralta on 24/05/2016.
 */
public class BillPocketSync extends RestSyncService {
    private final static String
            url = "/finance/api/card";

    private CardPaymentRequest cardPaymentRequest;

    public BillPocketSync(Context context, RestSyncListener restSyncListener) {
        super(context, restSyncListener, ("http://" + BrioGlobales.URL_CARD_IP_BRIO + url), Request.Method.POST, 0);
        showDialog = false;
    }

    @Override
    protected String getJsonRequest() {
        return Utils.toJSON(cardPaymentRequest);
    }

    public void sync(int taskId, CardPaymentRequest cardPaymentRequest) {
        this.taskId = taskId;
        this.cardPaymentRequest = cardPaymentRequest;

        DebugLog.log(getClass(), "BRIO_BILLPOCKET_SYNC", "init Sync");

        sync();
    }

    @Override
    protected boolean processResponse(JSONObject response) {
        boolean synched = true;

        try {
            Gson gson = new Gson();
            CardPaymentResponse cardPaymentResponse = gson.fromJson(response.toString(1), CardPaymentResponse.class);
            DebugLog.log(getClass(), "BRIO_BILLPOCKET_SYNC", "BillPocketSync RESPONSE: " + Utils.pojoToString(cardPaymentResponse));

            if(cardPaymentResponse.getTransactionId() < 0) {
                synched = false;
            }

        } catch (Exception e) {
            e.printStackTrace();

            synched = false;
        }

        return synched;
    }

    @Override
    protected void processError(VolleyError volleyError) {

        if(volleyError!=null){
            DebugLog.log(getClass(), "BRIO_BILLPOCKET_SYNC", "VOLLEY ERROR: " + volleyError.getMessage());
        }
    }
}
