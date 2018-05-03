package net.hova_it.barared.brio.apis.admin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Caja;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.views.BrioButton2;
import net.hova_it.barared.brio.apis.views.BrioEditText;


/**
 * Created by Mauricio Ceron on 29/03/2016.
 * clase que registra la caja, por el momento solo hay una caja se ocuparia en caso de ser multicaja
 */
public class RegistroCajaFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private BrioButton2 guardar;
    private BrioEditText textNombre;
    private DialogListener clickListener;
    private String nombre="";
    private ModelManager modelManager;
    private Caja caja;
    private static final String pattern = "[^0-9a-zA-Z\\-/\\s/]";
    private BrioConfirmDialog notificacion;
    private TextView caracteres;

    private DialogListener listener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {

            rootView = inflater.inflate(R.layout.admin_caja_registro, container, false);
            {


                modelManager = new ModelManager(getActivity());
                textNombre = (BrioEditText) rootView.findViewById(R.id.nombreCaja);
                caracteres = (TextView) rootView.findViewById(R.id.caracteres);
                guardar = (BrioButton2) rootView.findViewById(R.id.buttonSaveCaja);
                guardar.setOnClickListener(this);
                /*textNombre.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        Log.w("onTextChanged","ciclo infinitoooo!!!!");
                        if(!(s.toString()).equals(nombre)) {
                        //if(!(textNombre.getText().toString()).equals(nombre)) {
                            nombre = textNombre.getText().toString();
                            nombre = nombre.replaceAll(pattern, "");

                            textNombre.setText(nombre);
                            Log.w("onTextChanged", "ciclo infinitoooo dentro del if!!!! s:" + s + " nombre:" + nombre);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        /*textNombre.removeTextChangedListener(this);
                        s.beforeRemove();
                        //s.replace(0,0,nombre);
                        s.append(nombre);
                        textNombre.addTextChangedListener(this);
                        Log.w("afterTextChanged", "ciclo infinitoooo dentro del if!!!! s:" + s + " nombre:" + nombre);*/
                       /* if(!(s.toString()).equals(nombre)){
                            s.append(nombre);
                            Log.w("afterTextChanged", "ciclo infinitoooo dentro del if!!!! s:" + s + " nombre:" + nombre);
                        }*/

                  //  }
                //});*/

            }

            return rootView;
        }
    }


    /** Remueve el fragmento */

    public void removeThis() {
        ((BrioActivityMain) getActivity()).managerTeclado.closeKeyboard();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }


    @Override
    public void onClick(View btn) {

        switch (btn.getId()) {

            case R.id.buttonSaveCaja:

                Log.w("Registro caja", textNombre.getText().toString());
                if (textNombre.length() >= 3) {
                    caracteres.setText("");
                    caja = new Caja(Utils.getUUID(getContext()).toString(), textNombre.getText().toString());
                    String mensaje= (Utils.getString(R.string.form_registro_caja_notifocacion,getActivity())+
                            "\n" + caja.getNombre() +
                            "?");
                    DialogListener dialogListener = new DialogListener() {
                        @Override
                        public void onAccept() {
                            Long l = modelManager.cajas.save(caja);
                            Caja c = modelManager.cajas.getByIdCaja(l.intValue());
                            Log.w("Registro caja", "" + Utils.pojoToString(c));
                            boolean idCaja = modelManager.settings.update("id_CAJA",""+c.getIdCaja());
                            boolean nombreCaja = modelManager.settings.update("NOMBRE_CAJA",caja.getNombre());
                            Log.w("CAJA","registro settings id caja"+idCaja+" valor "+modelManager.settings.getByNombre("id_CAJA").getValor());
                            Log.w("CAJA","registro settings nombre caja"+nombreCaja+" valor "+modelManager.settings.getByNombre("NOMBRE_CAJA").getValor());

                            listener.onAccept();

                            removeThis();
                        }

                        @Override
                        public void onCancel() {

                        }
                    };

                    notificacion = new BrioConfirmDialog((AppCompatActivity) getActivity(), null, mensaje, null, dialogListener);
                    notificacion.show();


                    break;

                }else{
                    caracteres.setText(Utils.getString(R.string.form_registro_caja_caracteres, getActivity()));
                }

        }
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }
}

