package net.hova_it.barared.brio.apis.session;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.PermissionRequest;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Caja;
import net.hova_it.barared.brio.apis.models.entities.Comercio;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.models.entities.Usuario;
import net.hova_it.barared.brio.apis.sync.RestSyncService;
import net.hova_it.barared.brio.apis.update.DescargaApkTask;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.alarmas.Alarm;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.utils.info.InfoPojo;
import net.hova_it.barared.brio.apis.utils.preferencias.Preferencias;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;
import lat.brio.core.serviciosWeb.RequestService;

import static net.hova_it.barared.brio.apis.session.LoginFormFragment.LoginFormFragmentListener;

/**
 * Manager de componentes y consulta de informacion para permitir login
 * Created by Alejandro Gomez on 28/12/2015.
 */
public class LoginFormManager implements LoginFormFragmentListener {
    private final String TAG = "LoginFormManager";
    private final String FRAGMENT_TAG = "BRIO_SESSION_LOGIN";
    private final static int sessionTimeout = 180; //minutos
    
    private Context context;
    private FragmentManager fragmentManager;
    private LoginFormFragment loginFormFragment;
    
    private ModelManager modelManager;
    
    private EditText editLoginUsuario;
    private EditText editLoginPassword;
    private EditText editResetRespuesta;
    private EditText editResetPassword;
    private EditText editPass1;
    private EditText editPass2;
    
    private TextView textLoginError;
    private TextView textResetResult;
    private TextView textPreguntas;
    private TextView textVersion;
    private TextView textChangeAdminPass;
    
    private String textLoginUsuario;
    private String textLoginPassword;
    private String textResetRespuesta;
    private String textResetPassword;
    
    
    private LinearLayout loginLayout;
    private LinearLayout resetLayout;
    
    private View panelChangeAdminPass;
    
    private int usuarioLength;
    private int passwordLength;
    private int idComercio;
    private Usuario usuario;
    
    private ImageButton btn_rollback_app;
    
    private Fragment parent;
    
    //private UpdateService updateService;
    
    private static LoginFormManager mInstance = null;
    boolean Acceso;
    
    /**
     * Obtener instancia del manager, ya que la clase es singleton
     * @param context
     *
     * @return
     */
    public static LoginFormManager getInstance(Context context) {
        if (mInstance==null) {
            mInstance = new LoginFormManager(context, null);
        } return mInstance;
    }
    
    /**
     * Obtener instancia del manager, ya que la clase es singleton
     * @param context
     * @param parent
     *
     * @return
     */
    public static LoginFormManager getInstance(Context context, Fragment parent) {
        if (mInstance==null) {
            mInstance = new LoginFormManager(context, parent);
        } return mInstance;
    }
    
    /**
     * Obtener instancia del manager, ya que la clase es singleton
     * @param context
     * @param parent
     */
    public LoginFormManager(Context context, Fragment parent) {
        this.context = context; this.parent = parent;
        this.loginFormFragment = LoginFormFragment.newInstance();
        this.modelManager = new ModelManager(context);
        //this.updateService = new UpdateService(context);
    }
    
