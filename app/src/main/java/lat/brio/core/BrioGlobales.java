package lat.brio.core;


import android.database.sqlite.SQLiteDatabase;

import brio.sdk.logger.DataAccess.AccesoArchivosGeneral;
import brio.sdk.logger.util.BrioUtilsFechas;

/**
 * Created by guillermo.ortiz on 26/12/17.
 */

public class BrioGlobales {

    // SQLiteDatabase utilizado para acceder a la base de datos
    private static SQLiteDatabase mAccess;
    public static final String DB_NAME = "brio.db";
    public static final int DB_VERSION = 1;
    public static final String DIR_DB = "/databases/";
    public static final String DIR_APK = "apk";
    public static final String DIR_FILE_DB = "db";
    public static String APP_PATH;
    public static String DB_PATH;
    public static String DIR_APP_BACKUP = "backup";
    public static  String ID_Comercio="";//Codigo comercio

    public static AccesoArchivosGeneral ArchivoLog;
    public static String KEY_ROWID = "rowid";
    public static String version;

    //    bandera para desarrollo
    public static final boolean Desarrollador = true;
    public static final int DIEZ_SEGUNDOS_TIME_OUT_WEBSERVICE = 35 * 1000;
    public static final boolean VERSION_BETA = true;
    public static final String EMPTY_STRING = "";

    // ESTADOS ASOCIADOS A LOS CAMBIOS EN LA BASE DE DATOS
    public static final String statusMobileCrear = "01";
    public static final String statusMobileModificar = "02";
    public static String statusMobileBorrar = "99";
    public static String statusMobileNeutro = "00";

    ///////////////////////////////////////////////////////////////////////////
    // Informacion del FTP,
    ///////////////////////////////////////////////////////////////////////////

    public static final String FTP_SERVER = "miftp.brio.lat";
//    public static final String FTP_SERVER = "74.208.228.112";
    public static final int FTP_SERVER_PUERTO = 148;
    public static final String FTP_SERVER_USUARIO = "android";
    public static final String FTP_SERVER_PASS = "Barared2014";
    public static final String FTP_SERVER_FOLDER_APK = "/apk";


    ///////////////////////////////////////////////////////////////////////////
    // URL servicios
    ///////////////////////////////////////////////////////////////////////////

    private static final String URL_BASE_BRIO = "brio.lat";
//    public static final String URL_WEB_BRIO = "web." + URL_BASE_BRIO;
    public static final String URL_WEB_BRIO = "web." + URL_BASE_BRIO;
    public static final String URL_IPA_BRIO = "ipa." + URL_BASE_BRIO;
    public static final String URL_ZERO_BRIO = "zero." + URL_BASE_BRIO + ":8086";
    public static final String URL_CARD_IP_BRIO = "zero." + URL_BASE_BRIO + ":8086";
    public static final String URL_SYNC_IP_BRIO = "zero." + URL_BASE_BRIO + ":8086";

    public static final String NAME_APK = "app-release-N-.apk";

    ///////////////////////////////////////////////////////////////////////////
    // CONFIGURACIÓN DEL SERVIDOR DE TICKETS
    ///////////////////////////////////////////////////////////////////////////

    public static final String TICKET_MAIL_SENDER = "sistemas@brio.lat";
    public static final String TICKET_MAIL_PASS = "SaPi2016";
    public static final String TICKET_MAIL_NOREPPLY = "no-responder@brio.lat";
    public static final String TICKET_MAIL_HOST = "smtp.emailsrvr.com";
//    public static final String TICKET_MAIL_HOST = "micorreo.arcacontal.com";
    public static final String TICKET_MAIL_PORT = "587";

    public static final String MAIL_SUPPORT = "brio.log@brio.lat";

    ///////////////////////////////////////////////////////////////////////////
    // BillPocket
    ///////////////////////////////////////////////////////////////////////////

    public static final String NAME_PACKAGE_BILLPOCKET = "com.billpocket.billpocket";
    public static final String META_BILLPOCKET_PAYMENT_SERVICE = "billpocket.payments.START";
    public static final String FL_KEY_BILLPOCKET = "BillPocketService";


    ///////////////////////////////////////////////////////////////////////////
    // Constantes para descarga de apk
    ///////////////////////////////////////////////////////////////////////////
    public static final int UPDATE_ERR_DOWNLOAD = 1;
    public static final int UPDATE_ERR_INSTALL = 2;

    public static final int UPDATE_OK_UPTODATE = 6;
    public static final int UPDATE_OK_INSTALL = 7;

    ///////////////////////////////////////////////////////////////////////////
    // key para los registros de la tabla settings
    ///////////////////////////////////////////////////////////////////////////

    public static final String KEY_BRIO_VER_MAYOR = "BRIO_VER_MAYOR";
    public static final String KEY_BRIO_VER_MENOR = "BRIO_VER_MENOR";
    public static final String KEY_BRIO_VER_ERROR = "BRIO_VER_ERROR";
    public static final String KEY_BRIO_ACTUALIZA_VER = "BRIO_ACTUALIZA_VER";
    public static final String KEY_BRIO_VER_PREV = "BRIO_PREV_VER";
    public static final String KEY_BRIO_VER_ISBETA = "BRIO_VER_ISBETA";
    public static final String KEY_BRIO_VER_ROLLBACK = "BRIO_VER_ROLLBACK";
    public static final String KEY_ROLLBACK_TRUE = "1";
    public static final String KEY_ROLLBACK_FALSE = "0";
    public static final String KEY_ROLLBACK_DEFAULT = "-1";

    public static final String KEY_MI_BRIO_ORDERNAMIENTO = "MI_BRIO_ORDERNAMIENTO";

    public static SQLiteDatabase getAccess () {
        return mAccess;
    }

    public static void setAccess (SQLiteDatabase access) {
        mAccess = access;
    }

    public static void writeLog (String claseName, String methodName, String errorMesaje) {
        if(ArchivoLog!=null)
        {
            ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat ()+ " " +claseName + " ->" +methodName + " ->" + errorMesaje);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // time para los AlarmManager de sesión, banners y sincronizacion
    ///////////////////////////////////////////////////////////////////////////

    public static final int TIME_BANER_HOUR = 0;
    public static final int TIME_BANER_MIN = 20;
    public static final int TIME_BANER_SEC = 0;

    public static final int TIME_SYNC_HOUR = 1;
    public static final int TIME_SYNC_MIN = 0;
    public static final int TIME_SYNC_SEC = 0;

    public static final int TIME_SESSION_HOUR = 3;
    public static final int TIME_SESSION_MIN = 0;
    public static final int TIME_SESSION_SEC = 0;


}
