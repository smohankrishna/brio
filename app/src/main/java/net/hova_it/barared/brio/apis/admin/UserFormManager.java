package net.hova_it.barared.brio.apis.admin;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Perfil;
import net.hova_it.barared.brio.apis.models.entities.Pregunta;
import net.hova_it.barared.brio.apis.models.entities.Usuario;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAcceptDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoManager2;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.hova_it.barared.brio.apis.admin.UserFormFragment.UserFormFragmentListener;

/**Logica e implementacion del fragment de usuarios
 * Created by Alejandro Gomez on 18/12/2015.
 */
public class UserFormManager implements UserFormFragmentListener {
    private final String TAG = "BARA_LOG";

    private Context context;
    private FragmentManager fragmentManager;
    private UserFormFragment userFormFragment;
    private CatalogUsuarioFragment catalogUsuarioFragment;
    public Usuario usuario=null;//Edicion
    private UserAdapter userAdapter;
    private BrioConfirmDialog dialogConfirmacion;
    private String mensaje;
    private SessionManager managerSession;


    private ModelManager modelManager;
    private ScrollView scrollUserForm;
    private EditText
            editUsuario,
            editPassword1,
            editPassword2,
            editNombre,
            editApellidos,
            editRespuesta1;

    private String
            textUsuario,
            textPassword1,
            textPassword2,
            textNombre,
            textApellidos,
            textRespuesta1;

    private int
            usuarioLength,
            minLength,
            passwordLength;
    private Spinner
            spinnerPerfil,
            spinnerPregunta1;

    private Fragment parent;

    private static UserFormManager mInstance = null;

