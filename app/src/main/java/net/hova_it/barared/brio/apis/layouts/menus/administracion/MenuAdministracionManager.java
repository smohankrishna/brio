package net.hova_it.barared.brio.apis.layouts.menus.administracion;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.admin.CatalogUsuarioFragment;
import net.hova_it.barared.brio.apis.admin.UserFormManager;
import net.hova_it.barared.brio.apis.drawer.CajaBalance;
import net.hova_it.barared.brio.apis.drawer.CajaCierre;
import net.hova_it.barared.brio.apis.drawer.CajaEntrada;
import net.hova_it.barared.brio.apis.drawer.CajaSalida;
import net.hova_it.barared.brio.apis.hw.billpocket.BuzonSalidaBillPocketService;
import net.hova_it.barared.brio.apis.layouts.menus.administracion_inventarios.MenuAdministracionInventariosManager;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuFragment;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuManager;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.mail.MailService;
import net.hova_it.barared.brio.apis.network.NetworkStatus;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.sync.SyncUploadTask;
import net.hova_it.barared.brio.apis.sync.entities.FTPData;
import net.hova_it.barared.brio.apis.update.DescargaApkTask;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAcceptDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;

import lat.brio.api.sincronizador.Sincronizador;

/**
 * Manager del menu de administración
 * <p>
 * Created by Herman Peralta on 09/03/2016.
 */
public class MenuAdministracionManager extends MenuManager implements BuzonSalidaBillPocketService.BuzonSalidaBillPocketListener {
    protected static MenuAdministracionManager daManager;

    private MenuAdministracionInventariosManager managerMenuAdministracionInventarios;
    private SessionManager managerSession;
    static Activity activity;
    public static BrioBaseActivity.Splash splash;

    public static MenuAdministracionManager getInstance(Activity context, BrioBaseActivity.Splash msplash) {
        if (daManager == null) {
            daManager = new MenuAdministracionManager(context);
        }
        activity = context;
        splash = msplash;

        return daManager;
    }

    private MenuAdministracionManager(Activity context) {
        super(context, "ADMINISTRACION", R.array.menu_administracion, false);

        managerMenuAdministracionInventarios = MenuAdministracionInventariosManager.getInstance(context);
        managerSession = SessionManager.getInstance(context);
    }

    @Override
    protected FragmentListButtonListener getMenulistener() {
        return menulistener;
    }

    @Override
    protected MenuFragment getMenuFragmentInstance() {
        return MenuAdministracionFragment.newInstance();
    }

    /**
     * Estas opciones vienen de res/xml/arrays
     */
    private FragmentListButtonListener menulistener = new FragmentListButtonListener() {
        @Override
        public void onListButtonClicked(View btn, Object value) {
            int position = (Integer) value;

            OptionMenuFragment opcion = null;

            switch (position) {
                case 0: //Entrada de dinero
                    opcion = new CajaEntrada();
                    break;

                case 1: //Salida de dinero
                    opcion = new CajaSalida();
                    break;

                case 2: //Balance de caja
                    opcion = new CajaBalance();
                    break;

                case 3: //Cierre de caja
                    opcion = new CajaCierre();
                    break;

                case 4: //Administracion de inventarios
                    onShowSubMenu(managerMenuAdministracionInventarios, R.id.fragmentHolder);
                    break;

                case 5: //Administracion de usuarios
                    CatalogUsuarioFragment catalogUsuarioFragment = new CatalogUsuarioFragment();
                    catalogUsuarioFragment.setFragmentManager(((AppCompatActivity) context).getSupportFragmentManager());
                    opcion = catalogUsuarioFragment;
                    break;

                case 6: //Nuevo usuario
                    UserFormManager userFormManager = UserFormManager.getInstance(context);
                    opcion = userFormManager.createFragment(((AppCompatActivity) context).getSupportFragmentManager(), R.id.fragmentHolder);
                    break;

                case 7: //Cierre de sesión
                    menu_cerrarSesion();
                    break;

                case 8: //actualizacion
                    // Antes de sincronizar ver si esta sincronizando en segundo plano
                    if (BrioBaseActivity.SINCRONIZANDO) {
                        // TODO showAllBanners un alert dialog indicando que la sincronizacion en segundo plano esta en curso
                        BrioAlertDialog bad = new BrioAlertDialog(((AppCompatActivity) context), "Sincronizacion", "La sincronización ya ha iniciado, por favor espere.");
                        bad.show();
                    } else {
                        menu_actualizar();
                    }
                    break;

                case 9: //enviar tickets pendientes
                    if (NetworkStatus.hasInternetAccess(context)) {

                        menu_enviaticketspendientes();
                    } else {
                        BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) context,
                                Utils.getString(R.string.menu_administracion_enviaticketspendientes, context),
                                Utils.getString(R.string.brio_nointernet, context));
                        bad.show();
                    }
                    break;
            }

