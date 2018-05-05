package net.hova_it.barared.brio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import net.hova_it.barared.brio.apis.drawer.CajaAperturaManager;
import net.hova_it.barared.brio.apis.hw.barcodescanner.ScannerManager;
import net.hova_it.barared.brio.apis.hw.printer.PrinterManager;
import net.hova_it.barared.brio.apis.interfaces.OnFragmentListener;
import net.hova_it.barared.brio.apis.layouts.menus.administracion.MenuAdministracionManager;
import net.hova_it.barared.brio.apis.layouts.menus.mibrio.MenuMiBrioManager;
import net.hova_it.barared.brio.apis.layouts.menus.reportes.MenuReportesManager;
import net.hova_it.barared.brio.apis.mail.MailService;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.payment.PaymentService;
import net.hova_it.barared.brio.apis.pos.POSManager;
import net.hova_it.barared.brio.apis.services.ServicesViewManager;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.session.SessionManager.SessionListener;
import net.hova_it.barared.brio.apis.setup.SetupService;
import net.hova_it.barared.brio.apis.sync.RestSyncService;
import net.hova_it.barared.brio.apis.sync.SyncRememberManager;
import net.hova_it.barared.brio.apis.sync.TicketSync;
import net.hova_it.barared.brio.apis.utils.AnimationUtils2;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.alarmas.Alarm;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAcceptDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogActionListener;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.utils.dialogs.SplashDialogFragment;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoManager2;

import java.util.Map;

import brio.sdk.logger.log.BrioLog;
import lat.brio.api.alerta.Alerta;
import lat.brio.core.AppController;
import lat.brio.core.BrioGlobales;
import lat.brio.core.REQUEST;
import lat.brio.norris.SplashScreen;


/**
 * Activity que complementa al activity principal de la aplicación.
 * Contiene todos los componentes esenciales de toda la aplicación.
 * <p>
 * Created by Herman Peralta on 07/01/2016.
 * updated: 01/03/2016.
 */
public abstract class BrioBaseActivity extends AppCompatActivity implements SessionListener, OnClickListener, OnFragmentListener {
    
    /**
     * Bandera para mostrar algunos mensajes de debug en toda la aplicación.
     */
    public final static boolean DEBUG_SHOW_TOASTS = false;
    
    /**
     * Bandera para detectar si esta actualmente sincronizando y asi evitar errores
     */
    public static Boolean SINCRONIZANDO = false;
    private boolean _Alertas = false; // Se mostraran las alertas
    
    //Managers Core
    public ModelManager modelManager;
    public SessionManager managerSession;
    public TecladoManager2 managerTeclado;
    public GPSTracker gpsTracker;
    public ScannerManager managerScanner;
    protected PrinterManager managerPrinter;
    public CajaAperturaManager managerCajaApertura;
    
    // INI: Agregado por: Manuel Delgadillo - 21/FEB/2017
    // public SearchManager managerSearch;// Se movió esta propiedad de variable desde BrioActivityMain
    // FIN: Agregado por: Manuel Delgadillo - 21/FEB/2017
    
    // Se movieron para aqui por accesibilidad
    // Menus
    public POSManager menuPOS;
    public ServicesViewManager menuServices;
    public MenuReportesManager menuReportes;
    public MenuAdministracionManager menuAdministracion;
    public MenuMiBrioManager menuMiBrio;
    
    
    //Servicios Core
    protected SetupService serviceSetup;
    protected PaymentService servicePayment;
    public Alerta banners;
    protected SyncRememberManager syncRememberManager;
    public MailService serviceMail;
    
    //Otros
    protected boolean isBackEnabled = false;
    protected int pestana_activa = - 1;
    protected boolean dispatchKey = true;
    protected boolean loggedIn = false;
    protected FloatingActionButton _BotonFlotante;
    protected Map<Integer, BrioManagerInterface> menus;
    
    // Pestañas
    protected TextView[] btns_pestanas;
    protected static int[] btns_pestanas_ids = { R.id.btnTab1, R.id.btnTab2, R.id.btnTab3, R.id.btnTab4, R.id.btnTab5 };
    
    protected boolean activityRunning = false;
    
    public Splash splash;
    public Splash autosync;
    
