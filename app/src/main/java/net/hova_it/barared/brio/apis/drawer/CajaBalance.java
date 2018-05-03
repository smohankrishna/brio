package net.hova_it.barared.brio.apis.drawer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Balance;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.views.BrioButton2;

import java.text.NumberFormat;


/**Clase que muestra el balance de la caja
 * Created by Edith Maldonado on 27/01/2016.
 */

public class CajaBalance extends OptionMenuFragment implements View.OnClickListener {


    private TextView Sinicial, SalDinero, EntrDinero, VenFiado, VenEfect, VenPaTar, VenPaVal, total;
    private BrioButton2 btnAceptar;
    private ModelManager manager;




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            rootView = inflater.inflate(R.layout.caja_balance, container, false);

                manager = ((BrioBaseActivity) getActivity()).modelManager;
                mapControls(rootView);

            setBalanceValues();
            return rootView;


    }

    /**Cast de los campos con respecto a su id de la vista
     * @param root
     */
    private void mapControls(View root) {
        Sinicial = (TextView) root.findViewById(R.id.Sinicial);
        EntrDinero = (TextView) root.findViewById(R.id.EntrDinero);
        SalDinero = (TextView) root.findViewById(R.id.SalDinero);
       // VenFiado = (TextView) root.findViewById(R.id.VenFiado);
        VenEfect = (TextView) root.findViewById(R.id.VenEfect);
        VenPaTar = (TextView) root.findViewById(R.id.VenPaTar);
       // VenPaVal = (TextView) root.findViewById(R.id.VenPaVal);

        total = (TextView) root.findViewById(R.id.total);
        btnAceptar = (BrioButton2) root.findViewById(R.id.btnAceptar);
        btnAceptar.setOnClickListener(this);


    }

    /**Te trae los datos de la caja y los muestra en el layout   */
    private void setBalanceValues() {

        int idCaja = ((BrioBaseActivity) getActivity()).managerSession.readInt("idCaja");
        Balance balance = manager.balance.getBalance(idCaja);
        Log.w("Balance", idCaja + " " + balance.getVentasEfectivo());
        Log.w("Balance", Utils.pojoToString(balance));

        NumberFormat format = NumberFormat.getCurrencyInstance();

        Sinicial.setText(format.format(balance.getSaldoInicial()));
        EntrDinero.setText(format.format(balance.getEntradas()));
        SalDinero.setText(format.format(balance.getSalidas()));
       // VenFiado.setText(format.format(balance.getVentasFiado()));
        VenEfect.setText(format.format(balance.getVentasEfectivo()));
        VenPaTar.setText(format.format(balance.getVentasTarjeta()));
        //VenPaVal.setText(format.format(balance.getVentasVales()));
        //VenPaPun.setText(format.format(balance.getVentasPuntos()));
        total.setText(format.format(balance.getTotal()));




    }

    /**Al darle click en aceptar solo remueve el fragmento porque solo es una vista
     * @param view boton aceptar
     */
    @Override
    public void onClick(View view) {
        Fragment opcion = null;
        String tag = null;
        switch (view.getId()) {

            case R.id.btnAceptar:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(this)
                        .commit();

                   break;
        }
        if (opcion != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentHolder, opcion, tag)
                    .addToBackStack(tag)
                    .commit();
        }

        remove();


    }


    @Override
    protected View getRootView() {
        return rootView;
    }

    @Override
    protected void beforeRemove() {

    }
}
