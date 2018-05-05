package net.hova_it.barared.brio.apis.mail;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.apis.hw.billpocket.BillPocketService;
import net.hova_it.barared.brio.apis.mail.MailBuilder.MailListener;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.ItemsTicket;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.FileLogger;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAcceptDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.utils.info.BrioAppInfoPojo;
import net.hova_it.barared.brio.apis.utils.info.SystemInfoPojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.mail.Session;

import net.hova_it.barared.brio.apis.utils.GlobalExceptionHandler;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;

/**
 * Permite enviar correos electrónicos.
 * <p>
 * http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a
 * <p>
 * Created by Herman Peralta on 18/04/2016.
 */
public class MailService {
    private String sender;
    private String password;
    private String no_reply;
    public String supportMail;

    private final static String CID_IMG_TICKET_LOGO_ID = "ticketlogo";
    private final static String IMG_TICKET_LOGO = "<img width=\"256\" height=\"100\" title=\"\" alt=\"logo tienda\" src=\"cid:" + CID_IMG_TICKET_LOGO_ID + "\">";

    private static MailService daManager;

    private BuzonSalidaTicketsService buzonSalidaTicketsService;

    private Context context;
    private ModelManager managerModel;

    private Session brioSession;

    private static final String TAG = "MailService";

    public static MailService getInstance(Context context) {
        if (daManager == null) {
            daManager = new MailService(context);
        }

        return daManager;
    }

    private MailService(Context context) {
        this.context = context;

        managerModel = new ModelManager(context);

        recoverConfig();

        brioSession = MailSessionBuilder.getBararedSession(context, sender, password);

        buzonSalidaTicketsService = new BuzonSalidaTicketsService();
    }

    /**
     * Enviar un ticket por correo.
     *
     * @param taskId       - un identificador para el task de envío
     * @param ticketId     - el id del ticket a enviar
     * @param mail         - el correo de destino
     * @param mailListener
     */
    public void sendTicket(int taskId, int ticketId, String mail, MailListener mailListener) {
        SuperTicket superTicket = Utils.getSuperTicketFromDB(context, ticketId);
        sendTicket(taskId, superTicket, mail, mailListener);
    }

    /**
     * Enviar un ticket por correo.
     *
     * @param taskId       - un identificador para el task de envío.
     * @param sTicket      - El pojo del correo a enviar.
     * @param recipent     - el correo de destino.
     * @param mailListener
     */
    public void sendTicket(int taskId, final SuperTicket sTicket, String recipent, MailListener mailListener) {
        Settings tmp = managerModel.settings.getByNombre("DESCRIPCION_COMERCIO");

        String subject = "Ticket " + tmp.getValor();
        sendTicket(taskId, subject, sTicket, recipent, mailListener);
    }

