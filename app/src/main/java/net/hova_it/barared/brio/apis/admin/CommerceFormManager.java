package net.hova_it.barared.brio.apis.admin;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Caja;
import net.hova_it.barared.brio.apis.models.entities.Comercio;
import net.hova_it.barared.brio.apis.network.NetworkStatus;
import net.hova_it.barared.brio.apis.sync.ServerService;
import net.hova_it.barared.brio.apis.update.DownloadDBTask;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAcceptDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.views.BrioButton2;
import net.hova_it.barared.brio.apis.views.BrioEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lat.brio.core.BrioGlobales;

/**
 * Created by Mauricio Ceron on 17/03/2016.
 * Se implememtan las interfaces de CommerceFormFragment, como son dar de alta comercio  y recuperar
 */
public class CommerceFormManager implements CommerceFormFragment.CommerceFormManagerListener {
    private final String TAG = "BRIO_COMMERCE";

    private Context context;
    private FragmentManager fragmentManager;
    private CommerceFormFragment commerceFormManager;
    private BrioConfirmDialog dialogConfirmacion;
    private BrioAlertDialog alertDialog;
    private BrioAcceptDialog acceptDialog;
    private String mensaje;
    //private boolean commerceExist = false;
    private ModelManager modelManager;
    private ScrollView scrollCommerceForm;
    private static String[] estados;

    private DialogListener listener;
    private RelativeLayout loading;

    private EditText editDescripcion;
    private BrioEditText
            //editDescripcion,
            editNombreLegal,
            editRfc,
            editDireccionLegal,
            editExteriorLegal,
            editInteriorLegal,
            editColoniaLegal,
            editMunicipioLegal,
            editCodigoPostalLegal,
            editDireccionFisica,
            editExteriorFisico,
            editInteriorFisico,
            editColoniaFisica,
            editMunicipioFisico,
            editCodigoPostalFisico,
            editIdComercio,
            editTokenComercioRecuperar,
            editTokenComercioAlta;

    private String
            textDescripcion,
            textNombreLegal,
            textRfc,
            textDireccionLegal,
            textExteriorLegal,
            textInteriorLegal,
            textColoniaLegal,
            textMunicipioLegal,
            textCodigoPostalLegal,
            textDireccionFisica,
            textExteriorFisico,
            textInteriorFisico,
            textColoniaFisica,
            textMunicipioFisico,
            textCodigoPostalFisico;

    private int
            descripcionLength,
            nombreLength,
            minLength;

    private Spinner
            spinnerEstadoLegal,
            spinnerPaisLegal,
            spinnerEstadoFisico,
            spinnerPaisFisico;

    private Fragment parent;

    private BrioButton2
            opcAltaComercio,
            opcRecuperarComercio;

    private View
            panelPrincipal,
            panelAltaComercio,
            panelRecuperarComercio,
            buttonRecuperar,
            buttonBack,
            buttonBack2;

    private TextView
            txtTitle,
            txtSubtitle;

    private int
        idComercio,
        idCaja;

    private static CommerceFormManager mInstance = null;

    static {
        estados = new String[] {
                "Aguascalientes", "Baja California", "Baja California Sur", "Campeche", "Coahuila de Zaragoza", "Colima",
                "Chiapas", "Chihuahua", "Ciudad de México", "Durango", "Guanajuato", "Guerrero", "Hidalgo", "Jalisco",
                "México", "Michoacán de Ocampo", "Morelos", "Nayarit", "Nuevo León", "Oaxaca", "Puebla", "Querétaro de Arteaga",
                "Quintana Roo", "San Luis Potosí", "Sinaloa", "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala",
                "Veracruz de Ignacio de la Llave", "Yucatán", "Zacatecas"
        };
    }

