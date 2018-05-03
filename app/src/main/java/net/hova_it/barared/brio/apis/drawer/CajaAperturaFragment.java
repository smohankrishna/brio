package net.hova_it.barared.brio.apis.drawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.RegistroApertura;
import net.hova_it.barared.brio.apis.models.entities.RegistroCierre;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.views.BrioButton2;
import net.hova_it.barared.brio.apis.views.BrioEditText;

/**Clase que realiza la apertura de la caja  */
public class CajaAperturaFragment extends Fragment implements View.OnClickListener {

    private SessionManager managerSession;
    private ModelManager modelManager;

    private TextView ultimoCierre, cierreRealizadoPor;
    private View rootView;
    private BrioEditText efectivoReal;
    private BrioButton2 btnAceptar;
    private String ultCierre;
    public boolean status = false;

    public RegistroApertura lastApertura = null;
    public RegistroCierre lastCierre;

    public boolean getStatus() {
        return status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.caja_apertura, container, false);

        ultimoCierre = (TextView) rootView.findViewById(R.id.CA_cierre_mas_reciente);
        cierreRealizadoPor = (TextView) rootView.findViewById(R.id.CA_cierre_realizado_por);
        efectivoReal = (BrioEditText) rootView.findViewById(R.id.CA_efectivo_real);
        btnAceptar = (BrioButton2) rootView.findViewById(R.id.CA_btn_aceptar);
        btnAceptar.setOnClickListener(this);
        managerSession = SessionManager.getInstance(getActivity());

        configureUI(rootView);

        return rootView;
    }

    /**Muestra el ultimo cierre caja y por quien fue hecho
     * @param root regresa la vista
     */
    private void configureUI(View root) {
        modelManager = new ModelManager(getActivity());
        lastApertura = modelManager.registrosApertura.getLast();
        lastCierre = modelManager.registrosCierre.getLast();

        ultCierre = String.format("%.2f", lastCierre.getImporteReal());
        ultimoCierre.setText("$" + ultCierre);//// TODO: obtener cierre
        cierreRealizadoPor.setText(modelManager.usuarios.getByIdUsuario(lastCierre.getIdUsuario()).getUsuario() + " " + Utils.getBrioDate(lastCierre.getFechaCierre()));
    }


    /**Valida el status de la caja y guarda el registro en base de datos */
    private void validacion() {

        boolean status = false;

        if (ultimoCierre.getText().toString().equals("") || efectivoReal.getText().toString().equals("")) {

            BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) getActivity(),
                    Utils.getString(R.string.CajaApertura_ingreso_erroneo_informacion_faltante, getContext()),
                    "");
            bad.show();
        } else {
            double cierreReciente = Double.valueOf(ultCierre);

            double efectivoEnCaja = efectivoReal.getText().toString().length()==0? 0 : Double.valueOf(efectivoReal.getText().toString());

            //if ( Double.valueOf(ultimoCierre.getText().toString()) == Double.valueOf(efectivoReal.getText().toString()) ) {
            if (cierreReciente == efectivoEnCaja) {
                RegistroApertura Apertura = new RegistroApertura();
                int idCaja = ((BrioBaseActivity) getActivity()).managerSession.readInt("idCaja");
                Apertura.setIdRegistroCierre(lastCierre.getIdRegistroCierre());
                Apertura.setIdCaja(idCaja);
                Apertura.setIdUsuario(managerSession.readInt("idUsuario"));
                Apertura.setFechaApertura(Utils.getCurrentTimestamp());
                Apertura.setImporteContable(Double.valueOf(efectivoReal.getText().toString()));
                Apertura.setImporteReal(Double.valueOf(efectivoReal.getText().toString()));

                Long idAC = modelManager.registrosApertura.save(Apertura);

                Log.w("Apertura", "Apertura guardada " +
                        Utils.pojoToString(modelManager.registrosApertura.getByIdRegistroApertura(idAC.intValue())));


                status = true;

                this.status = status;

                remove();

            } else {
                BrioAlertDialog noEquals = new BrioAlertDialog((AppCompatActivity) getActivity(),
                        Utils.getString(R.string.CajaApertura_ingreso_erroneo_msn, getContext()),
                       "");
                noEquals.show();
            }
        }
    }

    /**Click de aceptar en la caja el cual valida que las cantidades sean iguales
     * @param v boton aceptar
     */
    public void onClick(View v) {
        ((BrioActivityMain) getActivity()).managerTeclado.closeKeyboard();

        switch (v.getId()) {
            case R.id.CA_btn_aceptar:

                validacion();
                break;
        }
    }

    /**Abre el pos despues de abrir la caja, es decir remueve el fragment de la caja
     */
    private void remove() {
        ((BrioBaseActivity) getActivity()).abrirPOSDespuesDeAperturaCaja();

        getActivity().getSupportFragmentManager()
            .beginTransaction()
                .remove(this)
            .commit();
    }
}
