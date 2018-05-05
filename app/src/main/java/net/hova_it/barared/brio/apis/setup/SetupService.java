package net.hova_it.barared.brio.apis.setup;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.admin.CommerceFormFragment;
import net.hova_it.barared.brio.apis.admin.CommerceFormManager;
import net.hova_it.barared.brio.apis.admin.RegistroCajaFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.session.LoginFormManager;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioBussines.BrioBusinessRules;
import lat.brio.core.BrioGlobales;

/**
 * Clase que se encarga de la inicialización de la aplicación.
 * <p>
 * Si es la primera vez que se abre la aplicación, lleva este flujo:
 * 1) Términos y condiciones.
 * 2) Alta / recuperación de comercio.
 * 3) SplashScreen de sesión.
 * Si ya hay un comercio registrado, solamente presenta el inicio de sesión.
 * <p>
 * Created by Herman Peralta on 19/03/16.
 */
public class SetupService {

    private static SetupService daService;

    private Context context;
    private BrioActivityMain mainActivity;
    private LoginFormManager loginFormManager;
    private ModelManager managerModel;

    public static SetupService getInstance(Context context) {
        if (daService == null) {
            daService = new SetupService(context);
        }
        return daService;
    }

    private SetupService(Context context) {
        this.context = context;
        this.mainActivity = (BrioActivityMain) context;
        this.managerModel = new ModelManager(context);

        //fixme parche to first run
        Settings setup_firstRun = managerModel.settings.getByNombre("SETUP_FIRST_RUN"); //---> BrioActivityMain
        if (setup_firstRun == null) {

            setup_firstRun = new Settings("SETUP_FIRST_RUN", "TRUE");
            managerModel.settings.save(setup_firstRun);
        }
    }

    /**
     * 1 Extraer assets y base de datos
     * 2 Terminos y condiciones
     * 2A Alta de comercio
     * 3A Login
     * 2B Recuperacion comercio
     * REINICIO APP
     * 3B Login
     * 3 Login
     * Actualizaciones legacy() y oneTime()
     * Session Manager abre sesion
     * Activity onLoginCompleted
     * Activity onSessionOpen
     */
    public void setup() {
        ((BrioActivityMain) context).splash.show();
        ((BrioActivityMain) context).splash.publish("Inicializando...");

        if (!isFirstRun()) {
            showLogin();
        } else {
            Utils.extractDBFromAssets((Activity) context, BrioGlobales.DB_NAME);
            Utils.extractGranelImagesFromAssets(context);
            Utils.extractFileFromAssets(context, "ticket_logo.png", Utils.getBrioInternalPath(context), false);

            ((BrioActivityMain) context).splash.dismiss();

            showTerminos();
        }
    }

    /**
     * Verificar si es la primer vez que se ejecuta la aplicación
     *
     * @return
     */
    private boolean isFirstRun() {
        Settings setup_firstRun = managerModel.settings.getByNombre("SETUP_FIRST_RUN"); //---> BrioActivityMain

        boolean value = setup_firstRun.getValor().equals("TRUE");

        DebugLog.log(getClass(), "BRIO_SETUP", "isFirstRun? " + value + " db:: " + setup_firstRun.getNombre() + ", " + setup_firstRun.getValor());

        return value; //todo: implementar la logica para la primera corrida
    }

    /**
     * notificacion de que se requiere activar brio la primera vez
     */
    private void showActivationError() {
        DebugLog.log(SetupService.this.getClass(), "BRIO_SETUP", "Notificacion de que no hay internet");
        BrioConfirmDialog bcd = new BrioConfirmDialog((AppCompatActivity) context, null, Utils.getString(R.string.setup_activation_error, context), null, new DialogListener() {
            @Override
            public void onAccept() {
                Utils.restartApp();
            }

            @Override
            public void onCancel() {
                System.exit(0);
            }
        });
        bcd.show();
    }

