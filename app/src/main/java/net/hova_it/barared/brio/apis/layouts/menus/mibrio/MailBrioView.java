package net.hova_it.barared.brio.apis.layouts.menus.mibrio;

import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.session.SessionManager;

import android.webkit.WebViewClient;


/**
 * Created by HOVAIT on 05/04/2016.
 * Clase que muestra la pagina de correo brio
 */
public class MailBrioView extends OptionMenuFragment {

    private ModelManager manager;
    private WebView webView;
    public String msg;



    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        manager = new ModelManager(getActivity());
        rootView = inflater.inflate(R.layout.mi_brio_web, container, false);

        SessionManager sesion = SessionManager.getInstance(getActivity());
        String comercio = "" + sesion.readInt("idComercio");

        final String usuario = comercio +  "@mibrio.mx";
        final String clave = comercio + "Brio";

        webView = (WebView) rootView.findViewById(R.id.webBrio);
        webView.requestFocus();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.loadUrl("https://mail.mibrio.mx");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Autologin
                webView.evaluateJavascript("document.getElementsByName('username')[0].value = '" + usuario + "';document.getElementsByName('password')[0].value = '" + clave + "';document.getElementsByName('loginForm')[0].submit();", null);
            }
        });




        return rootView;
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl("https://mail.mibrio.mx");

        return false;
    }

    @Override
    protected View getRootView() {
        return rootView;
    }

    @Override
    protected void beforeRemove() {

    }

}