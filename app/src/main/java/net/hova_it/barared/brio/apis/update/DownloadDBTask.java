package net.hova_it.barared.brio.apis.update;

import android.content.Context;
import android.os.AsyncTask;

import net.hova_it.barared.brio.apis.utils.Utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;

/**
 * Esta clase se utiliza al recuperar un comercio en la aplicación.
 *
 * Descarga la base de datos sqlite, asociada al comercio, del servidor FTP de Barared.
 *
 * Created by Alejandro Gomez on 14/12/2015.
 */
public class DownloadDBTask extends AsyncTask<String, Integer, Boolean> {
    private final String TAG = "BRIO_DWLDDB";
    
    private DownloadDBTaskListener downloadDBTaskListener;
    
    private Context context;
    private String server;
    private String user;
    private String pass;
    private int port;
    
    
    public DownloadDBTask(Context context, DownloadDBTaskListener downloadDBTaskListener) {
        this.context = context;
        this.server = BrioGlobales.FTP_SERVER;
        this.port = BrioGlobales.FTP_SERVER_PUERTO;
        this.user = BrioGlobales.FTP_SERVER_USUARIO;
        this.pass = BrioGlobales.FTP_SERVER_PASS;
        
        this.downloadDBTaskListener = downloadDBTaskListener;
    }
    
    
    /**
     * Descarga la base de datos del servidor FTP de Barared asociada al comercio indicado
     *
     * @param params
     *
     * @return
     */
    @Override
    protected Boolean doInBackground(String... params) {
        
        boolean success = false;
        String dbDownloadPath = Utils.getBrioPath() + File.separator + BrioGlobales.DIR_FILE_DB + File.separator;//ruta descarga
        String dbName = params[0];
        
        String protocol = "TLS";
        FTPSClient ftp;
        
        try {
            
            ftp = new FTPSClient(protocol, false); // TLS/SSL, Explicit
            ftp.setAuthValue(protocol);
            ftp.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
            
            //FIXME preguntar estos valores a Raudys
            ftp.setConnectTimeout(30000); //espera 10 segundos para conectarse a ftp
            ftp.setControlKeepAliveTimeout(10); //espera 3 segundos despues del ultimo keep alive mientras descargas, si no muere
            
            ftp.connect(server, port);
            
            int reply = ftp.getReplyCode();
            if (! FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                
                return false;
            }
            
            ftp.execPBSZ(0);
            ftp.execPROT("P");
            
            if (! ftp.login(user, pass)) {
                ftp.logout();
                return false;
            }
            
            ftp.setBufferSize(1024 * 1024);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            ftp.changeWorkingDirectory("/back");
            
            
            File filePath = new File(dbDownloadPath);
            filePath.mkdirs();
            File outputFile = new File(filePath, dbName);
            
            
            if (outputFile.exists()) {
                outputFile.delete();
            }
            
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            
            success = ftp.retrieveFile(dbName, outputStream);
            
            outputStream.close();
            
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "DownloadDBTask->doInBackground->127" + ex.getMessage());
                
            }
            
            return success;
            
        } catch (Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "DownloadDBTask->doInBackground->135" + e.getMessage());
            
        }
        
        
        return success;
        
    }
    
    @Override
    protected void onPostExecute(Boolean success) {
        
        try {
            downloadDBTaskListener.onDownloadFinish(success);
            
        } catch (Exception e) {
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "DownloadDBTask->onPostExecute->160" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public interface DownloadDBTaskListener {
        void onDownloadFinish(Boolean success);
    }
}
