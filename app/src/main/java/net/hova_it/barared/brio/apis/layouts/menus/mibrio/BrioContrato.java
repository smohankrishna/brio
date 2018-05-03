package net.hova_it.barared.brio.apis.layouts.menus.mibrio;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Comercio;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Edith Maldonado on 05/04/2016.
 * Clase que muestra la pagina de brioUniversidad
 */

public class BrioContrato extends OptionMenuFragment {

    private ModelManager manager;
    private WebView webView;
    public String msg;
    private String urlUni;




    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        manager = new ModelManager(getActivity());
        rootView = inflater.inflate(R.layout.webview_contrato, container, false);

        urlUni = "http://web.brio.lat/NBE/?Modulo=MiBrio&Vista=Contrato";

        webView = (WebView) rootView.findViewById(R.id.webview_con);


        Utils.clearCookies(getContext());
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        String cookieString = "";

        ModelManager modelManager = ((BrioActivityMain)getContext()).modelManager;
        SessionManager sessionManager = ((BrioActivityMain)getContext()).managerSession;

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
                    "brioUUID=" + URLEncoder.encode(Utils.getUUID(getContext()), "UTF-8") +
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
        }

        CookieManager.getInstance().setCookie(urlUni, cookieString);



        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setWebViewClient(new CustomWebClient());
        webView.getSettings().setUseWideViewPort(true);
        webView.requestFocus(View.FOCUS_DOWN);
        BrioAndroidInterface Brio = new BrioAndroidInterface();

        webView.removeJavascriptInterface("Brio");
        webView.addJavascriptInterface(Brio, "Brio");

        webView.loadUrl(urlUni);


        return rootView;
    }

    private class CustomWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return  true;
        }
    }


    final class BrioAndroidInterface {

        BrioAndroidInterface() {}

        @JavascriptInterface
        public void Cerrar() {
            ((BrioActivityMain)getContext()).onBackPressed();
            // Toast.makeText(getContext(), "Cerrando...", Toast.LENGTH_LONG).show();
        }


    }


    @Override
    protected View getRootView() {
        return rootView;
    }

    @Override
    protected void beforeRemove() {

    }


}

