package net.hova_it.barared.brio.apis.layouts.menus.mibrio;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;


/**
 * Created by Edith Maldonado on 05/04/2016.
 * Clase que muestra la pagina de brioUniversidad
 */

public class BrioUniversidad extends OptionMenuFragment implements View.OnClickListener {

    private ModelManager manager;
    private WebView webView;
    public String msg;
    private ImageView btnHome;
    private ImageView btnBack;
    private ImageView btnForward;
    private String urlUni;




    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        manager = new ModelManager(getActivity());
        rootView = inflater.inflate(R.layout.webview_universidad, container, false);

        urlUni = "http://elearning-barared.mx";

        webView = (WebView) rootView.findViewById(R.id.webview_uni);
        btnHome = (ImageView)rootView.findViewById(R.id.bHome);
        btnHome.setOnClickListener(this);
        btnBack = (ImageView)rootView.findViewById(R.id.bBack);
        btnBack.setOnClickListener(this);
        btnForward = (ImageView)rootView.findViewById(R.id.bForward);
        btnForward.setOnClickListener(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new CustomWebClient());
        webView.getSettings().setUseWideViewPort(true);
        webView.requestFocus(View.FOCUS_DOWN);

        webView.loadUrl(urlUni);


        return rootView;
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){

            case R.id.bHome:
                home(urlUni);
                break;

            case R.id.bBack:
                canGoBack();

                break;

            case R.id.bForward:
                canForward();
                break;
        }

    }

    /**
     * boton de regresar para interactuar con la pagina
     */
    public void canGoBack() {
        if (webView.canGoBack())
            webView.goBack();
    }
    /**
     * boton de siguiente para interactuar con la pagina
     */
    public void canForward() {
        if( webView.canGoForward())
            webView.goForward();
    }
    /**
     * boton de home para interactuar con la pagina, el cual es nuestra pagina principal
     */
    public void home(String url){

        webView.loadUrl(url);
        return;
    }


    private class CustomWebClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return  true;
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

