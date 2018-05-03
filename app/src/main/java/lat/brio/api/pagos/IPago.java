package lat.brio.api.pagos;

/**
 * Created by Delgadillo on 17/07/17.
 */

public interface IPago {
    void alAutorizar (String Autorizacion);
    void alAutorizar (String Autorizacion, String ARPC);
    void alRechazar (String ID, String Mensaje);
    void alDeclinar (String ID, String Mensaje);
    void alError (Error Error);
}