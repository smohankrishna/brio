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
import net.hova_it.barared.brio.apis.models.entities.Usuario;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.views.BrioButton2;
import net.hova_it.barared.brio.apis.views.BrioEditText;

import java.util.List;


/**Clase que genera salidas de dinero a la caja
 * Created by Edith Maldonado on 23/12/2015.
 */
public class CajaSalida extends OptionMenuFragment implements  View.OnClickListener {


    private Spinner spinner_sal;
    private BrioEditText cantidad_sal;
    private BrioEditText concepto_sal;
    private BrioButton2 btnGoo;
    ModelManager manager;
    Usuario usuario;
    Context context;

    private View rootView;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         context = getActivity();

          rootView = inflater.inflate(R.layout.caja_salida, container, false);
                manager = new ModelManager(getActivity());
                usuario = new Usuario();
                usuario.getNombre();


                spinner_sal = (Spinner) rootView.findViewById(R.id.spinner_sa);
                cantidad_sal = (BrioEditText) rootView.findViewById(R.id.id_cantidad_sal);
                concepto_sal = (BrioEditText) rootView.findViewById(R.id.concepto_sa);
                btnGoo = (BrioButton2) rootView.findViewById(R.id.btnAceptar);
                btnGoo.setOnClickListener(this);


                List<Moneda> monedaSpinner = manager.monedas.getAll();
                Moneda moneda = new Moneda();
                moneda.setDescMoneda(Utils.getString(R.string.caja_entrada_spinner, context));
                moneda.setIdMoneda(-1);
                ArrayAdapter<Moneda> dataAdapter = new ArrayAdapter<Moneda>(this.getActivity(), android.R.layout.simple_spinner_item, monedaSpinner);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataAdapter.insert(moneda, 0);

                spinner_sal.setAdapter(dataAdapter);
                spinner_sal.setSelection(1);  //para fase uno se manejaran pesos por default
                spinner_sal.setEnabled(false);

              return rootView;
        }

    /**Click del boton aceptar, valida los campos que no esten vacios, realiza la confrimación, y genera el ticket
     * @param view boton aceptar
     */
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnAceptar:

                if (concepto_sal.getText().toString().equals("") ||
                        cantidad_sal.getText().toString().equals("")) {

                    BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) getActivity(),
                            Utils.getString(R.string.caja_entrada_datos_faltantes, context), "");
                    bad.show();

                } else {
                  creaTicketSalida();
                    Toast.makeText(context, Utils.getString(R.string.caja_salida_exitosa, context), Toast.LENGTH_SHORT).show();
                    remove();
                }


        }
    }

    /**Obtiene los datos de los campos y genera el ticket
     */
    private  void creaTicketSalida(){

        Ticket ticketCaja = new Ticket();
        SessionManager sessionManager = SessionManager.getInstance(context);
        ticketCaja.setDescripcion(concepto_sal.getText().toString());
        ticketCaja.setImporteNeto(Double.valueOf(cantidad_sal.getText().toString().length() < 1 ? "0" : cantidad_sal.getText().toString()));
        ticketCaja.setImporteBruto(Double.valueOf(cantidad_sal.getText().toString().length() < 1 ? "0" : cantidad_sal.getText().toString()));//Todo cuando ahi impuestos
        ticketCaja.setIdMoneda(((Moneda) (spinner_sal.getSelectedItem())).getIdMoneda());
        ticketCaja.setImpuestos(0.00);//Todo cuando ahi impuestos
        ticketCaja.setDescuento(0.00);
        ticketCaja.setIdCliente(0);//0 por ser administrativo
        ticketCaja.setIdComercio(sessionManager.readInt("idComercio"));
        ticketCaja.setIdUsuario(sessionManager.readInt("idUsuario"));
        ticketCaja.setIdCaja(sessionManager.readInt("idCaja"));
        ticketCaja.setCambio(0.00);
        ticketCaja.setIdTipoTicket(3);//ID 3 representa las salidas de dinero en el catalogo de la DB

        Log.w("caja", Utils.pojoToString(ticketCaja));

        Long _id = manager.tickets.save(ticketCaja);

        Ticket prueba = new Ticket();
        prueba = manager.tickets.getByIdTicket(_id.intValue());

        Log.w("caja", Utils.pojoToString(prueba));
    }

    @Override
    protected View getRootView() {
        return rootView;
    }

    @Override
    protected void beforeRemove() {

    }
}






