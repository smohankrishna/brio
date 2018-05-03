package net.hova_it.barared.brio.apis.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AppCompatActivity;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.TimeoutDialogFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Sesion;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogActionListener;
import net.hova_it.barared.brio.apis.utils.TrackService;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lat.brio.core.BrioGlobales;

/**
 * Manager de Sesiones.
 * Contiene toda la informacion del usuario que inicio sesion y la mantiene disponible.
 * Singleton.
 * Gestion de cierre por inactividad.
 * Cierre de sesion y registro en la DB.
 * Created by Alejandro Gomez on 14/12/2015.
 */
public class SessionManager {
    //Todas las keys usadas para la session
    public final static String SESSION_KEY_PAYMENT_INT_LAST_TICKET_ID = "LAST_TICKET_ID";


    private final String TAG = "BARA_LOG";
    private static SessionManager mInstance = null;

    private Context context;
    private SharedPreferences sharedData;
    private Editor editor;
    private ModelManager modelManager;
    private int sessionTimeout;
    private Timer timer;
    private TrackService tracking;
    private boolean isActive;

    private SessionListener sessionListener;

    public static SessionManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SessionManager(context);
        }
        return mInstance;
    }

    private SessionManager(Context context){
        this.context = context;
        this.sharedData = context.getSharedPreferences("BRIO_SESSION", 0);
        this.editor = sharedData.edit();
        this.modelManager = new ModelManager(context);
        this.timer = new Timer();
        this.isActive = false;
    }

   

    /**
     * Lectura de parametro de tipo Int de sesion
     * @param key
     * @return
     */
    public int readInt(String key){
        return sharedData.getInt(key, 0);
    }

    /**
     * Guardado de parametro de tipo Int en sesion
     * @param key
     * @param value
     * @return
     */
    public boolean saveInt(String key, int value){
        return sharedData.edit().putInt(key, value).commit();
    }

    /**
     * Lectura de parametro de tipo Long de sesion
     * @param key
     * @return
     */
    public long readLong(String key){
        return sharedData.getLong(key, 0);
    }

    /**
     * Guardado de parametro de tipo Long en sesion
     * @param key
     * @param value
     * @return
     */
    public boolean saveLong(String key, long value){
        return sharedData.edit().putLong(key, value).commit();
    }

    /**
     * Lectura de parametro de tipo String de sesion
     * @param key
     * @return
     */
    public String readString(String key){
        return sharedData.getString(key, "");
    }

    /**
     * Guardado de parametro en tipo String de sesion
     * @param key
     * @param value
     * @return
     */
    public boolean saveString(String key, String value){
        return sharedData.edit().putString(key, value).commit();
    }

    /**
     * Inicio de sesion
     * @param sessionData
     * @param sessionTimeout
     * @return
     */
    public boolean open(Map<String, Object> sessionData, int sessionTimeout){
        DebugLog.log(getClass(), "BRIO_SESSION", "llaman open");
        tracking = new TrackService(context, mInstance);
        tracking.start();

        Sesion sesion = new Sesion();
        sesion.setIdCaja((int) sessionData.get("idCaja"));
        sesion.setIdUsuario((int) sessionData.get("idUsuario"));
        sesion.setFechaInicio((long) sessionData.get("fechaInicio"));
        // TODO Fill All Session

        int idSesion = modelManager .sesiones.save(sesion).intValue();

        saveInt("idSesion", idSesion);
        saveInt("idCaja", (int) sessionData.get("idCaja"));
        saveString("nombreCaja", modelManager.settings.getByNombre("NOMBRE_CAJA").getValor());
        saveInt("idUsuario", (int) sessionData.get("idUsuario"));
        saveLong("fechaInicio", (long) sessionData.get("fechaInicio"));
        saveInt("idComercio", (int) sessionData.get("idComercio"));
        saveString("descripcionComercio", sessionData.get("descripcionComercio").toString());
        saveString("direccionLegal", sessionData.get("direccionLegal").toString());
        saveString("codigoPostalLegal", sessionData.get("codigoPostalLegal").toString());
        saveString("usuario", sessionData.get("usuario").toString());
        saveInt("idPerfil", (int) sessionData.get("idPerfil"));

        this.setTimeout(sessionTimeout);
        this.isActive = true;
//        update();

        return true;
    }

    /**
     * Cierre de sesion y registro en la DB
     *
     * @return
     */
    public boolean close () {
        

        if(readInt("idSesion") > 0) {
            Sesion sesion = modelManager.sesiones.getByIdSesion(readInt("idSesion"));
            sesion.setFechaFin(Utils.getCurrentTimestamp());
            modelManager.sesiones.save(sesion);
            editor.clear().commit();

            timer.cancel();
            timer.purge();

            //Log.d(TAG, "Session Clear & Ended");
        }else{
            //Log.d(TAG, "No active sessions");
            return false;
        }

        this.isActive = false;
 
        //todo listener oye activity cierrate a login
        sessionListener.onSessionClose();

        return true;
    }

    /**
     * Actualizacion de timeout de sesion
     * @param minutes
     */
    private void setTimeout(int minutes){
        this.sessionTimeout = minutes * 60 * 1000;
    }

    /**
     * Actualizar el timeout de sesion
     */
    public void update() {
        timer.cancel();
        timer.purge();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                final String msg = Utils.getString(R.string.session_close_inactive, context);
                if(((BrioActivityMain)context).isActivityRunning()) {
                    TimeoutDialogFragment timeout = TimeoutDialogFragment.newInstance(msg, 30);
                    timeout.setListener(new DialogActionListener() {
                        @Override
                        public void onAcceptResult(Object results) {
                            //try {
                                close();
                            //}catch(Exception Ex){
                            //    Ex.printStackTrace();
                            //};
                        }

                        @Override
                        public void onCancelResult(Object results) {
                            //try {
                                update();
                            //}catch(Exception Ex){
                            //    Ex.printStackTrace();
                            //};
                        }
                    });
                    timeout.show(((AppCompatActivity) context).getSupportFragmentManager(), "timeoutsesion");
                } else {
                    try {
                        close();
                    }catch(Exception Ex){
                        Ex.printStackTrace();
                    }
                }
            }
        };

        if (this.isActive) {
            timer = new Timer();
            timer.schedule(timerTask, this.sessionTimeout);
        }


        //Log.d(TAG, "Session Updated");
    }
    
    public void showAlertSesionTimeOut( DialogActionListener listener){
        
        try{
            final String msg = Utils.getString(R.string.session_close_inactive, context);
            TimeoutDialogFragment timeout = TimeoutDialogFragment.newInstance(msg, 30);
            timeout.setListener(listener);
            timeout.show(((BrioBaseActivity) context).getSupportFragmentManager(), "timeoutsesion");
        }catch(Exception Ex){
            Ex.printStackTrace();
            BrioGlobales.writeLog (TAG,"showAlertSesionTimeOut",Ex.getMessage ());
        }
    }

    /**
     * Obtener status de sesion
     * @return
     */
    public boolean isActive(){
        return this.isActive;
    }

    public void setSessionListener (SessionListener listener) {
        sessionListener = listener;
    }

    /**
     * Manejo de cierre de sesion
     */
    public interface SessionListener {
        void onSessionClose ();
    }
}

