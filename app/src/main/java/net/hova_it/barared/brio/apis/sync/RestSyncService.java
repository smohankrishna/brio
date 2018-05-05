package net.hova_it.barared.brio.apis.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.network.NetworkStatus;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;

/**
 * Clase abstracta que permite consultar un servicio web por GET o enviar
 * datos por POST.
 *
 * Por default, mientras se consulta el servivio web y se procesa la respuesta / error,
 * se mantiene un splash dialog con un progressbar que impide la interacción del usuario
 * mientras se trabaja de forma asíncrona.
 *
 *
 * Actualmente solo funciona para péticiones POST.
 * Para GET, hay que harcodear los parametros en la url serviceUrl
 * o usar StriungRequest https://developer.android.com/training/volley/simple.html
 * Created by Herman Peralta on 06/05/2016.
 */
public abstract class RestSyncService {
    
    protected String
            splashTitle = "Recuperando datos.\nEsta operación puede tomar varios minutos.\nPor favor espere...";
    
    protected String
            serviceUrl = null;
    
    protected Context context;
    protected RequestQueue mRequestQueue;
    protected ModelManager managerModel;
    protected RestSyncListener restSyncListener;
    
    private int method;
    protected int taskId;
    
    protected boolean showDialog = true;
    
    protected DefaultRetryPolicy VOLLEY_RETRY_POLICY = new DefaultRetryPolicy (30000, 1, 0f); //El timeout para que volley mande excepcion, El máximo número de reintentos (1 + VOLLEY_RETRY)
    
    /**
     * @param context
     * @param restSyncListener
     * @param serviceUrl
     * @param method - com.android.volley.Request.Method.GET o com.android.volley.Request.Method.POST
     */
    public RestSyncService (Context context, RestSyncListener restSyncListener, String serviceUrl, int method, int taskId) {
        this (context, serviceUrl, method);
        this.restSyncListener = restSyncListener;
        this.taskId = taskId;
    }
    
    public RestSyncService (Context context, String serviceUrl, int method) {
        this.context = context;
        this.serviceUrl = serviceUrl;
        this.method = method;
        
        mRequestQueue = Volley.newRequestQueue (context);
        managerModel = new ModelManager (context);
    }
    
    /**
     * Realiza la petición al servicio web aplicando
     * la política de reintentos VOLLEY_RETRY_POLICY.
     * <p>
     * Si se pudo consultar correctamente el servicio,
     * se invoca processResponse(response) de forma
     * asíncrona para que las clases que extiendan
     * de esta clase puedan procesar los datos del
     * servicio web.
     * <p>
     * Si ocurrió un error con la petición,
     * se invoca processError(error).
     */
    public void sync () {
        if (NetworkStatus.hasInternetAccess (context)) {
            
            if (showDialog) {
                ((BrioActivityMain) context).splash.show ();
                ((BrioActivityMain) context).splash.publish (splashTitle);
            }
            
            String strJsonRequest = getJsonRequest ();
            
            DebugLog.log (getClass (), "BRIO_REST_SYNC", "serviceURL: " + serviceUrl + ", method: " + method + ", request: '" + strJsonRequest);
            
            JsonObjectRequest request;
            try {
                
                JSONObject jsonrequest = null;
                if (strJsonRequest != null) {
                    jsonrequest = new JSONObject (strJsonRequest);
                }
                
                request = new JsonObjectRequest (
                        method,
                        serviceUrl,
                        jsonrequest,
                        new Response.Listener<JSONObject> () {
                            @Override
                            public void onResponse (JSONObject response) {
                                DebugLog.log (getClass (), "BRIO_REST_SYNC", "got json response: " + Utils.pojoToString (response));
                                
                                ProcessTask processTask = new ProcessTask ();
                                processTask.execute (response);
                            }
                        },
                        new Response.ErrorListener () {
                            @Override
                            public void onErrorResponse (VolleyError volleyError) {
                                // DebugLog.log(getClass(), "BRIO_REST_SYNC", "ERROR: " + volleyError.getMessage());
                                //                            volleyError.printStackTrace();
                                // BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + getClass() + "->sync->onErrorResponse" + volleyError.getMessage());
                                doOnErrorResponse (volleyError);
                            }
                        });
                
                //Establecer politica de reintentos al servicio web, si no puede conectarse en ese tiempo, lanza excepcion
                //El timeout es de 500
                request.setRetryPolicy (VOLLEY_RETRY_POLICY);
                //  DebugLog.log(getClass(), "BRIO_REST_SYNC", String.format("timeout=%dms", VOLLEY_RETRY_POLICY.getCurrentTimeout()));
                
                mRequestQueue.add (request);
            } catch (JSONException e) {
                BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + getClass () + "->sync" + e.getMessage ());
                e.printStackTrace ();
                
                doOnErrorResponse (null);
            }
        }else{
            if (showDialog) {
                ((BrioActivityMain) context).splash.dismiss ();
            }
            restSyncListener.onRestSyncFinished (taskId, false);
        }
        
    }
    
    /**
     * Invocado para indicar al que hubo un error.
     * @param volleyError
     */
    private void doOnErrorResponse (VolleyError volleyError) {
        if (volleyError != null) {
            volleyError.printStackTrace ();
        }
        
        processError (volleyError);
        
        if (showDialog) {
            ((BrioActivityMain) context).splash.dismiss ();
        }
        
        restSyncListener.onRestSyncFinished (taskId, false);
    }
    
    /**
     * Permite ejecutar el procesamiento de la respuesta del servicio
     * web de forma asíncrona.
     */
    class ProcessTask extends AsyncTask<JSONObject, Void, Boolean> {
        
        @Override
        protected Boolean doInBackground (JSONObject... params) {
            boolean resp = false;
            try {
                resp = processResponse (params[0]);
            } catch (Exception e) {
                e.printStackTrace ();
                BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + "ProcessTask->doInBackground" + e.getMessage ());
            }
            
            return resp;
        }
        
        @Override
        protected void onPostExecute (Boolean processed) {
            if (showDialog) {
                ((BrioActivityMain) context).splash.dismiss ();
            }
            restSyncListener.onRestSyncFinished (taskId, processed);
        }
    }
    
    /**
     * Procesar la respuesta del servicio web.
     * @param response - La respuesta obtenida del web service.
     */
    protected abstract boolean processResponse (JSONObject response);
    
    /**
     * Procesar el error de conexión con el servicio web.
     * @param volleyError
     */
    protected abstract void processError (VolleyError volleyError);
    
    /**
     * @return Request JSON como String para hacer la petición al webservice en @param serviceUrl.
     */
    protected abstract String getJsonRequest ();
    
    /**
     * Interfaz para indicar cuando la consulta al servicio web y el procesamiento de sus datos o del error
     * han finalizado.
     */
    public interface RestSyncListener {
        /**
         * Indica cuando se ha terminado la consulta / procesamiento de datos del servicio web.
         * @param taskId - un identificador de task
         * @param synched - True si se pudo consultar, se obtuvo respuesta válida y se procesó divha respuesta. false en otro caso.
         */
        void onRestSyncFinished (int taskId, boolean synched);
    }
}
