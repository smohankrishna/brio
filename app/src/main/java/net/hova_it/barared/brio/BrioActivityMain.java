package net.hova_it.barared.brio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import net.hova_it.barared.brio.apis.hw.printer.PrinterManager;
import net.hova_it.barared.brio.apis.layouts.menus.administracion.MenuAdministracionManager;
import net.hova_it.barared.brio.apis.layouts.menus.mibrio.MenuMiBrioManager;
import net.hova_it.barared.brio.apis.layouts.menus.reportes.MenuReportesManager;
import net.hova_it.barared.brio.apis.mail.MailBuilder;
import net.hova_it.barared.brio.apis.mail.MailService;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.network.NetworkStatus;
import net.hova_it.barared.brio.apis.payment.PaymentService;
import net.hova_it.barared.brio.apis.pos.POSManager;
import net.hova_it.barared.brio.apis.pos.search.SearchManager;
import net.hova_it.barared.brio.apis.services.ServicesViewManager;
import net.hova_it.barared.brio.apis.session.LoginFormManager;
import net.hova_it.barared.brio.apis.setup.CommerceStatusChecker;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.RestSyncService;
import net.hova_it.barared.brio.apis.sync.SyncRememberManager;
import net.hova_it.barared.brio.apis.sync.SyncUploadTask;
import net.hova_it.barared.brio.apis.sync.entities.FTPData;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAcceptDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;

import java.util.HashMap;

import lat.brio.api.sincronizador.Sincronizador;
import lat.brio.core.AppController;

/**
 * Activity principal de la aplicación.
 * primer push
 * Created by Herman Peralta on 08/01/2016.
 */
