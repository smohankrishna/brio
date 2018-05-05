package net.hova_it.barared.brio.apis.services;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
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
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Comercio;
import net.hova_it.barared.brio.apis.models.entities.ItemsTicket;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.payment.PaymentService;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import lat.brio.core.BrioGlobales;

import static net.hova_it.barared.brio.apis.services.ServicesViewFragment.ServicesViewFragmentListener;

/**
 * Implementacion de los metodos que manda a llamar el fragment de servicios
 * <p>
 * Created by Alejandro Gomez on 18/12/2015.
 */
public class ServicesViewManager implements ServicesViewFragmentListener, BrioManagerInterface {
    private final String TAG = "BRIO_WEB";
    private final Gson gson;
    private final PaymentService paymentService;

    private String URL;

    private Context context;
    private FragmentManager fragmentManager;
    private ServicesViewFragment servicesViewFragment;

    private ModelManager modelManager;
    private WebView servicesWebView;
    private SessionManager sessionManager;
    private Fragment parent;

    private SuperTicket superTicket;

    String superTicketJson = null;

    private static ServicesViewManager mInstance = null;

    private BrioAndroidInterface brioAndroidInterface;

    public static ServicesViewManager getInstance(BrioActivityMain context) {
        if (mInstance == null) {
            mInstance = new ServicesViewManager(context, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        return mInstance;
    }

    public static ServicesViewManager getInstance(BrioActivityMain context, Fragment parent) {
        if (mInstance == null) {
            mInstance = new ServicesViewManager(context, parent);
        }
        return mInstance;
    }

    private ServicesViewManager(BrioActivityMain context, Fragment parent) {
        this.context = context;
        this.parent = parent;

        this.gson = new Gson();

        this.servicesViewFragment = ServicesViewFragment.newInstance();
        this.paymentService = PaymentService.getInstance(context);
        this.modelManager = new ModelManager(context);
        this.sessionManager = SessionManager.getInstance(context);

        this.URL = "http://" + BrioGlobales.URL_ZERO_BRIO + "/web/app/services/";

        brioAndroidInterface = new BrioAndroidInterface();
    }

    @Override
    public void onFragmentCreated() {
        Log.d(TAG, "Services Fragment Ready...");
        Map fields = servicesViewFragment.getFields();

        servicesWebView = (WebView) fields.get("servicesWebView");

        servicesWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e(TAG, "console.log(" + consoleMessage.message() + ") line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return true;
            }
        });

        servicesWebView.setWebViewClient(new WebViewClient() {
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
                    View v = ((AppCompatActivity) context).findViewById(R.id.webViewBar1);
                    if (v != null)
                    {
                        v.setVisibility(View.GONE);
                    }
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
     * propiedades para el webview y carga la url en el webview
     */
    public void reloadWebView() {
        ((BrioActivityMain) context).setDispatchKey(false);

        servicesWebView.clearCache(true);
        servicesWebView.requestFocus();
        servicesWebView.getSettings().setJavaScriptEnabled(true);
        servicesWebView.getSettings().setAppCacheEnabled(false);
        servicesWebView.getSettings().setSupportZoom(false);

        servicesWebView.removeJavascriptInterface("BrioAndroid");
        servicesWebView.addJavascriptInterface(brioAndroidInterface, "BrioAndroid");

        Utils.clearCookies(context);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(servicesWebView, true);

        String cookieString = "";

        Comercio comercio = modelManager.comercios.getByIdComercio(sessionManager.readInt("idComercio"));

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
                    "brioUUID=" + URLEncoder.encode(Utils.getUUID(context) + "-" + String.valueOf(sessionManager.readInt("idComercio")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }


        Log.d(TAG, cookieString);
        Log.d(TAG, URL);

        CookieManager.getInstance().setCookie(URL, cookieString);

        servicesWebView.loadUrl(URL);
    }

    @Override
    public Fragment createFragments(FragmentManager fragmentManager, int containerId) {
        Log.d(TAG, "Creating Services Fragment...");
        this.fragmentManager = fragmentManager;
        ((BrioActivityMain) context).setDispatchKey(false);

        servicesViewFragment.setServicesViewManager(this);

        this.fragmentManager
                .beginTransaction()
                .replace(containerId, servicesViewFragment)
                .commit();

        return servicesViewFragment;
    }

    @Override
    public void removeFragments() {
        Log.d(TAG, "Removing Services Fragment...");

        // INI: Agregado por: Manuel Delgadillo - 23/FEB/2017 -->
        //try { // Solamente se agregó el bloque TRY-CATCH para prevenir el cierre de el aplicativo
        ((BrioActivityMain) context).setDispatchKey(true);
        if (servicesWebView != null)
            servicesWebView.stopLoading();

        this.fragmentManager
                .beginTransaction()
                .remove(servicesViewFragment)
                .commit();

        //}catch(Exception Error){

        //}
    }

    /**
     * Conjunto de funciones que se invocan desde desde javascript
     * en la página web de Servicios.
     */
    public final class BrioAndroidInterface {
        BrioAndroidInterface() {
        }

        /**
         * showAllBanners un toast (invocado desde javascript)
         *
         * @param message - el texto a mostrar
         */
        @JavascriptInterface
        public void makeToast(String message) {
            Log.i(TAG, "Called makeToast: " + message);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }

        /**
         * Agregar un item a la venta actual (invocado desde javascript)
         *
         * @param itemsTicketJson - el item a agregar en JSON
         */
        @JavascriptInterface
        public void addItem(String itemsTicketJson) {
            Log.i(TAG, "Called addItem(ItemsTicket): " + itemsTicketJson);

            ItemsTicket itemsTicket = gson.fromJson(itemsTicketJson, ItemsTicket.class);
            Long savedItem = modelManager.itemsTicket.save(itemsTicket);

            Log.d(TAG, "Saved Auth Item: " + Utils.pojoToString(modelManager.itemsTicket.getByIdItemTicket(savedItem.intValue())));
        }

        /**
         * Mandar cobrar un ticket
         *
         * @param superTicketJson - el ticket completo en JSON
         * @see PaymentService
         */
        @JavascriptInterface
        public void payTicket(String superTicketJson) {
            if (superTicketJson.equals(ServicesViewManager.this.superTicketJson)) return;
            Log.i(TAG, "Called payTicket(superTicketJson): " + superTicketJson);

            ServicesViewManager.this.superTicketJson = superTicketJson;
            superTicket = gson.fromJson(superTicketJson, SuperTicket.class);
            Log.i(TAG, "Called payTicket: " + Utils.pojoToString(superTicket));

            paymentService.payTicket(superTicket, new PaymentService.AccountPaymentListener() {
                @Override
                public void onAccountPaymentStatusChange(PaymentService.ACCOUNT_STATUS status) {
                    Log.i(TAG, "payTicket(PaymentService.ACCOUNT_STATUS): " + status);
                    if (status == PaymentService.ACCOUNT_STATUS.PAGO_COMPLETADO) {
                        try {
                            servicesWebView.post(new Runnable() {
                                @Override
                                public void run() {
                                    String response = "{}";
                                    Log.i(TAG, "Calling JS onTicketPaid()");
                                    Log.i(TAG, response);
                                    servicesWebView.evaluateJavascript("onTicketPaid('" + response + "')", null);
                                }
                            });
                        } catch (Exception e) {
                            Log.i(TAG, "Called payTicket(exception): " + e.getMessage());
                        }
                    } else if (status == PaymentService.ACCOUNT_STATUS.CANCELADO) {
                        ((BrioActivityMain) context).enableMenus();
                        reloadWebView();
                    }

                    ServicesViewManager.this.superTicketJson = null;
                }
            });
        }

        /**
         * Abrir el fragment de envío de ticket por correo electrónico.
         *
         * @param superTicketJson
         * @see PaymentService
         */
        @JavascriptInterface
        public void sendTicket(String superTicketJson) {
            Log.i(TAG, "1 sendTicket(superTicketJson): " + superTicketJson);
            if (superTicketJson.equals(ServicesViewManager.this.superTicketJson)) return;
            Log.i(TAG, "Called sendTicket(superTicketJson): " + superTicketJson);

            ServicesViewManager.this.superTicketJson = superTicketJson;
            SuperTicket superTicketTmp = gson.fromJson(superTicketJson, SuperTicket.class);

            if (superTicket == null) {
                superTicket = new SuperTicket();
                superTicket.ticket = superTicketTmp.ticket;
            } //esto es para recibir los tickets de western union

            superTicket.itemsTicket = superTicketTmp.itemsTicket;
            superTicket.ticket.setDescripcion(superTicketTmp.ticket.getDescripcion());
            Log.i(TAG, "Called sendTicket: " + Utils.pojoToString(superTicket));
            Log.i(TAG, "Called sendTicket ticket: " + Utils.pojoToString(superTicket.getTicket()));

            paymentService.saveAndSendTicket(superTicket, new PaymentService.AccountPaymentListener() {
                @Override
                public void onAccountPaymentStatusChange(PaymentService.ACCOUNT_STATUS status) {
                    Log.i(TAG, "sendTicket(PaymentService.ACCOUNT_STATUS): " + status);
                    if (status == PaymentService.ACCOUNT_STATUS.ENVIADO) {
                        ((BrioActivityMain) context).enableMenus();

                        ServicesViewManager.this.superTicket = null;
                        ServicesViewManager.this.superTicketJson = null;

                        reloadWebView();
                    }
                    //ServicesViewManager.this.superTicketJson = null;
                }
            });
        }

        /**
         * Generar cookie para la página de Servicios con datos obtenidos de la app android.
         */
        @JavascriptInterface
        public void getCookies() {
            Log.i(TAG, "Called getCookies(): ");

            try {
                servicesWebView.post(new Runnable() {
                    @Override
                    public void run() {

                        ServCookies servCookies = new ServCookies();
                        servCookies.setBrioComercio(String.valueOf(sessionManager.readInt("idComercio")));
                        servCookies.setBrioUUID(Utils.getUUID(context));
                        servCookies.setBrioGPS(sessionManager.readString("DATA_GPS"));
                        servCookies.setBrioIdUsuario(String.valueOf(sessionManager.readInt("idUsuario")));
                        servCookies.setBrioUsuario(sessionManager.readString("usuario"));

                        String response = gson.toJson(servCookies);

                        Log.i(TAG, "Calling getCookies()");
                        Log.i(TAG, response);

                        servicesWebView.evaluateJavascript("setCookies('" + response + "')", null);
                    }
                });
            } catch (Exception e) {
                Log.i(TAG, "Called getCookies(exception): " + e.getMessage());
            }
        }

        /**
         * Refrescar el web view.
         */
        @JavascriptInterface
        public void refresh() {
            servicesWebView.post(new Runnable() {
                @Override
                public void run() {
                    reloadWebView();
                }
            });
        }

        @JavascriptInterface
        public void enableMenus() {
            ((BrioActivityMain) context).enableMenus();
        }

        @JavascriptInterface
        public void disableMenus() {
            ((BrioActivityMain) context).disableMenus();
        }

    }

    public class ServCookies {
        private String brioUUID;
        private String brioGPS;
        private String brioComercio;
        private String brioIdUsuario;
        private String brioUsuario;

        public void setBrioComercio(String brioComercio) {
            this.brioComercio = brioComercio;
        }

        public void setBrioGPS(String brioGPS) {
            this.brioGPS = brioGPS;
        }

        public void setBrioIdUsuario(String brioIdUsuario) {
            this.brioIdUsuario = brioIdUsuario;
        }

        public void setBrioUsuario(String brioUsuario) {
            this.brioUsuario = brioUsuario;
        }

        public void setBrioUUID(String brioUUID) {
            this.brioUUID = brioUUID;
        }
    }
}