    public static CommerceFormManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CommerceFormManager(context, null);
        }
        return mInstance;
    }

    public static CommerceFormManager getInstance(Context context, Fragment parent) {
        if (mInstance == null) {
            mInstance = new CommerceFormManager(context, parent);
        }
        return mInstance;
    }

    private CommerceFormManager(Context context, Fragment parent) {
        this.context = context;
        this.parent = parent;
        this.commerceFormManager = CommerceFormFragment.newInstance();
        this.modelManager = new ModelManager(context);
    }

    public CommerceFormFragment createFragment(FragmentManager fragmentManager, int container) {
        Log.d(TAG, "Creating Users Fragment...");
        this.fragmentManager = fragmentManager;

        commerceFormManager.setCommerceFormManager(this);

        return commerceFormManager;
    }

    @Override
    public void onFragmentCreated() {
        Log.d(TAG, "Users Fragment Ready...");
        mapFields();
        fillEntidadesSpinner();
    }

    /**
     * mostrar la opción de recuperar comercio
     */
    @Override
    public void onBtnOptionRecuperarComercioClick() {
        panelPrincipal.setVisibility(View.GONE);
        panelRecuperarComercio.setVisibility(View.VISIBLE);

        // Recuperacion automatica de comercio
        if(BrioBaseActivity.DEBUG_SHOW_TOASTS) {

            editIdComercio.setText("11557");
            //onButtonRecuperarComercioClick();
        }

        txtTitle.setText(Utils.getString(R.string.form_commerce_title_recuperar, context));
        txtSubtitle.setText(Utils.getString(R.string.form_commerce__subtitle, context));
    }

    /**
     * mostrar la opción de alta de comercio
     */
    @Override
    public void onBtnOptionAltaComercioClick() {
        panelPrincipal.setVisibility(View.GONE);
        panelAltaComercio.setVisibility(View.VISIBLE);

        txtTitle.setText(Utils.getString(R.string.form_commerce_title_alta, context));
        txtSubtitle.setText(Utils.getString(R.string.form_commerce__subtitle, context));
    }

    /**
     * regresar de opción de recuperar comercio
     */
    @Override
    public void onButtonRecuperarComercioBackClick() {
        panelAltaComercio.setVisibility(View.GONE);
        panelPrincipal.setVisibility(View.VISIBLE);

        txtTitle.setText(Utils.getString(R.string.form_commerce_title_alta, context));
        txtSubtitle.setText(Utils.getString(R.string.brio_seleccion, context));
    }

    /**
     * regresar de opción de alta de comercio
     */
    @Override
    public void onButtonAltaComercioBackClick() {
        panelAltaComercio.setVisibility(View.GONE);
        panelPrincipal.setVisibility(View.VISIBLE);

        txtTitle.setText(Utils.getString(R.string.form_commerce_title_alta, context));
        txtSubtitle.setText(Utils.getString(R.string.brio_seleccion, context));
    }

    /**
     * Recupera el comercio, checando que tenga internet y validando el numero de comercio
     */
    @Override
    public void onButtonRecuperarComercioClick() {

        idComercio = editIdComercio.getText().toString().length() == 0 ? 0 : Integer.parseInt(editIdComercio.getText().toString());
        idCaja = 1; //Fixme no hardcodear

        int msg = 0;
        BrioAlertDialog bad;

        /**Verifica si esta conectado a internet  */
        if (!NetworkStatus.hasInternetAccess(context)) {
            msg = R.string.brio_nointernet;

            bad = new BrioAlertDialog((AppCompatActivity)context,
                    Utils.getString(R.string.form_commerce_recuperacion_title, context),
                    Utils.getString(msg, context));
            bad.show();

            return;
        }

        if (idComercio < 10000) {
            msg = R.string.form_commerce_id_invalido;

            bad = new BrioAlertDialog((AppCompatActivity)context,
                    Utils.getString(R.string.form_commerce_recuperacion_title, context),
                    Utils.getString(msg, context));
            bad.show();

            return;
        }
        downloadDB();



        /*
        if(editTokenComercioRecuperar.length() < 5) {
            msg = R.string.form_commerce_token_invalido;

            bad = new BrioAlertDialog((AppCompatActivity)context,
                    Utils.getString(R.string.form_commerce_recuperacion_title, context),
                    Utils.getString(msg, context));
            bad.show();
        } else {
            //todo: verificar con wservice o algo
            downloadDB();
        }
        */
    }

    /**
     * Descargar la base de datos del FTP
     *
     */
    private void downloadDB() {
        ((BrioActivityMain) context).splash.show();
        ((BrioActivityMain) context).splash.publish(Utils.getString(R.string.form_commerce_recuperando_datos, context));

        String sIdComercio = Utils.zeroFillInteger(idComercio, 6);
        String sIdCaja = Utils.zeroFillInteger(idCaja, 3);

        final String fileToDownload = "brio-" + sIdComercio + "-" + sIdCaja + ".zip";


        /**Descarga la base de datos*/
        DownloadDBTask downloadDBTask = new DownloadDBTask(context, new DownloadDBTask.DownloadDBTaskListener() {
            @Override
            public void onDownloadFinish(Boolean success) {
                if (success) {

                    String fileToUnzip = Utils.getBrioPath() + File.separator + BrioGlobales.DIR_FILE_DB + File.separator + fileToDownload;
                    String destPath = Utils.getBrioDBPath(context);
                    destPath = destPath.substring(0, destPath.lastIndexOf("/"));


                    File localdb = new File(destPath, BrioGlobales.DB_NAME);

                    if(localdb.exists()) {
                        localdb.delete();
                    }

                    boolean unziped = Utils.unzipFilesToPath(fileToUnzip, destPath);

                    File downloadeddb = new File(fileToUnzip);
                    if(downloadeddb.exists()) {
                        downloadeddb.delete();
                    }

                    if(unziped) {
                        BrioAcceptDialog god = new BrioAcceptDialog((AppCompatActivity) context,
                                Utils.getString(R.string.form_commerce_recuperacion_title, context),
                                Utils.getString(R.string.form_commerce_recuperacion_exitosa, context),
                                Utils.getString(R.string.brio_aceptar, context), new DialogListener() {
                            @Override
                            public void onAccept() {
                                Utils.restartApp();
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                        god.show();
                    } else {
                        BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) context,
                                Utils.getString(R.string.form_commerce_recuperacion_title, context),
                                Utils.getString(R.string.form_commerce_recuperacion_fallida, context));
                        bad.show();
                    }

                } else {
                    BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) context,
                            Utils.getString(R.string.form_commerce_recuperacion_title, context),
                            Utils.getString(R.string.form_commerce_recuperacion_datosnoencontrados,context));
                    bad.show();
                }

                ((BrioActivityMain) context).splash.dismiss();
            }
        });
        downloadDBTask.execute(fileToDownload);
    }

    /**
     * Realizar el alta del comercio
     */
    @Override
    public void onButtonAltaComercioSaveClick() {
        getStrings();
        if (formValidation()) { // FIXME Production
            save();
        }
    }

    /** Muestra los espinners del formulario */

    private void fillEntidadesSpinner(){

        List<String> estadosLegalList = new ArrayList<>();
        List<String> paisesLegalList = new ArrayList<>();
        List<String> estadosFisicosList = new ArrayList<>();
        List<String> paisesFisicosList = new ArrayList<>();
        for(int i=0; i < estados.length; i++){
            estadosLegalList.add(estados[i]);
            estadosFisicosList.add(estados[i]);
        }
        paisesLegalList.add("México");
        paisesFisicosList.add("México");
        ArrayAdapter<String> spinnerItemsEstadosLegales =
                new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        estadosLegalList
                );
        ArrayAdapter<String> spinnerItemsPaisesLegales =
                new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        paisesLegalList
                );
        ArrayAdapter<String> spinnerItemsEstadosFisicos =
                new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        estadosFisicosList
                );
        ArrayAdapter<String> spinnerItemsPaisesFisicos =
                new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        paisesFisicosList
                );

        String eLegalNull = new String(Utils.getString(R.string.form_commerce_estado_legal, context));
        String pLegalNull = new String(Utils.getString(R.string.form_commerce_pais_legal, context));
        String eFisicoNull = new String(Utils.getString(R.string.form_commerce_estado_fisica, context));
        String pFisicoNull = new String(Utils.getString(R.string.form_commerce_pais_fisica, context));


        spinnerItemsEstadosLegales.insert(eLegalNull, 0);
        spinnerItemsPaisesLegales.insert(pLegalNull, 0);
        spinnerItemsEstadosFisicos.insert(eFisicoNull, 0);
        spinnerItemsPaisesFisicos.insert(pFisicoNull, 0);

        spinnerEstadoLegal.setAdapter(spinnerItemsEstadosLegales);
        spinnerPaisLegal.setAdapter(spinnerItemsPaisesLegales);
        spinnerEstadoFisico.setAdapter(spinnerItemsEstadosFisicos);
        spinnerPaisFisico.setAdapter(spinnerItemsPaisesFisicos);
        spinnerPaisFisico.setSelection(1);
        spinnerPaisFisico.setEnabled(false);

    }

    /**
     * Se obtienen las cadenas de los campos
     */
    private void getStrings() {
        textDescripcion = editDescripcion.getText().toString();
        textNombreLegal = editNombreLegal.getText().toString();
        textRfc = editRfc.getText().toString();
        textDireccionLegal = editDireccionLegal.getText().toString();
        textExteriorLegal = editExteriorLegal.getText().toString();
        textInteriorLegal = editInteriorLegal.getText().toString();
        textColoniaLegal = editColoniaLegal.getText().toString();
        textMunicipioLegal = editMunicipioLegal.getText().toString();
        textCodigoPostalLegal = editCodigoPostalLegal.getText().toString();
        textDireccionFisica = editDireccionFisica.getText().toString();
        textExteriorFisico = editExteriorFisico.getText().toString();
        textInteriorFisico = editInteriorFisico.getText().toString();
        textColoniaFisica = editColoniaFisica.getText().toString();
        textMunicipioFisico = editMunicipioFisico.getText().toString();
        textCodigoPostalFisico = editCodigoPostalFisico.getText().toString();
    }

    /**
     * validacion de los campos obligatorios
     * @return
     */
    private boolean formValidation() {
        getStrings();

            // Validación descripcion de comercio
            if (textDescripcion.length() < descripcionLength) {
                //SpannableStringBuilder ssbuilder = new SpannableStringBuilder(null);
                //ssbuilder.getSpan(0, 0, 0, 0);
                editDescripcion.setError("");//Utils.getString(R.string.form_commerce_valida_not_null,context));
                scrollCommerceForm.scrollTo(0, editDescripcion.getTop());
                Toast.makeText(context, Utils.getString(R.string.form_commerce_descripcion_hint, context), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "editUsuario: " + scrollCommerceForm.getScrollY());
                return false;
            } /*else if (modelManager.usuarios.getByUsuario(textUsuario) != null) {
                editUsuario.setError(Utils.getString(R.string.form_usuario_valida_usuario_nodisponible,context));
                scrollUserForm.scrollTo(0, editUsuario.getTop());
                Log.i(TAG, "editUsuario: " + scrollUserForm.getScrollY());
                return false;
            }*/

        // Validación Dirección
        if (textDireccionFisica.length() < minLength) {
            editDireccionFisica.setError("");
            scrollCommerceForm.scrollTo(0, editDireccionFisica.getTop());
            Log.i(TAG, "editPassword1: " + scrollCommerceForm.getScrollY());
            return false;
        }

        // Validación Numero exterior
        if (textExteriorFisico.length() < minLength) {
            editExteriorFisico.setError("");
            scrollCommerceForm.scrollTo(0, editExteriorFisico.getTop());
            Log.i(TAG, "editPassword1: " + scrollCommerceForm.getScrollY());
            return false;
        }

        // Validación Numero interior
        /*if (textInteriorFisico.length() < minLength) {
            editInteriorFisico.setError("");
            scrollCommerceForm.scrollTo(0, editInteriorFisico.getTop());
            Log.i(TAG, "editPassword1: " + scrollCommerceForm.getScrollY());
            return false;
        }*/
        // Validación Colonia
        if (textColoniaFisica.length() < minLength) {
            editColoniaFisica.setError("");
            scrollCommerceForm.scrollTo(0, editColoniaFisica.getTop());
            Log.i(TAG, "editPassword1: " + scrollCommerceForm.getScrollY());
            return false;
        }

        // Validación Municipio
        if (textMunicipioFisico.length() < minLength) {
            editMunicipioFisico.setError("");
            scrollCommerceForm.scrollTo(0, editMunicipioFisico.getTop());
            Log.i(TAG, "editPassword1: " + scrollCommerceForm.getScrollY());
            return false;
        }

        // Validación estado
        if(spinnerEstadoFisico.getSelectedItemPosition() == 0){
            TextView errorm = (TextView) spinnerEstadoFisico.getSelectedView();
            errorm.setError("");
            scrollCommerceForm.scrollTo(0, spinnerEstadoFisico.getTop());

            return false;
        }

        // Validación Codigo postal
        if (textCodigoPostalFisico.length() < 5) {
            editCodigoPostalFisico.setError("");
            scrollCommerceForm.scrollTo(0, editMunicipioFisico.getTop());
            Log.i(TAG, "editPassword1: " + scrollCommerceForm.getScrollY());
            return false;
        }

        // Validación País
        if(spinnerPaisFisico.getSelectedItemPosition() == 0){
            TextView errorm = (TextView) spinnerPaisFisico.getSelectedView();
            errorm.setError("");
            scrollCommerceForm.scrollTo(0, spinnerPaisFisico.getTop());

            return false;
        }

        return true;
    }

    /**Se guarda el comercio en la base de datos
     */
    private void save() {
        final Comercio comercio = new Comercio();

        comercio.setDescComercio(textDescripcion);
        comercio.setIdGrupo(1); // FIXME Next Version
        comercio.setNombreLegal(textNombreLegal);
        comercio.setRfc(textRfc);
        comercio.setDireccionLegal(textDireccionLegal);
        comercio.setNumeroExteriorLegal(textExteriorLegal);
        comercio.setNumeroInteriorLegal(textInteriorLegal);
        comercio.setColoniaLegal(textColoniaLegal);
        comercio.setMunicipioLegal(textMunicipioLegal);
        comercio.setEstadoLegal((String) spinnerEstadoLegal.getSelectedItem());
        comercio.setCodigoPostalLegal(textCodigoPostalLegal);
        comercio.setPaisLegal((String) spinnerPaisLegal.getSelectedItem());
        comercio.setDireccionFisica(textDireccionFisica);
        comercio.setNumeroExteriorFisica(textExteriorFisico);
        comercio.setNumeroInteriorFisica(textInteriorFisico);
        comercio.setColoniaFisica(textColoniaFisica);
        comercio.setMunicipioFisica(textMunicipioFisico);
        comercio.setEstadoFisica((String) spinnerEstadoFisico.getSelectedItem());
        comercio.setCodigoPostalFisica(textCodigoPostalFisico);
        comercio.setPaisFisica((String) spinnerPaisFisico.getSelectedItem());
        comercio.setTimestamp(Utils.getCurrentTimestamp() * 1000);

        mensaje = (Utils.getString(R.string.form_commerce_mensaje,context));//+ comercio.getDescComercio() +"?";


        final DialogListener dialogListenerConfirm = new DialogListener() {
            @Override
            public void onAccept() {
                loading.setVisibility(View.VISIBLE);
                //Fixme TEST
                /*
                Long rowId = modelManager.comercios.save(comercio);
                Comercio saved = modelManager.comercios.getByIdComercio(rowId.intValue());
                 boolean updateIdComercio = modelManager.settings.update("ID_COMERCIo", "1");
                 boolean updateNombreComercio = modelManager.settings.update("DeSCRIPCION_COMERCIO", comercio.getDescComercio());
                 Log.w(TAG,"settings id comercio "+updateIdComercio+" valor "+modelManager.settings.getByNombre("ID_COMERCIo").getValor());
                 Log.w(TAG, "settings nombre comercio" + updateNombreComercio + " valor " + modelManager.settings.getByNombre("DeSCRIPCION_COMERCIO").getValor());
                Log.d(TAG, "New Commerce: " + Utils.pojoToString(saved));
                Log.w("onAccept","dialog");
                listener.onAccept();*///Fixme end TEST


                //Fixme PRODUCCION
                Gson gson = new Gson();
                String json = gson.toJson(comercio);
                //Toast.makeText(context, "Clave del comercio: " + editTokenComercioAlta.getText().toString(), Toast.LENGTH_SHORT).show();
                ServerService serverService = new ServerService(context);
                String url = "http://" + BrioGlobales.URL_ZERO_BRIO + "/session/api/comm";
                Log.w(TAG,url);
                serverService.request(url, json, new ServerService.ServerServiceListener() {
                    @Override
                    public void onServerResponse(ServerService.ServerResponse syncResponse) {
                        if (!syncResponse.isExists() && syncResponse.getRow() > 0) {
                            comercio.setIdComercio(((Long) syncResponse.getRow()).intValue());
                            mensaje = (Utils.getString(R.string.form_commerce_save_exit,context));
                            Log.w("onServerResponse", "no existe");
                            Long rowId = modelManager.comercios.save(comercio);
                            boolean updateIdComercio = modelManager.settings.update("ID_COMERCIo", "" + syncResponse.getRow());
                            boolean updateNombreComercio = modelManager.settings.update("DeSCRIPCION_COMERCIO", comercio.getDescComercio());
                            Log.w(TAG,"settings id comercio "+updateIdComercio+" valor "+modelManager.settings.getByNombre("ID_COMERCIo").getValor());
                            Log.w(TAG, "settings nombre comercio" + updateNombreComercio + " valor " + modelManager.settings.getByNombre("DeSCRIPCION_COMERCIO").getValor());
                            Comercio saved = modelManager.comercios.getByIdComercio(rowId.intValue());
                            Log.d(TAG, "New Commerce: " + Utils.pojoToString(saved));
                            Log.w("DIALOG", "guardado con exito commerceExist");
                            acceptDialog = new BrioAcceptDialog((AppCompatActivity) context,comercio.getDescComercio(), mensaje,"Aceptar",new DialogListener() {

                                @Override
                                public void onAccept() {
                                    Caja caja = new Caja(Utils.getUUID(context).toString(), "Caja 1");
                                    caja.setIdCaja(1);
                                    Long l = modelManager.cajas.save(caja);
                                    Caja c = modelManager.cajas.getByIdCaja(l.intValue());
                                    Log.w("Registro caja", "" + Utils.pojoToString(c));
                                    boolean idCaja = modelManager.settings.update("id_CAJA", "" + c.getIdCaja());
                                    boolean nombreCaja = modelManager.settings.update("NOMBRE_CAJA", caja.getNombre());
                                    boolean uuidCaja = modelManager.settings.update("UUID", caja.getUuid());
                                    Log.w("CAJA","registro settings id caja"+idCaja+" valor "+modelManager.settings.getByNombre("id_CAJA").getValor());
                                    Log.w("CAJA","registro settings nombre caja"+nombreCaja+" valor "+modelManager.settings.getByNombre("NOMBRE_CAJA").getValor());
                                    Log.w("CAJA","registro settings UUID caja"+uuidCaja+" valor "+modelManager.settings.getByNombre("UUID").getValor());

                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                            loading.setVisibility(View.GONE);
                            acceptDialog.show();

                            listener.onAccept();
                        } else {

                            mensaje =  (Utils.getString(R.string.form_commerce_ya_existente,context));
                            Log.w("onServerResponse","else commerceExist");
                            Log.w("DIALOG","ya existe commerceExist");
                            alertDialog = new BrioAlertDialog((AppCompatActivity) context, comercio.getDescComercio(), mensaje);
                            loading.setVisibility(View.GONE);
                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onErrorResponse(ServerService.ServerResponse syncResponse) {


                        Log.w("onErrorResponse","erro de conexion");
                        mensaje = (Utils.getString(R.string.form_commerce_error_de_conexion,context));
                        alertDialog = new BrioAlertDialog((AppCompatActivity) context, (Utils.getString(R.string.form_commerce_error_de_conexion_titulo,context)), mensaje);
                        loading.setVisibility(View.GONE);
                        alertDialog.show();
                        Log.w(TAG, " getStatus " + syncResponse.getStatus());
                        Log.w(TAG, " getMessage " + syncResponse.getMessage());

                    }
                });//Fixme end of PRODUCCION

            }

            @Override
            public void onCancel() {
                listener.onCancel();
            }
        };

        String detalles = Utils.getString(R.string.form_commerce_nombre_comercio,context)+":\t\t\t\t"+ comercio.getDescComercio() + "\n"+
                 Utils.getString(R.string.form_commerce_direccion_fisica, context)+":\t\t\t\t\t\t\t\t\t"+ comercio.getDireccionFisica()+"\n"+
                 Utils.getString(R.string.form_commerce_numero_exterior_fisica, context)+":\t\t\t\t\t\t"+ comercio.getNumeroExteriorFisica()+"\n"+
                 Utils.getString(R.string.form_commerce_numero_interior_fisica, context)+":\t\t\t\t\t\t\t"+ comercio.getNumeroInteriorFisica()+"\n"+
                 Utils.getString(R.string.form_commerce_colonia_fisica, context)+":\t\t\t\t\t\t\t\t\t\t"+ comercio.getColoniaFisica()+"\n"+
                 Utils.getString(R.string.form_commerce_municipio_fisica, context)+":\t\t\t\t\t\t\t\t\t"+comercio.getMunicipioFisica()+"\n"+
                 Utils.getString(R.string.form_commmerce_estado_fisico_label, context)+":\t\t\t\t\t\t\t\t\t\t"+ comercio.getEstadoFisica()+"\n"+
                 Utils.getString(R.string.form_commerce_codigo_postal_fisica, context)+":\t\t\t\t\t\t\t"+ comercio.getCodigoPostalFisica() + "\n" +
                 Utils.getString(R.string.form_commerce_pais_fisic, context)+":\t\t\t\t\t\t\t\t\t\t\t"+ comercio.getPaisFisica();

        Log.w(TAG, detalles);
        dialogConfirmacion = new BrioConfirmDialog((AppCompatActivity) context, mensaje, detalles, dialogListenerConfirm,android.support.design.R.id.left);

        dialogConfirmacion.show();


    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    public interface UserFormManagerListener {
        void onFragmentRemoved(int tipo); //0 nuevo usuario, 1 login
    }

    /**
     * Mapeo de los campos con res
     */
    public void mapFields(){
        Map fields = commerceFormManager.getFields();
        descripcionLength = 1;
        nombreLength = 1;
        minLength = 1;

        scrollCommerceForm = commerceFormManager.getScroll();

        loading = (RelativeLayout) (fields.get("loading"));

        editDescripcion = (EditText) (fields.get("editDescripcion"));
        editDescripcion.setHint(Utils.getString(R.string.form_commerce_valida_not_null,context));
        editNombreLegal = (BrioEditText) (fields.get("editNombreLegal"));
        editRfc = (BrioEditText) (fields.get("editRfc"));
        editDireccionFisica = (BrioEditText) (fields.get("editDireccionFisica"));
        editDireccionFisica.setHint(Utils.getString(R.string.form_commerce_valida_not_null,context) );
        editExteriorFisico = (BrioEditText) (fields.get("editExteriorFisico"));
        editExteriorFisico.setHint( Utils.getString(R.string.form_commerce_valida_not_null,context));
        editInteriorFisico = (BrioEditText) (fields.get("editInteriorFisico"));
        //editInteriorFisico.setHint(Utils.getString(R.string.form_commerce_valida_not_null,context) );
        editColoniaFisica = (BrioEditText) (fields.get("editColoniaFisica"));
        editColoniaFisica.setHint( Utils.getString(R.string.form_commerce_valida_not_null,context) );
        editMunicipioFisico = (BrioEditText) (fields.get("editMunicipioFisico"));
        editMunicipioFisico.setHint( Utils.getString(R.string.form_commerce_valida_not_null,context));
        editCodigoPostalFisico = (BrioEditText) (fields.get("editCodigoPostalFisico"));
        editCodigoPostalFisico.setHint( Utils.getString(R.string.form_commerce_valida_not_null,context) );
        editDireccionLegal = (BrioEditText) (fields.get("editDireccionLegal"));
        editExteriorLegal = (BrioEditText) (fields.get("editExteriorLegal"));
        editInteriorLegal = (BrioEditText) (fields.get("editInteriorLegal"));
        editColoniaLegal = (BrioEditText) (fields.get("editColoniaLegal"));
        editMunicipioLegal = (BrioEditText) (fields.get("editMunicipioLegal"));
        editCodigoPostalLegal = (BrioEditText) (fields.get("editCodigoPostalLegal"));


        spinnerEstadoLegal = (Spinner) (fields.get("spinnerEstadoLegal"));
        spinnerPaisLegal = (Spinner) (fields.get("spinnerPaisLegal"));
        spinnerEstadoFisico = (Spinner) (fields.get("spinnerEstadoFisico"));
        spinnerPaisFisico = (Spinner) (fields.get("spinnerPaisFisico"));

        opcRecuperarComercio = (BrioButton2) (fields.get("opcRecuperarComercio"));
        opcAltaComercio = (BrioButton2) (fields.get("opcAltaComercio"));

        panelPrincipal = (View) (fields.get("panelPrincipal"));
        panelAltaComercio = (View) (fields.get("panelAltaComercio"));
        panelRecuperarComercio = (View) (fields.get("panelRecuperarComercio"));

        buttonRecuperar = (View) (fields.get("buttonRecuperar"));
        buttonBack = (View) (fields.get("buttonBack"));
        buttonBack2 = (View) (fields.get("buttonBack2"));
        txtTitle = (TextView) (fields.get("txtTitle"));
        txtSubtitle = (TextView) (fields.get("txtSubtitle"));
        editIdComercio = (BrioEditText) (fields.get("editIdComercio"));

        editTokenComercioAlta = (BrioEditText) (fields.get("editTokenComercioAlta"));
        editTokenComercioAlta.setHint(Utils.getString(R.string.form_commerce_valida_not_null, context));
        editTokenComercioRecuperar = (BrioEditText) (fields.get("editTokenComercioRecuperar"));
        editTokenComercioRecuperar.setHint(Utils.getString(R.string.form_commerce_valida_not_null, context));
    }

}


