package lat.brio.api.pagos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;

import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.apis.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import lat.brio.api.servicio.IServicio;
import lat.brio.api.servicio.Servicio;
import lat.brio.core.BrioGlobales;

/**
 * Created by Delgadillo on 10/07/17.
 */

public class Pago extends Servicio {

    private String _Comercio;
    private OPago _OPago;
    private IPago _IPago;

    private IServicio _IServicio = new IServicio(){
        @Override
        public void alSincronizarServicio(Integer Tarea, Boolean Sincronizado) {
            // Procesar respuesta de pago; ¿Autorizado, Declinado, Rechazado?
            if(Sincronizado){
                Log.w("PGS", "Sincronizado: Se realizo el pago?");
                Procesar();
            }else{
                Log.w("PGS", "No sincronizado: Intentar de nuevo");
            }
        }
    };

    // Método para procesar pago: TODO: devolver en caso de autorizar un objeto de pago
    private void Procesar(){
        switch(Codigo()){
            case "A": // Autoriza
                if(_OPago.has("ARPC")){
                    _IPago.alAutorizar(Valor("Autorizacion"), Valor("ARPC"));
                }else{
                    _IPago.alAutorizar(Valor("Autorizacion"));
                }
                break;
            case "D": // Declina
                _IPago.alDeclinar(Codigo(), Valor("Mensaje"));
                break;
            case "R": // Rechaza
                _IPago.alRechazar(Codigo(), Valor("Mensaje"));
                break;
        }
    }

    public Pago(Context Contexto){
        //super(Contexto, "http://192.168.200.38:8086/cabws/api/procesarPagosTarjeta", com.android.volley.Request.Method.POST);
        super(Contexto, "http://" + BrioGlobales.URL_IPA_BRIO  + "/?v0.0.2/Adquiriente", com.android.volley.Request.Method.POST);
        _OPago = new OPago(); // Crear el objeto de pago
        _OPago.Valor("Comercio", ((BrioBaseActivity) _Contexto).modelManager.settings.getByNombre("DESCRIPCION_COMERCIO").getValor());
        super._IServicio = _IServicio;
    }

    // Código de respuesta: A, D, R
    public String Codigo(){
        return _OPago.Valor("Codigo").toString();
    }


    public Object Valor(String Clave, String Valor){
        _OPago.Valor(Clave, Valor);
        return _OPago.Valor(Clave);
    }

    // Obtener
    public String Valor(String Clave){
        try {
            return _OPago.getString(Clave);
        } catch (JSONException Ex) {
            Ex.printStackTrace();
            return "";
        }
    }

    public void Comercio(String Comercio){
        _Comercio = Comercio;
    }

