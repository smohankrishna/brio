package lat.brio.api.servicio;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;


/**
 * Created by Delgadillo on 11/04/17.
 */
public abstract class Servicio {
    
    protected String _Mensaje = "Obteniendo info del servicio\nEspere un momento...";
    protected String _ServicioURL = null;
    protected Context _Contexto;
    protected RequestQueue _Lista;
    protected ModelManager _Modelo;
    protected IServicio _IServicio;
    
    protected Integer _Metodo;
    protected Integer _Tarea;
    
    protected Boolean _Dialogo = true;
    
    private DefaultRetryPolicy _Reintento = new DefaultRetryPolicy (30000, 5, 0F);
    
    public Servicio (Context Contexto, IServicio IServicio, String URLServicio, Integer Metodo, Integer Tarea) {
        this (Contexto, URLServicio, Metodo);
        this._Tarea = Tarea;
        this._IServicio = IServicio;
    }
    
    public Servicio (Context Contexto, String URLServicio, Integer Metodo) {
        this._Metodo = Metodo;
        this._ServicioURL = URLServicio;
        this._Contexto = Contexto;
        this._Lista = Volley.newRequestQueue (Contexto);
        this._Modelo = new ModelManager (Contexto);
    }
    
    public void Sincronizar () {
        
        if (BrioBaseActivity.DEBUG_SHOW_TOASTS) {
            Log.w ("SMN", "Servicio.Sincronizar: (" + _ServicioURL + " - Metodo:" + ((_Metodo == 1) ? "POST" : "GET") + ");");
        }
        
        if (_Dialogo) {
            // TODO Descomentar las siguientes dos lineas implementando lo necesario
            // ((ActividadBase)_Contexto).Dialogo.showAllBanners();
            // ((ActividadBase)_Contexto).Dialogo.Publicar(_Mensaje);
        }
        
        String consultastr = Consulta ();
        JsonObjectRequest consulta;
        
        if (consultastr == null) {
            consultastr = "";
        }
        try {
            JSONObject consultajs = null;
            if (! consultastr.equals ("")) {
                consultajs = new JSONObject (consultastr);
                if (BrioBaseActivity.DEBUG_SHOW_TOASTS) {
                    Log.w ("SMN", "Servicio.Consulta: ((JSONObject) " + consultajs.toString (4) + ");");
                }
            }
            
            consulta = new JsonObjectRequest (
                    _Metodo,
                    _ServicioURL,
                    consultajs,
                    new Response.Listener<JSONObject> () {
                        @Override
                        public void onResponse (JSONObject Respuesta) {
                            Procesador procesador = new Procesador ();
                            procesador.execute (Respuesta);
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError Error) {
                            enError (Error);
                        }
                    }
            
            );
            consulta.setRetryPolicy (_Reintento);
            _Lista.add (consulta);
        } catch (Exception Ex) {
            Ex.printStackTrace ();
            enError (new VolleyError (Ex.getMessage ()));
        }
        
        
    }
    
    private void enError (VolleyError Error) {
        if (Error != null) {
            Log.w ("SMN", "Servicio.Error((VolleyError) " + Error.getMessage () + " );");
            Error.printStackTrace ();
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + "Servicio->enError" + Error.getMessage ());
    
        }
        
        Error (Error);
        
        if (_Dialogo) {
            // TODO Descomentar la siguiente linea implementando lo necesario
            // ((ActividadBase)_Contexto).Dialogo.Cerrar();
        }
        
        _IServicio.alSincronizarServicio (_Tarea, false);
    }
    
    class Procesador extends AsyncTask<JSONObject, Void, Boolean> {
        
        @Override
        protected Boolean doInBackground (JSONObject... Parametros) {
            
            boolean resp = false;
            try {
                resp = Respuesta (Parametros[0]);
            } catch (Exception e) {
                e.printStackTrace ();
                BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + "Procesador->doInBackground" + e.getMessage ());
            }
            
            return resp;
        }
        
        @Override
        protected void onPostExecute (Boolean Sincronizado) {
            if (_Dialogo) {
                // TODO Descomentar la siguiente linea implementando lo necesario
                // ((ActividadBase)_Contexto).Dialogo.Cerrar();
            }
            _IServicio.alSincronizarServicio (_Tarea, Sincronizado);
        }
    }
    
    protected abstract String Consulta ();
    
    protected abstract void Error (VolleyError Error);
    
    protected abstract Boolean Respuesta (JSONObject Respuesta);
}