    protected View panelActions;
    protected View actionBanner;
    protected View actionPrueba;
    protected View brio_tabs_lay;
    protected TextView brio_info, brio_version;
    protected boolean servicesEnabled = true;
    public BrioLog log;
    private String TAG = BrioBaseActivity.class.getSimpleName ();
    public boolean CLOSE_ON_CRASH = true;
    static AppController appController;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.brio_activity_main);
        
        splash = new Splash ();
        _BotonFlotante = (FloatingActionButton) findViewById (R.id.btnSoporte);
        _BotonFlotante.setBackgroundResource (R.drawable.app_ic_actionbar_logo2);
        _BotonFlotante.setVisibility (View.GONE);
        // Establecer el listener para el evento touch de FAB
        _BotonFlotante.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                menuMiBrio.iniciarVideoLlamada ();
            }
        });
        
        log = new BrioLog (BrioGlobales.ArchivoLog);
        appController = AppController.getInstance ();
        
        initCrash ();
        
        init ();
        registerReceiver ();
    }


    private void initCrash() {
        if (CLOSE_ON_CRASH) {
            
            Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler () {
                @Override
                public void uncaughtException (Thread t, Throwable e) {
                    
                    
                    StringBuilder error = new StringBuilder ("");
                    
                    error.append ("\n\n");
                    error.append ("Mensaje:");
                    error.append (e.getMessage ());
                    error.append ("\n");
                    error.append("Cause: "+e.getCause ().getMessage ());
                    //                    error.append(t.getStackTrace().toString());
                    error.append ("\n");
                   // error.append ("UncaughtExceptionHandler");
//                    error.append (t.getUncaughtExceptionHandler ().toString ());
                    log.error (getClass ().getSimpleName (), "\n\nInitCrash", error.toString ());
                    e.printStackTrace ();
                    finish ();
                    Intent start = new Intent (BrioBaseActivity.this, SplashScreen.class);
                    start.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //start.putExtra("error", message);
                    startActivity (start);
                    System.exit (0);
                }
            });
            
        } else {
            Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler () {
                @Override
                public void uncaughtException (Thread thread, Throwable ex) {
                    
                    ex.printStackTrace ();
                    System.exit (0);
                }
            });
        }
    }
    
    
    /**
     * Esto realiza la inicialización principal de la app.
     * Cuando se agregue la validación de permisos de Android, se debe invocar
     * este método cuando se obtengan los permisos en versiones >= Marshmallow
     */
    private void init () {
        try {
            setupManagers ();
            initGeo ();
            setupUI ();
            serviceSetup.setup ();
        } catch (Exception e) {
            log.error (TAG, "init", e.getMessage ());
        }
    }
    
    
    /**
     * Inicializa managers y servicios Core.
     */
    private void setupManagers () {
        
        try {
            modelManager = new ModelManager (this);
            managerSession = SessionManager.getInstance (this);
            managerTeclado = TecladoManager2.getInstance (this);
            managerScanner = ScannerManager.getInstance (this);
            
            managerCajaApertura = CajaAperturaManager.getInstance (this);
            
            configManagers ();
            
            serviceSetup = SetupService.getInstance (this);
        } catch (Exception e) {
            log.error (this.getClass ().getSimpleName (), "setupManagers", e.getMessage ());
        }
    }
    
    private void initGeo () {
        try {
            // Inicializar Tracker GPS
            gpsTracker = new GPSTracker (getApplicationContext ());
            if (gpsTracker.canGetLocation ()) {
                managerSession.saveString ("DATA_GPS", gpsTracker.Altitud () + "," + gpsTracker.Latitud () + "," + gpsTracker.Longitud () + "," + gpsTracker.Tiempo ());
            }
        } catch (Exception e) {
            log.error (this.getClass ().getSimpleName (), "initGeo", e.getMessage ());
        }
    }
    
    
    public Boolean Alertas (Boolean Alertas) {
        
        try {
            _Alertas = Alertas;
            
            if (loggedIn) {
                if (_Alertas) {
                    if (banners == null) {
                        banners = new Alerta (this);
                        banners.setFragmentListener (this);
                    }
                    banners.Iniciar ();
                    Alarm.generaAlarmaSincronizarApp (this);
                } else {
                    Alarm.cancelarAlarmaBanner (this);
                    Alarm.cancelarAlarmaSincronizarApp (this);
                    Alarm.cancelarAlarmaCerrarSesion (this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
            log.error (TAG, "Alertas", e.getMessage ());
        }
        return _Alertas;
    }
    
    /**
     * Inicializa los componentes básicos de interfaz gráfica.
     */
    private void setupUI () {
        try {
            //Actionbar
            Toolbar actionbar = (Toolbar) findViewById (R.id.brio_actionbar);
            setSupportActionBar (actionbar);
            getSupportActionBar ().setLogo (getResources ().getDrawable (R.drawable.app_ic_actionbar_logo2, null));
            getSupportActionBar ().setHomeButtonEnabled (false); // disable the button
            getSupportActionBar ().setDisplayHomeAsUpEnabled (false); // delete the left caret
            
            panelActions = findViewById (R.id.panelActions);
            
            actionBanner = findViewById (R.id.actionBanner);
            actionBanner.setOnClickListener (this);
            
            //iconos del action bar
            brio_tabs_lay = findViewById (R.id.brio_tabs);
            brio_tabs_lay.setVisibility (View.GONE);
            
            brio_info = (TextView) findViewById (R.id.brio_info);
            brio_version = (TextView) findViewById (R.id.brio_version);
            
            brio_version.setText (String.format ("Versión: %s", Utils.getAppData (this).versionName));
            Settings idComercio = modelManager.settings.getByNombre ("ID_COMERCIO");
            if (idComercio != null && ! idComercio.getValor ().equals ("0")) {
                brio_info.setText (Utils.getString (R.string.activity_info_notlogged, this).replace ("?0", modelManager.settings.getByNombre ("DESCRIPCION_COMERCIO").getValor ()).replace ("?1", idComercio.getValor () + ""));
            } else {
                brio_info.setText (Utils.getString (R.string.activity_info_notregistered, this));
            }
            MediaUtils.hideSystemUI (this);
            
        } catch (Exception e) {
            log.error (TAG, "setupUI", e.getMessage ());
        }
    }
    
    
    /**
     * Gestiona el cambio entre pestanas (menús de la aplicación)
     * @param viewid
     *
     * @return
     */
    protected boolean manejaPestanas (int viewid) {
        try {
            //            managerSession.update ();
            
            
            if (viewid == pestana_activa) {
                return true; /*evitar pulsar la misma pestana dos veces seguidas*/
            }
            if (pestana_activa == R.id.btnTab1 && viewid != R.id.btnTab1 && ((BrioActivityMain) this).managerSearch != null) { // Al cambiar de ventas a otro diferente de ventas
                if (((BrioActivityMain) this).managerSearch.esVisible ()) {
                    ((BrioActivityMain) this).managerSearch.Cerrar ();
                    // return true;
                }
            }
            
            // Si esta en Mi Brio
            if (viewid == R.id.btnTab5) {
                _BotonFlotante.setVisibility (View.VISIBLE);
            } else {
                _BotonFlotante.setVisibility (View.GONE);
            }
            
            TextView pestana;
            if (menus.containsKey (viewid)) {
                
                if (viewid == R.id.btnTab2 && ! servicesEnabled) {
                    BrioAcceptDialog bad = new BrioAcceptDialog (BrioBaseActivity.this, Utils.getString (R.string.comm_status_suspendido_title, BrioBaseActivity.this), Utils.getString (R.string.comm_status_suspendido_text, BrioBaseActivity.this), "Aceptar", new DialogListener () {
                        @Override
                        public void onAccept () {
                        
                        }
                        
                        @Override
                        public void onCancel () {
                        
                        }
                    });
                    bad.show ();
                    
                    return true;
                }
                
                if (pestana_activa != - 1) {
                    // quitar la pestaña anterior
                    
                    if (managerTeclado.isOpen ()) {
                        managerTeclado.closeKeyboard ();
                    }
                    menus.get (pestana_activa).removeFragments ();
                    
                    //Pestana inactiva
                    pestana = (TextView) findViewById (pestana_activa);//.setBackgroundResource(R.drawable.view_bkg_button_blue);
                    pestana.setBackgroundResource (R.drawable.view_bkg_pleca_inactiva);
                    pestana.setTextColor (getResources ().getColor (R.color.app_sol_fuente_inactiva));
                }
                
                
                // pestana actual
                pestana_activa = viewid;
                
                pestana = (TextView) findViewById (pestana_activa);
                pestana.setBackgroundResource (R.drawable.view_bkg_pleca_activa);
                pestana.setTextColor (getResources ().getColor (R.color.app_sol_blanco));
                
                //menus.get(pestana_activa).createFragments(getSupportFragmentManager(), R.id.fragmentHolder);
                
                switch (pestana_activa) {
                    case R.id.btnTab3: //Reportes
                        disableMenus ();
                        TicketSync ticketSync = new TicketSync (this, new RestSyncService.RestSyncListener () {
                            @Override
                            public void onRestSyncFinished (int taskId, boolean synched) {
                                if (DEBUG_SHOW_TOASTS) {
                                    Toast.makeText (BrioBaseActivity.this, "Fin de ticketSync", Toast.LENGTH_SHORT).show ();
                                }
                                
                                menus.get (pestana_activa).createFragments (getSupportFragmentManager (), R.id.fragmentHolder);
                                enableMenus ();
                            }
                        });
                        ticketSync.sync ();
                        break;
                    default:
                        menus.get (pestana_activa).createFragments (getSupportFragmentManager (), R.id.fragmentHolder);
                }

            /*
            if(pestana_activa == R.id.btnTab3) {
                disableMenus();
                TicketSync ticketSync = new TicketSync(this, new RestSyncService.RestSyncListener() {
                    @Override
                    public void onRestSyncFinished(int taskId, boolean synched) {
                        if(DEBUG_SHOW_TOASTS) { Toast.makeText(BrioBaseActivity.this, "Fin de ticketSync", Toast.LENGTH_SHORT).show(); }

                        menus.get(pestana_activa).createFragments(getSupportFragmentManager(), R.id.fragmentHolder);
                        enableMenus();
                    }
                });
                ticketSync.sync();
            } else {
                menus.get(pestana_activa).createFragments(getSupportFragmentManager(), R.id.fragmentHolder);
            }
            */
                return true;
            }
            
        } catch (Exception e) {
            e.printStackTrace ();
            log.error (TAG, "manejaPestanas", e.getMessage ());
        }
        return false;
    }
    
    /**
     * Abre la ventana de POS después de aperturar la caja.
     */
    public void abrirPOSDespuesDeAperturaCaja () {
        
        try {
            enableMenus ();
            menus.get (R.id.btnTab1).createFragments (getSupportFragmentManager (), R.id.fragmentHolder);
        } catch (Exception e) {
            e.printStackTrace ();
            log.error (TAG, "abrirPOSDespuesDeAperturaCaja", e.getMessage ());
        }
    }
    
    @Override
    protected void onResume () {
        super.onResume ();
        //if(managerPrinter!=null) { managerPrinter.searchAlarmStart(); }
        //        if (loggedIn && managerSession != null) {
        //            managerSession.update ();
        //        }
        Alertas (true);
        
    }
    
    @Override
    protected void onPause () {
        super.onPause ();
       
    }
    
    @Override
    protected void onDestroy () {
        Alertas (false);
        unregisterReceiver ();
        super.onDestroy ();
    }
    
    /**
     * Este metodo se sobreescribe para ocultar las barras de sistema de android.
     * @param ev
     *
     * @return
     */
    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
        
        if (ev.getAction () == MotionEvent.ACTION_DOWN) {
            //todo: ver una forma mejor de hacer esto
            MediaUtils.hideSystemUI (this);
        }
        
        return super.dispatchTouchEvent (ev);
    }
    
    /**
     * Gestionar el boton de retroceso de la aplicación.
     * @param item
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        
        switch (item.getItemId ()) {
            case android.R.id.home:
                if (isBackEnabled) {
                    onBackPressed ();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected (item);
    }
    
    /**
     * Listener para click de views del activity. Si se presiona
     * una de las pestañas de menú, se invoca manejaPestanas().
     * @param btn
     */
    @Override
    public void onClick (final View btn) {
        boolean era_pestana = manejaPestanas (btn.getId ());
        if (! era_pestana) {
            if (Utils.shouldPerformClick ()) {
                AnimationUtils2.animateButtonPush (btn, new Runnable () {
                    @Override
                    public void run () {
                        switch (btn.getId ()) {
                            case R.id.actionBanner:
                                banners.showAllBanners ();
                                break;
                            
                            case R.id.btnSoporte:
                                
                                manejaPestanas (R.id.btnSoporte);
                            
                        }
                    }
                });
            }
        }
    }
    
    /**
     * Habilitar la barra de pestañas del activity.
     */
    public void enableMenus () {
        
        try {
            MediaUtils.hideSystemUI (this);
            for (TextView pestana : btns_pestanas) {
                pestana.setOnClickListener (this);
            }
        } catch (Exception e) {
            log.error (TAG, "enableMenus", e.getMessage ());
        }
    }
    
    /**
     * Deshabilitar la barra de pestañas del activity.
     */
    public void disableMenus () {
        
        try {
            MediaUtils.hideSystemUI (this);
            for (TextView pestana : btns_pestanas) {
                pestana.setOnClickListener (null);
            }
        } catch (Exception e) {
            log.error (TAG, "disableMenus", e.getMessage ());
        }
    }
    
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        //Guardar variables
        outState.putInt (BUNDLE_pestana_activa, pestana_activa);
        super.onSaveInstanceState (outState); //llamar siempre al final
    }
    
    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        super.onRestoreInstanceState (savedInstanceState); //llamar siempre al inicio
        //Recuperar variables
        pestana_activa = savedInstanceState.getInt (BUNDLE_pestana_activa, - 1);
    }
    
    @Override
    public void onStart () {
        activityRunning = true;
        super.onStart ();
    }
    
    @Override
    public void onStop () {
        activityRunning = false;
        super.onStop ();
    }
    
    
    /**
     * Gestionar la entrada: Lectora de código de barras
     */
    @Override
    public boolean dispatchKeyEvent (KeyEvent keyEvent) {
        
        //--------------------------------------------------
        // TENER CUIADADO AQUI, SIEMPRE VERIFICAR DESPUES DE
        // MOVER ESTO
        //--------------------------------------------------
        
        try {
            int action = keyEvent.getAction ();
            int keyCode = keyEvent.getKeyCode ();
            
            if (dispatchKey && action != KeyEvent.ACTION_DOWN) {
                        if (managerScanner.dispatchKeyEvent (keyEvent)) {
                            return true;
                        }
            } else {
                    super.dispatchKeyEvent (keyEvent);
            }
            
            
        } catch (Exception e) {
            log.error (TAG, "dispatchKeyEvent", e.getMessage ());
        }
        return false;
    }
    
    
    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        
        
        if (dispatchKey) {
            if (managerScanner.dispatchKeyEvent (event)) {
                return true;
            }
        } else {
            super.onKeyUp (keyCode, event);
        }
        
        return false;
    }
    
    /**
     * Habilitar o deshabilitar la intercepción del método dispatchKeyEvent()
     * @param dispatchKey
     */
    public void setDispatchKey (boolean dispatchKey) {
        this.dispatchKey = dispatchKey;
    }
    
    /**
     * Habilitar el funcionamiento del botón de retroceso.
     */
    public void enableBackNavigation () {
        
        try {
            DebugLog.log (getClass (), "BRIO_BACK", "");
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
            isBackEnabled = true;
        } catch (Exception e) {
            log.error (TAG, "enableBackNavigation", e.getMessage ());
        }
    }
    
    /**
     * Deshabilitar el funcionamiento del botón de retroceso.
     */
    public void disableBackNavigation () {
        
        try {
            DebugLog.log (getClass (), "BRIO_BACK", "");
            getSupportActionBar ().setDisplayHomeAsUpEnabled (false);
            isBackEnabled = false;
        } catch (Exception e) {
            log.error (TAG, "disableBackNavigation", e.getMessage ());
        }
    }
    
    /**
     * Instanciar los managers y
     * agregarlos al hash de managers usando como KEY la
     * pestaña en donde se quiere que el manager actual.
     * <p>
     * configManagers() se llama automaticamente en el activity
     * al invocar a super.setContentView(resid).
     * De lo contrario, hay que invocar manualmente a configManagers()
     * después de setContentView(resid) en onCreate del activity hijo
     */
    protected abstract void configManagers ();
    
    //Bundle keys
    private final static String BUNDLE_pestana_activa = "BUNDLE_pestana_activa";
    
    public boolean isActivityRunning () {
        return activityRunning;
    }
    
    @Override
    public void onFragment (int requestCode, String msj, Bundle params) {
        
        switch (requestCode) {
            case REQUEST.REQUEST_CODE_BANNERS:
                if (msj != null && msj.equals ("1")) {
                    actionBanner.setVisibility (View.VISIBLE);
                } else {
                    actionBanner.setVisibility (View.GONE);
                }
                break;
            
        }
        
    }
    
    public class Splash {
        private SplashDialogFragment splashDialogFragment;
        private boolean showingSplash;
        
        protected Splash () {
            showingSplash = false;
        }
        
        public void show () {
            
            try {
                if (! showingSplash && activityRunning) {
                    splashDialogFragment = new SplashDialogFragment ();
                    splashDialogFragment.show (getSupportFragmentManager (), "splash");
                    
                    showingSplash = true;
                }
            } catch (Exception e) {
                e.printStackTrace ();
                log.error (TAG, "Splash", e.getMessage ());
            }
        }
        
        public void publish (String msg) {
            try {
                if (showingSplash) {
                    splashDialogFragment.publish (msg);
                }
            } catch (Exception e) {
                e.printStackTrace ();
                log.error (TAG, "publish", e.getMessage ());
            }
        }
        
        public void dismiss () {
            try {
                if (showingSplash && splashDialogFragment != null) {
                    
                    splashDialogFragment.dismissAllowingStateLoss ();
                    
                    showingSplash = false;
                }
            } catch (Exception e) {
                e.printStackTrace ();
                log.error (TAG, "dismiss", e.getMessage ());
            }
        }
    }
    
    private void unregisterReceiver () {
        try {
            if (AlarmBanner != null) {
                this.unregisterReceiver (AlarmBanner);
            }
            if (AlarmSync != null) {
                this.unregisterReceiver (AlarmSync);
            }
            if (AlarmSesionTimeout != null) {
                this.unregisterReceiver (AlarmSesionTimeout);
            }
    
    
            Log.e (TAG, "unregistered unregisterReceiver");
        } catch (IllegalArgumentException e) {
            Log.e (TAG, "epicReciver is already unregistered");
            
        }
        
        Alarm.cancelarAlarmaBanner (this);
        Alarm.cancelarAlarmaSincronizarApp (this);
    }
    
    
    private void registerReceiver () {
        registerReceiver (AlarmBanner, new IntentFilter (Alarm.BROADCAST_BANNERS));
        registerReceiver (AlarmSync, new IntentFilter (Alarm.BROADCAST_APP_SYNC));
        registerReceiver (AlarmSesionTimeout, new IntentFilter (Alarm.BROADCAST_SESION_TIMEOUT));
    }
    
    public final BroadcastReceiver AlarmBanner = new BroadcastReceiver () {
        @Override
        public void onReceive (Context context, Intent intent) {
            if (loggedIn && splash != null && ! splash.showingSplash) {
                banners.showBanner (appController.getAlert ());
            } else {
                Alarm.generaAlarmaBanners (context);
            }
            
        }
    };
    
    public final BroadcastReceiver AlarmSync = new BroadcastReceiver () {
        @Override
        public void onReceive (Context context, Intent intent) {
            if (loggedIn && splash != null && ! splash.showingSplash) {
                
                
                if (syncRememberManager == null) {
                    syncRememberManager = SyncRememberManager.getInstance (context);
                }
                syncRememberManager.alarm ();
            } else {
                Alarm.generaAlarmaSincronizarApp (context);
            }
        }
    };
    
    public final BroadcastReceiver AlarmSesionTimeout = new BroadcastReceiver () {
        @Override
        public void onReceive (final Context context, Intent intent) {
            
            if(isActivityRunning ()){
    
                if (loggedIn && splash != null && ! splash.showingSplash && managerSession != null) {
        
                    DialogActionListener listener = new DialogActionListener () {
                        @Override
                        public void onAcceptResult (Object results) {
                            managerSession.close ();
                        }
            
                        @Override
                        public void onCancelResult (Object results) {
                            Alarm.generaAlarmaCerrarSesion (context);
                        }
                    };
        
                    managerSession.showAlertSesionTimeOut (listener);
                } else {
                    Alarm.generaAlarmaCerrarSesion (context);
                }
            }else{
                managerSession.close ();
            }
            
        }
    };
    
    @Override
    public void onUserInteraction () {
        super.onUserInteraction ();
        
        //Cada vez que el usuario interactua con la aplicación, se cancela la alarma y se genera nuevamente
        if (loggedIn) {
            appController.setLastInteraction (Utils.getCurrentTimestamp ());
            Alarm.generaAlarmaBanners (this);
            Alarm.generaAlarmaSincronizarApp (this);
            Alarm.generaAlarmaCerrarSesion (this);
        }
    }
    
    
}
