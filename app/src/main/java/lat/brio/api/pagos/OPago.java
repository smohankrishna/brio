package lat.brio.api.pagos;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

import lat.brio.api.servicio.IServicio;
import lat.brio.api.servicio.Servicio;

/**
 * Created by Delgadillo on 15/07/17.
 */

public class OPago extends JSONObject {
    // Constructor para objeto de pago
    public OPago(){
        super();
    }

    // Establecer
    public Boolean Valor(String Clave, Object Valor){
        try {
            this.accumulate(Clave, Valor);
            return true;
        } catch (JSONException Ex) {
            Ex.printStackTrace();
            return false;
        }
    }

    // Obtener
    public Object Valor(String Clave){
        try {
            return this.get(Clave);
        } catch (JSONException Ex) {
            Ex.printStackTrace();
            return "";
        }
    }

}
