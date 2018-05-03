package lat.brio.core.serviciosWeb;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import lat.brio.core.BrioGlobales;

import static com.android.volley.Response.*;

public class RequestService {

    //public static final String PUBLIC_KEY = "SERVER_PUBLIC_KEY";
    private boolean log = true;
    private Activity activity;
    private String name = "";
    private String url = "";
    private JSONObject bodyParams;
    private int method;
    private HashMap<String, String> requestHeaders = new HashMap<String, String>();
    private boolean sslPinning = true;
    private boolean crypt = true;

    public RequestService(Activity activity, String name, String url, JSONObject bodyParams, int method) {
        this.setActivity(activity);
        this.setName(name);
        this.setUrl(url);
        this.setBodyParams(bodyParams);
        this.setMethod(method);
    }

    public RequestService(Activity activity, String name, String url, JSONObject bodyParams) {
        this(activity, name, url, bodyParams, com.android.volley.Request.Method.POST);
    }

    public RequestService(Activity activity, String name, String url) {
        this(activity, name, url, new JSONObject(), com.android.volley.Request.Method.POST);
    }

    public void addParam(String key, String value) {
        try {
            this.getBodyParams().put(key, value);
        } catch (JSONException e) {
            Log.e("addParam", e.getMessage());
        }
    }

    public void addParam(String key, int value) {
        try {
            this.getBodyParams().put(key, value);
        } catch (JSONException e) {
            Log.e("addParam", e.getMessage());
        }
    }

    public void addParam(String key, long value) {
        try {
            this.getBodyParams().put(key, value);
        } catch (JSONException e) {
            Log.e("addParam", e.getMessage());
        }
    }

    public void addParam(String key, boolean value) {
        try {
            this.getBodyParams().put(key, value);
        } catch (JSONException e) {
            Log.e("addParam", e.getMessage());
        }
    }

    public void addParam(String key, Object value) {
        try {
            this.getBodyParams().put(key, value);
        } catch (JSONException e) {
            Log.e("addParam", e.getMessage());
        }
    }

    public void addParam(String key, double value) {
        try {
            this.getBodyParams().put(key, value);
        } catch (JSONException e) {
            Log.e("addParam", e.getMessage());
        }
    }

    public void setBodyParamsAsBundle(Bundle bundle) {
        try {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                addParam(key, value.toString());
            }
        } catch (Exception ex) {
            Log.e("bundleError", ex.getMessage());
        }
    }

    public void addHeader(String name, String value) {
        this.requestHeaders.put(name, value);
    }

    public void setToken(String token) {
        addHeader("Authorization", "Bearer " + token);
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JSONObject getBodyParams() {
        return bodyParams;
    }

    public void setBodyParams(JSONObject bodyParams) {
        this.bodyParams = bodyParams;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public HashMap<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(HashMap<String, String> headers) {
        this.requestHeaders = headers;
    }

    public void executeStr(Listener<String> success, ErrorListener error) {


        if (getBodyParams() == null) {
            setBodyParams(new JSONObject());
        }

        if (isLog()) {
            Log.i(this.getName() + "Url", getUrl() + "");
            Log.i(this.getName() + "Params", getBodyParams().toString() + "");
            Log.i(this.getName() + "Headers", getRequestHeaders().toString() + "");
        }

        final StringRequest stringRequest = new StringRequest(getMethod(), getUrl(), success, error) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() {
                return getRequestHeaders();
            }

            @Override
            public byte[] getBody() {
                JSONObject params = getBodyParams();
                String data = params.toString();
                return data.getBytes();
            }
        };

        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                BrioGlobales.DIEZ_SEGUNDOS_TIME_OUT_WEBSERVICE,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getActivity()).add(stringRequest);

    }
    public void executeJson(Listener<JSONObject> success, ErrorListener error) {


        if (getBodyParams() == null) {
            setBodyParams(new JSONObject());
        }

        if (isLog()) {
            Log.i(this.getName() + "Url", getUrl() + "");
            Log.i(this.getName() + "Params", getBodyParams().toString() + "");
            Log.i(this.getName() + "Headers", getRequestHeaders().toString() + "");
        }

        final JsonObjectRequest stringRequest = new JsonObjectRequest(getMethod(), getUrl(), success, error) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() {
                return getRequestHeaders();
            }

            @Override
            public byte[] getBody() {
                JSONObject params = getBodyParams();
                String data = params.toString();
                return data.getBytes();
            }
        };
    

        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                BrioGlobales.DIEZ_SEGUNDOS_TIME_OUT_WEBSERVICE,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getActivity()).add(stringRequest);

    }

}
