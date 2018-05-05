package net.hova_it.barared.brio.apis.setup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.views.BrioButton2;

/**
 * Fragment de términos y condiciones.
 *
 * El fragment cuenta con un listener que indica cuando el checkbox de
 * lectura de terminos se encuentra seleccionado y al pulsar el botón
 * aceptar indica mediante un listener.
 */
public class TerminosFragment extends Fragment implements  View.OnClickListener {

    private CheckBox checkTer;
    private BrioButton2 btnAceptarTerm;
    //private NotificacionDialog notificacionDialog;
    private Settings settings;
    private ModelManager modelManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {

            View root = inflater.inflate(R.layout.caja_terminos, container, false);
            {
                checkTer = (CheckBox) root.findViewById(R.id.CheckTer);
                btnAceptarTerm = (BrioButton2) root.findViewById(R.id.btnAceptarTerm);
                btnAceptarTerm.setOnClickListener(this);

                // En modo de depuración, auto acpetar lso terminos
                if(((BrioActivityMain) getContext()).DEBUG_SHOW_TOASTS) {
                    btnAceptarTerm.callOnClick();
                }

                //notificacionDialog = new NotificacionDialog();
            }

            return root;
        }

    }

    /**
     * Mostrar un aviso si no se han aceptado los términos y condiciones
     */
    private void dialogoAceptarTerminos() {

        BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) getActivity(),
                Utils.getString(R.string.brio_error, getActivity()),
                Utils.getString(R.string.terminos_noaccept, getActivity()));
        bad.show();

        /*
        DialogActionListener dialogActionListener = new DialogActionListener() {
            @Override
            public void onAcceptResult(Object results) {
                notificacionDialog.onDestroy();
            }

            @Override
            public void onCancelResult(Object results) {
                notificacionDialog.onDestroy();
            }

        };
        notificacionDialog = (NotificacionDialog.newInstance(
                "PARA CONTINUAR, ES NECESARIO\n ACEPTAR LOS TÉRMINOS Y\n CONDICIONES\n", dialogActionListener));
        notificacionDialog.show((getActivity()).getSupportFragmentManager(), "Confirmacion");
        */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CheckTer:
                break;
            case R.id.btnAceptarTerm:
                String ter="Terminos";
                // Autoaceptar los terminos si esta en modo de depuracion
                if(((BrioActivityMain) getContext()).DEBUG_SHOW_TOASTS){
                    modelManager = new ModelManager(getActivity());

                    //todo: guardar en bd que ya acepte
                    //settings = new Settings("Terminos","1");
                    //settings.setIdSetting(1);
                    //Log.w("Settings resID:", "" + modelManager.settings.update("Terminos","1"));


                    //Settings dada;
                    //dada = modelManager.settings.getByNombre("Terminos");
                    //Log.w("Settings Term(valor):", dada.getValor());

                    //settings=null;
                    //settings=settingsDAO.getByNombre("Terminos");
                    //if(settingsDAO.getByNombre("Terminos")== null){Log.w ("settings null","noooo");}
                    //Log.w ("obteniendo settings",modelManager.settings.getByNombre("Terminos").toString());

                    //...

                    //ya se acepto, me quito
                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

                    //lanzo el login
                    listener.onAccept();
                }else if (checkTer.isChecked()) { // Modo de producción

                    modelManager = new ModelManager(getActivity());

                    //todo: guardar en bd que ya acepte
                    settings = new Settings("Terminos","1");
                    settings.setIdSetting(1);
                    Log.w("Settings resID:", "" + modelManager.settings.update("Terminos","1"));


                    Settings dada;
                    dada = modelManager.settings.getByNombre("Terminos");
                    Log.w("Settings Term(valor):", dada.getValor());

                    //settings=null;
                    //settings=settingsDAO.getByNombre("Terminos");
                    //if(settingsDAO.getByNombre("Terminos")== null){Log.w ("settings null","noooo");}
                    //Log.w ("obteniendo settings",modelManager.settings.getByNombre("Terminos").toString());

                    //...

                    //ya se acepto, me quito
                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

                    //lanzo el login
                    listener.onAccept();
                } else {
                    dialogoAceptarTerminos();
                }
                break;
        }
    }

    /*  Agregado por Herman  */

    private DialogListener listener;

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

}