package lat.brio.core.BrioBussines;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.daos.TMAEVersionDB;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.utils.Utils;

import brio.sdk.logger.Business.GlobalesLogger;
import brio.sdk.logger.DataAccess.AccesoArchivosGeneral;
import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;
import lat.brio.core.DataBussines.DataBaseBusiness;

/**
 * Created by guillermo.ortiz on 07/03/18.
 */

public class BrioBusinessRules extends BrioBusinessRulesBase {
    
    private static BrioBusinessRules instance;
    
    private boolean inicializado = false;
    
    private static TMAEVersionDB tmaeVersion;
    private Context context;
    
    
    public static BrioBusinessRules getInstance (Context context, SQLiteDatabase mAccess) {
        if (instance == null) {
            instance = new BrioBusinessRules (context.getApplicationContext (),mAccess);
        }
        return instance;
    }
    
    public BrioBusinessRules (Context contexto, SQLiteDatabase mAccess) {
        super(contexto, mAccess);
        context = contexto;
    
        mAccess = BrioGlobales.getAccess();
        tmaeVersion = new TMAEVersionDB (context, mAccess);
    }
    
    @Override
    public void inicializar () {
        
        try {
            initDefinicionDatos ();
            
            
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    private void initDefinicionDatos () {
        try {
            String anioMes;
            anioMes = BrioUtilsFechas.obtenerFechaDia ();
            
            if (anioMes.length () == 8) {
                anioMes = anioMes.substring (2, 6);
            }
            GlobalesLogger.nombreFichLogApp = "LogBrio.txt";
            BrioGlobales.APP_PATH = getApp().dataDir;
            BrioGlobales.DB_PATH = BrioGlobales.APP_PATH + BrioGlobales.DIR_DB;
            
            
            //se añade la barra porque si esta tomando el path del conetxt no le pone la barra
            if (! GlobalesLogger.APP_FILES_PATH.endsWith ("/"))
                GlobalesLogger.APP_FILES_PATH += "/";
            BrioGlobales.ArchivoLog = new AccesoArchivosGeneral (getContexto ());
            BrioGlobales.ArchivoLog.crearFicheroEscritura (GlobalesLogger.APP_FILES_PATH, anioMes + GlobalesLogger.nombreFichLogApp, true);
            
            //   BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + TAG + "Test ");
            AccesoArchivosGeneral.crearDirectorio (GlobalesLogger.APP_FILES_PATH + BrioGlobales.DIR_APP_BACKUP);
            BrioGlobales.version = getContexto().getPackageManager ().getPackageInfo (getContexto().getPackageName (), 0).versionName;
            //            Inicializa bd
            DataBaseBusiness BaseDatos = new DataBaseBusiness (getContexto());
            SQLiteDatabase mAccess = BaseDatos.getAccess ();
            BrioGlobales.setAccess (mAccess);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    @Override
    public void actualizaciones () {
        Actualizacion00 ();
        Actualizacion01 ();
        actualizacionBase ();
    }
    
    
    private void actualizacionBase () {
        
        try {
            String[] columnas = new String[] { TMAEVersionDB.KEY_VERSION };
            String where = tmaeVersion.generarCondicionStr (TMAEVersionDB.KEY_VERSION, BrioGlobales.version);
            Cursor cursor = tmaeVersion.busquedaAvanzada (columnas, where, null, null, null, null);
            if (! cursor.moveToFirst ()) {
                long estado = tmaeVersion.insertarNuevoRegistro (new String[] { TMAEVersionDB.KEY_VERSION }, new String[] { BrioGlobales.version });
                if (estado <= 0) {
                    BrioGlobales.writeLog ("BrioBusinessRules", "Actualizacion01", "");
                }
            }
            if (! cursor.isClosed ()) {
                cursor.close ();
            }
            
        } catch (Exception e) {
            BrioGlobales.writeLog ("BrioBusinessRules", "actualizacionBase", e.getMessage ());
        }
    }
    
    private void Actualizacion00 () {
        try {
            SQLiteInit query = new SQLiteInit ();
            SQLiteDatabase db = BrioGlobales.getAccess ();
            db.execSQL (query.createTableTMAEVersion ());
        } catch (Exception e) {
            BrioGlobales.writeLog ("BrioBusinessRules", "Actualizacion00", e.getMessage ());
        }
    }
    
    
    /**
     * Encargado de crear la tabla Mail si no existe
     */
    private void Actualizacion01 () {
        
        try {
            SQLiteInit query = new SQLiteInit ();
            SQLiteDatabase db = BrioGlobales.getAccess ();
            db.execSQL (query.createTableMailUser ());
            db.execSQL (query.createTableTickesEnciados ());

            //Eliminamos la vista de inventarios, despues lavolvemos  a crear.
            //Por lo pronto se queda asi, hasta gestionar una manera de versionamiento, cuand
            //los asociados ya cuenten con una version beta y una version release

            db.execSQL(query.dropViewInventario());
            db.execSQL(query.createViewInventario());
        } catch (Exception e) {
            BrioGlobales.writeLog ("BrioBusinessRules", "Actualizacion01", e.getMessage ());
        }
        
    }
    
    public boolean getInicializado () {
        return inicializado;
    }
    
    public void setInicializado (boolean inicializado) {
        this.inicializado = inicializado;
    }
    
}
