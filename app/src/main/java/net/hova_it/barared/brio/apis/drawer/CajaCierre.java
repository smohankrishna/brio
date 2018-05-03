package net.hova_it.barared.brio.apis.drawer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Balance;
import net.hova_it.barared.brio.apis.models.entities.RegistroCierre;
import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.views.BrioButton2;
import net.hova_it.barared.brio.apis.views.BrioEditText;

/**Realiza el cierre de la caja y reinicia la aplicación
 * Created by Edith maldonado on 14/04/2016.
 */

public class CajaCierre extends OptionMenuFragment implements View.OnClickListener {

    private static final int TICKET_CAJA_ENTRADA = 2;
    private static final int TICKET_CAJA_SALIDA = 3;

    private final String TAG = "Cierre caja";

    private TextView CierreCajaEntrada;
    private TextView CierreCajaSalida;
    private TextView CierreCajaMontototal;
    private BrioEditText CierreCajaMontototalEfectivo;
    private BrioButton2 caja_cierre_btn_aceptar;
    private BrioButton2 caja_cierre_btn_cancelar;
    private ModelManager modelManager;
    private View rootView;
    private Context context;
    private SessionManager sessionManager;
    private RegistroCierre registroCierre;
    private Balance balance;
    private int idRegistroCierre;
    private BrioConfirmDialog dialogConfirmacion;

