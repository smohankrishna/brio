package net.hova_it.barared.brio.apis.videollamada;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Manager para el fragment de VideoLlamada.
 *
 * La conexión al servidor OpenTok se realiza en dos etapas:
 * (1) Se obtiene un sesionId consumiendo el webservice http://chat.barared.net:8284/OpenTokSessionAdmon/rest/opentok/generaSession GET { perfil="Asociado" }.
 * (2) Se obtiene un token de OpenTok al el id de sesion al servicio web http://chat.barared.net:8284/OpenTokSessionAdmon/rest/opentok/generaToken GET { sessionId=sesionId }".
 * (3) Se pide al servidor OpenTok abrir una conexión enviando los parametros { isAsesor=0, opcion=1, sesionId=sesionId }
 * (4) Se utilizan los parámetros sessionId y el Token para iniciar la videollamada desde android.
 *
 * NOTA: El parametro sessionId cambia a sesionId al enviarlo al segundo servivio Web.
 *
 * Created by Herman Peralta on 07/04/2016.
 */
public class VideoLlamadaManager {

    private static VideoLlamadaManager daManager;

    private Context context;

    private RequestQueue mRequestQueue;

    private String
            sessionId = null,
            Token = null;

    public static VideoLlamadaManager getInstance(Context context) {
        if(daManager == null) {
            daManager = new VideoLlamadaManager(context);
        }

        return daManager;
    }

    private VideoLlamadaManager(Context context) {
        this.context = context;

        mRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Inicia el proceso de petición de video llamada al servidor OpenTok de Barared.
     */
    public void iniciar() {
        fetchSessionId();
    }

    /**
     * Consulta el servicio web(1) para obtener el sessionId, si lo obtiene, invoca a fetchToken().
     * En caso contrario termina el proceso.
     */
    private void fetchSessionId() {
        DebugLog.log(getClass(), "VIDEOLLAMADA", "");
        JSONObject jsonrequest = null;
        try {
            jsonrequest = new JSONObject("{ \"perfil\"=\"Asociado\" }");
            Log.e("JSON", jsonrequest.toString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Utils.getString(R.string.videollamada_rest_sesion_id_get, context),
                jsonrequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DebugLog.log(getClass(), "videollamada", "response:\n" + Utils.pojoToString(response));

                        try {
                            sessionId = response.getString("sessionId");

                            if(!sessionId.equals("0")) {
                                DebugLog.log(getClass(), "VIDEOLLAMADA", "sessionId="+sessionId);
                                fetchToken();
                            } else {
                                videollamada_session_id_not_found();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            videollamada_session_id_not_found();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();

                        videollamada_session_id_fail();
                    }
                });

        mRequestQueue.add(request);
    }

    /**
     * Consulta el servicio web(2) para obtener el token, si lo obtiene, invoca a fetchConexion().
     * En caso contrario, termina el proceso.
     */
    private void fetchToken() {
        DebugLog.log(getClass(), "VIDEOLLAMADA", "");
        JSONObject jsonrequest = null;
        try {
            jsonrequest = new JSONObject("{ \"sessionId\"=\"" + sessionId + "\" }");
            Log.e("JSON", jsonrequest.toString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Utils.getString(R.string.videollamada_rest_token_get, context),
                jsonrequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DebugLog.log(getClass(), "videollamada", "response:\n" + Utils.pojoToString(response));

                        try {
                            Token = response.getString("Token");

                            if(Token != null && Token.length() > 0) {
                                DebugLog.log(getClass(), "videollamada", "TOKEN CORRECTO: " + Token);

                                fetchConexion();
                            } else {
                                videollamada_token_not_found();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            videollamada_token_not_found();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();

                        videollamada_token_fail();
                    }
                });

        mRequestQueue.add(request);
    }

    /**
     * Consulta el servicio web(3) para solicitar abrir una conexión al servidor OpenTok. Si lo obtiene,
     * invoca a videollamada(), que inicia la videollamada. En caso contrario, termina el proceso.
     */
    private void  fetchConexion() {
        final String json = "{\"isAsesor\":?0, \"opcion\": ?1, \"sesionId\": \"?2\"}";

        DebugLog.log(getClass(), "VIDEOLLAMADA", "");
        JSONObject jsonrequest = null;
        try {
            jsonrequest = new JSONObject(json
                    .replace("?0", "0")
                    .replace("?1", "1")
                    .replace("?2", sessionId));

            Log.e("JSON", jsonrequest.toString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Utils.getString(R.string.videollamada_rest_conexion_get, context),
                jsonrequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DebugLog.log(getClass(), "videollamada", "response:\n" + Utils.pojoToString(response));

                        try {
                            String Codigo = response.getString("Codigo");
                            if(Codigo.equals("0")) {
                                videollamada();
                            } else {
                                videollamada_token_fail();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();

                        videollamada_token_fail();
                    }
                });

        mRequestQueue.add(request);
    }

    /**
     * Inicia la videollamada. Este metodo solo puede invocarse desde iniciar().
     */
    private void videollamada() {
        Intent videollamada = new Intent(context/*getBaseContext()*/, VideoLlamadaActivity.class);
        videollamada.putExtra("sessionId", sessionId);
        videollamada.putExtra("Token", Token);

        context.startActivity(videollamada);
    }

    private void videollamada_session_id_not_found() {
        showMsg(Utils.getString(R.string.videollamada_session_id_not_found, context));
    }

    private void videollamada_session_id_fail() {
        showMsg(Utils.getString(R.string.videollamada_session_id_fail, context));
    }

    private void videollamada_token_not_found() {
        showMsg(Utils.getString(R.string.videollamada_token_not_found, context));
    }

    private void videollamada_token_fail() {
        showMsg(Utils.getString(R.string.videollamada_token_fail, context));
    }

    /**
     * Muestra un mensaje de error.
     * @param msg
     */
    private void showMsg(String msg) {
        DebugLog.log(getClass(), "VIDEOLLAMADA", msg);
        BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity)context, "Videollamada", msg);
        bad.show();
    }
}
