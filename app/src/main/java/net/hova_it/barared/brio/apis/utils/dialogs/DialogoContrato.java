package net.hova_it.barared.brio.apis.utils.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Comercio;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.views.BrioButton2;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import lat.brio.api.vistas.VistaFirma;

public class DialogoContrato extends DialogFragment {

    private View _wvVista;
    private TextView _tvContrato;
    private WebView _wvContrato;
    private BrioButton2 _btContinuar;
    private BrioButton2 _btLimpiar;
    private VistaFirma _vfFirma;
    private VistaFirma _vfFirmaJose;
    private String _Estado;
    private IBrio _IBrio;
    private LinearLayout _LlContratoFirmas;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        _wvVista = getActivity().getLayoutInflater().inflate(R.layout.brio_dialog_contrato, null);
        _wvContrato = (WebView) _wvVista.findViewById(R.id.wvContrato);
        _LlContratoFirmas = (LinearLayout) _wvVista.findViewById(R.id.llContratoFirmas);
        //_tvContrato = (TextView) _wvVista.findViewById(R.id.tvContrato);

        _IBrio = new IBrio();

        _wvContrato.getSettings().setJavaScriptEnabled(true);
        _wvContrato.setBackgroundColor(0x00000000);
        _wvContrato.addJavascriptInterface(_IBrio, "IBrio");
        _wvContrato.loadUrl("file:///android_asset/Contrato.html");

        _vfFirmaJose = (VistaFirma) _wvVista.findViewById(R.id.vfFirma);
        _vfFirma = (VistaFirma) _wvVista.findViewById(R.id.vfFirmaAsociado);

        _btContinuar = (BrioButton2) _wvVista.findViewById(R.id.btContinuar);
        _btLimpiar = (BrioButton2) _wvVista.findViewById(R.id.btLimpiar);

        _btContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = _vfFirma.Firma();
                _vfFirmaJose.Firma(bm);
            }
        });

        _btLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               _vfFirma.Limpiar();
            }
        });

        //_wvContrato.loadData(getResources().getAssets().open(""), "text/html; charset=UTF-8", null);
        //_wvContrato.loadData("<html><body><marquee>Texto</marquee></body></html>", "text/html; charset=UTF-8", null);
        //_tvContrato.setText(Html.fromHtml(getString(R.string.brio_contrato_contrato)));

        //_tvEstado = (TextView) _wvVista.findViewById(R.id.tvEstado);
        //_vfFirma = (VistaFirma) _wvVista.findViewById(R.id.vfFirma);
        _wvVista.setMinimumWidth(1024); // Cubrir toda la pantalla para bloquear la funcionalidad
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(_wvVista);
        setCancelable(false);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MediaUtils.setDialogTransparentBackground(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // _tvEstado.setText(_Estado);
    }

    /**
     * Cambia el estado de el mensaje
     * @param Estado
     */
    public void Estado(String Estado) {
        //if(BrioActivityMain.DEBUG_SHOW_TOASTS){
        //    if(_Estado != null) {
        //        _Estado += "\n" + Estado;
        //    } else {
        //        _Estado = Estado;
        //    }
        //}else{
           // _Estado = Estado;
        //}

        //if(_tvEstado != null) {
        //    _tvEstado.setText(_Estado);
        //}
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        if (getDialog() == null) {
            setShowsDialog(false);
        }
        super.onActivityCreated(arg0);
    }

    public Bitmap Firma(){
        if(_vfFirma!=null){
            return _vfFirma.Firma();
        }else{
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void dismissAllowingStateLoss() {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogoContrato.super.dismissAllowingStateLoss();
            }
        }, 1000);
    }

    final class IBrio {
        IBrio() {}

        @JavascriptInterface
        public void Iniciar(){


            final String firmaJose = Environment.getExternalStorageDirectory() + "/_firma.png";

            OutputStream stream = null;
            try {
                stream = new FileOutputStream(firmaJose);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
                ((BrioActivityMain) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Bitmap areafirma = BitmapFactory.decodeFile(firmaJose);
                            _LlContratoFirmas.setVisibility(View.VISIBLE);
                            //_vfFirmaJose.Firma(areafirma);

                        }catch(Exception Ex){
                            Ex.printStackTrace();
                        }
                        // Toast.makeText(context, Ticket, Toast.LENGTH_SHORT).show();

                    }
                });

            //Toast.makeText(getContext(), "IBrio.Iniciar();", Toast.LENGTH_SHORT).show();
            //ValueCallback<String> respuesta = null;
            // _wvContrato.evaluateJavascript("alert('De acuerdo');");
            //_wvContrato.evaluateJavascript("alert('Cargado');", null);
        }

        public void Info(){

        }
    }

    @JavascriptInterface
    public void Info(){
        InfoContrato infoContrato = new InfoContrato();
        ModelManager modelManager = new ModelManager(getContext());

        // Traer la sesión
        SessionManager sesion;
        sesion = SessionManager.getInstance(getContext());

        // Preparar datos del comercio
        Comercio comercio = modelManager.comercios.getByIdComercio(sesion.readInt("idComercio"));

        // Cargar datos para el contrato
        infoContrato.Titular(comercio.getDireccionFisica());



        /*ServCookies servCookies = new ServCookies();
        servCookies.setBrioComercio(String.valueOf(sessionManager.readInt("idComercio")));
        servCookies.setBrioUUID(Utils.getUUID(context));
        servCookies.setBrioGPS(sessionManager.readString("DATA_GPS"));
        servCookies.setBrioIdUsuario(String.valueOf(sessionManager.readInt("idUsuario")));
        servCookies.setBrioUsuario(sessionManager.readString("usuario"));

        String response = gson.toJson(servCookies);

        Log.i(TAG, "Calling getCookies()");
        Log.i(TAG, response);

        servicesWebView.evaluateJavascript("setCookies('" + response + "')", null);*/
    }

    public class InfoContrato{
        private String _Titular;

        public String Titular(){
            return Titular(null);
        }

        public String Titular(String Titular){
            if(Titular!=null){
                this._Titular = Titular;
            }
            return this._Titular;
        }
    }
}
