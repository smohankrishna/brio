package net.hova_it.barared.brio.apis.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.CachedValue;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAcceptDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.utils.preferencias.Preferencias;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.BreakIterator;

import brio.sdk.logger.log.BrioLog;
import lat.brio.core.BrioGlobales;

/**
 * Descarga el APK de la aplicación desde el servidor FTP de Barared.
 * Si la versión del APK descargado es superior a la de la aplicación instalada,
 * procede a enviar un intent para instalar la nueva APK.
 * <p>
 * Created by guillermo.ortiz on 14/02/18.
 */

public class DescargaApkTask extends AsyncTask<Boolean, String, Integer> {


    Context context;
    ModelManager Modelo;
    private BrioBaseActivity.Splash splash;
    public BrioLog log;
    boolean success = false;
    boolean isLastVersion = false;
    String Version = "";

    public DescargaApkTask(Context context, BrioBaseActivity.Splash splash) {
        this.context = context;
        this.Modelo = new ModelManager(context);
        this.splash = splash;
        log = new BrioLog(BrioGlobales.ArchivoLog);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        splash.show();
        splash.publish(Utils.getString(R.string.setup_update_init, context));
    }


    @Override
    protected Integer doInBackground(Boolean... isRollback) {

        int resp = 0;
        String[] versionActual = Utils.getVersionActual(Modelo);
        boolean install = false;
        boolean asRollback = false;
        Settings strRollback;
        Settings strBeta;
        strRollback = Modelo.settings.getByNombre(BrioGlobales.KEY_BRIO_VER_ROLLBACK);
        strBeta = Modelo.settings.getByNombre(BrioGlobales.KEY_BRIO_VER_ISBETA);

        if (isRollback[0]) {
            Version = Preferencias.i.getBrioVerPrevName();
            install = true;
        } else {
            Version = versionActual[0];

            //valida si se ha hecho un rollback
            asRollback = strRollback != null && strRollback.getValor().equals(BrioGlobales.KEY_ROLLBACK_TRUE);
            int codeVersion=Integer.parseInt(Utils.getAppData(context).versionName.split("\\.")[2]);
            int codeVersionBD=Integer.parseInt(versionActual[2].split("\\.")[2]);
            if (((codeVersionBD>=codeVersion)&&(  Preferencias.i.getBrioVerExistUpdate())&&Preferencias.i.getBrioVerIsBeta()!=BrioGlobales.VERSION_BETA)||
                    (codeVersionBD>=codeVersion)&&(Preferencias.i.getBrioVerExistUpdate())&&Preferencias.i.getBrioVerIsBeta()==BrioGlobales.VERSION_BETA) {
                install = true;
            }
        }

        if (install) {
            success = startDownloadApk(Version);
            resp = BrioGlobales.UPDATE_OK_INSTALL;
            //Si la instalacion no fue por rollback, se actualiza el campo
            AgregarAjuste(BrioGlobales.KEY_BRIO_VER_ROLLBACK, isRollback[0] ?BrioGlobales.KEY_ROLLBACK_TRUE : BrioGlobales.KEY_ROLLBACK_FALSE );


            /*Creamos un string para validar si se realizo la actualizacion de la app de forma correcta
            cuando se inicie la aplicacion de nuevo y se reinicien las banderas
            */
            strRollback = Modelo.settings.getByNombre(BrioGlobales.KEY_BRIO_VER_ROLLBACK);
            PackageInfo appData = Utils.getAppData(context);
            String roll = (strRollback == null) ? BrioGlobales.KEY_ROLLBACK_FALSE  : strRollback.getValor();
            String update = (Preferencias.i.getBrioVerExistUpdate()) ? BrioGlobales.KEY_ROLLBACK_FALSE : BrioGlobales.KEY_ROLLBACK_TRUE;

            String keyIstall=appData.versionCode + appData.versionName + roll + update;
            Preferencias.i.setBrioVerInstalled(keyIstall);


        } else {
            isLastVersion = true;//Dice que ya cuenta con la última versión
        }
        return resp;
    }

    private void AgregarAjuste(String Nombre, String Valor) {
        Settings ajuste = Modelo.settings.getByNombre(Nombre);

        if (ajuste == null) {
            ajuste = new Settings();
            ajuste.setNombre(Nombre);
            ajuste.setValor(Valor);
            Modelo.settings.save(ajuste);
        } else {
            Modelo.settings.update(Nombre,Valor);
        }
    }

