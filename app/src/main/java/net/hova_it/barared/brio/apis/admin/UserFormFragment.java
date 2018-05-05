package net.hova_it.barared.brio.apis.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.entities.Perfil;
import net.hova_it.barared.brio.apis.models.entities.Pregunta;
import net.hova_it.barared.brio.apis.models.entities.Usuario;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.views.BrioEditText;

import java.util.HashMap;
import java.util.Map;


/**Clase que te permite crear y editar un usuario
 * Created by Alejandro Gomez on 21/12/2015.
 */
public class UserFormFragment extends OptionMenuFragment {
    private final String TAG = "BARA_LOG";

    private View view;
    private UserFormManager userFormManager;
    private UserFormFragmentListener userFormFragmentListener;

    private BrioEditText editUsuario;
    private BrioEditText editPassword1;
    private BrioEditText editPassword2;
    private BrioEditText editNombre;
    private BrioEditText editApellidos;
    private BrioEditText editRespuesta1;
    private BrioEditText editRespuesta2;
    private BrioEditText editRespuesta3;

    private Spinner spinnerPerfil;
    private Spinner spinnerPregunta1;
    //private Spinner spinnerPregunta2;
    //private Spinner spinnerPregunta3;


    public static UserFormFragment newInstance() {
        Bundle args = new Bundle();

        UserFormFragment userFormFragment = new UserFormFragment();
        userFormFragment.setArguments(args);

        return userFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.admin_usuario_registro, container, false);
        this.view = view;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "searchAlarmStart Called");

        if(userFormManager != null){
            userFormFragmentListener = userFormManager;

            Button buttonSave = (Button)view.findViewById(R.id.buttonSave);
            buttonSave.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onSave Called");
                    userFormFragmentListener.onButtonSaveClick();

                }
            });

            Button buttonCancel = (Button)view.findViewById(R.id.buttonCancel);
            buttonCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onCancell Called");
                    userFormFragmentListener.onButtonCancelClick();


                    //delete();
                }
            });

            userFormFragmentListener.onFragmentCreated();
        }

    }
    /**Mapeo de los campos para ocuparlos en el manager */
    public Map<String, Object> getFields() {
        // Form Fields

        castFields();

        Map<String, Object> fields = new HashMap<>();
        fields.put("editUsuario", editUsuario);
        fields.put("editPassword1", editPassword1);
        fields.put("editPassword2", editPassword2);
        fields.put("editNombre", editNombre);
        fields.put("editApellidos", editApellidos);
        fields.put("spinnerPerfil", spinnerPerfil);
        fields.put("spinnerPregunta1", spinnerPregunta1);
        fields.put("editRespuesta1", editRespuesta1);
        //fields.put("spinnerPregunta2", spinnerPregunta2);
        fields.put("editRespuesta2", editRespuesta2);
        //fields.put("spinnerPregunta3", spinnerPregunta3);
        fields.put("editRespuesta3", editRespuesta3);

        return fields;
    }

    /**Limpia los campos */
    public  void cleanFields(){

        this.editUsuario.setText("");
        this.editPassword1.setText("");
        this.editPassword2.setText("");
        this.editNombre.setText("");
        this.editApellidos.setText("");
        this.editRespuesta1.setText("");
        this.spinnerPerfil.setSelection(0);
        this.spinnerPregunta1.setSelection(0);

        String test = UserFormManager.getInstance(getActivity()).usuario == null ? "null":Utils.pojoToString(UserFormManager.getInstance(getActivity()).usuario.toString());
        UserFormManager.getInstance(getActivity()).usuario = null;

        Log.w("Clean", "usuario" + test);
    }

    /**Te muestra los datos del usuario seleccionado
     * @param usuario usuario
     */
    public void setFields(Usuario usuario) {
        // Form Fields
        castFields();

        this.editUsuario.setText(usuario.getUsuario());
        this.editPassword1.setHint(Utils.getString(R.string.form_usuario_edit_password, getActivity()));
        this.editPassword1.setText("");//usuario.getPassword());
        this.editPassword2.setText("");//usuario.getPassword());
        this.editNombre.setText((usuario.getNombre()));
        this.editApellidos.setText((usuario.getApellidos()));
        this.editRespuesta1.setText((usuario.getRespuesta1()));

        editUsuario.setEnabled(false);

        if(usuario.getIdUsuario() == 1){
            this.editNombre.setEnabled(false);
            this.editApellidos.setEnabled(false);
            spinnerPerfil.setEnabled(false);

        }

        for(int i = 0; i<spinnerPerfil.getCount(); i++){
            if(((Perfil)spinnerPerfil.getItemAtPosition(i)).getIdPerfil() == usuario.getIdPerfil()){
                spinnerPerfil.setSelection(i);
                break;
            }
        }

        for(int i = 0; i<spinnerPregunta1.getCount(); i++){
            if(((Pregunta)spinnerPregunta1.getItemAtPosition(i)).getIdPregunta() == usuario.getPregunta1()){
                spinnerPregunta1.setSelection(i);
                break;
            }
        }

    }
    /**Cast de los campos con respecto a sus vistas*/
    private void castFields(){
        Log.i("BRIO_LOG", (view.findViewById(R.id.editUsuario).toString()));

        this.editUsuario = (BrioEditText)view.findViewById(R.id.editUsuario);
        this.editPassword1 = (BrioEditText)view.findViewById(R.id.editPassword1);
        this.editPassword2 = (BrioEditText)view.findViewById(R.id.editPassword2);
        this.editNombre = (BrioEditText)view.findViewById(R.id.editNombre);
        this.editApellidos = (BrioEditText)view.findViewById(R.id.editApellidos);
        this.editRespuesta1 = (BrioEditText)view.findViewById(R.id.editRespuesta1);
        this.spinnerPerfil = (Spinner)view.findViewById(R.id.spinnerPerfil);
        this.spinnerPregunta1 = (Spinner)view.findViewById(R.id.spinnerPregunta1);

    }

    /**
     * @return muestra scroll en caso de ser necesario
     */
    public ScrollView getScroll(){
        return (ScrollView)view.findViewById(R.id.scrollUserForm);
    }

    public void setUserFormManager(UserFormManager userFormManager) {
        this.userFormManager = userFormManager;
    }

    @Override
    protected View getRootView() {
        return this.view;
    }

    @Override
    protected void beforeRemove() {
        cleanFields();

    }

    /**Interfaces implementadas en el manager   */
    public interface UserFormFragmentListener {
        void onFragmentCreated();
        void onButtonSaveClick();
        void onButtonCancelClick();
    }

}