    /**
     * Enviar un ticket por correo.
     *
     * @param taskId       - un identificador para el task de envío.
     * @param subject      - el asunto del correo
     * @param superTicket  - el pojo del ticket a enviar
     * @param recipent     - el correo de destino
     * @param mailListener
     */
    private void sendTicket(int taskId, String subject, final SuperTicket superTicket, final String recipent, final MailListener mailListener) {
        DebugLog.log(getClass(), "BRIO_MAIL", "ENVIANDO TICKET");

        MailListener superListener = new MailListener() {
            @Override
            public void onMailStartSending(int taskId) {
                mailListener.onMailStartSending(taskId);
            }

            @Override
            public void onMailEndSending(int taskId, boolean sent) {
                if (!sent) {
                    buzonSalidaTicketsService.encolar(superTicket.getTicket().getIdTicket() + ";" + recipent);
                }

                mailListener.onMailEndSending(taskId, sent);
            }
        };

        try {
            String htmlTicket = HTMLTicketPatcher.superTicketToHTML(superTicket, context);

            htmlTicket = htmlTicket.substring(0, htmlTicket.indexOf("<!--logoini-->")) + IMG_TICKET_LOGO + htmlTicket.substring(htmlTicket.indexOf("<!--logofin-->"), htmlTicket.length());

            new MailBuilder(brioSession, sender, recipent, subject, no_reply)
                    .attachHTML(htmlTicket)
                    .attachImage(CID_IMG_TICKET_LOGO_ID, Utils.getBrioInternalPath(context) + "ticket_logo.png")
                    .send(taskId, superListener, context);

        } catch (Exception e) {
            e.printStackTrace();

            DebugLog.log(getClass(), "BRIO_MAIL", "ERROR ENVIANDO MAIL " + e.getMessage());
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "MailService->sendTicket->L152" + e.getMessage());
            superListener.onMailEndSending(taskId, false);
        }
    }

    /**
     * Enviar un correo con la información solicitada.
     *
     * @param managerSession
     * @param mailListener
     */
    public void sendLogMail(SessionManager managerSession, MailListener mailListener) {
        int taskId = -1;
        BrioAppInfoPojo brioAppInfoPojo = Utils.getBrioAppInfo(context, managerSession);
        SystemInfoPojo systemInfoPojo = Utils.getSystemInfo(context);

        String subject = String.format("BrioInfo [%d, %d]", brioAppInfoPojo.getIdComercio(), brioAppInfoPojo.getVersionCode());
        String body = Utils.toJSON(brioAppInfoPojo) + "<br/><br>" + Utils.toJSON(systemInfoPojo);
        MailBuilder mailBuilder;
        try {
            mailBuilder = new MailBuilder(brioSession,
                    sender,
                    supportMail,
                    subject,
                    supportMail);

            mailBuilder
                    .attachHTML("<p>" + body + "</p>")
                    .send(taskId, mailListener, context);

        } catch (Exception e) {
            mailListener.onMailEndSending(taskId, false);
        }
    }

    public void sendBillPocketTxMail() {
        //Enviar correo con los logs para billpocket
        String bpTxList = FileLogger.get(context, BrioGlobales.FL_KEY_BILLPOCKET).readAsJSONString();
        Log.i("bpTxList", bpTxList);
        if (bpTxList.length() > 2) {
            BrioAppInfoPojo brioAppInfoPojo = Utils.getBrioAppInfo(context, SessionManager.getInstance(context));

            sendPlainMail(
                    String.format("BrioAndroid [%d, %d] BillPocket TxLog", brioAppInfoPojo.getIdComercio(), brioAppInfoPojo.getVersionCode()),
                    bpTxList, new MailBuilder.MailListener() {
                        @Override
                        public void onMailStartSending(int taskId) {
                            Log.i("bpTxList", "Enviando correo");
                        }

                        @Override
                        public void onMailEndSending(int taskId, boolean sent) {
                            Log.i("bpTxList", "enviado? " + sent);

                            if (sent) {
                                FileLogger.get(context, BrioGlobales.FL_KEY_BILLPOCKET).delete();
                            }
                        }
                    });
        }
    }

    public void sendPlainMail(String subject, String body, MailListener mailListener) {
        MailBuilder mailBuilder;
        try {
            mailBuilder = new MailBuilder(brioSession,
                    sender,
                    supportMail,
                    subject,
                    supportMail);

            mailBuilder
                    .attachHTML("<p>" + body + "</p>")
                    .send(0, mailListener, context);

        } catch (Exception e) {
            mailListener.onMailEndSending(0, false);
        }
    }

    /**
     * Enviar correo con un reporte de error no controlado.
     *
     * @param subject      - el asunto del correo.
     * @param trace        - la traza de error a enviar por correo.
     * @param mailListener
     * @see GlobalExceptionHandler
     */
    public void sendErrorReportMail(String subject, String trace, MailListener mailListener) {
        int taskId = -1;

        MailBuilder mailBuilder;
        try {
            mailBuilder = new MailBuilder(brioSession,
                    sender,
                    supportMail,
                    subject,
                    supportMail);

            mailBuilder
                    .attachHTML("<p>" + trace + "</p>")
                    .send(taskId, mailListener, context);

        } catch (Exception e) {
            mailListener.onMailEndSending(taskId, false);
        }
    }

    /**
     * Obtener los datos de emisor de correo y correo de soporte
     */
    private void recoverConfig() {
        sender = BrioGlobales.TICKET_MAIL_SENDER;
        password = BrioGlobales.TICKET_MAIL_PASS;
        no_reply = BrioGlobales.TICKET_MAIL_NOREPPLY;
        supportMail = BrioGlobales.MAIL_SUPPORT;
    }

    public BuzonSalidaTicketsService getBuzonSalidaTicketsService() {
        return buzonSalidaTicketsService;
    }

    /**
     * Clase que permite encolar los correos de ticket cuando al momento de
     * la venta no logran enviarse.
     */
    public class BuzonSalidaTicketsService implements MailListener {
        public final static String SETTING_BUZON_SALIDA = "BUZON_SALIDA";
        private ModelManager managerModel;
        private ArrayList<String> mails;
        private boolean[] mailStatus;
        private BuzonSalidaTicketsListener buzonSalidaTicketsListener;

        private BuzonSalidaTicketsService() {

            managerModel = new ModelManager(context);

            Settings buzonSalidaSetting = managerModel.settings.getByNombre(SETTING_BUZON_SALIDA);
            if (buzonSalidaSetting == null) {
                buzonSalidaSetting = new Settings();
                buzonSalidaSetting.setNombre(SETTING_BUZON_SALIDA);
                buzonSalidaSetting.setValor("");

                managerModel.settings.save(buzonSalidaSetting);
            }
        }

        /**
         * Enviar los correos de tickets pendientes.
         *
         * @param showEmptyWarning           - indica si se debe mostrar un dialog para indicar que no hay correos pendientes.
         * @param buzonSalidaTicketsListener
         */
        public void enviarPendientes(boolean showEmptyWarning, BuzonSalidaTicketsListener buzonSalidaTicketsListener) {
            this.buzonSalidaTicketsListener = buzonSalidaTicketsListener;

            mails = parseQueue();

            if (mails != null && mails.size() > 0) {

                ((BrioActivityMain) context).splash.show();
                ((BrioActivityMain) context).splash.publish("Enviando " + mails.size() + " tickets pendientes...");

                mailStatus = new boolean[mails.size()];

                send(0, mails.get(0));
            } else {
                if (showEmptyWarning) {
                    BrioAcceptDialog bcd = new BrioAcceptDialog(
                            (AppCompatActivity) context,
                            "Tickets pendientes",
                            "No hay tickets pendientes por enviar",
                            "Aceptar", null);

                    bcd.show();
                }

                this.buzonSalidaTicketsListener.onBuzonSalidaTicketsFinished();
            }
        }

        /**
         * Encolar un correo de un ticket.
         *
         * @param mail - string con sintaxis "id-ticket;correo-destino"
         */
        public void encolar(String mail) {
            String queue = readQueue();
            queue += mail + "|";

            DebugLog.log(getClass(), "BRIO_MAIL_BUZON", "Encolando: '" + mail + "', cola: '" + queue + "'");

            managerModel.settings.update(SETTING_BUZON_SALIDA, queue);
        }

        /**
         * Enviar uno de los tickets pendientes.
         *
         * @param taskId
         * @param mail
         */
        private void send(int taskId, String mail) {
            DebugLog.log(getClass(), "BRIO_MAIL_BUZON", "send[ '" + mail + "' ] index:" + taskId);

            TicketMail ticketMail = parseMail(mail);
            sendTicket(taskId, ticketMail.idTicket, ticketMail.to, this);
        }

        /**
         * Leer la cola de correos pendientes.
         *
         * @return
         */
        private String readQueue() {
            Settings buzonSalida = managerModel.settings.getByNombre(SETTING_BUZON_SALIDA);
            String cola = buzonSalida.getValor();

            DebugLog.log(getClass(), "BRIO_MAIL_BUZON", "cola: '" + cola + "'");

            return cola;
        }

        /**
         * Actualizar la cola de correos pendientes con los que no se pudieron enviar
         * Valida que el ticket  exista y que el correo sea valido, que no comience con algún
         * caracter especial.
         */
        private void updateQueue() {

            try {

                StringBuilder queue = new StringBuilder("");
                for (int i = 0; i < mails.size(); i++) {
                    if (existeTicket(mails.get(i)) && Utils.validateEmail(parseMail(mails.get(i)).to)) {
                        boolean sent = mailStatus[i];
                        if (!sent) {
                            queue.append(mails.get(i));
                            queue.append("|");
                        }
                    }
                }


                managerModel.settings.update(SETTING_BUZON_SALIDA, queue.toString());
            } catch (Exception e) {
                BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "MailService->updateQueue->420" + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Leer la cola de correos de ticket pendientes.
         * Valida que el ticket  exista y que el correo sea valido, que no comience con algún
         * caracter especial.
         *
         * @return
         */
        private ArrayList<String> parseQueue() {
            ArrayList<String> queue = new ArrayList<String>();
            String cola = readQueue();

            if (cola != null && cola.length() > 0) {
                String mails[] = cola.split(Pattern.quote("|"));
                for (String mail : mails) {
                    if (existeTicket(mail) && Utils.validateEmail(parseMail(mail).to))
                        queue.add(mail);
                }
            }

            return queue;
        }

        /**
         * Valida que el ticket que se enviará realmente exista en la base de datos.
         *
         * @param mail
         * @return true/false
         */
        private boolean existeTicket(String mail) {
            boolean resp = false;

            try {
                TicketMail ticketMail = parseMail(mail);
                List<ItemsTicket> ticketList = Utils.getTickets(context, ticketMail.idTicket);
                if (ticketList.size() > 0)
                    resp = true;
            } catch (Exception e) {
                BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "MailService->ExisteTicket->L461" + e.getMessage());
                e.printStackTrace();
                return resp;
            }
            return resp;
        }

        /**
         * Parsear una cadena en sintaxis "id-ticket;correo-destino" a un pojo TicketMail.
         *
         * @param mail - el string a parsear
         * @return
         */
        private TicketMail parseMail(String mail) {
            String[] data = mail.replace("|", "").split(";");
            return new TicketMail(Integer.parseInt(data[0]), data[1]);
        }

        @Override
        public void onMailStartSending(int taskId) {

        }

        @Override
        public void onMailEndSending(int taskId, boolean sent) {

            try {
                mailStatus[taskId] = sent;
                taskId++;

                if (mails.size() > taskId) {
                    send(taskId, mails.get(taskId));
                } else {
                    // guardar los pendientes en la base de datos
                    updateQueue();

                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((BrioActivityMain) context).splash.dismiss();
                        }
                    });

                    int trueCount = 0;
                    int falseCount = 0;
                    for (int i = 0; i < mailStatus.length; i++) {
                        if (mailStatus[i]) {
                            trueCount++;
                        } else {
                            falseCount++;
                        }
                    }

//                Mensaje que indica el número de tickets enviados y no enviados
                    String ticketsEnviados = String.format(new Locale("ES", "MX"), "Tickets enviados: %s.", trueCount);
                    String ticketsNoEnviados = String.format(new Locale("ES", "MX"), "Tickets sin enviar: %s.", falseCount);
                    StringBuilder msj = new StringBuilder("");

                    if (trueCount > 0) {
                        msj.append(ticketsEnviados);
                    }
                    if (trueCount > 0 && falseCount > 0) {
                        msj.append(". ");
                    }
                    if (falseCount > 0) {
                        msj.append(ticketsNoEnviados);
                    }

                    Toast.makeText(context, msj, Toast.LENGTH_LONG).show();

                    buzonSalidaTicketsListener.onBuzonSalidaTicketsFinished();

                    //working = false;
                }

            } catch (Exception e) {

                BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + TAG + "->onMailEndSending" + e.getMessage());
            }
        }

        class TicketMail {
            int idTicket;
            String to;

            public TicketMail(int idTicket, String to) {
                this.idTicket = idTicket;
                this.to = to;
            }
        }
    }

    public interface BuzonSalidaTicketsListener {
        void onBuzonSalidaTicketsFinished ();
    }
}