    public static UserFormManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserFormManager(context, null);
        }
        return mInstance;
    }

    public static UserFormManager getInstance(Context context, Fragment parent) {
        if (mInstance == null) {
            mInstance = new UserFormManager(context, parent);
        }
        return mInstance;
    }

    private UserFormManager(Context context, Fragment parent) {
        this.context = context;
        this.parent = parent;
        this.userFormFragment = UserFormFragment.newInstance();
        this.modelManager = new ModelManager(context);
    }

    public UserFormFragment createFragment(FragmentManager fragmentManager, int container) {
        Log.d(TAG, "Creating Users Fragment...");
        this.fragmentManager = fragmentManager;

        userFormFragment.setUserFormManager(this);
/*
        this.fragmentManager
                .beginTransaction()
                .add(container, userFormFragment)
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .addToBackStack(null)
                .commit();
*/
        return userFormFragment;
    }

    public OptionMenuFragment replaceFragment(FragmentManager fragmentManager, int container, Usuario usuario) {
        Log.d(TAG, "Creating Users Fragment...");
        this.fragmentManager = fragmentManager;
        this.usuario = usuario;
        Log.w("replaceFragment", usuario.getIdUsuario() + "");

        userFormFragment.setUserFormManager(this);

        /*
        this.fragmentManager
                .beginTransaction()
                .add(container, userFormFragment)
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .commit();
                */

        return userFormFragment;
    }

    public void removeFragment() {
        Log.d(TAG, "Removing Users Fragment...");

        this.fragmentManager
                .beginTransaction()
                .remove(userFormFragment)
                .commit();
        TecladoManager2 managerTeclado = TecladoManager2.getInstance(context);
        managerTeclado.closeKeyboard();

        if(usuario == null) {
            if (parent == null) {
                ((UserFormManagerListener) context).onFragmentRemoved(0);
            } else {
                ((UserFormManagerListener) parent).onFragmentRemoved(0);
            }
        }else{
            usuario = null;
            Log.w("removeFragment","entreee "+usuario);
            userFormFragment.cleanFields();
            userAdapter.onCancel();
        }
    }

    public void  setAdapter(UserAdapter userAdapter){
        this.userAdapter = userAdapter;
    }

    @Override
    public void onFragmentCreated() {
        Log.d(TAG, "Users Fragment Ready...");
        mapFields();
        fillQuestionSpinner();
        fillPerfilSpiner();
        Log.w("onFragmentCreated", "entreee " + usuario);
        if(this.usuario != null){
            userFormFragment.setFields(this.usuario);
        }
    }

    private void fillQuestionSpinner(){



        List<Pregunta> preguntas = modelManager.preguntas.getAll();

        ArrayAdapter<Pregunta> spinnerItems =
                new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        preguntas
                );

        Pregunta pNull = new Pregunta(Utils.getString(R.string.form_usuario_pregunta,context));
        pNull.setIdPregunta(-1);

        spinnerItems.insert(pNull, 0);

        spinnerPregunta1.setAdapter(spinnerItems);
        /*spinnerPregunta2.setAdapter(spinnerItems);
        spinnerPregunta3.setAdapter(spinnerItems);*/
    }

    /**Validacion de los perfiles con respecto a los permisos para las opciones que te se muestran al usuario     */
    private void fillPerfilSpiner() {

        managerSession = SessionManager.getInstance(context);
        int idPerfil = managerSession.readInt("idPerfil");
        List<Perfil> perfiles = modelManager.perfil.getAll();
        List<Perfil> perfils = new ArrayList<>();
        /*for (int i=0; i<perfiles.size(); i++) {
            Perfil per = perfiles.get(i);
            if (!per.getPerfil().equals("Gerente") && !per.getPerfil().equals("Administrador")) {
                perfils.add(perfiles.get(i));
            } else if (!per.getPerfil().equals("Administrador")) {
                perfils.add(perfiles.get(i));
            }
        }*/
        Log.w("Perfiles", perfiles.get(1).getPerfil());

        ArrayAdapter<Perfil> spinnerItems;
        Perfil pNull = new Perfil(Utils.getString(R.string.form_usuario_perfil, context));
        switch (idPerfil) {

            case 1: //Administrador
                spinnerItems =
                        new ArrayAdapter<>(
                                context,
                                android.R.layout.simple_spinner_dropdown_item,
                                perfiles
                        );

                pNull.setIdPerfil(-1);
                spinnerItems.insert(pNull, 0);
                spinnerPerfil.setAdapter(spinnerItems);
                break;
            case 2:  //Gerente

                for (int i=0; i<perfiles.size(); i++) {
                    Perfil per = perfiles.get(i);
                    if (!per.getPerfil().equals("Gerente") && !per.getPerfil().equals("Administrador")) {
                        perfils.add(perfiles.get(i));
                    }
                }
                spinnerItems =
                        new ArrayAdapter<>(
                                context,
                                android.R.layout.simple_spinner_dropdown_item,
                                perfils
                        );
                //Perfil pNull = new Perfil(Utils.getString(R.string.form_usuario_perfil, context));
                pNull.setIdPerfil(-1);
                spinnerItems.insert(pNull, 0);
                spinnerPerfil.setAdapter(spinnerItems);
                break;

            case 3:  //Operador

                for (int i = 0; i < perfiles.size(); i++) {
                    Perfil per = perfiles.get(i);
                    if (!per.getPerfil().equals("Gerente") && !per.getPerfil().equals("Administrador")) {
                        perfils.add(perfiles.get(i));
                    }
                }
                spinnerItems =
                        new ArrayAdapter<>(
                                context,
                                android.R.layout.simple_spinner_dropdown_item,
                                perfils
                        );
                //Perfil pNull = new Perfil(Utils.getString(R.string.form_usuario_perfil, context));
                pNull.setIdPerfil(-1);
                spinnerItems.insert(pNull, 0);
                spinnerPerfil.setAdapter(spinnerItems);
                break;


        }
    }
    @Override
    public void onButtonSaveClick() {

        if (formValidation()) {
            save();
            userFormFragment.cleanFields();
            userFormFragment.remove();
        }
    }

    @Override
    public void onButtonCancelClick() {
        Log.d(TAG, "onButtonAltaComercioCancelClick Clicked...");

        //removeFragment();
        userFormFragment.cleanFields();
        userFormFragment.remove();

    }

    /**Se obtienen las cadenas de los campos   */
    private void getStrings() {
        textUsuario = editUsuario.getText().toString();
        textPassword1 = editPassword1.getText().toString();
        textPassword2 = editPassword2.getText().toString();
        textNombre = editNombre.getText().toString();
        textApellidos = editApellidos.getText().toString();
        textRespuesta1 = editRespuesta1.getText().toString();
        /*textRespuesta2 = editRespuesta2.getText().toString();
        textRespuesta3 = editRespuesta3.getText().toString();*/
    }

    /**Validaciones de los campos
     * @return de un boolean con respecto a la validacion
     */
    private boolean formValidation() {
        getStrings();

        if(this.usuario == null ) {
            // Validación Usuario
            if (textUsuario.length() < usuarioLength) {
                editUsuario.setError(Utils.getString(R.string.form_usuario_valida_usuario_invalido,context));
                scrollUserForm.scrollTo(0, editUsuario.getTop());
                Log.i(TAG, "editUsuario: " + scrollUserForm.getScrollY());
                return false;
            } else if (modelManager.usuarios.getByUsuario(textUsuario) != null) {
                editUsuario.setError(Utils.getString(R.string.form_usuario_valida_usuario_nodisponible,context));
                scrollUserForm.scrollTo(0, editUsuario.getTop());
                Log.i(TAG, "editUsuario: " + scrollUserForm.getScrollY());
                return false;
            }
        }

        //Si no esta editando un usuario validar contraseña o si se esta editando un usuario y el campo de texto pass no esta vacio
        if(usuario == null || (usuario!= null && (textPassword1.length()>0 || textPassword2.length()>0) ) ){

            // Validación Contraseña
            if (textPassword1.length() < passwordLength) {
                editPassword1.setError(Utils.getString(R.string.form_usuario_valida_password_invalido, context));
                scrollUserForm.scrollTo(0, editPassword1.getTop());
                Log.i(TAG, "editPassword1: " + scrollUserForm.getScrollY());
                return false;
            } else if (textPassword2.length() < passwordLength) {
                editPassword2.setError(Utils.getString(R.string.form_usuario_valida_password_invalido, context));
                scrollUserForm.scrollTo(0, editPassword2.getTop());
                Log.i(TAG, "editPassword2: " + scrollUserForm.getScrollY());
                return false;
            } else if (!textPassword1.equals(textPassword2)) {
                editPassword2.setError(Utils.getString(R.string.form_usuario_valida_password_nocoincide, context));
                scrollUserForm.scrollTo(0, editPassword2.getTop());
                Log.i(TAG, "editPassword2: " + scrollUserForm.getScrollY());
                return false;
            }
        }
        // Validación Nombre
        if (textNombre.length() < minLength) {
            editNombre.setError(Utils.getString(R.string.form_usuario_valida_campo_obligatorio,context));
            scrollUserForm.scrollTo(0, editNombre.getTop());
            Log.i(TAG, "editUsuario: " + scrollUserForm.getScrollY());
            return false;
        }

        // Validación Apellidos
        if (textApellidos.length() < minLength) {
            editApellidos.setError(Utils.getString(R.string.form_usuario_valida_campo_obligatorio,context));
            scrollUserForm.scrollTo(0, editApellidos.getTop());
            Log.i(TAG, "editUsuario: " + scrollUserForm.getScrollY());
            return false;
        }

        // Validación perfil
        if(spinnerPerfil.getSelectedItemPosition() == 0){
            TextView errorm = (TextView) spinnerPerfil.getSelectedView();
            errorm.setError("");
            scrollUserForm.scrollTo(0, spinnerPerfil.getTop());

            return false;
        }

        // Validación pregunta
        if(spinnerPregunta1.getSelectedItemPosition() == 0){
            TextView errorm = (TextView) spinnerPregunta1.getSelectedView();
            errorm.setError("");
            scrollUserForm.scrollTo(0, spinnerPregunta1.getTop());

            return false;
        }
        // Validación Respuesta
        if (textRespuesta1.length() < minLength) {
            editRespuesta1.setError(Utils.getString(R.string.form_usuario_valida_campo_obligatorio,context));
            scrollUserForm.scrollTo(0, editRespuesta1.getTop());
            Log.i(TAG, "editUsuario: " + scrollUserForm.getScrollY());
            return false;
        }

        return true;
    }

    /**Guarda en base de datos el usuario registrado     */
    private void save() {
        final Usuario usuario = new Usuario();

        if(this.usuario != null ){
            usuario.setIdUsuario(this.usuario.getIdUsuario());
        }
        usuario.setUsuario(textUsuario);

        //Si no esta editando un usuario validar contraseña o si se esta editando un usuario y el campo de texto pass no esta vacio
        if(usuario == null || (usuario!= null && (textPassword1.length()>0) ) ) {
            usuario.setPassword(Utils.toSHA256(textPassword1));
        }else{
            usuario.setPassword(this.usuario.getPassword());
        }

        usuario.setNombre(textNombre);
        usuario.setApellidos(textApellidos);
        usuario.setIdPerfil(((Perfil) spinnerPerfil.getSelectedItem()).getIdPerfil());
        usuario.setPregunta1(((Pregunta) spinnerPregunta1.getSelectedItem()).getIdPregunta());
        usuario.setRespuesta1(textRespuesta1);
        /*usuario.setPregunta2(((Pregunta) spinnerPregunta2.getSelectedItem()).getIdPregunta());
        usuario.setRespuesta2(textRespuesta2);
        usuario.setPregunta3(((Pregunta) spinnerPregunta3.getSelectedItem()).getIdPregunta());
        usuario.setRespuesta3(textRespuesta3);*/


        usuario.setActivo(true);
        usuario.setTimestamp(System.currentTimeMillis() / 1000);

        mensaje = (Utils.getString(R.string.cat_usuarios_mensaje,context))+ usuario.getUsuario() +"?";

        if(this.usuario != null ) {
            /*

            DialogActionListener dialogActionListener = new DialogActionListener() {
                @Override
                public void onAcceptResult(Object results) {
                    Long rowId = modelManager.usuarios.save(usuario);
                    Usuario saved = modelManager.usuarios.getByIdUsuario(rowId.intValue());
                    Log.d(TAG, "New User: " + Utils.pojoToString(saved));
                    ((UserFromManagerEditListener) userAdapter).onEdit();
                    removeFragment();
                }

                @Override
                public void onCancelResult(Object results) {

                }
            };

            dialogConfirmacion = (DialogConfirmacion.newInstance(mensaje, dialogActionListener));
            dialogConfirmacion.show(((AppCompatActivity) context).getSupportFragmentManager(), "Confirmacion");
            */

            DialogListener dialogListener = new DialogListener() {
                @Override
                public void onAccept() {
                    Long rowId = modelManager.usuarios.save(usuario);
                    Usuario saved = modelManager.usuarios.getByIdUsuario(rowId.intValue());
                    Log.d(TAG, "New User: " + Utils.pojoToString(saved));
                    (/*(UserFromManagerEditListener)*/ userAdapter).onEdit();
                    //removeFragment();


                }

                @Override
                public void onCancel() {

                }
            };
            dialogConfirmacion = new BrioConfirmDialog((AppCompatActivity) context, "", mensaje, null, dialogListener);
            dialogConfirmacion.show();

        }else {

            Long rowId = modelManager.usuarios.save(usuario);
            Log.d(TAG, "User created with ID:" + rowId);
            Log.d(TAG, "Loading Created User...");
            Usuario saved = modelManager.usuarios.getByIdUsuario(rowId.intValue());
            Log.d(TAG, "Edit User: " + Utils.pojoToString(saved));

            /*
            AlertDialog infoDialog = Utils.alertOK(context,
                    Utils.getString(R.string.form_usuario_valida_alert_title,context),
                    Utils.getString(R.string.form_usuario_valida_alert_message,context),
                    Utils.getString(R.string.alert_ok,context),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            removeFragment();
                        }
                    });

                    infoDialog.show();
            */

            BrioAcceptDialog bad = new BrioAcceptDialog((AppCompatActivity) context,
                    Utils.getString(R.string.form_usuario_valida_alert_title, context),
                    Utils.getString(R.string.form_usuario_valida_alert_message, context),
                    Utils.getString(R.string.brio_aceptar,context), new DialogListener() {
                @Override
                public void onAccept() {
                    userFormFragment.remove();
                   // removeFragment();

                }

                @Override
                public void onCancel() {

                }
            });
            bad.show();
        }
    }


    public interface UserFormManagerListener {
        void onFragmentRemoved(int tipo); //0 nuevo usuario, 1 login
    }

    /**Mapeo de los campos     */
    public void mapFields(){
        Map fields = userFormFragment.getFields();
        usuarioLength = 3;
        passwordLength = 3;
        minLength = 1;
        scrollUserForm = userFormFragment.getScroll();

        editUsuario = (EditText) (fields.get("editUsuario"));
        editUsuario.setHint("(min " + usuarioLength + " " + Utils.getString(R.string.form_usuario_valida_size,context) + ")");

        editPassword1 = (EditText) (fields.get("editPassword1"));
        editPassword1.setHint("(min " + passwordLength + " " + Utils.getString(R.string.form_usuario_valida_size,context) + ")");
        editPassword2 = (EditText) (fields.get("editPassword2"));

        editNombre = (EditText) (fields.get("editNombre"));
        editApellidos = (EditText) (fields.get("editApellidos"));
        spinnerPerfil = (Spinner) (fields.get("spinnerPerfil"));

        spinnerPregunta1 = (Spinner) (fields.get("spinnerPregunta1"));
        editRespuesta1 = (EditText) (fields.get("editRespuesta1"));


    }

    public void setCatalogUsuarioFragment(CatalogUsuarioFragment catalogUsuarioFragment) {
        this.catalogUsuarioFragment = catalogUsuarioFragment;
    }

    public interface UserFromManagerEditListener {
        void onEdit();
        void onCancel();
    }

}
