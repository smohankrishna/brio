package net.hova_it.barared.brio.apis.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.FragmentBase;
import net.hova_it.barared.brio.apis.adapters.CustomerEmailAdapter;
import net.hova_it.barared.brio.apis.hw.barcodescanner.ScannerManager;
import net.hova_it.barared.brio.apis.hw.printer.PrinterManager;
import net.hova_it.barared.brio.apis.mail.HTMLTicketPatcher;
import net.hova_it.barared.brio.apis.mail.MailBuilder;
import net.hova_it.barared.brio.apis.mail.MailService;
import net.hova_it.barared.brio.apis.models.DLCorreosClientes;
import net.hova_it.barared.brio.apis.models.DLTicketsEnviadosClientes;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;

import java.util.ArrayList;
import java.util.Locale;

import lat.brio.core.BrioGlobales;
import lat.brio.core.bussines.BrCustomer;
import lat.brio.core.interfaces.IBasicMethods;

/**
 * Este fragment se invoca cuando ya se ha pagado la venta.
 * En este fragment, se almacena el ticket en la base de datos, se renderea a HTML
 * y se muestra una previsualización.
 *
 * Si la tableta tiene una impresora STAR TSP100 futurePRNT, se envía a imprimir el ticket.
 * Finalmente, el fragment cuenta con la opción de enviar el ticket (HTML) a un correo del cliente.
 *
 * Created by Herman Peralta on 02/05/2016.
 */
public class PaymentSendTicketFragment extends FragmentBase implements MailBuilder.MailListener, IBasicMethods {
    private View rootView;
    private AppCompatActivity activity;
    private SuperTicket mSTicket;
    private ViewHolder viewHolder;
    private MailService serviceMail;
    String TAG = "PaymentSendTicketFragment";
    
    //private PrinterManager managerPrinter;
    private PaymentService.AccountPaymentListener accountPaymentListener;
    private int idTicket = - 1;
    //    private ModelManager modelManager;
    private BrCustomer brCustomer;
    private ArrayList<DLCorreosClientes> customerEmails;
    private CustomerEmailAdapter emailAdapter;
    
    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate (R.layout.payment_sendticket_fragment, container, false);
        
        setView (rootView);
        //        modelManager = new ModelManager (getActivity ());
        viewHolder = new ViewHolder (rootView);
        serviceMail = MailService.getInstance (getActivity ());
        //managerPrinter = PrinterManager.getInstance(getContext());
        loadBussines ();
        loadData ();
        
