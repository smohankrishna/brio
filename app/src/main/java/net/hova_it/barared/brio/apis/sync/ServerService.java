package net.hova_it.barared.brio.apis.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Consume un servicio web encargado del registro del comercio al iniciar la aplicacion por primera vez.
 * Created by Alejandro Gomez on 06/04/2016.
 */
public class ServerService extends Volley {
    private final String TAG = "BRIO_SERVER";
    private JsonObjectRequest jsonObjectRequest;
    private Context context;

    public ServerService(Context context) {
        this.context = context;
    }

    /**
     * Convierte la informacion de un modelo de datos del tipo "Comercio" en un json.
     * El json es enviado para su validacion y registro en la DB del servidor.
     * Captura la respuesta del servidor, ya sea un registro aprobado o un error en la validacion de
     * datos.
     * @param URL Servicio web.
     * @param json Informacion del comercio a registrar.
     * @param listener
     */
    public void request(final String URL, String json, final ServerServiceListener listener) {
        final ServerResponse syncResponse = new ServerResponse();

        Log.d(TAG, "Request: " + json);

        try {
            jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    URL,
                    new JSONObject(json),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, "Response: " + response.toString());
                            try {
                                syncResponse.setStatus(response.getString("status"));
                                syncResponse.setExists(response.getBoolean("exists"));
                                syncResponse.setMessage(response.getString("message"));
                                syncResponse.setRow(response.getLong("row"));
                                syncResponse.setData(response.getJSONObject("data"));
                                listener.onServerResponse(syncResponse);
                            } catch (Exception e) {
                                syncResponse.setStatus("ERR");
                                syncResponse.setMessage("ERR_MESSAGE(onResponse): " + e.getMessage());
                                        listener.onErrorResponse(syncResponse);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            syncResponse.setStatus("ERR");
                            syncResponse.setMessage("ERR_MESSAGE(onErrorResponse): " + error.getMessage());
                            listener.onErrorResponse(syncResponse);
                        }
                    });
        } catch (Exception e) {
            syncResponse.setStatus("ERR");
            syncResponse.setMessage("ERR_MESSAGE(request): " + e.getMessage());
            listener.onErrorResponse(syncResponse);
        }
        newRequestQueue(context).add(jsonObjectRequest);
    }

    public interface ServerServiceListener {
        void onServerResponse(ServerResponse syncResponse);

        void onErrorResponse(ServerResponse syncResponse);
    }

    /**
     * Modelo de datos para cachar respuesta del servidor.
     */
    public class ServerResponse {
        private String status;
        private boolean exists;
        private String message;
        private long row;
        private JSONObject data;

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }

        public JSONObject getData() {
            return data;
        }

        public void setData(JSONObject data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public long getRow() {
            return row;
        }

        public void setRow(long row) {
            this.row = row;
        }
    }
}