    public void VentaEMV(IPago IPago){
        _IPago = IPago;
        _OPago.Valor("Tipo", 1);
        _OPago.Valor("Fecha", new SimpleDateFormat("ddMMyyyy").format(Calendar.getInstance().getTime()));
        _OPago.Valor("Hora", new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime()));
        _OPago.Valor("CID", _Comercio);
        _OPago.Valor("Token", Utils.brioToken(_Contexto, Integer.parseInt(_Comercio))[0]);
        _OPago.Valor("UID", Utils.getUUID(_Contexto));
        super._ServicioURL = "http://" + BrioGlobales.URL_IPA_BRIO + "/?v0.0.2/Adquiriente";
        Sincronizar();
    }

    public void VentaMSR(IPago IPago){
        _IPago = IPago;
        _OPago.Valor("Tipo", 2);
        _OPago.Valor("Fecha", new SimpleDateFormat("ddMMyyyy",new Locale("Es","MX")).format(Calendar.getInstance().getTime()));
        _OPago.Valor("Hora", new SimpleDateFormat("HHmmss",new Locale("Es","MX")).format(Calendar.getInstance().getTime()));
        _OPago.Valor("CID", _Comercio);
        _OPago.Valor("Token", Utils.brioToken(_Contexto, Integer.parseInt(_Comercio))[0]);
        _OPago.Valor("UID", Utils.getUUID(_Contexto));
        super._ServicioURL = "http://" + BrioGlobales.URL_IPA_BRIO  + "/?v0.0.2/Adquiriente";
        Sincronizar();
    }

    public void VentaREV(IPago IPago){
        _IPago = IPago;
        _OPago.Valor("Tipo", 3);
        _OPago.Valor("Fecha", new SimpleDateFormat("ddMMyyyy",new Locale("Es","MX")).format(Calendar.getInstance().getTime()));
        _OPago.Valor("Hora", new SimpleDateFormat("HHmmss",new Locale("Es","MX")).format(Calendar.getInstance().getTime()));
        _OPago.Valor("CID", _Comercio);
        super._ServicioURL = "http://" + BrioGlobales.URL_IPA_BRIO+ "/?v0.0.2/Adquiriente";
        Sincronizar();
    }

    public void Cancelar(){
        // Sincronizar();
    }

    public void Devolucion(){
        // Sincronizar();
    }

    @Override
    protected String Consulta() {
        return _OPago.toString();
    }

    @Override
    protected void Error(VolleyError Error) {
        _IPago.alError(new Error(Error.getMessage()));
    }

    @Override
    protected Boolean Respuesta(JSONObject Respuesta) {
        try {

            Log.w("Pago", Respuesta.toString());

            if(Respuesta.has("Autorizacion") && Respuesta.has("Codigo")){
                String autorizacion = Respuesta.getString("Autorizacion");
                String codigo = Respuesta.getString("Codigo");

                // Número de autorización y codigo de respuesta
                _OPago.Valor("Codigo", codigo);

                // Es autorizado, declinado, rechazado ?
                switch(codigo){
                    case "A":
                        _OPago.Valor("Autorizacion", autorizacion);
                        break;
                    case "D": case "R":
                        _OPago.Valor("Autorizacion", "0");
                        break;
                }

                // Mensaje
                if(Respuesta.has("Mensaje")){
                    _OPago.Valor("Mensaje", Respuesta.getString("Mensaje"));
                }

                // Scripts ARPC
                if(Respuesta.has("ARPC")){
                    _OPago.Valor("ARPC", Respuesta.getString("ARPC"));
                }

                // Scripts ARQC
                if(Respuesta.has("Arqc")){
                    _OPago.Valor("Arqc", Respuesta.getString("Arqc"));
                }

                // Voucher
                if(Respuesta.has("Voucher")){
                    _OPago.Valor("Voucher", Respuesta.getString("Voucher"));
                }

                // Ultimos 4 digits d ela tarjeta
                if(Respuesta.has("Tarjeta")){
                    _OPago.Valor("Tarjeta", Respuesta.getString("Tarjeta"));
                }

                // Tipo de Cuenta/Tarjeta
                if(Respuesta.has("Cuenta")){
                    _OPago.Valor("Cuenta", Respuesta.getString("Cuenta"));
                }

                // Emisor
                if(Respuesta.has("Emisor")){
                    _OPago.Valor("Emisor", Respuesta.getString("Emisor"));
                }

                if(Respuesta.has("Transaccion")){
                    _OPago.Valor("Transaccion", Respuesta.getString("Transaccion"));
                }

                if(Respuesta.has("Afiliacion")){
                    _OPago.Valor("Afiliacion", Respuesta.getString("Afiliacion"));
                }

                if(Respuesta.has("Referencia")){
                    _OPago.Valor("Referencia", Respuesta.getString("Referencia"));
                }

                if(Respuesta.has("Aid")){
                    _OPago.Valor("Aid", Respuesta.getString("Aid"));
                }

                if(Respuesta.has("Tvr")){
                    _OPago.Valor("Tvr", Respuesta.getString("Tvr"));
                }

                if(Respuesta.has("Tsi")){
                    _OPago.Valor("Tsi", Respuesta.getString("Tsi"));
                }

                if(Respuesta.has("Apn")){
                    _OPago.Valor("Apn", Respuesta.getString("Apn"));
                }

                if(Respuesta.has("Titular")){
                    _OPago.Valor("Titular", Respuesta.getString("Titular"));
                }

                if(Respuesta.has("Expira")){
                    _OPago.Valor("Expira", Respuesta.getString("Expira"));
                }

            }else{
                _OPago.Valor("Autorizacion", "0");
                _OPago.Valor("Mensaje", "No hay servicio.");
            }
        } catch (JSONException Ex) {
            _OPago.Valor("Autorizacion", "0");
            _OPago.Valor("Mensaje", "No hay servicio.");
            Ex.printStackTrace();
        }

        // Almacenar la respuesta, para auditorias
        _OPago.Valor("Respuesta", Respuesta);

        //_IPago.alAutorizar("8A023030"); //  alAutorizar | alDeclinar | alRechazar
        return true;
    }

    public OPago OPago(){
        return _OPago;
    }
}
