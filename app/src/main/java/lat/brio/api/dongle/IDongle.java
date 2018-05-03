package lat.brio.api.dongle;

import lat.brio.api.pagos.OPago;
import lat.brio.api.pagos.Pago;

/**
 * Created by Delgadillo on 11/07/17.
 */

// Interfaz de conexión con dongle BrioPocket
public interface IDongle {
    void alError (Error Error); // Para indicar algun error durante el proceso con dongle
    void alConectar (String ID); // Para indicar que se conectó con dongle
    void alConcluir (OPago Pago); // Al concluir un pago, devuelve el objeto de Pago en linea con dongle
    void alConcluir (OPago Pago, String ARPC); // Al concluir un pago, devuelve el objeto de Pago y el ARPC en linea con dongle
    void alRechazar (String Codigo, String Mensaje); // Al rechazar un pago, devuelve el mensaje motivo por el cual fue rechazado
    void alDeclinar (String Codigo, String Mensaje); // Al declinar un pago, devuelve el mensaje motivo por el cual fue declinado
    void alCambiar (String Estado); // Para indicar el cambio de estado de dongle
    void alAutorizar (); // Evento que indica la conclusion del proceso de pago con dongle
}