    private double remanente = 0;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.caja_cierre, container, false);

        context = getActivity();
        modelManager = new ModelManager(context);
        sessionManager = SessionManager.getInstance(context);


        balance = modelManager.balance.getBalance(1);


        CierreCajaEntrada = (TextView)rootView.findViewById(R.id.caja_cierre_entrada);
        CierreCajaEntrada.setText(String.format("%.2f",balance.getEntradas()));
        CierreCajaSalida = (TextView)rootView.findViewById(R.id.caja_cierre_salida);
        CierreCajaSalida.setText(String.format("%.2f",balance.getSalidas()));
        CierreCajaMontototal= (TextView)rootView.findViewById(R.id.caja_cierre_monto);
        CierreCajaMontototal.setText(String.format("%.2f",balance.calcularCierreCaja()));
        CierreCajaMontototalEfectivo = (BrioEditText)rootView.findViewById(R.id.caja_cierre_monto_efectivo);
        caja_cierre_btn_aceptar = (BrioButton2)rootView.findViewById(R.id.caja_cierre_btn_aceptar);
        caja_cierre_btn_aceptar.setOnClickListener(this);
        caja_cierre_btn_cancelar = (BrioButton2)rootView.findViewById(R.id.caja_cierre_btn_cancelar);
        caja_cierre_btn_cancelar.setOnClickListener(this);

        return rootView;
    }

    /**Click del boton aceptar, valida los campos, si las cantidades son diferentes genera un ticket de entrada o salida, guarda
     * los datos y reinicia la aplicacion
     * @param v boton aceptar
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.caja_cierre_btn_aceptar:
                    BrioAlertDialog brioAlertDialog = new BrioAlertDialog((AppCompatActivity) context,Utils.getString(R.string.caja_cierre_monto_dialog, context), "");

                String strmonto =  CierreCajaMontototalEfectivo.getText().toString();

                if(strmonto.length() == 0 ) {
                    // no ingresaron monto
                    Log.w(TAG,"cadena vacia");
                    brioAlertDialog.show();
                }else {

                    String mensaje = Utils.getString(R.string.caja_cierre_confirmacion, context);
                    DialogListener dialogListener = new DialogListener() {
                        @Override
                        public void onAccept() {
                            validacionCaja();
                            saveCierreCaja();
                            remove();
                            Utils.restartApp();
                        }

                        @Override
                        public void onCancel() {

                        }
                    };

                    dialogConfirmacion = new BrioConfirmDialog((AppCompatActivity) context, mensaje, "", null, dialogListener);
                    dialogConfirmacion.show();
                }
                break;

            case R.id.caja_cierre_btn_cancelar:
                remove();

                break;


        }

    }

    /**
     * Valida si la cantidad es menor o superior y genera un ticket de entrada o de salida
     */
    private void validacionCaja(){

   // BrioAlertDialog  brioAlertDialogen = new BrioAlertDialog((AppCompatActivity) context,"Ticket", "Se creo un ticket de entrada");

        String strmonto =  CierreCajaMontototalEfectivo.getText().toString();
            // si ingresaron monto
            double montoIngresado = Double.valueOf(strmonto);
            double montoTotal = balance.calcularCierreCaja();

            remanente = montoIngresado - montoTotal;

            if(remanente > 0) {
                //sobra dinero
                //Hacer entrada de dinero por remanente

                creaTicketCaja(TICKET_CAJA_ENTRADA);

                Log.w(TAG,"ticket guardado");
               // brioAlertDialogen.show(); //Checar si se pone el dialog de la generacion del ticket
            } else if(remanente < 0) {
                //falta dinero!!!
                //Hacer salida por abs(remanente)

                creaTicketCaja(TICKET_CAJA_SALIDA);
               /* brioAlertDialog = new BrioAlertDialog((AppCompatActivity) context,"Ticket", "Se creo un ticket de salida");
                brioAlertDialog.show();*/  //Checar si se pone el dialog de la generacion del ticket
            }

        }

    /**Guarda en base de datos el cierre de caja
     */
    private void saveCierreCaja(){

        int idCaja = ((BrioBaseActivity) getActivity()).managerSession.readInt("idCaja");


        double efectivoEnCierreCaja = CierreCajaMontototalEfectivo.getText().toString().length() == 0 ? 0 : Double.valueOf(CierreCajaMontototalEfectivo.getText().toString());

        RegistroCierre registroCierre = new RegistroCierre();
        registroCierre.setIdUsuario(sessionManager.readInt("idUsuario"));
        registroCierre.setIdCaja(idCaja);
        registroCierre.setFechaCierre(Utils.getCurrentTimestamp());
        registroCierre.setImporteContable(balance.calcularCierreCaja());
        registroCierre.setImporteReal(efectivoEnCierreCaja);
        registroCierre.setImporteRemanente(remanente);

        Long idCierreCaja = modelManager.registrosCierre.save(registroCierre);

        Log.w(TAG, "Cierre caja guardado " +
                Utils.pojoToString(modelManager.registrosCierre.getByIdRegistroCierre(idCierreCaja.toString())));
    }


    @Override
    protected View getRootView() {
        return rootView;
    }

    @Override
    protected void beforeRemove() {

    }

    /**
     * tipo:
     *   TICKET_CAJA_ENTRADA
     *   TICKET_CAJA_SALIDA
     * @param tipo ticket de entrada o salida
     */
    private void creaTicketCaja(int tipo) {

        SessionManager sessionManager = SessionManager.getInstance(context);

        //mapeando datos
        String desc = null;
        double importe = Math.abs(remanente);
        int
            idComercio = 1,
            tipoTicket = tipo;

        switch (tipo) {
            case TICKET_CAJA_ENTRADA:
                desc = "Positivo";
                break;
            case TICKET_CAJA_SALIDA:
                desc = "Negativo";
                break;
        }

        //Seteando datos

        Ticket ticketCaja = new Ticket();
        ticketCaja.setDescripcion("Remanente " + desc);
        ticketCaja.setImporteNeto(importe);
        ticketCaja.setImporteBruto(importe);//Todo cuando hay impuestos
        ticketCaja.setIdMoneda(1);
        ticketCaja.setImpuestos(0.00);//Todo cuando ahi impuestos
        ticketCaja.setDescuento(0.00);
        ticketCaja.setIdCliente(0);//0 por ser administrativo
        ticketCaja.setIdComercio(sessionManager.readInt("idComercio"));//Fixme
        ticketCaja.setIdUsuario(sessionManager.readInt("idUsuario"));
        ticketCaja.setIdCaja(sessionManager.readInt("idCaja"));
        ticketCaja.setCambio(0.00);
        ticketCaja.setIdTipoTicket(tipoTicket);//directo de catalogo

        Log.w("caja", Utils.pojoToString(ticketCaja));

        Long _id = modelManager.tickets.save(ticketCaja);
        ticketCaja = modelManager.tickets.getByIdTicket(_id.intValue());

        Log.w("caja", Utils.pojoToString(ticketCaja));
    }

}
