package net.hova_it.barared.brio.apis.sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.entities.SuperSyncData;
import net.hova_it.barared.brio.apis.models.entities.SyncData;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.sqlite.SQLiteInit;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sqlite.templates.SyncDataTemplate;
import net.hova_it.barared.brio.apis.sync.entities.FTPData;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;
import lat.brio.core.bussines.BrCommerce;

/**
 * Sube los archivos de sincronización y el backup de la base de datos
 * al servidor ftp de Barared.
 *
 * El proceso de subida es asíncrono, por lo que esta clase hereda de AsyncTask
 *
 * @see Synchronizable
 *
 * Created by Alejandro Gomez on 16/03/2016.
 */
public class SyncUploadTask extends AsyncTask<FTPData, Integer, Boolean> {
    private final String TAG = "SyncUploadTask";
    
    private SessionManager sessionManager;
    
    private Context context;
    private String server;
    private int port;
    private String user;
    private String pass;
    
    //private StringBuilder syncData;
    
    private byte[] dbFile;
    
    private String error;
    
    private int lastSequence, idComercio, idCaja, currentSequence;
    
    private String idComercioS, idCajaS;
    
    private boolean sychronizable;
    
    private SyncUploadTaskListener listener;
    // public Boolean SubirFTP = false;
    private SuperSyncData superSyncData;
    
    public final static String TRACE_FILE = "ftpsync.log";
    
    private File filePath = null;
    private File targetFile = null;
    private BrCommerce brCommerce;
    
    public SyncUploadTask(Context context,  SyncUploadTaskListener listener) {
        this.context = context;
        this.sessionManager = SessionManager.getInstance(context);
        this.listener = listener;

        brCommerce=BrCommerce.getInstance(context,BrioGlobales.getAccess());
        
        this.server = BrioGlobales.FTP_SERVER;
        this.port = BrioGlobales.FTP_SERVER_PUERTO;
        this.user = BrioGlobales.FTP_SERVER_USUARIO;
        this.pass = BrioGlobales.FTP_SERVER_PASS;
    }
    
 
    