            if (opcion != null) {
                onShowOptionFragment(opcion);
            }

            DebugLog.log(getClass(), "MENU", "Listener opcion " + position);
        }
    };

    /**
     * Boton de cierre de sesion, reinicia la aplicacion
     */
    private BrioConfirmDialog bcd;

    private void menu_cerrarSesion() {

        DialogListener listener = new DialogListener() {
            @Override
            public void onAccept() {
                if (bcd != null)
                    bcd.dismiss();
                Utils.restartApp();
            }

            @Override
            public void onCancel() {
            }
        };

        String title = Utils.getString(R.string.menu_administracion_cierresesion, context);
        String msj = Utils.getString(R.string.menu_administracion_cierresesion_mensaje, context);
        bcd = new BrioConfirmDialog((AppCompatActivity) context, title, msj, null, listener);
        bcd.show();
    }

    /**
     * Actualiza el buzon de salida de billPocket
     */
    private void menu_actualizar() {
//        managerSession.update();

        if (NetworkStatus.hasInternetAccess(context)) {
//            ((BrioActivityMain) context).Alertas(false);
            BuzonSalidaBillPocketService buzonSalidaBillPocketService = BuzonSalidaBillPocketService.getInstance(context);

            buzonSalidaBillPocketService.setBuzonSalidaBillPocketListener(this);

            buzonSalidaBillPocketService.sync();

        } else {
            BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) context,
                    Utils.getString(R.string.menu_administracion_actualizacion, context),
                    Utils.getString(R.string.brio_nointernet, context));
            bad.show();
        }
    }

    @Override
    public void onBuzonSalidaBillPocketFinished() {
        splash.show();
        splash.publish("Sincronizando datos...");

        FTPData ftpData = new FTPData();
        ftpData.setFilename("sync");
        Sincronizador syncManager = new Sincronizador(context);

        /*syncManager.fileUpload(*/
        syncManager.sync(ftpData, new SyncUploadTask.SyncUploadTaskListener() {
            @Override
            public void onSyncUploadTask(boolean success) {
                splash.dismiss();

                if (success) {
                    //Actualizar la app
                    new DescargaApkTask(context, splash).execute(false);
                } else {
                    BrioAcceptDialog bad = new BrioAcceptDialog((AppCompatActivity) context,
                            Utils.getString(R.string.menu_administracion_sincronizacion, context),
                            Utils.getString(R.string.sync_error, context),
                            Utils.getString(R.string.brio_aceptar, context), null);
                    bad.show();

                }
//                ((BrioActivityMain) context).Alertas(true);
            }
        });
    }


    private void menu_enviaticketspendientes() {
        MailService serviceMail = MailService.getInstance(context);
        serviceMail.getBuzonSalidaTicketsService().enviarPendientes(true, new MailService.BuzonSalidaTicketsListener() {
            @Override
            public void onBuzonSalidaTicketsFinished() {
                MediaUtils.hideSystemUI((AppCompatActivity) context);
            }
        });
    }
}

