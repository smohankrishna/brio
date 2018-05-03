package lat.brio.core.bussines;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.hova_it.barared.brio.apis.models.DLCorreosClientes;
import net.hova_it.barared.brio.apis.models.DLTicketsEnviadosClientes;
import net.hova_it.barared.brio.apis.models.daos.CorreosClientesDB;
import net.hova_it.barared.brio.apis.models.daos.TicketsEnviadosClientesDB;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.ArrayList;

import lat.brio.core.BrioGlobales;

/**
 * Created by guillermo.ortiz on 07/03/18.
 */

public class BrCustomer extends BRMain {
    
    private static BrCustomer instance;
    
    CorreosClientesDB correosClientesDB;
    TicketsEnviadosClientesDB ticketsEnviadosClientesDB;
    
    
    public static BrCustomer getInstance (Context context, SQLiteDatabase mAcess) {
        if (instance == null) {
            instance = new BrCustomer (context.getApplicationContext (), mAcess);
        }
        
        return instance;
    }
    
    public BrCustomer (Context contexto, SQLiteDatabase mAccess) {
        super (contexto, mAccess);
        
        correosClientesDB = new CorreosClientesDB (contexto, mAccess);
        ticketsEnviadosClientesDB = new TicketsEnviadosClientesDB (contexto, mAccess);
    }
    
    /**
     * Metodo encargado de obtener el listado de correos registrados de la tabla  email
     * @return
     */
    
    public ArrayList<DLCorreosClientes> getCustomerEmails () {
        ArrayList<DLCorreosClientes> dl = new ArrayList<DLCorreosClientes> ();
        
        try {
            
            Cursor cursor = correosClientesDB.busquedaAvanzada (CorreosClientesDB.columnas, null, null, null, null, null);
            if (cursor.moveToFirst ()) {
                do {
                    dl.add (new DLCorreosClientes (cursor));
                } while (cursor.moveToNext ());
            }
            if (! cursor.isClosed ()) {
                cursor.close ();
            }
            
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.writeLog ("BrCustomer", "getCustomerEmails()", e.getMessage ());
        }
        return dl;
    }
    
    public long saveCustomerEmail (DLCorreosClientes dl) {
        long actualizado = 0;
        try {
            String keys[] = new String[] {
                    CorreosClientesDB.KEY_EMAIL, CorreosClientesDB.KEY_FECHA_CREACION,
                    CorreosClientesDB.KEY_FECHA_MODIFICACION, CorreosClientesDB.KEY_ID_REMOTO,
                    CorreosClientesDB.KEY_STATUS_MOBILE };
            String values[] = new String[] {
                    dl.getEmail (), dl.getFecha_creacion (),
                    dl.getFecha_modificacion (), dl.getId_remoto (),
                    BrioGlobales.statusMobileCrear };
            actualizado =  correosClientesDB.insertarNuevoRegistro (keys, values);
            
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.writeLog ("BrCustomer", "saveCustomerEmail()", e.getMessage ());
        }
        return actualizado;
    }
    
    public DLCorreosClientes getCustomerEmail (String email) {
        DLCorreosClientes dl = null;
        try {
            String where = correosClientesDB.generarCondicionStr (CorreosClientesDB.KEY_EMAIL, email);
            Cursor cursor = correosClientesDB.busquedaAvanzada (CorreosClientesDB.columnas, where, null, null, null, CorreosClientesDB.KEY_EMAIL);
            
            if (cursor.moveToFirst ()) {
                dl = new DLCorreosClientes (cursor);
                
            }
            if (! cursor.isClosed ()) {
                cursor.close ();
            }
            return dl;
            
        } catch (Exception e) {
            BrioGlobales.writeLog ("BrCustomer", "saveCustomerEmail()", e.getMessage ());
            return dl;
        }
    }
    
    public long updateCustomerEmail (String email) {
        
        long actualizado = 0;
        try {
            String where = correosClientesDB.generarCondicionStr (CorreosClientesDB.KEY_EMAIL, email);
            String keys[] = new String[] {
                    CorreosClientesDB.KEY_FECHA_MODIFICACION, CorreosClientesDB.KEY_STATUS_MOBILE
            };
            String values[] = new String[] { Utils.getStrFmtDateHour (),
                    BrioGlobales.statusMobileModificar
            };
            
            actualizado = correosClientesDB.consolidarCambiosAvanzado (where, keys, values);
        } catch (Exception e) {
            BrioGlobales.writeLog ("BrCustomer", "updateCustomerEmail()", e.getMessage ());
        }
        return actualizado;
    }
    
    
    public long saveTicketEnviadoCliente (DLTicketsEnviadosClientes dl) {
        
        long actualizado = 0;
        
        String keys[] = new String[] {
                TicketsEnviadosClientesDB.KEY_EMAIL, TicketsEnviadosClientesDB.KEY_FECHA_CREACION,
                TicketsEnviadosClientesDB.KEY_FECHA_MODIFICACION, TicketsEnviadosClientesDB.KEY_ID_REMOTO,
                TicketsEnviadosClientesDB.KEY_STATUS_MOBILE, TicketsEnviadosClientesDB.KEY_ID_TICKETS };
        String values[] = new String[] { dl.getEmail (), dl.getFecha_creacion (),
                dl.getFecha_modificacion (), dl.getId_remoto (), BrioGlobales.statusMobileCrear, String.valueOf (dl.getId_ticket () ) };
        
        String where = ticketsEnviadosClientesDB.generarCondicion (
                TicketsEnviadosClientesDB.KEY_ID_TICKETS ,String.valueOf (dl.getId_ticket () ));
        where+=" AND ";
        where+=ticketsEnviadosClientesDB.generarCondicionStr (TicketsEnviadosClientesDB.KEY_EMAIL,dl.getEmail ());
    
        try {
            actualizado = ticketsEnviadosClientesDB.consolidarCambiosAvanzado( where, keys, values );
            if (actualizado <= 0) {
                actualizado = ticketsEnviadosClientesDB.insertarNuevoRegistro( keys, values );
            }

        
        } catch (Exception e) {
            e.printStackTrace();
            BrioGlobales.writeLog ("BrCustomer", "saveTicketEnviadoCliente()", e.getMessage ());
        }
    
    
        return actualizado;
    }
}