public class BrioActivityMain extends BrioBaseActivity implements LoginFormManager.LoginFormManagerListener,
        MailService.BuzonSalidaTicketsListener, PrinterManager.PrinterManagerListener {
    
    
    private Context context;
    //Managers
    public SearchManager managerSearch;
    
    private boolean _Alertas = false; // Se mostraran las alertas
    
    private String TAG = BrioActivityMain.class.getSimpleName ();
    
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        /*//--> IMPORTANTE, esto invoca al onCreate() de BrioBaseActivity, y una vez que éste termina, continúa el flujo de este onCreate()*/
        super.onCreate (savedInstanceState);
        context=this;
        
    }
    
    
    @Override
    public void onStart () {
        super.onStart ();
        AppController.setCurrentActivity (this);
    }
    
    
    private CommerceStatusChecker commerceStatusChecker;
    
    /**
     * Callback que se invoca al loguearse en la aplicación
     */
    @Override
    public void onLoginCompleted () {
        
        autosync = new Splash ();
        autosync.show ();
        autosync.publish ("Sincronizando datos.\nEsta operación puede tomar varios minutos.\nPor favor espere...");
        
        // ######################################### Sincronizacion en segundo plano se deshabilita el boton al sincronizar en seguno plano ###############
        try {
            FTPData Auto = new FTPData ();
            Auto.setFilename ("Auto");
            
            Settings AutoSinc = modelManager.settings.getByNombre ("AUTO_SINC");
            
            Integer autosinc = 0;
            
            if (AutoSinc != null) {
                autosinc = Integer.parseInt (AutoSinc.getValor ());
            } else {
                AutoSinc = new Settings ();
                AutoSinc.setNombre ("AUTO_SINC");
                AutoSinc.setValor ("0");
                autosinc = 0;
            }
            if (autosinc == 1 && ! SINCRONIZANDO && NetworkStatus.hasInternetAccess (context)) {
                SINCRONIZANDO = true;
                Sincronizador sinc = new Sincronizador (this);
                sinc.sync (Auto, new SyncUploadTask.SyncUploadTaskListener () {
                    @Override
                    public void onSyncUploadTask (boolean success) {
                        // La sincronización en segundo plano ha terminado
                        BrioBaseActivity.SINCRONIZANDO = false;
                        autosync.dismiss ();
                        SincronizadorAutoFin ();
                        //Toast.makeText(context, "onSyncUploadTask", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                BrioBaseActivity.SINCRONIZANDO = false;
                autosync.dismiss ();
                SincronizadorAutoFin ();
                //Toast.makeText(context, "ELSE SYNC", Toast.LENGTH_LONG).show();
            }
        } catch (Exception Ex) {
            BrioBaseActivity.SINCRONIZANDO = false;
            autosync.dismiss ();
            SincronizadorAutoFin ();
            //Toast.makeText(context, "Exception SYNC", Toast.LENGTH_LONG).show();
            Ex.printStackTrace ();
            log.error (TAG, "SincronizadorAutoFin", Ex.getMessage ());
        }
        
        
        // ######################################### Sincronizacion en segundo plano ###############
    }
    
    private void SincronizadorAutoFin () {
        
        try {
            
            commerceStatusChecker = new CommerceStatusChecker (BrioActivityMain.this,
                    
                    new RestSyncService.RestSyncListener () {
                        @Override
                        public void onRestSyncFinished (int taskId, boolean synched) {
                            //  Log.i("COMMERCE_STATUS", "status " + commerceStatusChecker.getStatus()
                            // + " action " + commerceStatusChecker.getAction());
                            
                            switch (commerceStatusChecker.getAction ()) {
                                case CommerceStatusChecker.ACT_NO_SERVICES:
                                    servicesEnabled = false;
                                    showSimpleAlert (getString (R.string.comm_status_suspendido_title), getString (R.string.comm_status_suspendido_text), null);
                                    break;
                                case CommerceStatusChecker.ACT_OK:
                                    //inicialización normal
                                    managerTeclado.closeKeyboard ();
                                    onSessionOpen ();
                                    enableMenus ();
                                    loggedIn = true;
                                    managerSession.saveLong ("LAST_UPDATE", Long.valueOf (modelManager.settings.getByNombre ("LAST_UPDATE").getValor ()));

                        /* Cuando el buzon termina de enviar , el flujo se va a al metodo onBuzonSalidaTicketsFinished()
                        NOTA: Si no se requiere enviar los tickets pendientes, aquí hay que invocar onBuzonSalidaTicketsFinished() directamente*/
                                    serviceMail.getBuzonSalidaTicketsService ().enviarPendientes (false, BrioActivityMain.this);
                                    
                                    break;
                                
                                case CommerceStatusChecker.ACT_SHALL_NOT_PASS:
                                    //vuelvo a mostrar el login
                                    
                                    LoginFormManager loginFormManager = LoginFormManager.getInstance (BrioActivityMain.this);
                                    loginFormManager.createFragment (getSupportFragmentManager (), R.id.fragmentHolder);
                                    
                                    showSimpleAlert (getString (R.string.comm_status_cancelado_title), getString (R.string.comm_status_cancelado_text), null);
                                    
                                    break;
                            }
                        }
                    });
            commerceStatusChecker.sync ();
            
            
        } catch (Exception e) {
            e.printStackTrace ();
            log.error (TAG, "SincronizadorAutoFin", e.getMessage ());
        }
    }
    
    /**
     * Metodo que abre la sesion y corre los servicios principales junto con las pestañas de los menus
     */
    private void onSessionOpen () {
        
        
        try {
            DebugLog.log (getClass (), "BRIO_LOGIN", "");
            
            modelManager.settings.update ("SETUP_FIRST_RUN", "FALSE");
            
            Settings firmado = modelManager.settings.getByNombre ("APP_CONTRATO_FIRMADO");
            
            // Validar que firmado no sea nulo
            if (firmado == null) {
                firmado = new Settings ();
                firmado.setNombre ("APP_CONTRATO_FIRMADO");
                firmado.setValor ("0");
                modelManager.settings.save (firmado);
            }
            
            // No esta firmado, entonces, pedir firma de contrato
            if (firmado.getValor ().equals ("0")) {
                //DialogoContrato dialogoContrato = new DialogoContrato();
                //dialogoContrato.show(getSupportFragmentManager(), "Firma Contrato");
                // TODO: Forzar la firma del contrato
                
            }
            
            // Establecer el nivel de CRM de asociado
            TextView brio_nivelcrm = (TextView) findViewById (R.id.lblAsociadoCRMNombre);
            ImageView brio_colorcrm = (ImageView) findViewById (R.id.lblAsociadoCRMColor);
            
            // Nivel CRM
            Settings idNivel = modelManager.settings.getByNombre ("ID_NIVEL_CRM");
            Settings txtNivel = modelManager.settings.getByNombre ("TXT_NIVEL_CRM");
            
            String nivel, strnivel;
            
            
            if (idNivel != null || txtNivel != null) {
                nivel = idNivel.getValor ();
                strnivel = txtNivel.getValor ();
            } else {
                nivel = "1";
                strnivel = "EMERGENTE";
            }
            
            
            // Establecer el nivel de CRM de asociado
            brio_nivelcrm.setVisibility (View.VISIBLE);
            brio_colorcrm.setVisibility (View.GONE);//Se oculta el icono por el momento. Todos los negocios son plata.
            
            switch (nivel) {
                case "1": // Emergente
                    brio_colorcrm.setBackgroundResource (R.drawable.sello_emergente);
                    break;
                case "2": // Plata
                    brio_colorcrm.setBackgroundResource (R.drawable.sello_plata);
                    break;
                case "3": // Oro
                    brio_colorcrm.setBackgroundResource (R.drawable.sello_oro);
                    break;
                case "4": // Titanio
                    brio_colorcrm.setBackgroundResource (R.drawable.sello_magenta);
                    break;
            }
            
            brio_nivelcrm.setText (strnivel);
            
            // ######################################### Sincronizacion en segundo plano se deshabilita el boton al sincronizar en seguno plano ###############
            
            FTPData Auto = new FTPData ();
            Auto.setFilename ("sync");
            
            if (modelManager.settings.getByNombre ("AUTO_SINC").getValor ().equals ("1") && ! SINCRONIZANDO) {
                SINCRONIZANDO = true;
                Sincronizador sinc = new Sincronizador (this);
                //sinc.Mensajes(false);
                if (NetworkStatus.hasInternetAccess (context)) {
                    
                    sinc.sync (Auto, success -> {
                        // La sincronizacion en segundo plano ha terminado
                        BrioBaseActivity.SINCRONIZANDO = false;
                        Alertas (true); // Habilitar alertas despues de finalizar la sincronizacion automatica
                    });
                }
            }
            
            
            // ######################################### Sincronizacion en segundo plano ###############
            
            menuPOS = POSManager.getInstance (this);
            menuServices = ServicesViewManager.getInstance (this);
            menuReportes = MenuReportesManager.getInstance (this);
            menuAdministracion = MenuAdministracionManager.getInstance (this, splash);
            menuMiBrio = MenuMiBrioManager.getInstance (this);
            
            managerSearch = SearchManager.getInstance (this);
            
            managerPrinter = PrinterManager.getInstance (this);
            managerPrinter.setPrinterManagerListener (this);
            managerPrinter.searchPrinter ();
            
            servicePayment = PaymentService.getInstance (this);
            serviceMail = MailService.getInstance (this);
            
            menus = new HashMap<> ();
            menus.put (R.id.btnTab1, menuPOS);
            menus.put (R.id.btnTab2, menuServices);
            menus.put (R.id.btnTab3, menuReportes);
            menus.put (R.id.btnTab4, menuAdministracion);
            menus.put (R.id.btnTab5, menuMiBrio);
            
            getSupportActionBar ().setHomeAsUpIndicator (getResources ().getDrawable (R.drawable.app_ic_back2, null));
            
            //Utils.testExtractDataBase(this, "brio.db", "/storage/emulated/0/Download/brio.db");
            
            serviceMail.sendBillPocketTxMail ();
            
            //Enviar correo informativo de pruebas
            serviceMail.sendLogMail (managerSession, new MailBuilder.MailListener () {
                @Override
                public void onMailStartSending (int mailId) {
                
                }
                
                @Override
                public void onMailEndSending (int mailId, boolean sent) {
                    //sendFTPMails();
                }
            });
            
            configurePestanas ();
        } catch (Exception Ex) {
            Ex.printStackTrace ();
            log.error (TAG, "sendFTPMails", Ex.getMessage ());
            BrioBaseActivity.SINCRONIZANDO = false; // Para permitir sincronizar manualmente
            Alertas (true); // Habilitar alertas aun en caso de excepción
        }
    }
    
    
    /**
     * Listener que se invoca cuando el buzon de Tickets termina de enviar. El flujo procede a
     * mostrar el POS (o abrir la caja si es necesario).
     */
    @Override
    public void onBuzonSalidaTicketsFinished () {
        if (DEBUG_SHOW_TOASTS) {
            Toast.makeText (BrioActivityMain.this, "Termina buzon tickets", Toast.LENGTH_SHORT).show ();
        }
        
        //Esta linea funcionara solo si el cierre de caja NO reinicia la app
        //manejaPestanas(btns_pestanas_ids[0]); //abre el pos directamente
        
        //Este bloque solo funcionara si el cierre de caja reinicia la app
        if (! managerCajaApertura.isOpen ()) {
            DebugLog.log (getClass (), "BRIO_CAJA", "La caja esta cerrada, abriendo...");
            
            disableMenus ();
            
            //Si se presiono la pestana de POS y la caja no esta abierta, abrela
            managerCajaApertura.createFragments (getSupportFragmentManager (), R.id.fragmentHolder);
        } else {
            manejaPestanas (btns_pestanas_ids[0]); //abre el pos directamente
        }
        
        // syncRememberManager.alarm();
        
        MediaUtils.hideSystemUI (BrioActivityMain.this);
        
    }
    
    
    /**
     * Cuando el timeout de sesión inactiva llega al final, el SessionManager cierra la sesión en
     * BD y finalmente invoca este callback para avisar a la parte gráfica el cierre de sesión
     * Solo se reinicia la aplicación.
     */
    @Override
    public void onSessionClose () {
        DebugLog.log (getClass (), "BRIO_SESSION", "Se cierra sesion por timeout");
        
        Utils.restartApp ();
    }
    
    /**
     * Configurar los últimos managers.
     */
    @Override
    protected void configManagers () {
        managerSession.setSessionListener (this);
    }
    
    /**
     * Configurar la barra de pestanas (menús) y la barra de información.
     */
    private void configurePestanas () {
        
        try {
            //muestra tabs
            brio_tabs_lay.setVisibility (View.VISIBLE);
            
            // Pestañas
            btns_pestanas = new TextView[btns_pestanas_ids.length];
            for (int p = 0; p < btns_pestanas_ids.length; p++) {
                btns_pestanas[p] = (TextView) findViewById (btns_pestanas_ids[p]);
            }
            
            //Views
            brio_info.setText (getResources ().getString (R.string.activity_info_loggedin)
                    .replace ("?0", managerSession.readString ("descripcionComercio"))
                    .replace ("?1", managerSession.readInt ("idComercio") + "")
                    .replace ("?2", managerSession.readString ("usuario")));
            
            brio_version.setText (String.format ("Versión: %s", Utils.getAppData (this).versionName));
            
        } catch (Exception e) {
            e.printStackTrace ();
            log.error (TAG, "configurePestanas", e.getMessage ());
        }
    }
    
    /**
     * Este callback se invoca cuando Brío envía un intent a otra app, y ésta regresa un
     * resultado a Brío.
     * <p>
     * Actualmente solo se utiliza para enviar pagos a la app billpocket y recibir la respuesta.
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent) {
        super.onActivityResult (requestCode, resultCode, intent);
        
        try {
            
            MediaUtils.hideSystemUI (BrioActivityMain.this);
            // DebugLog.log(getClass(), "BRIO_INSTANCE", "onActivityResult");
            servicePayment.onActivityResult (requestCode, resultCode, intent);
            
        } catch (Exception e) {
            e.printStackTrace ();
            log.error (TAG, "onActivityResult", e.getMessage ());
        }
    }
    
    /**
     * Este metódo se invoca cuando se presiona el botón de retroceso de la aplicación.
     */
    @Override
    public void onBackPressed () {
        
        try {
            if (isBackEnabled) {
                if (managerTeclado.isOpen ()) {
                    managerTeclado.closeKeyboard ();
                } else {
                    if (pestana_activa == btns_pestanas_ids[2]) {
                        //   DebugLog.log(BrioActivityMain.this.getClass(), "BRIO_BACK", "BACK en reportes");
                        menuReportes.onRemoveOptionFragment ();
                    } else
                        if (pestana_activa == btns_pestanas_ids[3]) {
                            //  DebugLog.log(BrioActivityMain.this.getClass(), "BRIO_BACK", "BACK en administracion");
                            menuAdministracion.onRemoveOptionFragment ();
                        } else
                            if (pestana_activa == btns_pestanas_ids[4]) {
                                // DebugLog.log(BrioActivityMain.this.getClass(), "BRIO_BACK", "BACK en mibrio");
                                menuMiBrio.onRemoveOptionFragment ();
                            }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace ();
            log.error (TAG, "onBackPressed", e.getMessage ());
        }
    }
    
    @Override
    public void onPrinterReady () {
    
    }
    
    @Override
    public void onPrinterError (int error) {
    
    }
    
    @Override
    public void paperNearEmpty () {
    
    }
    
    
    public void showSimpleAlert (String title, String msj, DialogListener listener) {
        BrioAcceptDialog bad = new BrioAcceptDialog (this, title, msj, getString (R.string.brio_aceptar), listener);
        bad.show ();
    }
    
    
}