    @Override
    protected void onPostExecute(Integer s) {
        super.onPostExecute(s);
        splash.dismiss();
        String title;
        String msj;

        title = context.getResources().getString(R.string.setup_update_title);
        DialogListener listener = null;
        if (!isLastVersion) {
            if (success) {

                msj = context.getResources().getString(R.string.setup_update_ok_install);
                listener = new DialogListener() {
                    @Override
                    public void onAccept() {
                        installIntent();
                    }

                    @Override
                    public void onCancel() {
                    }
                };
            } else {

                msj = context.getResources().getString(R.string.setup_update_msj);
                listener = null;
            }
        } else {
            msj = context.getResources().getString(R.string.setup_update_ok_uptodate);
            listener = new DialogListener() {
                @Override
                public void onAccept() {
                    Long timestampThisSync = System.currentTimeMillis() / 1000;
                    Modelo.settings.update("LAST_UPDATE", timestampThisSync.toString());
                    ((BrioActivityMain) context).managerSession.saveLong("LAST_UPDATE", timestampThisSync);
                }

                @Override
                public void onCancel() {

                }
            };
        }

        showSimpleAlert(title, msj, listener);

    }


    private boolean startDownloadApk(String version) {

        boolean success = false;
        String appPath = Utils.getBrioPath() + File.separator + BrioGlobales.DIR_APK + File.separator;
        String appName = BrioGlobales.NAME_APK;
        String appVersion = version;


        // TODO Connect by TLS
        String protocol = "TLS";
        FTPSClient ftp;

        try {
            ftp = new FTPSClient(protocol, false); // TLS/SSL, Explicit
            ftp.setAuthValue(protocol);
            ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
            ftp.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());

            //FIXME preguntar estos valores a Raudys
            ftp.setConnectTimeout(30000); //espera 10 segundos para conectarse a ftp
            ftp.setControlKeepAliveTimeout(300); //espera 3 segundos despues del ultimo keep alive mientras descargas, si no muere

            ftp.connect(BrioGlobales.FTP_SERVER, BrioGlobales.FTP_SERVER_PUERTO);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return false;
            }

            ftp.execPBSZ(0);
            ftp.execPROT("P");

            if (!ftp.login(BrioGlobales.FTP_SERVER_USUARIO, BrioGlobales.FTP_SERVER_PASS)) {
                ftp.logout();
                return false;
            }

            ftp.setBufferSize(1024);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            ftp.changeWorkingDirectory(BrioGlobales.FTP_SERVER_FOLDER_APK);

            File filePath = new File(appPath);
            filePath.mkdirs();
            // "app-release-0-1-47.apk"
            appName = appName.replace("-N-", "-" + appVersion);

            // Existe la nueva versión, bajarla
            File outputFile = new File(filePath, appName);
            // Log.d(TAG, "UpdateTask (outputFile): " + outputFile.getPath());

            if (outputFile.exists()) {
                outputFile.delete();
            }

            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            success = ftp.retrieveFile(appName, outputStream); //Descargando archivo
            outputStream.close();
            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }
            return success;
        } catch (IOException e) {
            log.error(getClass().getSimpleName(), "startDownloadApk->221", e.getMessage());
            e.printStackTrace();
        }
        return success;
    }

    /**
     * Solicita al usuario la autorizacion para instalar la nueva version de la apk.
     */
    private void installIntent() {
        try {

            String PATH = Utils.getBrioPath() + File.separator+ BrioGlobales.DIR_APK + File.separator;
            String APK = BrioGlobales.NAME_APK;
            File file;
            file = new File(PATH, APK.replace("-N-", "-" + Version));
            String mimeType = "application/vnd.android.package-archive";
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setDataAndType(Uri.fromFile(file), mimeType);

            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(install);
        } catch (Exception e) {
            log.error(getClass().getSimpleName(), "installIntent-", e.getMessage());
            e.printStackTrace();

        }
    }

    public void showSimpleAlert(String title, String msj, DialogListener listener) {
        BrioAcceptDialog bad = new BrioAcceptDialog((BrioActivityMain) context, title, msj, context.getResources().getString(R.string.brio_aceptar), listener);
        bad.show();
    }
}