    /**
     * notificacion de que se requiere internet para la primera ejecucion
     */
    private void showInternetError() {
        DebugLog.log(SetupService.this.getClass(), "BRIO_SETUP", "Notificacion de que no hay internet");
        BrioConfirmDialog bcd = new BrioConfirmDialog((AppCompatActivity) context, null, Utils.getString(R.string.setup_internet_error, context), null, new DialogListener() {
            @Override
            public void onAccept() {
                Utils.restartApp();
            }

            @Override
            public void onCancel() {
                System.exit(0);
            }
        });
        bcd.show();
    }

    /**
     * Mostrar fragment de terminos y condiciones
     */
    private void showTerminos() {

        boolean aceptado = false;

        Settings settings = managerModel.settings.getByNombre("Terminos");
        if (Integer.parseInt(settings.getValor()) != 0) {
            aceptado = true;
        }

        if (aceptado) {
            showAltaComercio();
        } else {
            TerminosFragment terminosFragment = new TerminosFragment();
            terminosFragment.setListener(new DialogListener() {
                @Override
                public void onAccept() {
                    showAltaComercio();
                }

                @Override
                public void onCancel() {

                }
            });

            ((AppCompatActivity) context)
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentHolder, terminosFragment, "terminos")
                    .commit();
        }
    }

    /**
     * Mostrar fragment de alta de comercio
     */
    private void showAltaComercio() {
        Settings setup_altaComercio = managerModel.settings.getByNombre("SETUP_ALTA_COMERCIO");

        if (setup_altaComercio.getValor().equals("FALSE")) {
            CommerceFormManager commerceFormManager = CommerceFormManager.getInstance(context);
            commerceFormManager.setListener(new DialogListener() {
                @Override
                public void onAccept() {
                    managerModel.settings.update("SETUP_ALTA_COMERCIO", "TRUE");
                    ((BrioActivityMain) context).managerTeclado.closeKeyboard();

                    showLogin();
                }

                @Override
                public void onCancel() {

                }
            });

            CommerceFormFragment fgmt = commerceFormManager.createFragment(((AppCompatActivity) context).getSupportFragmentManager(), R.id.fragmentHolder);
            ((AppCompatActivity) context)
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentHolder, fgmt, "altacomercio")
                    .commit();
        } else {
            showLogin();
        }
    }

    /**
     * Mostrar fragment de registro de caja
     */
    private void showRegistroCaja() {

        Settings setup_altaCaja = managerModel.settings.getByNombre("SETUP_ALTA_CAJA");

        if (setup_altaCaja.getValor().equals("FALSE")) {
            RegistroCajaFragment fgmt = new RegistroCajaFragment();
            fgmt.setListener(new DialogListener() {
                @Override
                public void onAccept() {
                    managerModel.settings.update("SETUP_ALTA_CAJA", "TRUE");
                    showLogin();
                }

                @Override
                public void onCancel() {

                }
            });

            ((AppCompatActivity) context)
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentHolder, fgmt, "altacomercio")
                    .commit();
        } else {
            showLogin();
        }
    }

    /**
     * Mostrar fragment de login
     */
    private void showLogin() {

        try {


            //  mainActivity.getSupportFragmentManager().beginTransaction().remove(mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragmentHolder)).commit();
            Modificaciones modificaciones = new Modificaciones();
            modificaciones.legacyUpdate(mainActivity);
            modificaciones.oneTimeUpdate(mainActivity);
    
//            revisa las actualizaciones, creación de tables y actualizaciones
            BrioBusinessRules brioBusinessRules= BrioBusinessRules.getInstance (mainActivity,BrioGlobales.getAccess ());
            brioBusinessRules.actualizaciones ();

          //  mainActivity.splash.dismiss();

            loginFormManager = LoginFormManager.getInstance(mainActivity);
            loginFormManager.createFragment(mainActivity.getSupportFragmentManager(), R.id.fragmentHolder);

        } catch (Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + getClass() + "->showLogin" + e.getMessage());
        }
    }
}