    public void createFragment(FragmentManager fragmentManager, int container) {
        try {
            this.fragmentManager = fragmentManager; Log.d(TAG, "Creating Login Fragment...");
            
            
            loginFormFragment.setLoginFormManager(this);
            this.fragmentManager.beginTransaction()
             .replace(container, loginFormFragment, loginFormFragment.getTag()).commit();
        } catch(Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + getClass() + "->createFragment" + e.getMessage());
        }
    }
    
    
    public void removeFragment() {
        try {
            this.fragmentManager.beginTransaction().remove(loginFormFragment).commit();
            
            if (parent==null) {
                ((LoginFormManagerListener) context).onLoginCompleted();
            }
            else {
                ((LoginFormManagerListener) parent).onLoginCompleted();
            }
        } catch(Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + getClass() + "->removeFragment" + e.getMessage());
        }
    }
    
    /**
     * Asignacion de informacion a componentes graficos del fragment
     */
    @Override
    public void onFragmentCreated() {
        Log.d(TAG, "Login Fragment Ready..."); Map fields = loginFormFragment.getFields();
        usuarioLength = 3; passwordLength = 3;
        
        editLoginUsuario = (EditText) fields.get("editLoginUsuario");
        editLoginPassword = (EditText) fields.get("editLoginPassword");
        
        //Metodo para login con teclado nativo
        enterOnKeyboardLogin();
        
        textLoginError = (TextView) fields.get("textLoginError");
        textVersion = (TextView) fields.get("textVersion");
        
        textPreguntas = (TextView) fields.get("textPreguntas");
        editResetRespuesta = (EditText) fields.get("editResetRespuesta");
        editResetPassword = (EditText) fields.get("editResetPassword");
        textResetResult = (TextView) fields.get("textResetResult");
        
        loginLayout = (LinearLayout) fields.get("loginLayout");
        resetLayout = (LinearLayout) fields.get("resetLayout");
        panelChangeAdminPass = (View) fields.get("panelChangeAdminPass");
        
        editPass1 = (EditText) fields.get("editPass1");
        editPass2 = (EditText) fields.get("editPass2");
        textChangeAdminPass = (TextView) fields.get("textChangeAdminPass");
        btn_rollback_app = (ImageButton) fields.get("btn_rollback_app");
        
        String version = Utils.getString(R.string.form_login_version, context) + " " + Utils.getAppData(context).versionName; //updateService.getAppData().versionName
        textVersion.setText(version);
        
        try {
            Usuario uAdmin = modelManager.usuarios.getByUsuario("admin");
            if (uAdmin.getPassword().equals(Utils.toSHA256("admin"))) {
                textChangeAdminPass.setVisibility(View.VISIBLE);
            }
            else {
                textChangeAdminPass.setVisibility(View.GONE);
            }
        } catch(Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + getClass() + "->onFragmentCreated" + e.getMessage());
            
        }
    }
    
    /**
     * Metodo agregado, se utiliza para que al presionar enter en el teclado haga la funcion de login.
     * Se manda a llamar en la creacion del fragment
     * agregado por Adolfo Chavez - 09/junio/2017
     */
    private void enterOnKeyboardLogin() {
        editLoginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId==EditorInfo.IME_ACTION_DONE) {
                    Log.i(TAG, "Validando Login."); onButtonLoginClick();
                    editLoginPassword.setText(null);
                } return false;
            }
        });
    }
    
    
    @Override
    public void onButtonResetClick() {
        if (resetValidation()) {
            reset();
        }
    }
    
    @Override
    public void onButtonUpdateClick() {
        // TODO
        /*if(NetworkStatus.isConnected(context)) {
            UpdateService updateService = new UpdateService(context);
            updateService.update();
            updateService.setUpdateServiceListener(new UpdateService.UpdateServiceListener() {
                @Override
                public void onInstallIntent(Boolean intentResult, int status) {
                    if (intentResult) {
                        switch (status){
                            case UpdateService.UPDATE_OK_UPTODATE:
                                Log.d(TAG, "Application Up To Date!");
                                AlertDialog infoDialog = Utils.alertOK(context, "Actualización", "La aplicación se encuentra actualizada", "Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO
                                    }
                                });
                                infoDialog.show();
                                break;
                            case UpdateService.UPDATE_OK_INSTALL:
                                Log.d(TAG, "Application Update Installed!");
                                AlertDialog infoDialog2 = Utils.alertOK(context, "Actualización", "La aplicación se ha actualizado", "Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO
                                    }
                                });
                                infoDialog2.show();
                                break;
                        }
                    } else {
                        switch (status){
                            case UpdateService.UPDATE_ERR_DOWNLOAD:
                                Log.d(TAG, "ERROR Downloading Application Update!");
                                break;
                            case UpdateService.UPDATE_ERR_INSTALL:
                                Log.d(TAG, "ERROR Installing Application Update!");
                                break;
                        }
                    }
                }
            });
        }*/
    }
    
    @Override
    public void onButtonBackClick() {
        resetLayout.setVisibility(LinearLayout.GONE);
        loginLayout.setVisibility(LinearLayout.VISIBLE);
    }
    
    /**
     * Validacion de datos y despliegue de mensajes de error
     */
    @Override
    public void onButtonForgetClick() {
        getStrings(); textLoginError.setText(""); textLoginError.setError(null);
        
        boolean validation = true;
        
        if (textLoginUsuario.length() < usuarioLength) {
            textLoginError.setError("");
            textLoginError.setText(Utils.getString(R.string.form_login_reset_error_user, context));
            validation = false;
        }
        
        usuario = modelManager.usuarios.getByUsuario(textLoginUsuario); if (usuario==null) {
            textLoginError.setError("");
            textLoginError.setText(Utils.getString(R.string.form_login_user_error, context));
            validation = false;
        }
        
        if (validation) {
            textResetResult.setText(""); textResetResult.setError(null);
            
            editLoginPassword.setText(""); editResetRespuesta.setText("");
            editResetPassword.setText("");
            
            loginLayout.setVisibility(LinearLayout.GONE);
            resetLayout.setVisibility(LinearLayout.VISIBLE);
            
            String pregunta = modelManager.preguntas.getByIdPregunta(usuario.getPregunta1()).getPregunta();
            textPreguntas.setText(pregunta);
            
            
            // fillQuestionSpinner(usuario);
        }
    }
    
    
    @Override
    public void onBtnAdminPassBackClick() {
        panelChangeAdminPass.setVisibility(View.GONE); loginLayout.setVisibility(View.VISIBLE);
        
        editPass1.setText(""); editPass2.setText("");
        
        editLoginUsuario.setText(""); editLoginPassword.setText("");
    }
    
    @Override
    public void onBtnAdminPassGoClick() {
        usuario = modelManager.usuarios.getByUsuario("admin"); loginLayout.setVisibility(View.GONE);
        panelChangeAdminPass.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onBtnAdminPassAcceptClick() {
        String pass1 = editPass1.getText().toString();
        String pass2 = editPass2.getText().toString(); String msg; if (pass1.equals(pass2)) {
            if (pass1.equals("admin")) {
                msg = "La contraseña no puede ser \"admin\".";
                
                editPass1.setText(""); editPass2.setText("");
            }
            else
                if (pass1.length() < passwordLength) {
                    msg = "La longitud mínima de la contraseña es " + passwordLength + ".";
                    
                    editPass1.setText(""); editPass2.setText("");
                }
                else {
                    usuario.setPassword(Utils.toSHA256(pass1)); modelManager.usuarios.save(usuario);
                    onBtnAdminPassBackClick();
                    
                    msg = "La contraseña de la cuenta de administrador fue cambiada correctamente.";
                    textChangeAdminPass.setVisibility(View.GONE);
                }
        }
        else {
            msg = "La contraseña no coincide,\npor favor ingrésala nuevamente.";
            
            editPass1.setText(""); editPass2.setText("");
        }
        
        BrioAlertDialog bad = new BrioAlertDialog((BrioActivityMain) context, "Cambio de contraseña de administrador", msg);
        bad.show();
    }
    
    
    BrioConfirmDialog alertYesNo = null;
    
    /**
     * Interfaz encargada de realizar Rollback cuando la aplicacion es una versión Beta y se requiere
     * regresar a la versión anterior de producción
     */
    @Override
    public void onBtnRollbacVersion() {
        
        
        DialogListener listener = new DialogListener() {
            @Override
            public void onAccept() {
                alertYesNo.dismiss();
                
                ((BrioActivityMain) context).splash.show();
                ((BrioActivityMain) context).splash.publish(Utils.getString(R.string.dialog_version_beta_rollback, context));
                new DescargaApkTask(context, ((BrioActivityMain) context).splash).execute(true);
            }
            
            @Override
            public void onCancel() {
                alertYesNo.dismiss();
            }
        };
        
        String titulo = "Recuperar versión";
        
        String mensaje = "\n¿Desea regresar a la versión anterior?\nUna vez confirmada la operación no podrá cancelar el proceso.\n";
        alertYesNo = new BrioConfirmDialog((AppCompatActivity) context, titulo, mensaje, listener, android.support.design.R.id.center);
        alertYesNo.show();
    }
    
    /**
     * Obtiene la informacion ingresada por el usuario en la UI para su validacion
     */
    private void getStrings() {
        textLoginUsuario = editLoginUsuario.getText().toString();
        textLoginPassword = editLoginPassword.getText().toString();
        
        textResetRespuesta = editResetRespuesta.getText().toString();
        textResetPassword = editResetPassword.getText().toString();
    }
    
    
    /**
     * Validacion de datos y despliegue de mensajes de error
     */
    private Settings setting;
    
    private boolean loginValidation() {
        
        if (setting==null) {
            setting = modelManager.settings.getByNombre("ID_COMERCIO");
        }
        
        idComercio = 0;
        
        try {
            idComercio = Integer.parseInt(setting.getValor());
        } catch(Exception e) {
            idComercio = 0;
        }
        
        String[] Tkns = Utils.brioToken(context, idComercio);
        // Auto login en caso de depuracion
        if (BrioBaseActivity.DEBUG_SHOW_TOASTS /*|| idComercio == 10135*/) {
            editLoginUsuario.setText("admin"); editLoginPassword.setText(Tkns[0]);
        }
        
        getStrings(); textLoginError.setText(""); textLoginError.setError(null);
        
        if (idComercio < 10000||textLoginUsuario.length() < usuarioLength||textLoginPassword.length() < passwordLength) {
            textLoginError.setError("Usuario/Clave no validos");
            textLoginError.setText(Utils.getString(R.string.form_login_user_error, context));
            return false;
        }
        
        // Valida y carga Usuario
        usuario = modelManager.usuarios.getByUsuario(textLoginUsuario); if (usuario==null) {
            textLoginError.setError("");
            textLoginError.setText(Utils.getString(R.string.form_login_user_error, context));
            return false;
        }
        
        if (! usuario.getActivo()) {
            textLoginError.setError("");
            textLoginError.setText(Utils.getString(R.string.form_login_user_error, context));
            return false;
        }
        
        if (! validaCambioPasswordAdmin()) {
            return false;
        }
        
        if (! usuario.getPassword().equals(Utils.toSHA256(textLoginPassword))) {
            // String[] Tkns = Utils.brioToken(context, idComercio);
            
            if (textLoginPassword.toUpperCase().equals(Tkns[0].toUpperCase())) {
                ((BrioActivityMain) context).managerSession.saveString("clave", "TokenA");
                return true;
            }
            else
                if (textLoginPassword.toUpperCase().equals(Tkns[1].toUpperCase())) {
                    ((BrioActivityMain) context).managerSession.saveString("clave", "TokenB");
                    return true;
                }
                else
                    if (textLoginPassword.toUpperCase().equals(Tkns[2].toUpperCase())) {
                        ((BrioActivityMain) context).managerSession.saveString("clave", "TokenC");
                        return true;
                    }
                    else {
                        textLoginError.setError("");
                        textLoginError.setText(Utils.getString(R.string.form_login_password_error, context));
                        ((BrioActivityMain) context).managerSession.saveString("clave", "Invalida");
                        return false;
                    }
        } ((BrioActivityMain) context).managerSession.saveString("clave", "Valida"); return true;
    }
    
    @Override
    public void onButtonLoginClick() {
        String[] Tkns = Utils.brioToken(context, idComercio);
    
    
        Acceso = loginValidation ();
        try {
            if (textLoginPassword.toUpperCase ().equals (Tkns[0].toUpperCase ())) {
                ((BrioActivityMain) context).managerSession.saveString ("clave", "TokenA");
            } else
                if (textLoginPassword.toUpperCase ().equals (Tkns[1].toUpperCase ())) {
                    ((BrioActivityMain) context).managerSession.saveString ("clave", "TokenB");
                } else
                    if (textLoginPassword.toUpperCase ().equals (Tkns[2].toUpperCase ())) {
                        ((BrioActivityMain) context).managerSession.saveString ("clave", "TokenC");
                    } else
                        if (usuario != null && usuario.getPassword ().equals (Utils.toSHA256 (textLoginPassword))) {
                            ((BrioActivityMain) context).managerSession.saveString ("clave", "Valida");
                        } else {
                            ((BrioActivityMain) context).managerSession.saveString ("clave", "Invalida(" + textLoginPassword + ")");
                        }
        
        
            boolean ticketsVencidos = validaTicketsVencidos ();
            if (ticketsVencidos) {
                modelManager.itemsTicket.deleteOutdatedTicketsItems ();
                modelManager.ticketTipoPago.deleteOutdatedTicketTipoPago ();
                modelManager.tickets.deleteOutdatedTickets ();
            }
        
            ((BrioActivityMain) context).managerSession.saveString ("acceso", (Acceso ? "1" : "0"));
        
            if (Acceso) {
                Preferencias.i.setIdComercio (idComercio);
                ((BrioActivityMain) context).splash.show ();
                ((BrioActivityMain) context).splash.publish (Utils.getString (R.string.brio_loading_msg, context));
                consultaSettingsWs ();
            }
        } catch(Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + TAG + "->onButtonLoginClick" + e.getMessage());
            if (Acceso) {
                login();
            }
        }
    }
    
    private void consultaSettingsWs() {
        
        try {
            InfoPojo Info = Utils.getInfoPojo(context, ((BrioActivityMain) context).managerSession);
            
            Info.setIdComercio(idComercio); Info.setUsuario(textLoginUsuario);
            if ("Archos 133 Oxygen".equals(Info.getAndroidModel())) {
                Info.setAndroidModel("1016");//Temporal bypass.
            }
    
            Settings sttRoll=modelManager.settings.getByNombre(BrioGlobales.KEY_BRIO_VER_ROLLBACK);
            String Rollback =(sttRoll==null)?"0": sttRoll.getValor();
            Info.setBrioVerRollback(Rollback);
    
            String URL;
            URL = "http://" + BrioGlobales.URL_WEB_BRIO + "/?Brio";
            //            TODO quitar URL pruebas
            //            URL = "http://webqa.brio.lat/?Brio";
            RequestService wsSettings = new RequestService((BrioActivityMain) context, "Brio", URL);
            wsSettings.setBodyParams(new JSONObject(Utils.toJSON(Info)));
            
            wsSettings.executeJson(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject Respuesta) {
                    try {
    
                        String PrefInstall = Preferencias.i.getBrioVerInstalled();
                        Settings strRoll = modelManager.settings.getByNombre(BrioGlobales.KEY_BRIO_VER_ROLLBACK);
                        if (strRoll!=null&&strRoll.getValor().equals(BrioGlobales.KEY_ROLLBACK_TRUE)) {
                            modelManager.settings.update(BrioGlobales.KEY_BRIO_VER_ROLLBACK, BrioGlobales.KEY_ROLLBACK_FALSE);
//                            Preferencias.i.setContadorRollbackReset();
                            Preferencias.i.setShowDialogBeta(true);
//                            Preferencias.i.setContadorRollbackPlus();
                        }
    
                        if (Preferencias.i.getContadorRollback() > 1&&PrefInstall.equals("")) {
                        
                        }
                        procesaRespuesta(Respuesta);
                        
                    } catch(Exception e) {
                        e.printStackTrace();
                        BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + TAG + "->consultaSettingsWs.onResponse" + e.getMessage());
                    }
                    
                    
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    e.printStackTrace();
                    BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + TAG + "->consultaSettingsWs.onErrorResponse" + e.getMessage());
                    
                    if (Acceso) {
                        login();
                    }
                }
            });
        } catch(JSONException e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + TAG + "->consultaSettingsWs" + e.getMessage());
        }
        
    }
    
    private void procesaRespuesta(JSONObject Respuesta) {
        
        try {
            if (Respuesta!=null) {
                Respuesta = Respuesta.getJSONObject("Brio"); if (Respuesta.has("Ajustes")) {
                    JSONObject ajustes = Respuesta.getJSONObject("Ajustes");
                    if( !ajustes.getString("APP_VERSION").equals ("null")){
                        String vMayor = ajustes.getString(BrioGlobales.KEY_BRIO_VER_MAYOR);
                        String vMenor = ajustes.getString(BrioGlobales.KEY_BRIO_VER_MENOR);
                        if (ajustes.has(BrioGlobales.KEY_BRIO_VER_PREV)&&ajustes.has(BrioGlobales.KEY_BRIO_VER_ISBETA)) {
        
                            String wsVerPrev = ajustes.getString(BrioGlobales.KEY_BRIO_VER_PREV);
                            String wsVerB = ajustes.getString(BrioGlobales.KEY_BRIO_VER_ISBETA);
                            String wsVerApp = ajustes.getString(BrioGlobales.KEY_BRIO_VER_ERROR);
                            String wsVerRoll = ajustes.has(BrioGlobales.KEY_BRIO_VER_ROLLBACK) ? ajustes.getString(BrioGlobales.KEY_BRIO_VER_ROLLBACK) : BrioGlobales.KEY_ROLLBACK_DEFAULT;
                            String VersionPrev = vMayor + "-" + vMenor + "-" + wsVerPrev;
        
                            if (! wsVerRoll.equals(BrioGlobales.KEY_ROLLBACK_DEFAULT)) {
                                modelManager.settings.update(BrioGlobales.KEY_BRIO_VER_ROLLBACK, wsVerRoll);
                            }
        
                            boolean wsVerBeta = wsVerB.equals("1");
                            Settings setRoll=modelManager.settings.getByNombre(BrioGlobales.KEY_BRIO_VER_ROLLBACK);
                            boolean isRollback=false;
                            if(setRoll!=null&&setRoll.getValor ()!=null)
                                isRollback = setRoll.getValor().equals(BrioGlobales.KEY_ROLLBACK_FALSE);
                            int codeVersionApp = Integer.parseInt(Utils.getAppData(context).versionName.split("\\.")[2]);
        
        
                            Preferencias.i.setBrioVerPrev(wsVerPrev);
                            Preferencias.i.setBrioVerPrevName(VersionPrev);//utilizamos la version a descargar
                            Preferencias.i.setBrioVerExistUpdate(false);
        
                            if ((BrioGlobales.VERSION_BETA!=wsVerBeta&&isRollback)||(! wsVerBeta&&Integer.parseInt(wsVerApp) > codeVersionApp)) {
                                Preferencias.i.setBrioVerExistUpdate(true);//Indica que existe nueva actualizacion
                                Preferencias.i.setBrioVerIsBeta(wsVerBeta);
                            }
                        }
                        for (int a = 0; a < ajustes.length(); a++) {
                            String nombre = ajustes.names().getString(a);
                            String valor = ajustes.getString(nombre);
                            //No se debe actualizar el registro rollback, solo se actualiza cuando se hace
                            //Rollback o se instala una nueva version
                            AgregarAjuste(nombre, valor);
                        }
                    }
                }
            }
            ((BrioActivityMain) context).splash.dismiss ();
            if (Acceso) {
                login ();
            }
        } catch(Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + TAG + "->procesaRespuesta" + e.getMessage());
        }
    }
    
    private void AgregarAjuste(String Nombre, String Valor) {
        Settings ajuste = modelManager.settings.getByNombre(Nombre);
        
        if (ajuste==null) {
            ajuste = new Settings(); ajuste.setNombre(Nombre); ajuste.setValor(Valor);
            modelManager.settings.save(ajuste);
        }
        else {
            ajuste.setValor(Valor); modelManager.settings.save(ajuste);
        }
    }
    
    private boolean validaTicketsVencidos() {
        
        long count = modelManager.tickets.getCountOutdatedTickets(); if (count <= 0) {
            return false;
        }
        
        Settings ticketsVencidos = modelManager.settings.getByNombre("TICKETS_VENCIDOS");
        if (ticketsVencidos==null) {
            ticketsVencidos = new Settings(); ticketsVencidos.setNombre("TICKETS_VENCIDOS");
            ticketsVencidos.setValor(String.valueOf(count));
            modelManager.settings.save(ticketsVencidos);
        }
        else {
            count = count + Long.parseLong(ticketsVencidos.getValor());
            modelManager.settings.update("TICKETS_VENCIDOS", String.valueOf(count));
        } return true;
    }
    
    private boolean validaCambioPasswordAdmin() {
        final int count_limit = 3;
        //Usuario uAdmin = modelManager.usuarios.getByUsuario("admin");
        if (/*uAdmin.getPassword()*/usuario.getUsuario().equals("admin")&&usuario.getPassword().equals(Utils.toSHA256("admin"))) {
            
            Settings adminPwd = modelManager.settings.getByNombre("PWD_ADMIN_CHANGE");
            if (adminPwd==null) {
                adminPwd = new Settings(); adminPwd.setNombre("PWD_ADMIN_CHANGE");
                adminPwd.setValor("0");
                
                modelManager.settings.save(adminPwd);
                
                adminPwd = modelManager.settings.getByNombre("PWD_ADMIN_CHANGE");
            }
            
            int count = Integer.parseInt(adminPwd.getValor()); count++;
            
            modelManager.settings.update("PWD_ADMIN_CHANGE", String.valueOf(count));
            
            String msg = count_limit - count > 0 ? Utils.getString(R.string.form_login_reset_admin_msg1, context).replace("{0}", String.valueOf((count_limit - count))) : Utils.getString(R.string.form_login_reset_admin_msg2, context);
            
            BrioAlertDialog bad = new BrioAlertDialog((BrioActivityMain) context, "Brío", msg);
            bad.show();
            
            if (count >= count_limit) {
                onBtnAdminPassGoClick(); return false;
            }
            else {
                return true;
            }
        }
        
        return true;
    }
    
    /**
     * Validacion de pregunta de seguridad en caso de olvidar contraseña
     * @return
     */
    private boolean resetValidation() {
        getStrings(); textResetResult.setText(""); textResetResult.setError(null);
        
        if (! usuario.getRespuesta1().toLowerCase().equals(textResetRespuesta.toLowerCase())) {
            textResetResult.setError("");
            textResetResult.setText(Utils.getString(R.string.form_login_reset_error_response, context));
            return false;
        } if (textResetPassword.length() < passwordLength) {
            textResetResult.setError("");
            textResetResult.setText(Utils.getString(R.string.form_login_reset_password, context));
            return false;
        }
        
        return true;
    }
    
    /**
     * Mapeo y asignacion de informacion al SessionManager y registro en la DB de la
     * informacion del usuario que ha iniciado sesion.
     */
    private void login() {
        
        
        try {
            getStrings();
            // DUMMY DATA TO EDIT ***   //TODO SE QUEDA HASTA LA SIGUIENTE VERSION SE QUITA CUANDO YA TENGAMOS BASE
            Comercio comercio = modelManager.comercios.getByIdComercio(Integer.valueOf(modelManager.settings.getByNombre("ID_COMERCIO").getValor()));//createDummy(Comercio.class); // TODO modelManager.comercios.getByIdComercio(usuario.getIdComercio());
            //comercio.setIdComercio(1);
            //comercio.setIdGrupo(1);
            // END DUMMY ***
            Caja caja = modelManager.cajas.getByIdCaja(Integer.valueOf(modelManager.settings.getByNombre("ID_CAJA").getValor()));
            
            Map<String, Object> sessionData = new HashMap<>();
        /* inicializacion, tambien poner las variables en SessionManager.open() o sessionManager.save...*/
            sessionData.put("fechaInicio", Utils.getCurrentTimestamp());
            sessionData.put("idComercio", comercio.getIdComercio());
            sessionData.put("descripcionComercio", comercio.getDescComercio());
            sessionData.put("direccionLegal", comercio.getDireccionLegal());
            sessionData.put("codigoPostalLegal", comercio.getCodigoPostalLegal());
            sessionData.put("idCaja", caja.getIdCaja());
            sessionData.put("idUsuario", usuario.getIdUsuario());
            sessionData.put("usuario", usuario.getUsuario());
            sessionData.put("idPerfil", usuario.getIdPerfil());
            
            SessionManager sessionManager = SessionManager.getInstance(context);
            sessionManager.open(sessionData, sessionTimeout);
    
            Alarm.generaAlarmaCerrarSesion (context);
    
            ((BrioActivityMain) context).setDispatchKey(true);
            this.removeFragment();
            
            
        } catch(Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaHoraFormat() + TAG + "->login" + e.getMessage());
        }
        
        
    }
    
    /**
     * Validacion de reseteo de contraseña y registro en la DB en caso de validacion exitosa
     */
    private void reset() {
        getStrings(); usuario.setPassword(Utils.toSHA256(textResetPassword));
        int updated = modelManager.usuarios.save(usuario).intValue(); if (updated > 0) {
            //Log.d(TAG, "Password Reset Saved!");
            resetLayout.setVisibility(LinearLayout.GONE);
            loginLayout.setVisibility(LinearLayout.VISIBLE);
        }
        //Log.d(TAG, "Reset Password Error on Save");
    }
    
    
    public interface LoginFormManagerListener {
        void onLoginCompleted();
    }

}
