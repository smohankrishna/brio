package net.hova_it.barared.brio.apis.sync;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.alarmas.Alarm;

import java.util.concurrent.ScheduledExecutorService;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.api.dialogo.BrioSyncRememberDialog;
import lat.brio.core.BrioGlobales;

/**
 * Recordatorio de sincronización de la aplicación
 *
 * Created by Herman Peralta on 08/07/2016.
 * Modificado Guillermo Ortiz 13/03/2018
 */
public class SyncRememberManager {
    
    private final static int DIALOG_TIME_MINUTES = 60;
    private final static int REMEMBER_LIMIT_HOURS = 24;
    
    private Context context;
    private ScheduledExecutorService scheduleTaskExecutor;
    private SessionManager managerSession;
    private BrioSyncRememberDialog dialog;
    
    private static SyncRememberManager daManager;
    
    private static boolean running = false;
    
    public static SyncRememberManager getInstance (Context context) {
        if (daManager == null) {
            daManager = new SyncRememberManager (context);
        }
        
        return daManager;
    }
    
    private SyncRememberManager (Context context) {
        this.context = context;
        managerSession = SessionManager.getInstance (context);
    }
    
    /**
     * Inicia una alarma periodica que revisará cada hora la fecha de la
     * última sincronización y si es mayor a REMEMBER_LIMIT_HOURS, indicará
     * con un fragment que se debe sincronizar.
     */
    public void alarm () {
        if (running) {
            return;
        }
        running = true;
        
        try {
            long lastUpdate = managerSession.readLong ("LAST_UPDATE");//Long.valueOf(managerModel.settings.getByNombre("LAST_UPDATE").getValor());
            long now = Utils.getCurrentTimestamp ();
            
            final int hoursSinceLastUpdate = (int) (((now - lastUpdate) / 60) / 60);
            
            if (hoursSinceLastUpdate >= REMEMBER_LIMIT_HOURS) {
                Alarm.cancelarAlarmaSincronizarApp (context);
                ((AppCompatActivity) context).runOnUiThread (new Runnable () {
                    @Override
                    public void run () {
                        if (dialog != null) {
                            dialog.dismiss ();
                        }
                        if (running) {
                            if (context != null) {
                                dialog = new BrioSyncRememberDialog (context);
                                dialog.show (hoursSinceLastUpdate);
                                dialog.Cerrar (new OnClickListener () {
                                    @Override
                                    public void onClick (View v) {
                                        running = false;
                                        dialog.dismiss ();
                                        Alarm.generaAlarmaSincronizarApp (context);
                                    }
                                });
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + "SyncRememberManager->alarm" + e.getMessage ());
        }
    }
    
}
