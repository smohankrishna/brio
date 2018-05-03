package net.hova_it.barared.brio.apis.setup;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.sync.RestSyncService;
import net.hova_it.barared.brio.apis.utils.Utils;

import org.json.JSONObject;

import lat.brio.core.BrioGlobales;
import lat.brio.core.bussines.BrCommerce;

/**
 * Obtener el estaus del comercio.
 * Las constantes COMM_STATUS_* indican los status asignables en Betria para los comercios.
 * De acuerdo al estatus, se aplicara una acción ACT_*
 * La cual puede ser, flujo normal, no dar acceso a la pestaña de servicios o no dejar entrar
 * a la aplicación.
 * Lo que realiza esta clase es tratar de obtener el estatus del comercio indicado desde el servicio
 * rest. Si lo encuentra, actualiza el setting SET_COMMERCE_STATUS, y si no lo encuentra, regresa el
 * estatus actual en SET_COMMERCE_STATUS.
 *
 * Created by Herman Peralta on 28/11/2016.
 */
public class CommerceStatusChecker extends RestSyncService {
    public final static String SET_COMMERCE_STATUS = "COMMERCE_STATUS";

    public final static String COMM_STATUS_OPERANDO      = "1";
    public final static String COMM_STATUS_CANCELADO     = "2";
    public final static String COMM_STATUS_LEGAL         = "3";
    public final static String COMM_STATUS_PROCCANCELADO = "4";
    public final static String COMM_STATUS_SUSPENDIDO    = "5";

    public final static int ACT_OK             = 1;
    public final static int ACT_NO_SERVICES    = 2;
    public final static int ACT_SHALL_NOT_PASS = 3;

    private String commerce_status = COMM_STATUS_OPERANDO;
    private int action = ACT_OK;

    private RestSyncListener restSyncListener;

    private final static String
            url = "/cabws/api/getCommerceInfo?idComercio={idComercio}";

   // private ModelManager modelManager;
    private SessionManager sessionManager;
    private BrCommerce brCommerce;

    public CommerceStatusChecker(Context context, RestSyncListener restSyncListener) {
        super(context,
                restSyncListener,
                ("http://" + BrioGlobales.URL_ZERO_BRIO+ url),
                Request.Method.GET,
                0);

        this.restSyncListener = restSyncListener;

       // modelManager = new ModelManager(context);
        sessionManager = SessionManager.getInstance(context);
        brCommerce=BrCommerce.getInstance(context,BrioGlobales.getAccess());
    }

    /**
     * Obtener el estatus obtenido del servicio web
     * @return
     */
    public String getStatus() {
        return commerce_status;
    }

    /**
     * Obtener la acción a realizar de acuerdo al estatus obtenido
     * @return
     */
    public int getAction() {
        return action;
    }

    /**
     * Consultar el servicio web para obtener el estatus.
     * El estatus se guarda en sqlite/SETTINGS/SET_COMMERCE_STATUS
     * @param response
     */
    private void obtainStatus(JSONObject response) {
        Settings commStatus;

        try {
            commerce_status = ""+ ((JSONObject)response.get("response")).get("status");
        } catch (Exception e) {
            e.printStackTrace();

            commerce_status = COMM_STATUS_OPERANDO; //default

            commStatus = brCommerce.getByNombre(SET_COMMERCE_STATUS);
            if(commStatus != null) {
                commerce_status = commStatus.getValor();
            }
        } finally {
            commStatus = new Settings(SET_COMMERCE_STATUS, commerce_status);
            brCommerce.updateSetting(commStatus);
            //modelManager.settings.save(commStatus);
        }

        switch(commerce_status) {
            case COMM_STATUS_OPERANDO:
                //todo ok
                action = ACT_OK;
                break;

            case COMM_STATUS_CANCELADO:
            case COMM_STATUS_PROCCANCELADO:
                //no deja entrar
                action = ACT_SHALL_NOT_PASS;
                break;

            case COMM_STATUS_LEGAL:
            case COMM_STATUS_SUSPENDIDO:
            default:
                //sin servicios
                action = ACT_NO_SERVICES;
                break;
        }
    }

    @Override
    protected boolean processResponse(JSONObject response) {
        obtainStatus(response);

        return true;
    }

    @Override
    protected void processError(VolleyError volleyError) {
        obtainStatus(null);
    }

    @Override
    protected String getJsonRequest() {
        this.serviceUrl = ("http://" +BrioGlobales.URL_ZERO_BRIO + url.replace("{idComercio}", "" + sessionManager.readInt("idComercio")));

        Log.i("COMMERCE_STATUS", "URL: " + serviceUrl);

        return "{}";
    }
}