        return rootView;
    }
    
    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        
        MediaUtils.loadStringInWebView (viewHolder.wvTicket, HTMLTicketPatcher.superTicketToHTML (mSTicket, getActivity ()));
        
        viewHolder.tvTotalCobro.setText (String.format (new Locale ("es", "MX"), "$%.2f", mSTicket.getTicket ().getImporteNeto ()));
        viewHolder.tvCambio.setText (String.format (new Locale ("es", "MX"), "$%.2f", mSTicket.getTicket ().getCambio ()));
    }
    
    @Override
    public void onResume () {
        super.onResume ();
        
        ((BrioActivityMain) activity).managerScanner.stopScannerListening (((BrioActivityMain) activity).menuPOS);
    }
    
    public void showFragment (AppCompatActivity activity, int idTicket, int layoutId, SuperTicket superTicket, PaymentService.AccountPaymentListener accountPaymentListener) {
        this.activity = activity;
        this.idTicket = idTicket;
        this.mSTicket = superTicket;
        this.accountPaymentListener = accountPaymentListener;
        
        activity.getSupportFragmentManager ().beginTransaction ().setCustomAnimations (android.R.anim.slide_in_left, android.R.anim.slide_out_right).add (layoutId, this).commit ();
    }
    
    private void removeFragment () {
        ((BrioActivityMain) getActivity ()).disableMenus ();
        
        accountPaymentListener.onAccountPaymentStatusChange (PaymentService.ACCOUNT_STATUS.ENVIADO);
        
        activity.getSupportFragmentManager ()
                .beginTransaction ()
                .setCustomAnimations (android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .remove (this).commit ();
        
        ((BrioActivityMain) getActivity ()).managerScanner
                .startScannerListening (((BrioActivityMain) getActivity ())
                        .menuPOS, ScannerManager.KEYCODE_DELIM_ENTER);
        
        MediaUtils.hideSystemUI ((AppCompatActivity) getActivity ());
    }
    
    /**
     * Enviar correo con el ticket actual.
     */
    private void sendMail () {
        String recipent = viewHolder.edMail.getText ().toString ();
        long actualizado = 0;
        long inserTEC = 0;
        
        if (! recipent.trim ().equals ("") && Utils.validateEmail (recipent)) {
            serviceMail.sendTicket (- 1, mSTicket, recipent, this);
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
            
            removeFragment ();
        } else {
            
            String title = getString(R.string.envio_ticket_title);
            String msj = getString(R.string.envio_ticket_msj);
            
            DialogListener listener = new DialogListener () {
                @Override
                public void onAccept () {
                    removeFragment ();
                }
                
                @Override
                public void onCancel () {
                
                }
            };
            showAlert (title, msj, listener);
            
        }
    }
    
    @Override
    public void onMailStartSending (int mailId) {
        
        //viewHolder.panelMain.setVisibility(View.GONE);
        //viewHolder.panelLoading.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onMailEndSending (int mailId, boolean sent) {
        viewHolder.panelLoading.setVisibility (View.GONE);
        viewHolder.panelMain.setVisibility (View.VISIBLE);
        
        String msg = sent ? "Ticket enviado exitosamente." : "No fue posible enviar el ticket en este momento.\nSe enviará la próxima vez inicie sesión.";
        Toast.makeText (activity, msg, Toast.LENGTH_LONG).show ();
        
        
    }
    
    @Override
    public void loadViews () {
    
    }
    
    @Override
    public void loadBundle () {
    
    }
    
    @Override
    public void loadBussines () {
        
        try {
            brCustomer = BrCustomer.getInstance (getActivity ().getApplicationContext (), BrioGlobales.getAccess ());
            
        } catch (Exception e) {
            BrioGlobales.writeLog (TAG, "LoadBussines", e.getMessage ());
        }
        
    }
    
    @Override
    public void loadData () {
        try {
            customerEmails = brCustomer.getCustomerEmails ();
            emailAdapter = new CustomerEmailAdapter (getActivity (), R.layout.item_customer_email, customerEmails);
            viewHolder.edMail.setAdapter (emailAdapter);
            
        } catch (Exception e) {
            BrioGlobales.writeLog (TAG, "loadData", e.getMessage ());
        }
        
    }
    
    class ViewHolder implements View.OnClickListener {
        
        public WebView wvTicket;
        public AutoCompleteTextView edMail;
        
        public TextView tvTotalCobro;
        public TextView tvCambio;
        
        public View panelInfo;
        public View panelMail;
        public View panelMain;
        public View panelLoading;
        
        public ViewHolder (View rootView) {
            wvTicket = (WebView) rootView.findViewById (R.id.wvTicket);
            edMail = (AutoCompleteTextView) rootView.findViewById (R.id.edMail);
            
            tvTotalCobro = (TextView) rootView.findViewById (R.id.tvImporteNeto);
            tvCambio = (TextView) rootView.findViewById (R.id.tvCambio);
            
            panelInfo = rootView.findViewById (R.id.panelInfo);
            panelMail = rootView.findViewById (R.id.panelMail);
            
            panelMain = rootView.findViewById (R.id.panelMain);
            panelLoading = rootView.findViewById (R.id.panelLoading);
            
            rootView.findViewById (R.id.btnAccept).setOnClickListener (this);
            rootView.findViewById (R.id.btnContinuarSinEnviar).setOnClickListener (this);
            rootView.findViewById (R.id.btnSendMail).setOnClickListener (this);
            rootView.findViewById (R.id.btnShowMail).setOnClickListener (this);
            rootView.findViewById (R.id.btnReprint).setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
            switch (v.getId ()) {
                case R.id.btnShowMail:
                    panelInfo.setVisibility (View.GONE);
                    panelMail.setVisibility (View.VISIBLE);
                    break;
                
                case R.id.btnContinuarSinEnviar:
                    panelMail.setVisibility (View.GONE);
                    panelInfo.setVisibility (View.VISIBLE);
                    break;
                
                case R.id.btnReprint:
                    PrinterManager managerPrinter = PrinterManager.getInstance ((BrioActivityMain) getContext ());
                    managerPrinter.searchPrinter ();
                    managerPrinter.printTicket (mSTicket);
                    break;
                
                case R.id.btnSendMail:
                    sendMail ();
                    break;
                
                case R.id.btnAccept:
                    removeFragment ();
                    break;
            }
        }
    }
}
