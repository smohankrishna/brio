package net.hova_it.barared.brio.apis.reports;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hovait3 on 03/11/2016.
 */

public class GetTransactions extends Volley {
    private final String TAG = "BRIO_SERVER";
    private JsonObjectRequest jsonObjectRequest;
    private Context context;

    public GetTransactions(Context context) {
        this.context = context;
    }

    /**
     * Convierte la informacion de un modelo de datos del tipo "Comercio" en un json.
     * El json es enviado para su validacion y registro en la DB del servidor.
     * Captura la respuesta del servidor, ya sea un registro aprobado o un error en la validacion de
     * datos.
     * @param URL Servicio web.
     * @param listener
     */
    public void request(final String URL, final ServerServiceListener listener) {
        final List<ServerResponse> syncResponse = new ArrayList<>();
        final JSONArray syncTicketInfos = new JSONArray();
        Log.e(TAG, "URL: " + URL);

        try {
            jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    URL,
                    //new JSONObject(json),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, "Response: " + response.toString());

                            try {
                                for(int i=0 ; response.getJSONArray("syncTicketInfos").length() > i ; i++ ){
                                    syncTicketInfos.put(i,response.getJSONArray("syncTicketInfos").get(i));
                                    ServerResponse pojo = new ServerResponse();
                                    pojo.setIdComercio(syncTicketInfos.getJSONObject(i).getLong("comm"));
                                    pojo.setTransaction(syncTicketInfos.getJSONObject(i).getLong("trans"));
                                    pojo.setTipo(syncTicketInfos.getJSONObject(i).getInt("ttype"));
                                    pojo.setAuthorization(syncTicketInfos.getJSONObject(i).getString("authorization"));
                                    pojo.setAmount(syncTicketInfos.getJSONObject(i).getDouble("amount"));
                                    pojo.setCommision(syncTicketInfos.getJSONObject(i).getDouble("commi"));
                                    pojo.setgenerated(syncTicketInfos.getJSONObject(i).getLong("generated"));
                                    pojo.setFinalComm(syncTicketInfos.getJSONObject(i).getDouble("finalComm"));
                                    pojo.setFinalCommType(syncTicketInfos.getJSONObject(i).getDouble("finalCommType"));
                                    pojo.setCrmAsoc(syncTicketInfos.getJSONObject(i).getDouble("crmAsoc"));

                                    syncResponse.add(pojo);

                                }
                                Log.e("GetTransactions", "Tamaño de JSON syncTicketInfos: " + response.getJSONArray("syncTicketInfos").length());


                                listener.onServerResponse(syncResponse);

                            } catch (Exception e) {
                                listener.onErrorResponse("ERR_MESSAGE(onErrorResponse): " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            listener.onErrorResponse("ERR_MESSAGE(onErrorResponse): " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            listener.onErrorResponse("ERR_MESSAGE(request): " + e.getMessage());
        }
        newRequestQueue(context).add(jsonObjectRequest);
    }

    public interface ServerServiceListener {
        void onServerResponse(List<ServerResponse> syncResponse);

        void onErrorResponse(String syncResponse);
    }

    /**
     * Modelo de datos para cachar respuesta del servidor.
     */
    public class ServerResponse {
        private long idComercio;
        private long transaction;
        private int tipo;
        private String authorization;
        private double amount;
        private double commision;
        private long generated;
        private JSONObject data;
        private double finalComm;
        private double finalCommType;
        private double crmAsoc;



        public double getFinalCommType() {
            return finalCommType;
        }

        public void setFinalCommType(double finalCommType) {
            this.finalCommType = finalCommType;
        }

        public double getCrmAsoc() {
            return crmAsoc;
        }

        public void setCrmAsoc(double crmAsoc) {
            this.crmAsoc = crmAsoc;
        }



        public double getFinalComm() {
            return finalComm;
        }

        public void setFinalComm(double finalComm) {
            this.finalComm = finalComm;
        }



        public long getIdComercio() {
            return idComercio;
        }

        public void setIdComercio(long idComercio) {
            this.idComercio = idComercio;
        }

        public JSONObject getData() {
            return data;
        }

        public void setData(JSONObject data) {
            this.data = data;
        }

        public long getTransaction() {
            return transaction;
        }

        public void setTipo(int tipo) {
            this.tipo = tipo;
        }

        public int getTipo() {
            return tipo;
        }

        public void setTransaction(long transaction) {
            this.transaction = transaction;
        }

        public String getAuthorization() {
            return authorization;
        }

        public void setAuthorization(String authorization) {
            this.authorization = authorization;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getCommision() {
            return commision;
        }

        public void setCommision(double commision) {
            this.commision = commision;
        }

        public long getGenerated() {
            return generated;
        }

        public void setgenerated(long generated) {
            this.generated = generated;
        }
    }
}
