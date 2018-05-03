package net.hova_it.barared.brio.apis.mail;

import android.content.Context;

import net.hova_it.barared.brio.apis.utils.Utils;

import java.security.Security;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import lat.brio.core.BrioGlobales;

/**
 * Permite obtener un Session para determinado servidor de correo.
 *
 * Created by Herman Peralta on 01/06/2016.
 */
public class MailSessionBuilder extends javax.mail.Authenticator {

    private String
            user,
            password;

    static {
        Security.addProvider(new JSSEProvider());
    }

    private MailSessionBuilder(String user, String password) {
        this.user = user;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    /**
     * Genera una sesión para el servidor smtp de Barared.
     *
     * @param context
     * @param user - el correo electrónico del emisor
     * @param password - contraseña del correo.
     * @return objeto javax.mail.Session con los datos de sesión para el servidor smtp de Barared.
     */
    public static Session getBararedSession(Context context, String user, String password) {


        String host = BrioGlobales.TICKET_MAIL_HOST;
        String port = BrioGlobales.TICKET_MAIL_PORT;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
    
        Session session = Session.getInstance(props, new  Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

//        return Session.getInstance(props, new MailSessionBuilder(user, password));
        return session;
    }
    
    class MyPasswordAuthenticator extends Authenticator
    {
        String user;
        String pw;
        
        public MyPasswordAuthenticator (String username, String password)
        {
            super();
            this.user = username;
            this.pw = password;
        }
        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(user, pw);
        }
    }
    
    /**
     * Genera una sesión para el servidor smtp de Gmail.
     *
     * @param user - el correo electrónico del emisor
     * @param password - contraseña del correo.
     * @return objeto javax.mail.Session con los datos de sesión para el servidor smtp de Gmail.
     */
    public static Session getGmailSession(String user, String password) {
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", host);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //GMail requiere esto para poder loguearse a una cuenta gmail
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        return Session.getInstance(props, new MailSessionBuilder(user, password));
    }
}
