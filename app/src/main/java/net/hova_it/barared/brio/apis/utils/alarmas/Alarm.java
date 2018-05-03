package net.hova_it.barared.brio.apis.utils.alarmas;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.hova_it.barared.brio.apis.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.AppController;
import lat.brio.core.BrioGlobales;

/**
 * Created by guillermo.ortiz on 12/03/18.
 */

public class Alarm {
    
    private static String TAG = Alarm.class.getName ();
    
    public static final String BROADCAST_BANNERS = "net.hova_it.barared.brio.apis.utils.alarmas.android.action.broadcast.banners";
    public static final String BROADCAST_APP_SYNC = "net.hova_it.barared.brio.apis.utils.alarmas.android.action.broadcast.appsync";
    public static final String BROADCAST_SESION_TIMEOUT = "net.hova_it.barared.brio.apis.utils.alarmas.android.action.broadcast.sesiontimeout";
    static int ALARMMANAGER_REQUESTCODE_BANNER = 2090;
    static int ALARMMANAGER_REQUESTCODE_APP_SYNC = 2095;
    static int ALARMMANAGER_REQUESTCODE_SESION_TIMEOUT = 2100;
    
    static int flagBanner = PendingIntent.FLAG_ONE_SHOT;
    public static int INTERVAL_BANNER = 1000 * 60 * 60;
    public static int INTERVAL_APP_SYNC = 1000 * 60; /** 60*/
    public static int INTERVAL_APP_SESION_TIMEOUT = 1000 * 60 /** 60*/
            ;
    
    /**
     * genera alarma cada hora y cancela si ya existe una, es decir resetea la alarma y la genera nuevamente
     */
    public static void generaAlarmaBanners (Context context) {
        
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService (Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent (BROADCAST_BANNERS); // AlarmReceiver1 = broadcast receiver
            PendingIntent pendingIntent = PendingIntent.getBroadcast (context, ALARMMANAGER_REQUESTCODE_BANNER, alarmIntent, flagBanner);
            if (alarmManager != null) {
                alarmManager.cancel (pendingIntent);
                Calendar alarmStartTime = Calendar.getInstance ();
                alarmStartTime.add(Calendar.HOUR_OF_DAY, BrioGlobales.TIME_BANER_HOUR);
                alarmStartTime.add(Calendar.MINUTE, BrioGlobales.TIME_BANER_MIN);
                alarmStartTime.add(Calendar.SECOND,  BrioGlobales.TIME_BANER_SEC);
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), pendingIntent);
//                alarmManager.setRepeating (AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis (), INTERVAL_BANNER, pendingIntent);
                String fecha = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss").format (alarmStartTime.getTimeInMillis ());
              //  Log.e (TAG + " - ", "generaAlarmaBanners :" + fecha);
            }
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + TAG + "->generaAlarmaBanners" + e.getMessage ());
        }
    }
    
    /**
     * genera alarma cada hora y cancela si ya existe una, es decir resetea la alarma y la genera nuevamente
     */
    public static void generaAlarmaSincronizarApp (Context context) {
        
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService (Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent (BROADCAST_APP_SYNC); // AlarmReceiver1 = broadcast receiver
            PendingIntent pendingIntent = PendingIntent.getBroadcast (context, ALARMMANAGER_REQUESTCODE_APP_SYNC, alarmIntent, flagBanner);
            if (alarmManager != null) {
                alarmManager.cancel (pendingIntent);
                Calendar alarmStartTime = Calendar.getInstance ();
                alarmStartTime.add(Calendar.HOUR_OF_DAY,  BrioGlobales.TIME_SYNC_HOUR);
                alarmStartTime.add(Calendar.MINUTE, BrioGlobales.TIME_SYNC_MIN);
                alarmStartTime.add(Calendar.SECOND,  BrioGlobales.TIME_SYNC_SEC);
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), pendingIntent);
    
//                alarmManager.setRepeating (AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis (), INTERVAL_APP_SYNC, pendingIntent);
                String fecha = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss").format (alarmStartTime.getTimeInMillis ());
              //  Log.e(TAG + " - ", "generaAlarmaSincronizarApp:" + fecha);
            }
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + TAG + "->generaAlarmaSincronizarApp" + e.getMessage ());
        }
    }
    
     /**
     * genera alarma cada hora y cancela si ya existe una, es decir resetea la alarma y la genera nuevamente
     */
    public static void generaAlarmaCerrarSesion (Context context) {
        
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService (Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent (BROADCAST_SESION_TIMEOUT); // AlarmReceiver1 = broadcast receiver
            PendingIntent pendingIntent = PendingIntent.getBroadcast (context, ALARMMANAGER_REQUESTCODE_SESION_TIMEOUT, alarmIntent, flagBanner);
            if (alarmManager != null) {
                alarmManager.cancel (pendingIntent);
                Calendar alarmStartTime = Calendar.getInstance ();
                alarmStartTime.add(Calendar.HOUR_OF_DAY,  BrioGlobales.TIME_SESSION_HOUR);
                alarmStartTime.add(Calendar.MINUTE, BrioGlobales.TIME_SESSION_MIN);
                alarmStartTime.add(Calendar.SECOND,  BrioGlobales.TIME_SESSION_SEC);
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), pendingIntent);
    
                //  alarmManager.setRepeating (AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis (), INTERVAL_APP_SYNC, pendingIntent);
                String fecha = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss").format (alarmStartTime.getTimeInMillis ());
               // Log.i (TAG + " - ", "generaAlarmaCerrarSesion:" + fecha);
            }
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + TAG + "->generaAlarmaSincronizarApp" + e.getMessage ());
        }
    }
    
    
    public static void cancelarAlarmaBanner (Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService (Context.ALARM_SERVICE);
            Intent intent_sesion = new Intent (BROADCAST_BANNERS);
            PendingIntent pendingIntentBroadCast = PendingIntent.getBroadcast (context, ALARMMANAGER_REQUESTCODE_BANNER, intent_sesion, flagBanner);
            
            if (alarmManager != null) {
                alarmManager.cancel (pendingIntentBroadCast);
            }
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + TAG + "->cancelarAlarmaBanner" + e.getMessage ());
        }
    }
    
    public static void cancelarAlarmaSincronizarApp (Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService (Context.ALARM_SERVICE);
            Intent intent_sesion = new Intent (BROADCAST_APP_SYNC);
            PendingIntent pendingIntentBroadCast = PendingIntent.getBroadcast (context, ALARMMANAGER_REQUESTCODE_APP_SYNC, intent_sesion, flagBanner);
            
            if (alarmManager != null) {
                alarmManager.cancel (pendingIntentBroadCast);
            }
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + TAG + "->cancelarAlarmaSincronizarApp" + e.getMessage ());
        }
    }
    
    public static void cancelarAlarmaCerrarSesion (Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService (Context.ALARM_SERVICE);
            Intent intent_sesion = new Intent (BROADCAST_SESION_TIMEOUT);
            PendingIntent pendingIntentBroadCast = PendingIntent.getBroadcast (context, ALARMMANAGER_REQUESTCODE_SESION_TIMEOUT, intent_sesion, flagBanner);
            
            if (alarmManager != null) {
                alarmManager.cancel (pendingIntentBroadCast);
            }
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + TAG + "->cancelarAlarmaCerrarSesion" + e.getMessage ());
        }
    }
}
