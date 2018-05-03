package net.hova_it.barared.brio.apis.utils.dialogs;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.adapters.CustomerEmailAdapter;
import net.hova_it.barared.brio.apis.mail.MailBuilder;
import net.hova_it.barared.brio.apis.mail.MailService;
import net.hova_it.barared.brio.apis.models.DLCorreosClientes;
import net.hova_it.barared.brio.apis.models.DLTicketsEnviadosClientes;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.views.BrioButton2;
import net.hova_it.barared.brio.apis.views.BrioCloseButton;

import java.util.ArrayList;

import lat.brio.core.BrioGlobales;
import lat.brio.core.bussines.BrCustomer;

/**
 * Created by Herman Peralta on 23/06/2016.
 */
public class MailDialog implements View.OnClickListener, MailBuilder.MailListener {
    
    private AppCompatActivity activity;
    
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    
    
    private MailService serviceMail;
    
    
    private int idTicket;
    private SuperTicket _Ticket;
    private View rootView;
    private AutoCompleteTextView edMail;
    
    private BrioButton2 btnSendMail;
    private BrioCloseButton btnCancelar;
    private BrCustomer brCustomer;
    private ArrayList<DLCorreosClientes> customerEmails;
    private CustomerEmailAdapter emailAdapter;
    String TAG = "MailDialog";
    
    public MailDialog (AppCompatActivity activity) {
        this.activity = activity;
        
        LayoutInflater inflater = activity.getLayoutInflater ();
        rootView = inflater.inflate (R.layout.brio_dialog_mail, null);
        edMail = (AutoCompleteTextView) rootView.findViewById (R.id.edMail);
        rootView.findViewById (R.id.btnContinuarSinEnviar).setVisibility (View.GONE);
        btnSendMail = (BrioButton2) rootView.findViewById (R.id.btnSendMail);
        btnSendMail.setOnClickListener (this);
        btnCancelar = (BrioCloseButton) rootView.findViewById (R.id.btnCancelar);
        btnCancelar.setOnClickListener (this);
        
        builder = new AlertDialog.Builder (activity);
        builder.setView (rootView);
        builder.setCancelable (false);
        builder.create ();
        
        serviceMail = MailService.getInstance (activity);
        loadBussines ();
        loadData ();
    }
    public void loadBussines () {
        
        try {
            brCustomer = BrCustomer.getInstance (activity.getApplicationContext (), BrioGlobales.getAccess ());
            
        } catch (Exception e) {
            BrioGlobales.writeLog (TAG, "LoadBussines", e.getMessage ());
        }
    }
    public void loadData () {
        try {
            customerEmails = brCustomer.getCustomerEmails ();
            emailAdapter = new CustomerEmailAdapter (activity, R.layout.item_customer_email, customerEmails);
            edMail.setAdapter (emailAdapter);
            
        } catch (Exception e) {
            BrioGlobales.writeLog (TAG, "loadData", e.getMessage ());
        }
    }
    
    public void show (int idTicket) {
        this.idTicket = idTicket;
        alertDialog = builder.show ();
        alertDialog.getWindow ().setBackgroundDrawable (new ColorDrawable (android.graphics.Color.TRANSPARENT));
    }
    
    public void show (SuperTicket Ticket) {
        this._Ticket = Ticket;
        this.alertDialog = this.builder.show ();
        this.alertDialog.getWindow ().setBackgroundDrawable (new ColorDrawable (0));
    }
    
    @Override
    public void onClick (View v) {
        switch (v.getId ()) {
            case R.id.btnSendMail:
                sendMail ();
                break;
            
            case R.id.btnCancelar:
                alertDialog.dismiss ();
                break;
        }
    }
    
    private void sendMail () {
        String recipent = edMail.getText ().toString ();
        long actualizado = 0;
        long inserTEC = 0;
        
        if (! recipent.trim ().equals ("") &&Utils.validateEmail (recipent)) {
            
            if (this._Ticket != null) {
                serviceMail.sendTicket (- 1, _Ticket, recipent, this);
                idTicket= _Ticket.getTicket ().getIdTicket ();
            } else {
                serviceMail.sendTicket (- 1, idTicket, recipent, this);
            }
    
            String fecha = Utils.getStrFmtDateHour ();
            DLTicketsEnviadosClientes dlTEC = new DLTicketsEnviadosClientes ();
    
            dlTEC.setEmail (recipent);
            dlTEC.setFecha_creacion (fecha);
            dlTEC.setFecha_modificacion (fecha);
            dlTEC.setId_remoto (Utils.generarIdRandom ());
            dlTEC.setId_ticket (idTicket);
    
            inserTEC = brCustomer.saveTicketEnviadoCliente (dlTEC);
    
            DLCorreosClientes customerEmail = brCustomer.getCustomerEmail (recipent);
            if (customerEmail == null) {
                customerEmail = new DLCorreosClientes ();
                customerEmail.setEmail (recipent);
                customerEmail.setFecha_creacion (fecha);
                customerEmail.setFecha_modificacion (fecha);
                customerEmail.setId_remoto (Utils.generarIdRandom ());
        
                brCustomer.saveCustomerEmail (customerEmail);
            } else {
                actualizado = brCustomer.updateCustomerEmail (recipent);
            }
            
        } else {
            
            String title = activity.getString (R.string.envio_ticket_title);
            String msj = activity.getString (R.string.envio_ticket_msj);
            DialogListener listener = new DialogListener () {
                @Override
                public void onAccept () {
                    alertDialog.dismiss ();
                }
                
                @Override
                public void onCancel () {
                
                }
            };
            BrioConfirmDialog bcd = new BrioConfirmDialog (activity, title, msj, null,
                    listener);
            bcd.show ();
        }
    }
    
    @Override
    public void onMailStartSending (int taskId) {
        btnCancelar.setVisibility (View.GONE);
        btnSendMail.setEnabled (false);
    }
    
    @Override
    public void onMailEndSending (int taskId, boolean sent) {
        
        int msg = sent ? R.string.mail_ok : R.string.mail_error;
        Toast.makeText (activity, msg, Toast.LENGTH_LONG).show ();
        
        alertDialog.dismiss ();
    }
}
