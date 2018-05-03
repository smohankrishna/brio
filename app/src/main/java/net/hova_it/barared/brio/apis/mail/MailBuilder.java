package net.hova_it.barared.brio.apis.mail;

import android.content.Context;
import android.os.AsyncTask;

import net.hova_it.barared.brio.apis.network.NetworkStatus;
import net.hova_it.barared.brio.apis.utils.DebugLog;

import javax.activation.*;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;

/**
 * Esta clase permite construir un correo mediante
 * una estructura de MIMEMessage y MIMEBodyparts.
 *
 * Created by Herman Peralta on 01/06/2016.
 */
public class MailBuilder {
    
    private MimeMultipart mainMultipart;
    private MimeMessage mail;
    private Session session;
    
    private String sender, recipients, subject, replyTo;
    private Context context;
    
    public MailBuilder(Session session, String sender, String recipients, String subject, String replyTo) throws MessagingException {
        this.session = session;
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.replyTo = replyTo;
        
        mainMultipart = new MimeMultipart("related"); //related
    }
    
    /**
     * Agregar HTML al correo.
     *
     * @param html
     *
     * @return
     *
     * @throws MessagingException
     */
    public MailBuilder attachHTML(String html) throws MessagingException {
        MimeBodyPart htmlBodyPart = new MimeBodyPart();
        
        htmlBodyPart.setContent(html, "text/html; charset=utf-8");
        mainMultipart.addBodyPart(htmlBodyPart);
        
        return this;
    }
    
    /**
     * Crea un multipart con una imagen, la cual se agrega en un elemento html <img src="cid:imageId">
     *
     * @param cidImage - El id de la imagen, el cual debe estar en el elemento img del html como <img src="cid:imageId">
     * @param imageFilename - La ruta local de la imagen
     *
     * @throws Exception
     */
    public MailBuilder attachImage(String cidImage, String imageFilename) {
        MimeBodyPart imageBodyPart = new MimeBodyPart();
        
        DataSource source = new FileDataSource(imageFilename);
    
        try {
            imageBodyPart.setDataHandler(new DataHandler(source));
            imageBodyPart.setFileName(imageFilename);
            imageBodyPart.setHeader("Content-ID", "<" + cidImage + ">");
    
            mainMultipart.addBodyPart(imageBodyPart);
        } catch (MessagingException e) {
            e.printStackTrace ();
            BrioGlobales.writeLog ("MailBuilder","attachImage",e.getMessage () +" - \n"+e.getCause ());
        }

        
        return this;
    }
    
    /**
     * Envia el correo actual.
     *
     * @param taskId
     * @param listener
     * @param context
     *
     * @throws MessagingException
     */
    public void send(int taskId, MailListener listener, Context context) throws MessagingException {
        //Transport.send(mail);
        
        this.context = context;
        
        SendEmailAsyncTask task = new SendEmailAsyncTask(taskId, listener);
        task.execute();
    }
    
    
    
    /**
     * La clase Transport de javamail requiere que el envío de correos se haga en segundo plano,
     * por esa razón cada correo se envía en un AsyncTask.
     */
    private class SendEmailAsyncTask extends AsyncTask<String, Void, Boolean> implements TransportListener {
        
        private MailListener listener;
        private int taskId;
        
        private SendEmailAsyncTask(int taskId, MailListener listener) {
            this.taskId = taskId;
            this.listener = listener;
        }
        
        @Override
        protected void onPreExecute() {
            listener.onMailStartSending(taskId);
        }
        
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                if (! NetworkStatus.hasInternetAccess(context))
                    return false;
                mail = new MimeMessage(session);
                mail.setSender(new InternetAddress(sender));
                mail.setSubject(subject, "utf-8");
                
                mail.addHeader("From", sender);
                mail.addHeader("Reply-To", replyTo);
                mail.addHeader("Content-Type", "text/html; charset=utf-8"); //image/png;
                mail.addHeader("MIME-Version", "1.0");
                
                if (recipients.indexOf(',') > 0) {
                    mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
                }
                else {
                    mail.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
                }
                
                mail.setContent(mainMultipart);
                
                //todo: poner listener a transport
                //Transport.send(mail);
                Transport transport = session.getTransport("smtp");
                //transport.addTransportListener(this);
                
                transport.connect();
                transport.sendMessage(mail, mail.getAllRecipients());
                transport.close();
                
                return true;
            } catch (Exception e1) {
                BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + "MailBuilder->doInBackground" + e1.getMessage());
                e1.printStackTrace();
                
                return false;
            }
        }
        
        @Override
        protected void onPostExecute(Boolean sent) {
            DebugLog.log(MailBuilder.this.getClass(), "BRIO_MAIL", "onPostExecute sent: " + sent);
            
            listener.onMailEndSending(taskId, sent);
        }
        
        @Override
        public void messageDelivered(TransportEvent transportEvent) {
            DebugLog.log(MailBuilder.this.getClass(), "BRIO_MAIL", "messageDelivered");
            
            listener.onMailEndSending(taskId, true);
        }
        
        @Override
        public void messageNotDelivered(TransportEvent transportEvent) {
            DebugLog.log(MailBuilder.this.getClass(), "BRIO_MAIL", "messageNotDelivered");
            
            listener.onMailEndSending(taskId, false);
        }
        
        @Override
        public void messagePartiallyDelivered(TransportEvent transportEvent) {
            DebugLog.log(MailBuilder.this.getClass(), "BRIO_MAIL", "messagePartiallyDelivered");
            
            listener.onMailEndSending(taskId, false);
        }
    }
    
    public interface MailListener {
        void onMailStartSending (int taskId);
        
        void onMailEndSending (int taskId, boolean sent);
    }
}
