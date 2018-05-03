package lat.brio.core.DataBussines;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

import lat.brio.core.DataAccess.DataBaseGeneral;

//import net.sqlcipher.database.SQLiteDatabase;

/**
 * Clase que permite el acceso a la base de datos. Obtiene el objeto SQLiteDatabase que ser? usado por todas las clases para las consultas a la base
 * de datos
 * @author Isabel Merch?n, Rebeca Lazaro
 * @version 2.0
 */

public class DataBaseBusiness {
    
    private static DataBaseGeneral mDataBaseAccess;
    private SQLiteDatabase mAccess;
    
    /**
     * Metodo constructor
     * @param context contexto de la aplicacion
     *
     * @throws Exception
     */
    public DataBaseBusiness (Context context) throws Exception {
        mDataBaseAccess = new DataBaseGeneral (context);
        
        try {
            mDataBaseAccess.crearDataBase ();
            mAccess = mDataBaseAccess.abrirBaseDatos ();
        } catch (IOException ioe) {
            throw new Exception ("No se puede crear la base de datos: " + ioe.getMessage ());
        } catch (SQLException sqle) {
            throw new Exception ("No se puede crear la base de datos: " + sqle.getMessage ());
        } catch (Exception e) {
            throw new Exception ("No se puede crear la base de datos: " + e.getMessage ());
        }
    }
    
    
    /**
     * Metodo que devueleve el objeto SQLiteDatabase que hace referencia la base de datos de la aplicacion
     * @return el objeto SQLiteDatabase quehace referencia a la base de datos de la aplicacion
     */
    public SQLiteDatabase getAccess () {
        
        return mAccess;
    }
    
    /**
     * Metodo encargado de recuperar la base de datos copiandola al sistema de fichero externo
     * @throws Exception
     */
    protected static void recuperarBaseDatos () throws Exception {
        
        mDataBaseAccess.recuperarBaseDatos ();
    }
    
    /**
     * Metodo encargado de hacer un backup de la base de datos copiandola en la carpeta especifiada para ello
     * @throws Exception
     */
    protected static void backupBaseDatos () throws Exception {
        mDataBaseAccess.backupBaseDatos ();
        
    }
    
}
