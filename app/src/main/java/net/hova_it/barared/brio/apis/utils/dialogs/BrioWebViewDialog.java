package net.hova_it.barared.brio.apis.utils.dialogs;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.hw.printer.PrinterManager;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.views.BrioButton2;


/**
 * Clase que muestra el dialog de los tikets y de los banners
 * Created by Mauricio Cerón on 27/04/2016.
 */
public class BrioWebViewDialog implements View.OnClickListener {

    private View rootView;
    private BrioActivityMain activity;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private WebView webView;
    private BrioButton2 btnImprimir, btnEnviar;
    private SuperTicket ticket;

    private int idTicket = -1;

        //constructor que recibe la actividad y el html
    public BrioWebViewDialog(BrioActivityMain activity, String html) {
        this.activity = activity;

        LayoutInflater inflater = activity.getLayoutInflater();
        rootView = inflater.inflate(R.layout.webview_dialog, null);
        rootView.findViewById(R.id.btnCancelar).setOnClickListener(this);
        webView = (WebView) rootView.findViewById(R.id.show_webView);

        btnImprimir = (BrioButton2) rootView.findViewById(R.id.btnImprimir);
        btnImprimir.setOnClickListener(this);
        btnEnviar = (BrioButton2) rootView.findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(this);

        MediaUtils.loadStringInWebView(webView, html);
        builder = new AlertDialog.Builder(activity);
        builder.setView(rootView);
        builder.setCancelable(false);
        builder.create();

    }

    /**
     * metodo que se utiliza para los banner en el cual se le pasa la url
     *
     * @param url url que trae el servicio como mandan imagen se ocupara el constructor de arriba en html
     */
    public void openUrl(String url) {
/*
      FrameLayout layout = (FrameLayout)rootView.findViewById(R.id.frame_layout_webview);
// Gets the layout params that will allow you to resize the layout
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(50, 150);
      layout.setLayoutParams(params);
      builder = new AlertDialog.Builder(activity);
      builder.setView(rootView);
      builder.setCancelable(false);
      builder.create();*/


        webView.loadUrl(url);
        show();


    }

    /**
     * Muestra la ventana en transparente se ocupa cuando mandas una url en este dialogo
     */
    public void show() {
        if (idTicket != -1) {
            btnImprimir.setVisibility(View.VISIBLE);
            btnEnviar.setVisibility(View.VISIBLE);
        } else {
            btnImprimir.setVisibility(View.GONE);
            btnEnviar.setVisibility(View.GONE);
        }

        alertDialog = builder.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    /**Se ocupa para poner el banner en ventana transparente y renderiza el tamaño de la pantalla segun la resolucion
     *  y las dimensiones
     */
    public void showBanner() {
        btnImprimir.setVisibility(View.GONE);
        btnEnviar.setVisibility(View.GONE);

        alertDialog = builder.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //Se ocupa para calcular el tamaño de la pantalla y tendra dos tercios de la pantalla
        int size[] = Utils.getDisplaySize(activity);
        alertDialog.getWindow().setLayout(4 * size[0] / 5, size[1]);
    }


    public void showBanner(String URL) {
        btnImprimir.setVisibility(View.GONE);
        btnEnviar.setVisibility(View.GONE);
        alertDialog = builder.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        webView.loadUrl(URL);
        //Se ocupa para calcular el tamaño de la pantalla y tendra dos tercios de la pantalla
        int size[] = Utils.getDisplaySize(activity);
        alertDialog.getWindow().setLayout(4 * size[0] / 5, size[1]);
    }

    /**
     * Metodo que muestra un dialogo para imprimir desde web real desde un superticket
     */
    public void show(SuperTicket Ticket){
        if (Ticket != null) {
            btnImprimir.setVisibility(View.VISIBLE);
            btnEnviar.setVisibility(View.VISIBLE);
            ticket = Ticket;
        } else {
            btnImprimir.setVisibility(View.GONE);
            btnEnviar.setVisibility(View.GONE);
        }

        alertDialog = builder.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancelar:
                alertDialog.dismiss();

                MediaUtils.hideSystemUI(activity);
                break;

            case R.id.btnImprimir:
                if(BrioBaseActivity.DEBUG_SHOW_TOASTS) { Toast.makeText(activity, "btnImprimir " + idTicket, Toast.LENGTH_SHORT).show(); }
                PrinterManager managerPrinter = PrinterManager.getInstance(activity);

                managerPrinter.searchPrinter();
                if(ticket == null){
                    managerPrinter.printTicket(idTicket);
                }else{
                    managerPrinter.printTicket(ticket);
                }

                break;

            case R.id.btnEnviar:
                if(BrioBaseActivity.DEBUG_SHOW_TOASTS) { Toast.makeText(activity, "btnEnviar " + idTicket, Toast.LENGTH_SHORT).show(); }
                MailDialog mailDialog = new MailDialog(activity);
                if(ticket != null){
                    mailDialog.show(ticket);
                }else{
                    mailDialog.show(idTicket);
                }


                break;
        }
    }
}
