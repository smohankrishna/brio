package lat.brio.api.alerta;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.interfaces.OnFragmentListener;
import net.hova_it.barared.brio.apis.network.NetworkStatus;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.alarmas.Alarm;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledExecutorService;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.api.servicio.IServicio;
import lat.brio.core.AppController;
import lat.brio.core.BrioGlobales;
import lat.brio.core.REQUEST;
import lat.brio.core.serviciosWeb.RequestService;

/**
 * Created by Manuel Delgadillo on 03/07/2017.
 */
public class Alerta/* extends Servicio*/ {
    
    private static final String TAG = ".alerta.Alerta";
    private ArrayList<OAlerta> _Alertas = null;
    private ScheduledExecutorService _Executor;
    private Integer _Comercio;
    private Context context;
    private SessionManager _Sesion;
    private lat.brio.api.dialogo.Alerta _AlertaTodas;
    private boolean _Alertando = false;
    
    private OnFragmentListener fragmentListener;
    
    public Alerta (Context Contexto) {
        // super(Contexto, "http://" + BrioGlobales.URL_IPA_BRIO + "/?v0.0.0/Alertas/" , com.android.volley.Request.Method.POST);
        
        context = Contexto;
    }
    
    
    public void Iniciar () {
        if (_Alertando) {
            return;
        }
        _Alertando = true;
        ConsultaBannersWs ();
    }
    
    public void setFragmentListener (OnFragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }
    
    private void ConsultaBannersWs () {
        
        String URL = "http://" + BrioGlobales.URL_IPA_BRIO + "/?v0.0.0/Alertas/";
        
        try {
            RequestService wsSettings = new RequestService ((BrioActivityMain) context, "Brio", URL);
            wsSettings.setBodyParams (new JSONObject (Consulta ()));
            
            wsSettings.executeJson (new Response.Listener<JSONObject> () {
                @Override
                public void onResponse (JSONObject Respuesta) {
                    try {
                        
                        Respuesta (Respuesta);
                        _Alertando = false;
                    } catch (Exception e) {
                        e.printStackTrace ();
                        BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + TAG + "->ConsultaBannersWs.onResponse" + e.getMessage ());
                    }
                }
            }, new Response.ErrorListener () {
                @Override
                public void onErrorResponse (VolleyError e) {
                    e.printStackTrace ();
                    BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + TAG + "->ConsultaBannersWs.ErrorListener" + e.getMessage ());
                }
            });
            
            
        } catch (JSONException e) {
            e.printStackTrace ();
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + TAG + "->ConsultaBannersWs" + e.getMessage ());
        }
    }
    
    
    public void showBanner (final OAlerta alerta) {
        
        if (alerta != null && alerta.Activo () && ! alerta.Mostrando ()) {
            
            Alarm.cancelarAlarmaBanner (context);
            alerta.Mostrando (true);
            alerta.Alerta = new lat.brio.api.dialogo.Alerta ((AppCompatActivity) context);
            alerta.Alerta.showBanner ("http://" + BrioGlobales.URL_IPA_BRIO + "/?v0.0.0/Alertas/" + alerta.Anuncio ());
            alerta.Alerta.hideShowButton (false);
            alerta.Alerta.Cerrar (new View.OnClickListener () {
                @Override
                public void onClick (View vista) {
                    alerta.Mostrando (false);
                    if (alerta.Alerta != null) {
                        alerta.Alerta.Cerrar ();
                    }
                    //Después de cerrar el banner actual, se vuelve a generar el AlarmManager para volver a mostrar el banner
                    Alarm.generaAlarmaBanners (context);
                }
            });
            
            //            Se muestra el botón 10 segundos despues, para asegurar que el asociado lea el msj
            int SPLASH_TIME_OUT = 1000 * 10;
            new Handler ().postDelayed (new Runnable () {
                @Override
                public void run () {
                    alerta.Alerta.hideShowButton (true);
                }
            }, SPLASH_TIME_OUT);
        }
    }
    
    
    private String Consulta () {
        _Sesion = SessionManager.getInstance (context);
        JSONObject consulta = new JSONObject ();
        _Comercio = _Sesion.readInt ("idComercio");
        try {
            consulta.accumulate ("CID", _Comercio);
        } catch (JSONException e) {
            e.printStackTrace ();
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + TAG + "->Consulta()" + e.getMessage ());
        }
        return consulta.toString ();
    }
    
    private void Respuesta (JSONObject Respuesta) {
        JSONArray alertas = null;
        try {
            JSONObject extra = Respuesta.getJSONObject ("Extra");
            JSONObject brio = Respuesta.getJSONObject ("Brio");
            if (extra.getJSONObject ("Error").getString ("Id").equals ("0")) {
                if (brio.has ("Alertas")) {
                    alertas = brio.getJSONArray ("Alertas");
                } else {
                    alertas = new JSONArray ();
                }
            }
            if (_Alertas != null) {
                for (OAlerta alerta : _Alertas) {
                    alerta.Activo (false);
                }
            }
            _Alertas = new ArrayList<OAlerta> ();
            
            for (Integer a = 0; a < alertas.length (); a++) {
                JSONObject alerta = alertas.getJSONObject (a);
                final Integer intervalo = alerta.isNull ("Intervalo") ? 0 : alerta.getInt ("Intervalo");
                final String anuncio = alerta.isNull ("Anuncio") ? "" : alerta.getString ("Anuncio");
                if (anuncio != "") {
                    _Alertas.add (new OAlerta (anuncio, intervalo));
                }
            }
            
            //Guarda el listado de alertas en appControler, para poder accesar a ella en cualquier momento
            AppController.setAlertas (_Alertas);
            Alarm.generaAlarmaBanners (context);
            
            if (fragmentListener != null) {
                String msj=_Alertas.size ()>0?"1":"0";
                fragmentListener.onFragment (REQUEST.REQUEST_CODE_BANNERS, msj, null);
            }
            
        } catch (JSONException Ex) {
            Ex.printStackTrace ();
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + TAG + "->Respuesta()" + Ex.getMessage ());
            
        }
    }
    
    
    // Metodo que mostrará todas las alertas disponibles
    public void showAllBanners () {
        if (! NetworkStatus.hasInternetAccess (context)) {
            Integer msg = R.string.brio_nointernet;
            
            BrioAlertDialog bad = new BrioAlertDialog ((AppCompatActivity) context,
                    "Brío alertas",
                    Utils.getString (msg, context));
            bad.show ();
            return;
        } else {
            _AlertaTodas = new lat.brio.api.dialogo.Alerta ((AppCompatActivity) context);
            _AlertaTodas.showBanner ("http://" + BrioGlobales.URL_IPA_BRIO + "/?v0.0.0/Alertas/Todo/" + _Comercio + "#Auto-1");
        }
    }
}