    /**
     * Realiza la inicialización de los datos a enviar al ftp
     */
    private boolean pre() {
        long executionTime;
        
        // Values from SETTINGS
        this.lastSequence = Integer.valueOf(brCommerce.getByNombre("LAST_SEQUENCE").getValor());
        this.idComercio = Integer.valueOf(brCommerce.getByNombre("ID_COMERCIO").getValor());
        this.idCaja = Integer.valueOf(brCommerce.getByNombre("ID_CAJA").getValor());
        
        this.idComercioS = Utils.zeroFillInteger(this.idComercio, 6);
        this.idCajaS = Utils.zeroFillInteger(this.idCaja, 3);
        
        /////////////////////////////////////////////////////////////
        
        fillSuperSyncData();
        
        // CLONE LOCAL DB
        executionTime = System.currentTimeMillis();
        File dbLocal = new File(Utils.getBrioDBPath(context));
        this.dbFile = new byte[(int) dbLocal.length()];
        try {
            // bd local a byte array
            FileInputStream fileInputStream = new FileInputStream(dbLocal);
            fileInputStream.read(this.dbFile);
            
            // ** Save Locally
            String dbExternalPath = Utils.getBrioPath() + File.separator + BrioGlobales.DIR_FILE_DB + File.separator;
            String dbName = BrioGlobales.DB_NAME;
            
            //verificar que existan directorios
            filePath = new File(dbExternalPath);
            filePath.mkdirs();
            
            //Copiando (vaciar buffer a targetFile)
            targetFile = new File(dbExternalPath + dbName);
            
            if (targetFile.exists()) {
                targetFile.delete();
            }
            
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(dbFile);
            outStream.close();
            
            if (targetFile.exists()) {
                DebugLog.log(getClass(), "SYNC", "ARCHIVO CREADO EN " + dbExternalPath + dbName);
            }
            else {
                DebugLog.log(getClass(), "SYNC", "ARCHIVO NO CREADO :(");
            }
            
            fileInputStream.close();
        } catch (Exception e) {
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "SyncUploadTask->pre->168" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    // Esta funcion determina si hay registros pendientes por sincronizar
    public Boolean hayPendientes() {
        Boolean hay = false;
        SQLiteDatabase db = BrioGlobales.getAccess ();
        Cursor syncId = db.rawQuery("SELECT COUNT(*) FROM SYNC_DATA", null);
        if (syncId.moveToFirst()) {
            if (syncId.getInt(0) > 0) {
                hay = true;
            }
        }
        syncId.close();
        return hay;
    }
    
    // Funcion para eliminar registros ya sincronizados
    public boolean limpiarAnteriores(long Secuencia) {
        SQLiteService sqlLiteService = SQLiteService.getInstance(context);
        SyncDataTemplate syncDataTemplate = new SyncDataTemplate(sqlLiteService);
        
        // Se eliminan registros que sean igual o mayores a Secuenca
        syncDataTemplate.delete(String.valueOf(Secuencia));
        return false;
    }
    
    /**
     * Obtiene el currentSequence de sincronización y
     * además obtiene los datos a sincronizar (si los hay)
     */
    public void fillSuperSyncData() {
        try {
            // GET CURRENT SEQUENCE
            SQLiteDatabase db = BrioGlobales.getAccess ();
            Cursor syncId = db.rawQuery("SELECT MAX(id_sequence) AS sync_id FROM (SELECT id_sequence FROM Sync_Data LIMIT 500)", null);
            if (syncId.moveToFirst()) {
                this.currentSequence = syncId.getInt(0);
            }
            else {
                this.currentSequence = 0;
            }
            syncId.close();
            
            this.lastSequence = 0;
            
            if (currentSequence > lastSequence) {
                // FILL SYNC DATA
                this.sychronizable = true;
                long executionTime = System.currentTimeMillis();
                Cursor resource = db.rawQuery("SELECT id_sequence, id_comercio, uuid, sync_table, sync_operation, sync_data, timestamp FROM Sync_Data WHERE id_sequence >= " + this.lastSequence + " LIMIT 500", null);
                // Log.w("BRIO_SYNC", "Se enviaran transacciones desde " + this.lastSequence + " hasta " + (this.lastSequence + 500));
                
                // Omitir la siguiente linea por que marca una excepcion en el descriptor de archivo
                
                superSyncData = new SuperSyncData();
                superSyncData.setIdComercio(this.idComercio);
                superSyncData.setIdCaja(sessionManager.readInt("idCaja"));
                superSyncData.setLastSequence(this.lastSequence);
                
                while (resource.moveToNext()) {
                    SyncData tx = new SyncData();
                    tx.setIdSequence(resource.getInt(0));
                    tx.setIdComercio(resource.getInt(1));
                    tx.setUuid(resource.getString(2));
                    tx.setTable(resource.getString(3));
                    tx.setOperation(resource.getString(4));
                    tx.setData(resource.getString(5));
                    tx.setTimestamp(resource.getLong(6));
                    
                    superSyncData.getTransacciones().add(tx);
                }
                resource.close();
                Log.d(TAG, "fillSuperSyncData Fill Data Time (ms): " + (System.currentTimeMillis() - executionTime));
            }
            else {
                superSyncData = null;
                this.sychronizable = false;
            }

        } catch (Exception e) {
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "SyncUploadTask->fillSuperSyncData->259" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public int getCurrentSequence() {
        return currentSequence;
    }
    
    public int getLastSequence() {
        return lastSequence;
    }
    
    /**
     * Sube los datos de sincronización y de backup al FTP
     *
     * @param params
     *
     * @return True si se subió correctamente, false en caso contrario
     */
    @Override
    protected Boolean doInBackground(FTPData... params)  {
        
        long executionTime;
        error = "";
        
        
        // No subir el archivo si es autosincronizacion
        if (params[0].getFilename().equals("Auto")) {
            return true;
        }
        boolean successFTPUpload = false;
        
        try {
            //No hacer nada si falla algo en la inicialización
            if (! pre()) {
                superSyncData = null;
                return false;
            }
            
            // TODO Connect by TLS
            String protocol = "TLS";
            FTPSClient ftp;
            
            
            ftp = new FTPSClient(protocol, false); // TLS/SSL, Explicit
            ftp.setAuthValue(protocol);
            ftp.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
            
            //FIXME preguntar estos valores a Raudys
            
            // Limite
            ftp.setConnectTimeout(30000); // Espera 30 Segundos la conexión
            ftp.setControlKeepAliveTimeout(300); // Máximo 5 minutos
            // espera 3 segundos despues del ultimo keep alive mientras subes, si no muere
            
            ftp.connect(server, port);
            int reply = ftp.getReplyCode();
            if (! FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                error = "FTP server refused connection.";
                
                return false;
            }
            
            
            ftp.execPBSZ(0);
            ftp.execPROT("P");
            
            if (! ftp.login(user, pass)) {
                ftp.logout();
                error = "FTP server refused login.";
                
                return false;
            }
            
            
            ftp.setBufferSize(1048576);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            
            Log.i(TAG, "Logged to FTP(Status):" + ftp.getStatus());
            
            String pathDB =BrioGlobales.DB_NAME;
            String pathDBZIP =BrioGlobales.DB_NAME.split("\\.")[0] + "-" + this.idComercioS + "-" + this.idCajaS + ".zip";
            
            ////////////////////////////////////////////////////////////////////////////////////////
            // *** BACKUP
            ftp.changeWorkingDirectory("/back");
            ftp.execPBSZ(0);
            ftp.execPROT("P");
            
            executionTime = System.currentTimeMillis();
            InputStream dbStream = new ByteArrayInputStream(Utils.zipByteArray(pathDB, this.dbFile));
            successFTPUpload = ftp.storeFile(pathDBZIP, dbStream);
            dbStream.close();
            
            error += ! successFTPUpload ? "Error Uploading db.zip" : "";
            
            ftp.disconnect();
            
            
        } catch (IOException e) {
            error = e.getMessage();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "SyncUploadTask->doInBackground->366" + e.getMessage());
            e.printStackTrace();
        } finally {
            
            
            if (targetFile != null && targetFile.exists()) {
                boolean delete = targetFile.delete();
                if (delete && filePath != null && filePath.exists()) {
                    filePath.delete();
                }
            }
        }
        
        return successFTPUpload;
    }
    
    @Override
    protected void onPostExecute(Boolean successFTPUpload) {
         try {
             /**
              * SyncManager.java:66: public void onSyncUploadTask(boolean success){...}
              * Verifica si hace sincronización con el servicio REST
              */
             listener.onSyncUploadTask(successFTPUpload);
        } catch (Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "SyncUploadTask->onPostExecute->389" + e.getMessage());
        }
        
        
    }
    
    public SuperSyncData getSuperSyncData() {
        return superSyncData;
    }
    
    public interface SyncUploadTaskListener {
        void onSyncUploadTask(boolean success);
    }
}