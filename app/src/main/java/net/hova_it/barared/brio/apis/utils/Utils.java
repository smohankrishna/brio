package net.hova_it.barared.brio.apis.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;

import com.google.gson.Gson;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.mibrio.TagMiBrioEnum;
import net.hova_it.barared.brio.apis.models.CachedValue;
import net.hova_it.barared.brio.apis.models.DLSortMenu;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.ItemsTicket;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.payment.PaymentService;
import net.hova_it.barared.brio.apis.pos.api.ItemsTicketController;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.utils.info.BrioAppInfoPojo;
import net.hova_it.barared.brio.apis.utils.info.BrioErrorReportPojo;
import net.hova_it.barared.brio.apis.utils.info.InfoPojo;
import net.hova_it.barared.brio.apis.utils.info.SystemInfoPojo;
import net.hova_it.barared.brio.apis.utils.preferencias.Preferencias;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.AppController;
import lat.brio.core.BrioGlobales;
import lat.brio.core.support.DateTimeItem;
import lat.brio.core.support.EFormatos;
import lat.brio.norris.SplashScreen;

/**
 * Created by Alejandro Gomez on 15/12/2015.
 */
public class Utils {
    private static final String TAG = "Utils";
    
    
    /**
     * Obtiene un metadata del manifest.xml
     * @param name
     * @param context
     *
     * @return
     */
    public static String getMetadata (String name, Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        String metadata = "";
        try {
            ApplicationInfo ai = activity.getPackageManager ().getApplicationInfo (activity.getPackageName (), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            metadata = bundle.getString (name);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e (TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage ());
        } catch (NullPointerException e) {
            Log.e (TAG, "Failed to load meta-data, NullPointer: " + e.getMessage ());
        }
        return metadata;
    }
    
    /**
     * Obtiene un string de resources por su nombre.
     * @param name
     * @param context
     *
     * @return
     */
    public static String getString (String name, Context context) {
        int id = context.getResources ().getIdentifier (name, "string", context.getPackageName ());
        return context.getResources ().getString (id);
    }
    
    /**
     * Obtiene un string de resources por su Id.
     * @param id
     * @param context
     *
     * @return
     */
    public static String getString (int id, Context context) {
        return context.getResources ().getString (id);
    }
    
    /**
     * Obtiene un array de string de resources por su id.
     * @param id
     * @param context
     *
     * @return
     */
    public static String[] getStringArray (int id, Context context) {
        return context.getResources ().getStringArray (id);
    }
    
    /**
     * Formatea un modelo de datos en un string para visualizar su contenido.
     * @param pojo
     *
     * @return
     */
    public static String pojoToString (Object pojo) {
        StringBuilder result = new StringBuilder ();
        String newLine = System.getProperty ("line.separator");
        
        result.append (pojo.getClass ().getName ());
        result.append (" Object {");
        result.append (newLine);
        
        Field[] fields = pojo.getClass ().getDeclaredFields ();
        
        for (Field field : fields) {
            result.append ("  ");
            try {
                field.setAccessible (true);
                result.append (field.getName ());
                result.append (": ");
                //requires access to private field:
                result.append (field.get (pojo));
            } catch (Exception e) {
                result.append ("Error: ");
                result.append (e.getMessage ());
            }
            result.append (newLine);
        }
        result.append ("}");
        
        return result.toString ();
    }
    
    /**
     * Codifica un string a SHA256.
     * @param base
     *
     * @return
     */
    public static String toSHA256 (String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance ("SHA-256");
            byte[] hash = digest.digest (base.getBytes ("UTF-8"));
            StringBuffer hexString = new StringBuffer ();
            
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString (0xff & hash[i]);
                if (hex.length () == 1) hexString.append ('0');
                hexString.append (hex);
            }
            
            return hexString.toString ();
        } catch (Exception ex) {
            throw new RuntimeException (ex);
        }
    }
    
    /**
     * Convierte un entero a boolean
     * @param value
     *
     * @return
     */
    public static boolean int2boolean (int value) {
        return value != 0;
    }
    
    /**
     * Convierte un boolean a entero
     * @param value
     *
     * @return
     */
    public static int boolean2int (boolean value) {
        return value ? 1 : 0;
    }
    
    /**
     * Obtiene el timestamp actual en segundos.
     * @return
     */
    public static Long getCurrentTimestamp () {
        return System.currentTimeMillis () / 1000;
    }
    
    /**
     * Obtiene el identificardor del dispositivo
     * @param context
     *
     * @return
     */
    public static String getUUID (Context context) {
        String uuid = Settings.Secure.getString (
                context.getContentResolver (),
                Settings.Secure.ANDROID_ID);
        Log.d (TAG, "Device UUID: " + uuid);
        return uuid;
    }
    
    /**
     * Limpia las cookies de un WebView
     * @param context
     */
    @SuppressWarnings("deprecation")
    public static void clearCookies (Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d (TAG, "Using ClearCookies code for API >=" + String.valueOf (Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance ().removeAllCookies (null);
            CookieManager.getInstance ().flush ();
        } else {
            Log.d (TAG, "Using ClearCookies code for API <" + String.valueOf (Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance (context);
            cookieSyncMngr.startSync ();
            CookieManager cookieManager = CookieManager.getInstance ();
            cookieManager.removeAllCookie ();
            cookieManager.removeSessionCookie ();
            cookieSyncMngr.stopSync ();
            cookieSyncMngr.sync ();
        }
    }
    
    /**
     * Trae el directorio de brio de la memoria externa
     * @return
     */
    public static String getBrioPath () {
        return Environment.getExternalStorageDirectory ().getPath () + File.separator + "Brio";
    }
    
    /**
     * Trae el directorio de brio de la memoria interna
     * @param context
     *
     * @return
     */
    public static String getBrioInternalPath (Context context) {
        return context.getFilesDir ().getAbsolutePath () + File.separator;
    }
    
    /**
     * Obtiene el PATH de la BD que se encuentra dentro de la aplicacion
     * @param context
     *
     * @return
     */
    public static String getBrioDBPath (Context context) {
        return context.getDatabasePath (BrioGlobales.DB_NAME).toString ();
    }
    
    
    /**
     * Reiniciar la aplicacion
     * <p>
     * http://stackoverflow.com/questions/15564614/how-to-restart-an-android-application-programmatically
     * <p>
     * Created by Herman Peralta
     */
    public static void restartApp () {
        
        Context contextApp = AppController.getInstance ();
        if (AppController.getCurrentActivity () != null)
            AppController.getCurrentActivity ().finish ();
        Intent start = new Intent (contextApp, SplashScreen.class);
        start.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        contextApp.startActivity (start);
        //        context.getApplicationContext().startActivity(start);
        System.exit (0);
    }
    
    /**
     * Abre el playStore para la aplicacion indicada
     * <p>
     * Created by Herman Peralta
     * @param mainActivity - el activity principal de la app
     * @param appPackageName - el nombre de paquete a buscar en play store
     */
    public static void launchPlayStoreForApp (AppCompatActivity mainActivity, String appPackageName) {
        try {
            mainActivity.startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mainActivity.startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
    
    /**
     * Add write permission to use this
     * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     * Created by Herman Peralta
     * @param dest
     */
    public static void testExtractDataBase (Context ctx, String dbfilename, String dest) {
        final String TAG = "testExtractDataBase";
        
        String sourcedir = ctx.getDatabasePath (dbfilename).getAbsolutePath ();
        
        Log.d (TAG, "testExtractDataBase: source: '" + sourcedir /*+ dbfilename + "'"*/);
        Log.d (TAG, "testExtractDataBase: dest: '" + dest + "'");
        
        File sourcef = new File (sourcedir /*+ dbfilename*/);
        File destf = new File (dest);
        
        try {
            copyFile (sourcef, destf);
        } catch (Exception e) {
            Log.e (TAG, "testExtractDataBase: Couldn't copy file: " + e.getMessage ());
            e.printStackTrace ();
        }
        
        if (destf.exists ()) {
            Log.e (TAG, "testExtractDataBase: [OK] Dest file created: " + dest);
        } else {
            Log.e (TAG, "testExtractDataBase: [ERROR] Dest file NOT created");
        }
        
    }
    
    /**
     * Created by Herman Peralta
     * @param source
     * @param dest
     *
     * @throws IOException
     */
    public static void copyFile (File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream (source).getChannel ();
            destChannel = new FileOutputStream (dest).getChannel ();
            destChannel.transferFrom (sourceChannel, 0, sourceChannel.size ());
        } finally {
            if (sourceChannel != null && sourceChannel.isOpen ()) {
                sourceChannel.close ();
            }
            if (destChannel != null && destChannel.isOpen ()) {
                destChannel.close ();
            }
        }
    }
    
    /**
     * Add write permission to use this
     * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     * <p>
     * Created by Herman Peralta
     */
    public static boolean extractDBFromAssets (Activity activity, String dbFilename) {
        // Get application Directory
        String destdir = activity.getDatabasePath (dbFilename).getParent ();
        if (destdir != null) {
          /*  if (!destdir.endsWith("/")) {
                destdir += "/";
            }
            destdir += "databases/";*/
            
            return extractFileFromAssets (activity, dbFilename, destdir, true);
            
        } else {
            return false;
        }
    }
    
    /**
     * Recordar que el directorio de assets en AndroidStudio esta en app/src/main/assets
     * @param cxt
     */
    public synchronized static boolean extractFileFromAssets (Context cxt, String assetFilename, String destDirectory, boolean overwriteFile) {
        // Open Asset File
        InputStream myInput;
        try {
            myInput = cxt.getAssets ().open (assetFilename);
        } catch (IOException e1) {
            Log.e ("extractFileFromAssets", "No existe el archivo ASSET '" + assetFilename + "'");
            e1.printStackTrace ();
            
            return false;
        }
        
        // Path to the destination file
        String outFileName = destDirectory + (destDirectory.endsWith ("/") ? "" : "/") + assetFilename;
        
        // Crete parent dir if it does not exist
        File dir = new File (destDirectory);
        if (! dir.exists ()) {
            if (! dir.mkdirs ()) {
                Log.e ("extractFileFromAssets", "Error, no se puede generar el directorio de destino especificado");
                
                return false;
            }
        }
        
        // Creating the copy file
        File copiedFile = new File (outFileName);
        try {
            if (! copiedFile.exists ()) {
                copiedFile.createNewFile ();
            } else {
                // El archivo existe, verificar si puedo sobreescribir
                if (overwriteFile) {
                    copiedFile.delete ();
                    copiedFile.createNewFile ();
                } else {
                    Log.e ("extractFileFromAssets", "no se pudo escribir la copia del archivo por que ya existe y se especifico no sobreescribir");
                    
                    return false;
                }
            }
        } catch (IOException e) {
            Log.e ("extractFileFromAssets", "Error al copiar el archivo " + e.getMessage ());
            e.printStackTrace ();
            
            return false;
        }
        
        // Write the copiedFile
        FileOutputStream myOutput = null;
        try {
            myOutput = new FileOutputStream (copiedFile, false);
            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read (buffer)) > 0) {
                myOutput.write (buffer, 0, length);
            }
            
            // Close the streams
            myOutput.flush ();
            myOutput.close ();
            myInput.close ();
        } catch (FileNotFoundException e) {
            Log.e ("extractFileFromAssets", "FileNotFoundException " + e.getMessage ());
            e.printStackTrace ();
            
            return false;
        } catch (IOException e) {
            Log.e ("extractFileFromAssets", "IOException " + e.getMessage ());
            e.printStackTrace ();
            
            return false;
        }
        
        // verificando que la copia exista
        copiedFile = new File (outFileName);
        if (! copiedFile.exists ()) {
            Log.e ("extractFileFromAssets", "Error, el archivo copiado no es accesible");
            return false;
        }
        
        return true;
    }
    
    /**
     * Extrarer las imagenes de granel que se encuentran en assets a una carpeta dentro de
     * la instalacion de la app en la memoria interna del dispositivo.
     * Esto es para que posteriormente el grid de granel del pos las pueda leerlas.
     */
    public static void extractGranelImagesFromAssets (Context context) {
        AssetManager assetManager = context.getAssets ();
        String[] files;
        
        try {
            String destPath = Utils.getBrioInternalPath (context);
            files = assetManager.list ("");
            for (int f = 0; f < files.length; f++) {
                if (files[f].startsWith ("granel_")) {
                    boolean extracted = Utils.extractFileFromAssets (context, files[f], destPath, false);
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
    
    /**
     * Verificar la existencia de una DB
     * @param activity
     * @param dbFilename
     *
     * @return
     */
    private static boolean dbFileExists (Activity activity, String dbFilename) {
        String dbPath = Utils.getAppInstallDir (activity, activity.getPackageName ()) + "databases/" + dbFilename;
        
        return fileExists (dbPath);
    }
    
    /**
     * Verificar la existencia un archivo
     * @param path
     *
     * @return
     */
    public static boolean fileExists (String path) {
        File f = new File (path);
        return f.exists ();
    }
    
    /**
     * Obtener la direccion de una aplicacion instalada en la memoria interna
     * @param context
     * @param packageName
     *
     * @return
     */
    private static String getAppInstallDir (final Context context, final String packageName) {
        try {
            // tambien se puede usar:  "/data/data/" + getApplicationContext().getPackageName()
            String path = context.getPackageManager ().getPackageInfo (packageName, 0).applicationInfo.dataDir;
            if (! path.endsWith ("/")) {
                path += "/";
            }
            
            return path;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e ("Utils", "getAppInstallDir");
            e.printStackTrace ();
            
            return null;
        }
    }
    
    /**
     * Fecha actual en formato dd-MM-yy HH:mm:ss
     * @return
     */
    public static String getBrioDate () {
        DateFormat dateFormat = new SimpleDateFormat ("dd-MM-yy HH:mm:ss");
        Date now = new Date ();
        return dateFormat.format (now);
    }
    
    /**
     * Convierte Timestamp en segundos a formato dd-MM-yy HH:mm:ss
     * @param timestamp in seconds
     *
     * @return
     */
    public static String getBrioDate (long timestamp) {
        DateFormat dateFormat = new SimpleDateFormat ("dd-MM-yy HH:mm:ss");
        Date now = new Date (timestamp * 1000);
        return dateFormat.format (now);
    }
    
    /**
     * Convierte un timestamp a partir de una fecha
     * @param date
     *
     * @return
     */
    public static Long dateToTimeatamp (String date) {
        
        DateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
        try {
            Date time = formatter.parse (date);
            Log.w (TAG, "timestamp=" + time.getTime () / 1000);
            return (time.getTime () / 1000);
        } catch (Exception e) {
            Log.w (TAG, "error al convertir fecha");
            return 0l;
        }
        
    }
    
    /**
     * Comprime en zip un archivo.
     * @param fileName
     * @param dataBytes
     *
     * @return
     * @throws IOException
     */
    public static byte[] zipByteArray (String fileName, byte[] dataBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        ZipOutputStream zos = new ZipOutputStream (baos);
        zos.setMethod (ZipOutputStream.DEFLATED);
        zos.setLevel (Deflater.BEST_COMPRESSION);
        ZipEntry entry = new ZipEntry (fileName);
        entry.setSize (dataBytes.length);
        zos.putNextEntry (entry);
        zos.write (dataBytes);
        zos.closeEntry ();
        zos.close ();
        return baos.toByteArray ();
    }
    
    /**
     * Unzip files to path.
     * @param zipFileName the zip file name
     * @param fileExtractPath the file extract path
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean unzipFilesToPath (final String zipFileName, final String fileExtractPath) {
        boolean success = false;
        FileInputStream fis = null;
        ZipInputStream zis = null;
        try {
            fis = new FileInputStream (zipFileName);
            zis = new ZipInputStream (new BufferedInputStream (fis));
            
            String destPath = fileExtractPath.endsWith ("/") ? fileExtractPath : fileExtractPath + File.separator;
            ZipEntry entry;
            
            while ((entry = zis.getNextEntry ()) != null) {
                int count;
                byte[] data = new byte[1024];
                final FileOutputStream fos = new FileOutputStream (destPath + entry.getName ());
                final BufferedOutputStream dest = new BufferedOutputStream (fos, 1024);
                while ((count = zis.read (data, 0, 1024)) != - 1) {
                    dest.write (data, 0, count);
                }
                dest.flush ();
                dest.close ();
            }
            
            success = true;
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            try {
                if (fis != null) {
                    fis.close ();
                }
                
                if (zis != null) {
                    zis.close ();
                }
            } catch (Exception e) {
            }
        }
        
        return success;
    }
    
    /**
     * Genera una cadena agregando ceros a la izquierda de un numero hasta cumplir la longitud requerida.
     * @param number
     * @param length
     *
     * @return
     */
    public static String zeroFillInteger (int number, int length) {
        return String.format ("%0" + length + "d", number);
    }
    
    
    public static String[] brioToken (Context Contexto, int Comercio) {
        Calendar Hoy = Calendar.getInstance ();
        
        Integer Hora = Hoy.get (Calendar.HOUR_OF_DAY); // Hora de el dia
        Integer Dia = Hoy.get (Calendar.DAY_OF_YEAR); // Dia de el año
        Integer Mes = Hoy.get (Calendar.MONTH); // Mes de el año
        Integer Año = Hoy.get (Calendar.YEAR); // Año actual
        
        String UUID = Utils.getUUID (Contexto);
        // Integer Comercio = ((BrioActivityMain)Contexto).managerSession.readInt("idComercio");
        
        Integer usuario = (Dia * Mes) * Comercio * Hora;
        Integer clave = (Mes + Año) * Comercio * Hora;
        
        String A0, A1, A2, A3, A4, A5;
        
        A0 = RevStr ((MD5 (String.valueOf (usuario) + UUID + Comercio + " - " + String.valueOf (clave))) + (MD5 (Comercio + String.valueOf (clave) + String.valueOf (usuario) + " - " + UUID)));
        A1 = A0.substring (0, 9);
        A0 = A0.replace (A1, "");
        
        A0 = RevStr (A0);
        A2 = A0.substring (0, 9);
        A0 = A0.replace (A2, "");
        
        A0 = RevStr (A0);
        A3 = A0.substring (0, 9);
        A0 = A0.replace (A3, "");
        
        A0 = RevStr (A0);
        A4 = A0.substring (0, 9);
        A0 = A0.replace (A4, "");
        
        A0 = RevStr (A0);
        A5 = A0.substring (0, 9);
        A0 = A0.replace (A5, "");
        
        String Tkn[] = new String[3];
        Tkn[0] = "A" + A1;
        Tkn[1] = "B" + A2;
        Tkn[2] = "C" + A3;
        return Tkn;
    }
    
    
    // Funcion para convertir a Hexadecimal
    public static String EncodeHex (String Cadena) {
        char[] hexArray = "0123456789ABCDEF".toCharArray ();
        byte[] bytes = Cadena.getBytes ();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String (hexChars);
    }
    
    public static String MD5 (String Cadena) {
        //final String MD5 = "MD5";
        try {
            // Crear Hash MD5
            MessageDigest digest = java.security.MessageDigest.getInstance ("MD5");
            digest.update (Cadena.getBytes ());
            byte messageDigest[] = digest.digest ();
            
            // Crear Cadena Hex
            StringBuilder hexString = new StringBuilder ();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString (0xFF & aMessageDigest);
                while (h.length () < 2)
                    h = "0" + h;
                hexString.append (h);
            }
            return hexString.toString ();
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace ();
        }
        return "";
    }
    
    public static String RevStr (String Cadena) {
        return new StringBuilder (Cadena).reverse ().toString ();
    }
    
    /**
     * Valida la contraseña de recuperacion.
     * @return
     */
    public static String brioPass () {
        Date date = new Date ();
        SimpleDateFormat simpleDateFormat0 = new SimpleDateFormat ("dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat ("MM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat ("yy");
        int key0 = Integer.parseInt (simpleDateFormat0.format (date));
        int key1 = Integer.parseInt (simpleDateFormat1.format (date));
        int key2 = Integer.parseInt (simpleDateFormat2.format (date));
        String mu1 = simpleDateFormat2.format (date).substring (0, 1);
        String mu2 = simpleDateFormat2.format (date).substring (1);
        
        String origin = "brio" +
                simpleDateFormat0.format (date) +
                simpleDateFormat1.format (date) +
                simpleDateFormat2.format (date);
        
        String tao = origin.replace ("br", "" + (key0 * Integer.parseInt (mu2)));
        String pi = tao.replace (origin.replaceAll ("[brio]", ""), "" + (key1 * key0));
        Log.w (TAG, pi);
        return pi;
    }
    
    /**
     * Verifica si un email es correcto.
     * @param email
     *
     * @return
     */
    public static boolean validateEmail (String email) {
        
        return validaCorreoSinGuion (email) && android.util.Patterns.EMAIL_ADDRESS.matcher (email).matches ();
    }
    
    /**
     * Reconstruye un pojo superticket a partir de un id de un ticket de la DB.
     * @param context
     * @param ticketid
     *
     * @return
     */
    public static SuperTicket getSuperTicketFromDB (Context context, int ticketid) {
        ModelManager modelManager = ((BrioBaseActivity) context).modelManager;
        SuperTicket sticket = new SuperTicket ();
        
        try {
            sticket.ticket = modelManager.tickets.getByIdTicket (ticketid);
            
            
            if (sticket.ticket == null) {
                return null;
            }
            //Recuperar datos del ticket
            
            
            //Recuperar items
            //sticket.itemsTicket = modelManager.itemsTicket.getByIdTicket(ticketid);
            ArrayList<ItemsTicketController> itemsTicket = new ArrayList<> ();
            List<ItemsTicket> dbItemsTicket = getTickets (context, ticketid);
            
            //            Valida que existan tickets
            if (dbItemsTicket.size () == 0)
                return sticket;
            
            dbItemsTicket = dbItemsTicket == null ? new ArrayList<ItemsTicket> () : dbItemsTicket;
            for (ItemsTicket item : dbItemsTicket) {
                itemsTicket.add (new ItemsTicketController (item));
            }
            sticket.itemsTicket = itemsTicket;
            
            //Recuperar pagos
            sticket.ticketTipoPago = modelManager.ticketTipoPago.getByIdTicket (ticketid);
            sticket.ticketTipoPago = sticket.ticketTipoPago == null ? new ArrayList<TicketTipoPago> () : sticket.ticketTipoPago;
            sticket.montoPagado = sticket.ticket.getImporteNeto () + sticket.ticket.getCambio ();
        } catch (Exception e) {
            // Aqui es por que el ticket no se encontro de forma local, no deberia poder causarse esta excepcion
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + "Utils->getSuperTicketFromDB" + e.getMessage ());
            
            return sticket;
        }
        
        return sticket;
    }
    
    public static List<ItemsTicket> getTickets (Context context, int ticketid) {
        
        ModelManager modelManager = ((BrioBaseActivity) context).modelManager;
        return modelManager.itemsTicket.getByIdTicket (ticketid);
        
    }
    
    
    /**
     * Funcion para obtener la direcicon ip
     * @return
     */
    public static String getIP (Context Contexto) {
        WifiManager wm = (WifiManager) Contexto.getSystemService (Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress (wm.getConnectionInfo ().getIpAddress ());
        return ip;
    }
    
    /**
     * @param context
     *
     * @return // Construye un pojo superticket a partir de datos Json
     */
    public static SuperTicket getSuperTicketFromJS (Context context, JSONObject JSTicket) {
        JSONObject Ticket;
        JSONArray Items;
        
        try {
            Ticket = JSTicket.getJSONArray ("Ticket").getJSONObject (0);
            Items = JSTicket.getJSONArray ("Items");
        } catch (JSONException Ex) {
            Ex.printStackTrace ();
            return null;
        }
        
        ModelManager Modelo = ((BrioActivityMain) context).modelManager;
        SuperTicket sticket = new SuperTicket ();
        
        // Generar los datos de el ticket
        Ticket ticket = new Ticket ();
        try {
            ticket.setIdTicket (Ticket.getInt ("Descripcion"));
            ticket.setImporteBruto (Ticket.getDouble ("ImporteBruto"));
            ticket.setImporteNeto (Ticket.getDouble ("ImporteNeto"));
            ticket.setImpuestos (Ticket.getDouble ("Impuestos")); // Double.parseDouble(Ticket[2]));
            ticket.setDescuento (Ticket.getDouble ("Descuento"));
            ticket.setIdMoneda (Ticket.getInt ("Moneda"));
            ticket.setIdCliente (Ticket.getInt ("Cliente"));
            ticket.setIdComercio (Ticket.getInt ("Comercio"));
            ticket.setIdUsuario (Ticket.getInt ("Usuario"));
            ticket.setIdCaja (Ticket.getInt ("Caja"));
            ticket.setCambio (Ticket.getDouble ("Cambio"));
            ticket.setIdTipoTicket (Ticket.getInt ("TipoTicket"));
            ticket.setDescripcion (Ticket.getString ("Descripcion"));
            ticket.setTimestamp (Ticket.getLong ("Timestamp"));
        } catch (JSONException Ex) {
            String Msj = Ex.getMessage ();
            Ex.printStackTrace ();
        }
        sticket.ticket = ticket;
        
        List<ItemsTicket> itemsTickets = new ArrayList<> ();
        ItemsTicket itemTicket;
        try {
            for (int a = 0; a < Items.length (); a++) {
                itemTicket = new ItemsTicket ();
                JSONObject Item = Items.getJSONObject (a);
                
                itemTicket.setIdItemTicket (Item.getInt ("IdTicket"));
                itemTicket.setIdTicket (Item.getInt ("IdTicket"));
                itemTicket.setIdArticulo (Item.getInt ("IdArticulo"));
                itemTicket.setCodigoBarras (Item.getString ("CodigoBarras"));
                itemTicket.setDescripcion (Item.getString ("Descripcion"));
                itemTicket.setCantidad (Item.getDouble ("Cantidad"));
                itemTicket.setImporteUnitario (Item.getDouble ("ImporteUnitario"));
                itemTicket.setImporteTotal (Item.getDouble ("ImporteTotal"));
                itemTicket.setTimestamp (Item.getLong ("Timestamp"));
                //precio compra add
                itemTicket.setPrecioCompra (Item.getDouble ("PrecioCompra"));
                
                sticket.itemsTicket.add (new ItemsTicketController (itemTicket));
            }
        } catch (JSONException Ex) {
            String Msj = Ex.getMessage ();
            Ex.printStackTrace ();
        }
        
        
        //Recuperar pagos
        sticket.ticketTipoPago = Modelo.ticketTipoPago.getByIdTicket (- 1);
        sticket.ticketTipoPago = sticket.ticketTipoPago == null ? new ArrayList<TicketTipoPago> () : sticket.ticketTipoPago;
        sticket.montoPagado = sticket.ticket.getImporteNeto () + sticket.ticket.getCambio ();
        
        return sticket;
        
    }
    
    /**
     * Trunca un double.
     * @param number
     *
     * @return
     */
    public static double getDoubleRounded (Double number) {
        Double temp = number;
        temp = Math.floor (number * 100);
        temp = temp / 100;
        return temp;
    }
    
    /**
     * Obtiene informacion de la aplicacion
     * @param context
     *
     * @return
     */
    public static PackageInfo getAppData (Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager ().getPackageInfo (context.getPackageName (), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e (TAG, "getAppData: " + e.getMessage ());
        }
        
        return packageInfo;
    }
    
    /**
     * Obtiene el total de la memoria RAM del dispositivo en MB.
     * @param activity
     *
     * @return
     */
    @SuppressLint("NewApi")
    private static long getTotalMemory (Context activity) {
        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo ();
            ActivityManager activityManager = (ActivityManager) activity.getSystemService (Context.ACTIVITY_SERVICE);
            activityManager.getMemoryInfo (mi);
            long availableMegs = mi.totalMem / 1048576L; // in megabyte (mb)
            
            return availableMegs;
        } catch (Exception e) {
            e.printStackTrace ();
            return 0;
        }
    }
    
    /**
     * Obtiene la memoria RAM disponible del dispositivo en MB.
     * @param activity
     *
     * @return
     */
    private static long getFreeMemory (Context activity) {
        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo ();
            ActivityManager activityManager = (ActivityManager) activity.getSystemService (Context.ACTIVITY_SERVICE);
            activityManager.getMemoryInfo (mi);
            long availableMegs = mi.availMem / 1048576L; // in megabyte (mb)
            
            return availableMegs;
        } catch (Exception e) {
            e.printStackTrace ();
            return 0;
        }
    }
    
    public static InfoPojo getInfoPojo (Context Contexto, SessionManager Sesion) {
        InfoPojo informacion = new InfoPojo ();
        SystemInfoPojo sistema = getSystemInfo (Contexto);
        BrioAppInfoPojo aplicacion = getBrioAppInfo (Contexto, Sesion);
        
        
        // Información del sistema operativo android
        informacion.setBrioDate (sistema.getBrioDate ());
        informacion.setAndroidSDK (sistema.getAndroidSDK ());
        informacion.setAndroidCodename (sistema.getAndroidCodename ());
        informacion.setAndroidDevice (sistema.getAndroidDevice ());
        informacion.setAndroidModel (sistema.getAndroidModel ());
        informacion.setAndroidProduct ("Brio");
        informacion.setAndroidUUID (sistema.getAndroidUUID ());
        informacion.setAndroidLanguaje (sistema.getAndroidLanguaje ());
        informacion.setAndroidCodPais (sistema.getAndroidCodPais ());
        informacion.setAndroidCores (sistema.getAndroidCores ());
        informacion.setAndroidMemoryTotal (sistema.getAndroidMemoryTotal ());
        informacion.setAndroidMemoryFree (sistema.getAndroidMemoryFree ());
        informacion.setAndroidMemoryUsed (sistema.getAndroidMemoryUsed ());
        informacion.setDireccionIP (Utils.getIP (Contexto));
        
        // Informacion del aplicativo Brio
        informacion.setPackageName (aplicacion.getPackageName ());
        informacion.setVersionName (aplicacion.getVersionName ());
        informacion.setVersionCode (aplicacion.getVersionCode ());
        informacion.setLastUpdateTime (aplicacion.getLastUpdateTime ());
        informacion.setApplicationInfo (aplicacion.getApplicationInfo ().toString ());
        informacion.setIdComercio (Sesion.readInt ("idComercio"));
        informacion.setNombreComercio (Sesion.readString ("descripcionComercio"));
        informacion.setUsuario (Sesion.readString ("usuario"));
        informacion.setClave (Sesion.readString ("clave"));
        informacion.setAcceso (Sesion.readString ("acceso"));
        informacion.setGPS (Sesion.readString ("DATA_GPS"));
        
        return informacion;
    }
    
    public static void HabilitarGPS (Context Contexto) {
        String provider = Settings.Secure.getString (Contexto.getContentResolver (), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        
        if (! provider.contains ("gps")) {
            final Intent poke = new Intent ();
            poke.setClassName ("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory (Intent.CATEGORY_ALTERNATIVE);
            poke.setData (Uri.parse ("3"));
            Contexto.sendBroadcast (poke);
        }
    }
    
    
    /**
     * Obtiene informacion del dispositivo.
     * @param context contexto de la aplicacion
     *
     * @return regresa el pojo ya lleo con los datos del sistema
     */
    //http://stackoverflow.com/questions/3213205/how-to-detect-system-information-like-os-or-device-type
    //http://stackoverflow.com/questions/2298208/how-do-i-discover-memory-usage-of-my-application-in-android
    public static SystemInfoPojo getSystemInfo (Context context) {
        long DEVICE_TOTAL_MEMORY = getTotalMemory (context);
        long DEVICE_FREE_MEMORY = getFreeMemory (context);
        long DEVICE_USED_MEMORY = DEVICE_TOTAL_MEMORY - DEVICE_FREE_MEMORY;
        
        SystemInfoPojo systemInfoPojo = new SystemInfoPojo ();
        
        systemInfoPojo.setBrioDate (Utils.getBrioDate ());
        systemInfoPojo.setAndroidSDK (android.os.Build.VERSION.SDK_INT);
        systemInfoPojo.setAndroidCodename (Build.VERSION.CODENAME);
        systemInfoPojo.setAndroidDevice (Build.DEVICE);
        systemInfoPojo.setAndroidModel (Build.MODEL);
        systemInfoPojo.setAndroidProduct (Build.PRODUCT);
        systemInfoPojo.setAndroidUUID (Utils.getUUID (context));
        systemInfoPojo.setAndroidLanguaje (Locale.getDefault ().getDisplayLanguage ());
        systemInfoPojo.setAndroidCodPais (context.getResources ().getConfiguration ().locale.getCountry ());
        systemInfoPojo.setAndroidCores (Runtime.getRuntime ().availableProcessors ());
        systemInfoPojo.setAndroidMemoryTotal (DEVICE_TOTAL_MEMORY);
        systemInfoPojo.setAndroidMemoryFree (DEVICE_FREE_MEMORY);
        systemInfoPojo.setAndroidMemoryUsed (DEVICE_USED_MEMORY);
        
        return systemInfoPojo;
    }
    
    /**
     * Obtiene informacion de la aplicacion.
     * @param context contexto de la aplicacion
     * @param managerSession se llama al sessionManager
     *
     * @return retorna el pojo ya lleno con la informacion de la aplicacion
     */
    public static BrioAppInfoPojo getBrioAppInfo (Context context, SessionManager managerSession) {
        BrioAppInfoPojo brioAppInfoPojo = new BrioAppInfoPojo ();
        
        PackageInfo appInfo = Utils.getAppData (context);
        brioAppInfoPojo.setPackageName (appInfo.packageName);
        brioAppInfoPojo.setVersionName (appInfo.versionName);
        brioAppInfoPojo.setVersionCode (appInfo.versionCode);
        brioAppInfoPojo.setLastUpdateTime (appInfo.lastUpdateTime);
        brioAppInfoPojo.setApplicationInfo (appInfo.applicationInfo.toString ());
        brioAppInfoPojo.setIdComercio (managerSession.readInt ("idComercio"));
        brioAppInfoPojo.setNombreComercio (managerSession.readString ("descripcionComercio"));
        brioAppInfoPojo.setUsuario (managerSession.readString ("usuario"));
        
        return brioAppInfoPojo;
    }
    
    /**
     * Genera un reporte de error.
     * @param context contexto de la aplicacion
     * @param e hilo donde se ejecuta
     * @param managerSession manda a llamar el  sessionManager para obtener datos
     *
     * @return regresa el pojo con el error que se genero en la aplicacion
     */
    public static BrioErrorReportPojo getBrioErrorReport (Context context, Throwable e, SessionManager managerSession) {
        BrioErrorReportPojo brioErrorReportPojo = new BrioErrorReportPojo ();
        
        StackTraceElement[] arr = e.getStackTrace ();
        
        brioErrorReportPojo.setError (e.toString ());
        String stackTrace = "";
        for (int i = 0; i < arr.length; i++) {
            stackTrace += arr[i].toString () + "\n";
        }
        brioErrorReportPojo.setStackTrace (stackTrace);
        
        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        brioErrorReportPojo.setCause ("");
        brioErrorReportPojo.setCauseTrace ("");
        Throwable cause = e.getCause ();
        if (cause != null) {
            brioErrorReportPojo.setCause (cause.toString ());
            arr = cause.getStackTrace ();
            String causeTrace = "";
            for (int i = 0; i < arr.length; i++) {
                causeTrace += arr[i].toString () + "\n";
            }
            brioErrorReportPojo.setCauseTrace (causeTrace);
        }
        
        brioErrorReportPojo.setBrioAppInfo (getBrioAppInfo (context, managerSession));
        brioErrorReportPojo.setSystemInfo (Utils.getSystemInfo (context));
        
        return brioErrorReportPojo;
    }
    
    /**
     * Borra un archivo
     * @param filePath
     * @param fileName
     */
    public static void deleteFile (String filePath, String fileName) {
        File outputFile = new File (filePath, fileName);
        if (outputFile.exists ()) {
            outputFile.delete ();
        }
    }
    
    /**
     * Verifica si la aplicacion tiene permiso para accesar a la memoria.
     * http://stackoverflow.com/questions/33162152/storage-permission-error-in-marshmallow
     * @param context
     *
     * @return
     */
    public static boolean isStoragePermissionGranted (Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission (android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v (TAG, "Permission is granted");
                return true;
            } else {
                
                Log.v (TAG, "Permission is revoked");
                ((AppCompatActivity) context).requestPermissions (new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v (TAG, "Permission is granted");
            return true;
        }
    }
    
    
    private static long mLastClickTime = 0;
    
    /**
     * Evita problemas de doble click en los views de toda la aplicacion al establecer un tiempo
     * minimo entre clicks consecutivos.
     * @return
     */
    public static boolean shouldPerformClick () {
        // mis-clicking prevention, using threshold
        if (SystemClock.elapsedRealtime () - mLastClickTime < 500) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime ();
        return true;
    }
    
    /**
     * Obtiene el tamaño en pixeles de la pantalla del dispositivo.
     * @param activity
     *
     * @return
     */
    public static int[] getDisplaySize (AppCompatActivity activity) {
        DisplayMetrics metrics = new DisplayMetrics ();
        activity.getWindowManager ().getDefaultDisplay ().getMetrics (metrics);
        
        int sizes[] = new int[2];
        sizes[0] = metrics.widthPixels;
        sizes[1] = metrics.heightPixels;
        
        return sizes;
    }
    
    public static String toJSON (Object object) {
        Gson gson = new Gson ();
        String json;
        try {
            json = gson.toJson (object);
        } catch (Exception e) {
            json = null;
        }
        return json;
    }
    
    /**
     * Rellena un pojo a partir de una cadena en json
     * @param json
     * @param clazz
     *
     * @return
     */
    public static Object fromJSON (String json, Class clazz) {
        Gson gson = new Gson ();
        Object object = gson.fromJson (json, clazz);
        
        return object;
    }
    
    
    public static String getNombreTicket (Context context, int idTipoTicket) {
        int tituloTicket = - 1;
        switch (idTipoTicket) {
            
            case PaymentService.TIPO_TICKET_POS:
                tituloTicket = R.string.payment_tipo_ticket_pos;
                break;
            
            case PaymentService.TIPO_TICKET_SERVICIO:
                tituloTicket = R.string.payment_tipo_ticket_servicio;
                break;
            
            case PaymentService.TIPO_TICKET_TAE:
                tituloTicket = R.string.payment_tipo_ticket_tae;
                break;
            
            case PaymentService.TIPO_TICKET_INTERNET:
                tituloTicket = R.string.payment_tipo_ticket_internet;
                break;
            
            case PaymentService.TIPO_TICKET_BANCO_ENTRADA:
                tituloTicket = R.string.payment_tipo_ticket_banco_entrada;
                break;
            
            case PaymentService.TIPO_TICKET_BANCO_SALIDA:
                tituloTicket = R.string.payment_tipo_ticket_banco_salida;
                break;
            
            case PaymentService.TIPO_PAGO_SEGURO:
                tituloTicket = R.string.payment_tipo_ticket_seguro;
                break;
            
            case PaymentService.TIPO_TICKET_WESTERN_PAGO:
                tituloTicket = R.string.payment_tipo_ticket_western_pago;
                break;
        }
        
        String ret = "";
        if (tituloTicket != - 1) {
            ret = context.getResources ().getString (tituloTicket);
        }
        
        return ret;
    }
    
    /**
     * Obteber el nombre del tipo de pago indicado
     * @param context
     * @param idTipoPago
     *
     * @return
     */
    public static String getNombrePago (Context context, int idTipoPago) {
        int pagoNombre = - 1;
        switch (idTipoPago) {
            case PaymentService.TIPO_PAGO_EFECTIVO:
                pagoNombre = R.string.ticket_name_efectivo;
                break;
            
            case PaymentService.TIPO_PAGO_TARJETA:
                pagoNombre = R.string.ticket_name_tarjeta;
                break;
            
            case PaymentService.TIPO_PAGO_FIADO:
                pagoNombre = R.string.ticket_name_fiado;
                break;
            
            case PaymentService.TIPO_PAGO_VALES:
                pagoNombre = R.string.ticket_name_vales;
                break;
        }
        
        String ret = "";
        if (pagoNombre != - 1) {
            ret = context.getResources ().getString (pagoNombre);
        }
        
        return ret;
    }
    
    /**
     * Fragmentae un texto en fragmentos de fragmentSize caracteres
     * @param text - el texto a fragmentar
     * @param fragmentSize - el numero de caracteres por fragmento
     *
     * @return una lista con los fragmentos
     */
    public static ArrayList<String> fragmentString (String text, int fragmentSize) {
        ArrayList<String> res = new ArrayList<> ();
        int index = 0;
        while (text.length () > index) {
            
            int length = index + fragmentSize;
            length = length > text.length () ? text.length () : length;
            
            res.add (text.substring (index, length));
            index += fragmentSize;
        }
        return res;
    }
    
    /**
     * Leer un archivo interno de la aplicacion
     * @param context
     * @param filename
     *
     * @return
     */
    public static String readInternalFile (Context context, String filename) {
        StringBuilder trace = new StringBuilder ();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader (new InputStreamReader (context.openFileInput (filename)));
            String line;
            while ((line = reader.readLine ()) != null) {
                trace.append (line);
                trace.append ("\n");
            }
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            try {
                if (reader != null) {
                    reader.close ();
                }
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
        
        return trace.toString ();
    }
    
    /**
     * Convertir un objeto a su cadena JSON
     * @param object
     *
     * @return
     */
    public static String toJSONString (Object object) {
        Gson gson = new Gson ();
        String json = gson.toJson (object);
        
        return json;
    }
    
    /**
     * Escapar texto (remover backslash y escapar comillas)
     * @param text
     *
     * @return
     */
    public static String escapeText (String text) {
        return text
                .replace ("\\", "\\\\")
                .replace ("\"", "\\\"");
    }
    
    /**
     * Valida que el correo no inicie con alguno de los caracteres dentro del arreglo
     * @param email
     *
     * @return
     */
    public static boolean validaCorreoSinGuion (String email) {
        
        try {
            String[] chars = new String[] { "-", "*", "/", "\\", ",", ".", "@" };
            for (int i = 0; i < chars.length; i++) {
                if (String.valueOf (email.trim ().charAt (0)).equals (chars[i])) {
                    return false;
                }
            }
        } catch (Exception e) {
            BrioGlobales.ArchivoLog.escribirLineaFichero (BrioUtilsFechas.obtenerFechaDiaHoraFormat () + "Utils->validaCorreoSinGuion" + e.getMessage ());
            return false;
        }
        return true;
    }
    
    
    /**
     * Metodo encargado de eliminar el archivo apk que se descargo al realizar la actualización de
     * versión y su carpeta contenedora
     */
    public static void deleteDileApk () {
        
        try {
            String pathApk = Utils.getBrioPath () + File.separator + BrioGlobales.DIR_APK;
            deleteDir (new File (pathApk));
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    
    /**
     * Método recursivo, encargado de eliminar carpetas y los archivos dentro de la carpeta
     * @param file
     */
    public static void deleteDir (File file) {
        
        if (file.isDirectory ()) {
            
            //directory is empty, then delete it
            if (file.list ().length == 0) {
                file.delete ();
            } else {
                //list all the directory contents
                String files[] = file.list ();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File (file, temp);
                    //recursive delete
                    deleteDir (fileDelete);
                }
                
                //check the directory again, if empty then delete it
                if (file.list ().length == 0) {
                    file.delete ();
                }
            }
        } else {
            //if file, then delete it
            file.delete ();
            System.out.println ("File is deleted : " + file.getAbsolutePath ());
        }
    }
    
    
    /**
     * Método que obtiene la versión instalada desde sqlite
     * @param Modelo
     *
     * @return
     */
    public static String[] getVersionActual (ModelManager Modelo) {
        String Version = "";
        net.hova_it.barared.brio.apis.models.entities.Settings stMayor = Modelo.settings.getByNombre (BrioGlobales.KEY_BRIO_VER_MAYOR);
        net.hova_it.barared.brio.apis.models.entities.Settings stMenor = Modelo.settings.getByNombre (BrioGlobales.KEY_BRIO_VER_MENOR);
        net.hova_it.barared.brio.apis.models.entities.Settings stError = Modelo.settings.getByNombre (BrioGlobales.KEY_BRIO_VER_ERROR);
        net.hova_it.barared.brio.apis.models.entities.Settings stActua = Modelo.settings.getByNombre (BrioGlobales.KEY_BRIO_ACTUALIZA_VER);
        
        if (stActua == null) {
            stActua = new net.hova_it.barared.brio.apis.models.entities.Settings ("Actua", "0");
            Modelo.settings.save (stActua);
        }
        
        String vMayor, vMenor, vError;
        
        // Si no se ha establecido la version, poner la version: Base v 0.1.47
        if (stMayor == null || stMenor == null || stError == null) {
            stMayor = new net.hova_it.barared.brio.apis.models.entities.Settings (BrioGlobales.KEY_BRIO_VER_MAYOR, "0");
            stMenor = new net.hova_it.barared.brio.apis.models.entities.Settings (BrioGlobales.KEY_BRIO_VER_MENOR, "1");
            stError = new net.hova_it.barared.brio.apis.models.entities.Settings (BrioGlobales.KEY_BRIO_VER_ERROR, "49");
            
            // Predeterminadamente guardar version 0.1.49 (Version Base)
            Modelo.settings.save (stMayor);
            Modelo.settings.save (stMenor);
            Modelo.settings.save (stError);
        }
        
        vMayor = stMayor.getValor ();
        vMenor = stMenor.getValor ();
        vError = stError.getValor ();
        
        Version = vMayor + "-" + vMenor + "-" + vError;
        String versionInstalada = vMayor + "." + vMenor + "." + vError;
        
        return new String[] { Version, stActua.getValor (), versionInstalada };
        
    }
    
    public static String getStrFmtDateHour () {
        String result;
        
        DateTimeItem date = DateTimeItem.getNow ();
        result = date.getFmtStrDateHourBD ();
        
        
        return result;
    }
    
    public static String generarIdRandom () {
        return Preferencias.i.getIdComercio () + UUID.randomUUID ().toString ();
    }
    
    public static ArrayList<DLSortMenu> getSortMiBrio () {
        ArrayList<DLSortMenu> list = new ArrayList<> ();
        ModelManager modelManager = AppController.getInstance ().getModelManager ();
        net.hova_it.barared.brio.apis.models.entities.Settings strSort = modelManager.settings.getByNombre (BrioGlobales.KEY_MI_BRIO_ORDERNAMIENTO);
        if (strSort != null) {
            String[] sort = strSort.getValor ().split ("\\|");
            
            for (String s : sort) {
                String[] item = s.split (";");
                list.add (new DLSortMenu (item[0], Integer.parseInt (item[1])));
            }
            Collections.sort (list);
            
        } else {
            
            //            Mostramos por defecto el menú
            list.add (new DLSortMenu (TagMiBrioEnum.UNIVERSIDAD.toString (), 0));
            list.add (new DLSortMenu (TagMiBrioEnum.NOTICIAS.toString (), 0));
            list.add (new DLSortMenu (TagMiBrioEnum.MAIL.toString (), 0));
            list.add (new DLSortMenu (TagMiBrioEnum.CONTRATO.toString (), 0));
            list.add (new DLSortMenu (TagMiBrioEnum.GARANTIA.toString (), 0));
            list.add (new DLSortMenu (TagMiBrioEnum.HORARIOS.toString (), 0));
            list.add (new DLSortMenu (TagMiBrioEnum.POLITICAS.toString (), 0));
            list.add (new DLSortMenu (TagMiBrioEnum.ASOCIADOS.toString (), 0));
            list.add (new DLSortMenu (TagMiBrioEnum.ATENCION.toString (), 0));
            list.add (new DLSortMenu (TagMiBrioEnum.RECOMPENSAS.toString (), 0));
        }
        return list;
    }
    
    
    public static String toNoPalatals(String original) {
        original = original.replace("Ú", "U");
        original = original.replace("Ó", "O");
        original = original.replace("Í", "I");
        original = original.replace("É", "E");
        original = original.replace("Á", "A");
        
        original = original.replace("ú", "u");
        original = original.replace("ó", "o");
        original = original.replace("í", "i");
        original = original.replace("é", "e");
        original = original.replace("á", "a");
        
        return original;
    }
}
