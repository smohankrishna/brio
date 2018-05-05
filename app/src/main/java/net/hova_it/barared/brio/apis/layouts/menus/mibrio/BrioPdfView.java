package net.hova_it.barared.brio.apis.layouts.menus.mibrio;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import android.webkit.WebViewClient;

import java.util.Calendar;


/**
 * Created by HOVAIT on 05/04/2016.
 * Clase que muestra las noticias del mes atravez de un lector de pdfs
 */
public class BrioPdfView extends OptionMenuFragment {

    private ModelManager manager;
    private WebView webView;
    public String msg;
    private Context context;
    private String _Url;

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        context = getActivity();

        manager = new ModelManager(getActivity());
        rootView = inflater.inflate(R.layout.mi_brio_web, container, false);

        webView = (WebView) rootView.findViewById(R.id.webBrio);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.requestFocus();
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        Calendar fecha = Calendar.getInstance();
        int month = fecha.get(Calendar.MONTH)+1;
        String year =Integer.toString(fecha.get(Calendar.YEAR));
        //Toast.makeText(context, month+"", Toast.LENGTH_LONG).show();
        String monthf;
        if(month<10){
            monthf = "0"+month;
        }else monthf = Integer.toString(month);

        //"http://zero.brio.lat:8086/baranotas/BrioNoticias"+monthf+year+".pdf"
        String pdfURL = ( "http://zero.brio.lat:8086/baranotas/BrioNoticias"+monthf+year+".pdf");
        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + pdfURL);
       // Toast.makeText(context, monthf+year, Toast.LENGTH_LONG).show();

        webView.setWebViewClient(new WebViewClient());


        return rootView;
    }

    @Override
    protected View getRootView() {
        return rootView;
    }

    @Override
    protected void beforeRemove() {

    }

}

