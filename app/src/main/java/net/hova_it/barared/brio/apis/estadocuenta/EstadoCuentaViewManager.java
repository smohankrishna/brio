package net.hova_it.barared.brio.apis.estadocuenta;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioManagerInterface;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.hw.barcodescanner.ScannerManager;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Comercio;
import net.hova_it.barared.brio.apis.models.entities.EstadoPos;
import net.hova_it.barared.brio.apis.models.entities.Transacciones;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import lat.brio.core.BrioGlobales;

/**
 * Implementacion de la clase de Estado de cuenta
 * Created by Alejandro Gomez on 18/12/2015.
 */
public class EstadoCuentaViewManager implements EstadoCuentaViewFragment.EstadoCuentaViewFragmentListener, BrioManagerInterface {
    private final String TAG = "BRIO_WEB";
    private String URL;

    private Context context;
    private FragmentManager fragmentManager;
    private EstadoCuentaViewFragment estadoCuentaViewFragment;

    private ModelManager modelManager;
    private WebView estadoCuentaWebView;
    private SessionManager sessionManager;
    private Fragment parent;

    private ScannerManager scannerManager;

    private static EstadoCuentaViewManager mInstance = null;

    private BrioAndroidInterface brioAndroidInterface;

    public static EstadoCuentaViewManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new EstadoCuentaViewManager(context, null);
        }
        return mInstance;
    }

    public static EstadoCuentaViewManager getInstance(Context context, Fragment parent) {
        if (mInstance == null) {
            mInstance = new EstadoCuentaViewManager(context, parent);
        }
        return mInstance;
    }

    private EstadoCuentaViewManager(Context context, Fragment parent) {
        this.context = context;
        this.parent = parent;
        this.estadoCuentaViewFragment = EstadoCuentaViewFragment.newInstance();
        this.modelManager = new ModelManager(context);
        this.sessionManager = SessionManager.getInstance(context);

        // INI: Agregado por: Manuel Delgadillo - 21/FEB/2017
        this.URL = "http://" + BrioGlobales.URL_WEB_BRIO + "?Modulo=Reportes&Seccion=EdoCta"; // Sobre escribir la variable URL
        // FIN: Agregado por: Manuel Delgadillo - 21/FEB/2017

        this.scannerManager = ScannerManager.getInstance(context);
        Log.d(TAG, "URL service = " + this.URL);
        brioAndroidInterface = new BrioAndroidInterface();
    }

    @Override
    public void onFragmentCreated() {
        Log.d(TAG, "EstadoCuenta Fragment Ready...");
        Map fields = estadoCuentaViewFragment.getFields();

        estadoCuentaWebView = (WebView)fields.get("estadoCuentaWebView");

        estadoCuentaWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e(TAG, "console.log(" + consoleMessage.message() + ") line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return true;
            }
        });

        estadoCuentaWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try {
                    ((AppCompatActivity) context).findViewById(R.id.webViewBar1).setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onPageStarted:" + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished:" + url);
                try {
                    ((AppCompatActivity) context).findViewById(R.id.webViewBar1).setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                String data =
                        "<html>" +
                                "<head>" +
                                "<style>" +
                                "   .message{padding:10px 50px;margin:20px auto;font-size:20px;text-align:center;color:#666;}" +
                                "   .retry{padding:10px 50px;margin:20px auto;font-size:18px;text-align:center;}" +
                                "   .retry a, .retry a:link, .retry a:visited{color:#f08;text-decoration:none;}" +
                                "   .code{padding:20px 50px;margin:20px auto;font-size:14px;text-align:center;color:#666}" +
                                "</style>" +
                                "</head>" +
                                "<body>" +
                                "   <div class='message'>No hay buena conexión a Internet o el servicio Brío no está disponible.</div>" +
                                "   <div class='retry'><a href='" + URL + "'>REINTENTAR</a></div>" +
                                "   <div class='code'>(err_code::" + String.valueOf(errorCode) + ", " + description + ")</div>" +
                                "</body>" +
                                "</html>";
                view.loadData(data, "text/html; charset=UTF-8", null);
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(URL);
                return false;
            }
        });

        reloadWebView();
    }

    /**
     * Carga de la url en el webview y sus propiedades
     */
    public void reloadWebView() {
        ((BrioActivityMain) context).setDispatchKey(false);

        //Log.i(TAG, "CONFIG DATA: " + Utils.pojoToString(context.getResources().getConfiguration()));

        estadoCuentaWebView.clearCache(true);
        estadoCuentaWebView.requestFocus();
        estadoCuentaWebView.getSettings().setJavaScriptEnabled(true);
        estadoCuentaWebView.getSettings().setAppCacheEnabled(false);
        estadoCuentaWebView.getSettings().setSupportZoom(true);

        estadoCuentaWebView.removeJavascriptInterface("BrioAndroid");
        estadoCuentaWebView.addJavascriptInterface(brioAndroidInterface, "BrioAndroid");

        Utils.clearCookies(context);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(estadoCuentaWebView, true);

        String cookieString = "";

        Comercio comercio = modelManager.comercios.getByIdComercio(sessionManager.readInt("idComercio"));

        Log.d(TAG, Utils.pojoToString(comercio));

        String address =
                comercio.getDireccionFisica() + " " +
                        comercio.getNumeroExteriorFisica() + " " + comercio.getNumeroInteriorFisica() + ", " +
                        comercio.getColoniaFisica();

        String cdstate =
                comercio.getMunicipioFisica() + ", " +
                        comercio.getEstadoFisica();

        try {
            // INI: Agregado por: Manuel Delgadillo - 21/FEB/2017
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Formato de fecha de Membresia
            // FIN: Agregado por: Manuel Delgadillo - 21/FEB/2017
            cookieString =
                    "brioUUID=" + URLEncoder.encode(Utils.getUUID(context), "UTF-8") +
                            ",brioGPS=" + URLEncoder.encode(sessionManager.readString("DATA_GPS"), "UTF-8") +
                            ",brioComercio=" + URLEncoder.encode(String.valueOf(sessionManager.readInt("idComercio")), "UTF-8") +
                            ",brioDesc=" + URLEncoder.encode(comercio.getDescComercio(), "UTF-8") +
                            ",brioAddress=" + URLEncoder.encode(address, "UTF-8") +
                            ",brioState=" + URLEncoder.encode(cdstate, "UTF-8") +
                            // INI: Agregado por: Manuel Delgadillo - 21/FEB/2017
                            ",Membresia=" + URLEncoder.encode(dateFormat.format(new Date(comercio.getTimestamp() * 1000)), "UTF-8") + // Agregar la cookie Membresia
                            // FIN: Agregado por: Manuel Delgadillo - 21/FEB/2017
                            ",brioUsuario=" + URLEncoder.encode(String.valueOf(sessionManager.readInt("idUsuario")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }


        Log.d(TAG, cookieString);
        Log.d(TAG, URL);

        CookieManager.getInstance().setCookie(URL, cookieString);

        estadoCuentaWebView.loadUrl(URL);
    }

    /**
     * Implementar aqui la muestra de los fragments de cada manager (transaction sobre containerId)
     *
     * @param fragmentManager fragmentManager del esatado de cuenta
     * @param containerId webview
     */
    @Override
    public Fragment createFragments(FragmentManager fragmentManager, int containerId) {
        Log.d(TAG, "Creating EstadoCuenta Fragment...");
        this.fragmentManager = fragmentManager;
        ((BrioActivityMain)context).setDispatchKey(false);

        estadoCuentaViewFragment.setEstadoCuentaViewManager(this);

       /*
       this.fragmentManager
               .beginTransaction()
               .replace(containerId, estadoCuentaViewFragment)
               .commit();
       */

        return estadoCuentaViewFragment;
    }

    /**
     * Implementar aquí la forma de remover los fragments de cada manager
     */
    @Override
    public void removeFragments() {
        Log.d(TAG, "Removing EstadoCuenta Fragment...");

        ((BrioActivityMain)context).setDispatchKey(true);

        estadoCuentaWebView.stopLoading();

        this.fragmentManager
                .beginTransaction()
                .remove(estadoCuentaViewFragment)
                .commit();
    }

    final class BrioAndroidInterface {

        BrioAndroidInterface() {}

        @JavascriptInterface
        public void makeToast(String message) {
            Log.i(TAG, "Called toastThis: " + message);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void getEstado(String rango) {
            Log.i(TAG, "Called getEstado(rango): " + rango);

            final Gson gson = new Gson();
            final Rango estadoRango = gson.fromJson(rango, Rango.class);

            try {
                estadoCuentaWebView.post(new Runnable() {
                    @Override
                    public void run() {

                        EstadoPos estadoPOS = modelManager.estadoPos.getEstadoPos(estadoRango.getFechaInicial(), estadoRango.getFechaFinal());
                        String response = gson.toJson(estadoPOS);
                        Log.i(TAG, "Calling getEstado()");
                        Log.i(TAG, response);

                        estadoCuentaWebView.evaluateJavascript("setEstado('" + response + "')", null);
                    }
                });
            } catch (Exception e) {
                Log.i(TAG, "Called getEstado(error): " + e.getMessage());
            }
        }

        @JavascriptInterface
        public void getTrans(String rango) {
            Log.i(TAG, "Called getTrans(rango): " + rango);

            final Gson gson = new Gson();
            final Rango transRango = gson.fromJson(rango, Rango.class);

            try {
                estadoCuentaWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        Transacciones transacciones = modelManager.transacciones.getTransacciones(transRango.getFechaInicial(), transRango.getFechaFinal());
                        String response = gson.toJson(transacciones);
                        Log.i(TAG, "Calling getEstado()");
                        Log.i(TAG, response);

                        estadoCuentaWebView.evaluateJavascript("setTrans('" + response + "')", null);
                    }
                });
            } catch (Exception e) {
                Log.i(TAG, "Called getTrans(error): " + e.getMessage());
            }
        }

        @JavascriptInterface
        public void getComercio() {
            Log.i(TAG, "Called getComercio(): ");

            final Gson gson = new Gson();

            try {
                estadoCuentaWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        Comercio comercio = modelManager.comercios.getByIdComercio(sessionManager.readInt("idComercio"));

                        String address =
                                comercio.getDireccionFisica() + " " +
                                        comercio.getNumeroExteriorFisica() + " " + comercio.getNumeroInteriorFisica() + ", " +
                                        comercio.getColoniaFisica();

                        String cdstate =
                                comercio.getMunicipioFisica() + ", " + comercio.getEstadoFisica();

                        EstadoHead estadoHead = new EstadoHead();
                        estadoHead.setDescComercio(comercio.getDescComercio());
                        estadoHead.setComercioCID(comercio.getIdComercio());
                        estadoHead.setDireccion(address);
                        estadoHead.setCiudadEstado(cdstate);

                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                        Date now = new Date(comercio.getTimestamp() * 1000);

                        estadoHead.setMemberSince(dateFormat.format(now));

                        String response = gson.toJson(estadoHead);

                        Log.i(TAG, "Calling loadEstadoHeader()");
                        Log.i(TAG, response);
                        estadoCuentaWebView.evaluateJavascript("setComercio('" + response + "')", null);
                    }
                });
            } catch (Exception e) {
                Log.i(TAG, "Called saveAndSendTicket(error): " + e.getMessage());
            }
        }

        @JavascriptInterface
        public void refresh() {
            estadoCuentaWebView.post(new Runnable() {
                @Override
                public void run() {

                    reloadWebView();
                }
            });
        }
    }

    /**
     * Pojo para el llenado del estado de cuenta
     */
    public class EstadoHead{
        private String descComercio;
        private int comercioCID;
        private String direccion;
        private String ciudadEstado;
        private String memberSince;

        public String getCiudadEstado() {
            return ciudadEstado;
        }

        public void setCiudadEstado(String ciudadEstado) {
            this.ciudadEstado = ciudadEstado;
        }

        public int getComercioCID() {
            return comercioCID;
        }

        public void setComercioCID(int comercioCID) {
            this.comercioCID = comercioCID;
        }

        public String getDescComercio() {
            return descComercio;
        }

        public void setDescComercio(String descComercio) {
            this.descComercio = descComercio;
        }

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }

        public String getMemberSince() {
            return memberSince;
        }

        public void setMemberSince(String memberSince) {
            this.memberSince = memberSince;
        }
    }

    public class Rango{
        private long fechaInicial;
        private long fechaFinal;

        public long getFechaFinal() {
            return fechaFinal;
        }

        public void setFechaFinal(long fechaFinal) {
            this.fechaFinal = fechaFinal;
        }

        public long getFechaInicial() {
            return fechaInicial;
        }

        public void setFechaInicial(long fechaInicial) {
            this.fechaInicial = fechaInicial;
        }
    }
	
}
