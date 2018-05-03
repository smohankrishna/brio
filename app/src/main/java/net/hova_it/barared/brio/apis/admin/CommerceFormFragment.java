package net.hova_it.barared.brio.apis.admin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.views.BrioButton2;
import net.hova_it.barared.brio.apis.views.BrioEditText;

import java.util.HashMap;
import java.util.Map;

import static net.hova_it.barared.brio.BrioBaseActivity.DEBUG_SHOW_TOASTS;

/**
 * Created by Mauricio Ceron on 17/03/2016.
 * Clase que manda a llamar a las interfaces para dar de alta un comercio o la recuperacion del mismo desde el ftp
 */
public class CommerceFormFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "BARA_LOG";

    private View view;
    private CommerceFormManager commerceFormManager;
    private CommerceFormManagerListener commerceFormManagerListener;
    private RelativeLayout loading;

    private BrioEditText
            editDescripcion,
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

    private Spinner
            spinnerEstadoLegal,
            spinnerPaisLegal,
            spinnerEstadoFisico,
            spinnerPaisFisico;

    private BrioButton2
            opcRecuperarComercio,
            opcAltaComercio;

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


    public static CommerceFormFragment newInstance() {
        Bundle args = new Bundle();

        CommerceFormFragment commerceFormFragment = new CommerceFormFragment();
        commerceFormFragment.setArguments(args);

        return commerceFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.admin_commerce_registro, container, false);
        this.view = view;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "searchAlarmStart Called");

        if(commerceFormManager != null) {
            commerceFormManagerListener = commerceFormManager;

            view.findViewById(R.id.opcRecuperarComercio)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commerceFormManagerListener.onBtnOptionRecuperarComercioClick();
                        }
                    });

            view.findViewById(R.id.buttonRecuperar)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commerceFormManagerListener.onButtonRecuperarComercioClick();
                        }
                    });

            view.findViewById(R.id.buttonRecuperarBack)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commerceFormManagerListener.onButtonRecuperarComercioBackClick();
                        }
                    });

            view.findViewById(R.id.opcAltaComercio)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commerceFormManagerListener.onBtnOptionAltaComercioClick();
                        }
                    });

            view.findViewById(R.id.buttonAltaBack)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commerceFormManagerListener.onButtonAltaComercioBackClick();
                        }
                    });

            Button buttonSave = (Button)view.findViewById(R.id.buttonSave);
            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onSave Called");
                    commerceFormManagerListener.onButtonAltaComercioSaveClick();
                }
            });
            
            commerceFormManagerListener.onFragmentCreated();
        }

    }

    /**
     * Mapeo de los campos para mandarlos a llamar en el Manager
     * @return retorna los campos
     */
    public Map<String, Object> getFields() {
        // Form Fields

        castFields();

        Map<String, Object> fields = new HashMap<>();
        fields.put("editDescripcion", editDescripcion);
        fields.put("editNombreLegal", editNombreLegal);
        fields.put("editRfc", editRfc);
        fields.put("editDireccionLegal", editDireccionLegal);
        fields.put("editExteriorLegal", editExteriorLegal);
        fields.put("editInteriorLegal", editInteriorLegal);
        fields.put("editColoniaLegal", editColoniaLegal);
        fields.put("editMunicipioLegal", editMunicipioLegal);
        fields.put("editCodigoPostalLegal", editCodigoPostalLegal);
        fields.put("editDireccionFisica", editDireccionFisica);
        fields.put("editExteriorFisico", editExteriorFisico);
        fields.put("editInteriorFisico", editInteriorFisico);
        fields.put("editColoniaFisica", editColoniaFisica);
        fields.put("editMunicipioFisico", editMunicipioFisico);
        fields.put("editCodigoPostalFisico", editCodigoPostalFisico);
        fields.put("spinnerEstadoLegal", spinnerEstadoLegal);
        fields.put("spinnerPaisLegal", spinnerPaisLegal);
        fields.put("spinnerEstadoFisico", spinnerEstadoFisico);
        fields.put("spinnerPaisFisico", spinnerPaisFisico);
        fields.put("loading", loading);

        fields.put("opcRecuperarComercio", opcRecuperarComercio);
        fields.put("opcAltaComercio", opcAltaComercio);

        fields.put("panelPrincipal", panelPrincipal);
        fields.put("panelAltaComercio", panelAltaComercio);
        fields.put("panelRecuperarComercio", panelRecuperarComercio);

        fields.put("buttonRecuperar", buttonRecuperar);
        fields.put("buttonBack", buttonBack);
        fields.put("buttonBack2", buttonBack2);
        fields.put("txtTitle", txtTitle);
        fields.put("txtSubtitle", txtSubtitle);
        fields.put("editIdComercio", editIdComercio);
        fields.put("editTokenComercioRecuperar", editTokenComercioRecuperar);
        fields.put("editTokenComercioAlta", editTokenComercioAlta);

        return fields;
    }


    /**
     * Cast de los campos con respecto a su id del view
     */
    private void castFields(){

        this.loading = (RelativeLayout)view.findViewById(R.id.loading_commerce);
        this.editDescripcion = (BrioEditText)view.findViewById(R.id.editDescripcion);
        this.editNombreLegal = (BrioEditText)view.findViewById(R.id.editNombreLegal);
        this.editRfc = (BrioEditText)view.findViewById(R.id.editRFC);
        this.editDireccionLegal = (BrioEditText)view.findViewById(R.id.editDireccionLegal);
        this.editExteriorLegal = (BrioEditText)view.findViewById(R.id.editExteriorLegal);
        this.editInteriorLegal = (BrioEditText)view.findViewById(R.id.editInteriorLegal);
        this.editColoniaLegal = (BrioEditText)view.findViewById(R.id.editColoniaLegal);
        this.editMunicipioLegal = (BrioEditText)view.findViewById(R.id.editMunicipioLegal);
        this.editCodigoPostalLegal = (BrioEditText)view.findViewById(R.id.editCodigoPostalLegal);
        this.editDireccionFisica = (BrioEditText)view.findViewById(R.id.editDireccionFisica);
        this.editExteriorFisico = (BrioEditText)view.findViewById(R.id.editExteriorFisica);
        this.editInteriorFisico = (BrioEditText)view.findViewById(R.id.editInteriorFisica);
        this.editColoniaFisica = (BrioEditText)view.findViewById(R.id.editColoniaFisica);
        this.editMunicipioFisico = (BrioEditText)view.findViewById(R.id.editMunicipioFisica);
        this.editCodigoPostalFisico = (BrioEditText)view.findViewById(R.id.editCodigoPostalFisica);
        this.spinnerEstadoLegal = (Spinner)view.findViewById(R.id.spinnerEstadoLegal);
        this.spinnerPaisLegal = (Spinner)view.findViewById(R.id.spinnerPaisLegal);
        this.spinnerEstadoFisico = (Spinner)view.findViewById(R.id.spinnerEstadoFisico);
        this.spinnerPaisFisico = (Spinner)view.findViewById(R.id.spinnerPaisFisico);

        this.opcRecuperarComercio = (BrioButton2)view.findViewById(R.id.opcRecuperarComercio);
        this.opcAltaComercio = (BrioButton2)view.findViewById(R.id.opcAltaComercio);

        this.panelPrincipal = view.findViewById(R.id.panelPrincipal);
        this.panelAltaComercio = view.findViewById(R.id.panelAltaComercio);
        this.panelRecuperarComercio = view.findViewById(R.id.panelRecuperarComercio);

        this.buttonRecuperar = view.findViewById(R.id.buttonRecuperar);
        this.buttonBack = view.findViewById(R.id.buttonBack);
        this.buttonBack2 = view.findViewById(R.id.buttonRecuperarBack);
        this.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        this.txtSubtitle = (TextView) view.findViewById(R.id.txtSubtitle);
        this.editIdComercio = (BrioEditText) view.findViewById(R.id.editIdComercio);
        this.editTokenComercioRecuperar = (BrioEditText) view.findViewById(R.id.editTokenComercioRecuperar);
        this.editTokenComercioAlta = (BrioEditText) view.findViewById(R.id.editTokenComercioAlta);
    }

    /**
     * Manda a llamar al Scroll el cual se posiciona en el campo donde se presenta un error
     * @return el scroll en el campo indicado
     */
    public ScrollView getScroll(){
        return (ScrollView)view.findViewById(R.id.scrollUserForm);
    }


    public void setCommerceFormManager(CommerceFormManager commerceFormManager) {
        this.commerceFormManager = commerceFormManager;
    }

    /**
     * Dependiendo en que boton le den click da de alta un nuevo comercio o recupera la informacion del mismo
     * @param v es la vista, en este caso botones
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.opcAltaComercio:
                commerceFormManagerListener.onBtnOptionAltaComercioClick();
                break;

            case R.id.opcRecuperarComercio:
                commerceFormManagerListener.onBtnOptionRecuperarComercioClick();
                break;
        }
    }

    /**
     * Interfaces que estan implementadas en el manager
     */
    public interface CommerceFormManagerListener {
        void onFragmentCreated();
        void onBtnOptionRecuperarComercioClick();
        void onBtnOptionAltaComercioClick();

        void onButtonAltaComercioBackClick();
        void onButtonRecuperarComercioBackClick();

        void onButtonAltaComercioSaveClick();
        void onButtonRecuperarComercioClick();
    }

     /*public  void cleanFields(){

        this.editDescripcion.setText("");
        this.editNombreLegal.setText("");
        this.editPassword2.setText("");
        this.editNombre.setText("");
        this.editApellidos.setText("");
        this.editRespuesta1.setText("");
        this.spinnerPerfil.setSelection(0);
        this.spinnerPregunta1.setSelection(0);
    }*/

    /*public void setFields(Usuario usuario) {
        // Form Fields
        castFields();

        this.editDescripcion.setText(usuario.getUsuario());
        this.editPassword1.setText(usuario.getPassword());
        this.editPassword2.setText(usuario.getPassword());
        this.editNombre.setText((usuario.getNombre()));
        this.editApellidos.setText((usuario.getApellidos()));
        this.editRespuesta1.setText((usuario.getRespuesta1()));

        editUsuario.setEnabled(false);

        if(usuario.getIdUsuario() == 1){
            this.editNombre.setVisibility(View.GONE);
            this.editApellidos.setVisibility(View.GONE);
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

    }*/
}
