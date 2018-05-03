package net.hova_it.barared.brio.apis.drawer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Moneda;
import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.views.BrioButton2;
import net.hova_it.barared.brio.apis.views.BrioEditText;

import java.util.List;

/**Genera entradas de dinero a la caja
 * Created by Edith Maldonado on 23/12/2015.
 */
public class CajaEntrada extends OptionMenuFragment implements  View.OnClickListener {


    private Spinner spinner;
    private BrioEditText cantidad, concepto;
    private BrioButton2 btnAceptar;
    private ModelManager manager;
    private Context context;



    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

            context = getActivity();
            manager = new ModelManager(context);
            rootView = inflater.inflate(R.layout.caja_entrada, container, false);
            cantidad = (BrioEditText)rootView.findViewById(R.id.id_cantidad);
            concepto = (BrioEditText)rootView.findViewById(R.id.concepto);
            spinner = (Spinner) rootView.findViewById(R.id.spinnerr);

        btnAceptar = (BrioButton2) rootView.findViewById(R.id.btnAceptar);
                    btnAceptar.setOnClickListener(this);


                List<Moneda> monedaSpinner = manager.monedas.getAll();
                 Moneda moneda = new Moneda();
                ArrayAdapter<Moneda> dataAdapter;
                                   moneda.setIdMoneda(-1);
                                    dataAdapter = new ArrayAdapter<Moneda>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, monedaSpinner);
                                        moneda.setDescMoneda(Utils.getString(R.string.caja_entrada_spinner, context));
                                            dataAdapter.insert(moneda, 0);
                                                spinner.setAdapter(dataAdapter);
                                                spinner.setSelection(1);  //para fase uno se manejaran pesos por default
                                                spinner.setEnabled(false);

            return rootView;

    }

    /**click del botón aceptar valida los campos, realiza la confirmación y genera el ticket
     * @param v boton aceptar
     */
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnAceptar:
                if (concepto.getText().toString().equals("") ||
                        cantidad.getText().toString().equals("")) {

                    BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) getActivity(),
                            Utils.getString(R.string.caja_entrada_datos_faltantes, context), "");
                    bad.show();
                } else {
                    creaTicketCaja();
                    Toast.makeText(context, Utils.getString(R.string.caja_entrada_exitosa, context), Toast.LENGTH_SHORT).show();
                    remove();
                }
        }
    }


    /**Crea el ticket de entrada obteniendo la información de los campos y los guarda en la base de datos
     */
    private void creaTicketCaja() {
        Ticket ticketCaja = new Ticket();
        SessionManager sessionManager = SessionManager.getInstance(context);
        ticketCaja.setDescripcion(concepto.getText().toString());
        ticketCaja.setImporteNeto(Double.valueOf(cantidad.getText().toString().length() < 1 ? "0" : cantidad.getText().toString()));
        ticketCaja.setImporteBruto(Double.valueOf(cantidad.getText().toString().length() < 1 ? "0" : cantidad.getText().toString()));//Todo cuando ahi impuestos
        ticketCaja.setIdMoneda(((Moneda) (spinner.getSelectedItem())).getIdMoneda());
        ticketCaja.setImpuestos(0.00);//Todo cuando ahi impuestos
        ticketCaja.setDescuento(0.00);
        ticketCaja.setIdCliente(0);//0 por ser administrativo
        ticketCaja.setIdComercio(sessionManager.readInt("idComercio"));
        ticketCaja.setIdUsuario(sessionManager.readInt("idUsuario"));
        ticketCaja.setIdCaja(sessionManager.readInt("idCaja"));
        ticketCaja.setCambio(0.00);
        ticketCaja.setIdTipoTicket(2);//ID 2 representa las entradas de dinero en el catalogo de la DB

        Log.w("caja", Utils.pojoToString(ticketCaja));

        Long _id = manager.tickets.save(ticketCaja);

        ticketCaja = manager.tickets.getByIdTicket(_id.intValue());

        Log.w("caja", Utils.pojoToString(ticketCaja));
    }



    @Override
    protected View getRootView() {
        return rootView;
    }

    @Override
    protected void beforeRemove() {


    }
}