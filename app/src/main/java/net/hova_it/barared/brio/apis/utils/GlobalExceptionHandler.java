package net.hova_it.barared.brio.apis.utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import net.hova_it.barared.brio.apis.mail.MailBuilder;
import net.hova_it.barared.brio.apis.mail.MailService;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.info.BrioErrorReportPojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import lat.brio.core.BrioGlobales;

/**
 * Manejador de excepciones globales.
 * Esta clase permite cachar cualquier excepción dentro de la aplicación (que no esté contemplada en un bloque try-catch).
 * Guarda la traza en un archivo de texto, y al proximo reinicio de la aplicación, envía dicha traza por correo electrónico.
 *
 * http://stackoverflow.com/questions/3643395/how-to-get-android-crash-logs
 *
 * Created by Herman Peralta on 22/04/2016.
 */
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler, MailBuilder.MailListener {

    public final static String TRACE_FILE = "stack.trace";

    private Thread.UncaughtExceptionHandler defaultUEH;

    private AppCompatActivity app = null;
    private SessionManager managerSession;

    private String supportMail;

    public GlobalExceptionHandler(AppCompatActivity app) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;

        managerSession = SessionManager.getInstance(app);

        supportMail = BrioGlobales.MAIL_SUPPORT;

        sendError();
    }

    /**
     * Escribe en una archivo el log del error en la aplicacion
     */
    public void uncaughtException(Thread thread, Throwable e) {

        BrioErrorReportPojo report = Utils.getBrioErrorReport(app, e, managerSession);

        try {
            FileOutputStream trace = app.openFileOutput(TRACE_FILE, Context.MODE_APPEND);
            trace.write(Utils.toJSON(report).getBytes());
            trace.close();

            System.out.println("#### Error no controlado detectado:\n" + report + "\n######");
        } catch(IOException ioe) {

        }

        defaultUEH.uncaughtException(thread, e);
    }

    /**
     * Envia la traza de error contenida en el archivo de texto por correo.
     */
    public void sendError() {
        MailService serviceMail = MailService.getInstance(app);

        String trace = "";
        BufferedReader reader = null;
        try {
            if(new File(TRACE_FILE).exists()) {

                reader = new BufferedReader(new InputStreamReader(app.openFileInput(TRACE_FILE)));
                String line;
                while ((line = reader.readLine()) != null) {
                    trace += line + "\n";
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(trace.length() == 0) { return; }
        DebugLog.log(getClass(), "BRIO_INSTANCE", "Ultimo error no controlado: ");
        System.out.println("#################\n" + trace + "\n#################");

        SessionManager managerSession = SessionManager.getInstance(app);

        serviceMail.sendErrorReportMail(
            String.format("Error en Brio Android [%d, %d]", managerSession.readInt("idComercio"), Utils.getAppData(app).versionCode),
            trace, this);
    }

    @Override
    public void onMailStartSending(int mailId) {

    }

    @Override
    public void onMailEndSending(int mailId, boolean sent) {
        if(sent) {

            app.deleteFile(TRACE_FILE);
        }
    }
}
