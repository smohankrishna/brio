package net.hova_it.barared.brio.apis.payment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;



import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.GPSTracker;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.hw.billpocket.BillPocketChicken;
import net.hova_it.barared.brio.apis.hw.billpocket.BillPocketService;
import net.hova_it.barared.brio.apis.hw.billpocket.BuzonSalidaBillPocketService;
import net.hova_it.barared.brio.apis.hw.printer.PrinterManager;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.CardPaymentRequest;
import net.hova_it.barared.brio.apis.models.entities.Inventario;
import net.hova_it.barared.brio.apis.models.entities.ItemsTicket;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.pos.api.ItemsTicketController;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.GlobalExceptionHandler;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAcceptDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.utils.info.BrioErrorReportPojo;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoManager2;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoOnClickListener;

import org.json.JSONException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

//import lat.brio.api.dongle.Dongle;
//import lat.brio.api.dongle.IDongle;
import lat.brio.api.dongle.IDongle;
import lat.brio.api.dongle.ServicioBBPosDev;
import lat.brio.api.dongle.SistemaBBPos;
import lat.brio.api.pagos.IPago;
import lat.brio.api.pagos.OPago;
import lat.brio.api.pagos.Pago;
import lat.brio.api.dongle.Dongle;
/**
 * Esta clase se encarga del manejo de cobro de ventas y servicios
 * de POS y de PAGO DE SERVICIOS. También se encarga de guardar
 * los pojos tickets generados por las ventas en la base de datos.
 *
 * También se encarga de la conexión con BillPocket para realizar
 * cobros con tarjeta
 *
 * Created by Herman Peralta on 23/02/2016.
 */
public class PaymentService {
    private static PaymentService daService;

    private SuperTicket mSTicket;

    private BrioActivityMain _Contexto;
    private BrioBaseActivity mainActivity;
    private PaymentDialogFragment paymentDialogFragment;

    private AccountPaymentListener accountPaymentListener;

    private BillPocketService serviceBillPocket;
    private Dongle _Dongle;

    private PrinterManager managerPrinter;
    private ModelManager managerModel;
    private SessionManager managerSession;
    private TecladoManager2 managerTeclado;

    private String idDongle;
    private Long idTicket;

    /**
     * El estatus del pago del ticket:
     *
     * ACCOUNT_STATUS.PAGO_COMPLETADO - cuando se ha aceptado el pago completo del ticket - En este estatus, PaymentService guarda en BD el ticket.
     * ACCOUNT_STATUS.ENVIADO - Despues de PAGO_COMPLETADO, el status ENVIADO indica que el flujo ha pasado cuando el PaymentDialogFragment muestra el ticket generado y el usuario especifica continuar (ya sea enviando o no el ticket).
     * ACCOUNT_STATUS.INCOMPLETO - Cuando se ha aceptado un PAGO pero la cantidad es menor al monto total a cobrar (Se pueden generar varios eventos de este tipo).
     * ACCOUNT_STATUS.CANCELADO - Cuando el usuario cancela el cobro del ticket.
     */
    public enum ACCOUNT_STATUS {
        PAGO_COMPLETADO,
        ENVIADO,
        INCOMPLETO,
        CANCELADO
    }

    /*
     * Estos valores estan mapeados con los tipos de pagos
     * en la tabla Tipo_pago
     */
    public final static int TIPO_PAGO_EFECTIVO = 1;
    public final static int TIPO_PAGO_TARJETA  = 2;
    public final static int TIPO_PAGO_VALES    = 3;
    public final static int TIPO_PAGO_FIADO    = 4;

    /*
     * Estos valores estan mapeados con los tipos de pagos
     * en la tabla Tipo_Tickets
     */
    public final static int TIPO_TICKET_POS = 1;
    public final static int TIPO_TICKET_ENTRADA_CAJA = 2;
    public final static int TIPO_TICKET_SALIDA_CAJA = 3;
    public final static int TIPO_TICKET_SERVICIO = 4;
    public final static int TIPO_TICKET_TAE = 5;
    public final static int TIPO_TICKET_INTERNET = 6;
    public final static int TIPO_TICKET_BANCO_ENTRADA = 7; //Abono
    public final static int TIPO_TICKET_BANCO_SALIDA = 8;
    public final static int TIPO_TICKET_BANCO_COMPRA = 9;
    public final static int TIPO_TICKET_WESTERN_PAGO = 10;
    public final static int TIPO_PAGO_SEGURO = 11;



    public static PaymentService getInstance(BrioActivityMain Contexto) {
        if(daService == null) {
            daService = new PaymentService(Contexto);
        }

        return daService;
    }

    private PaymentService(BrioActivityMain Contexto) {
        _Contexto = Contexto;
        this.mainActivity = _Contexto;
        this.managerPrinter = PrinterManager.getInstance(_Contexto);

        managerModel = new ModelManager(_Contexto);

        serviceBillPocket = BillPocketService.getInstance(_Contexto);
        managerSession = SessionManager.getInstance(_Contexto);
        managerTeclado = TecladoManager2.getInstance(_Contexto);

        try{
            idDongle = managerModel.settings.getByNombre("ID_DONGLE").getValor();
        }catch(Exception Ex){
            idDongle = "CHB106704002000"; // Pruebas por omision de argumento ID_DONGLE
            Ex.printStackTrace();
        }
        _Dongle = new Dongle(_Contexto);
    }

    /**
     * Enviar a cobrar un ticket.
     * @param STicket - El ticket a cobrar
     * @param accountPaymentListener
     */
    public void payTicket(final SuperTicket STicket, AccountPaymentListener accountPaymentListener) {
        DebugLog.log(getClass(), "PAYMENT", "voy a cobrar ticket\n" + Utils.pojoToString(STicket));
        this.mSTicket = STicket;
        this.accountPaymentListener = accountPaymentListener;

        paymentDialogFragment = new PaymentDialogFragment();
        paymentDialogFragment.showFragment(_Contexto, R.id.paymentHolder, STicket, accountPaymentListener);
    }

    /**
     * Este metodo se invoca cuando ya se ha ingresado el dinero completo de la venta.
     * @param STicket
     * @param accountPaymentListener
     */
    public void saveAndSendTicket(final SuperTicket STicket, AccountPaymentListener accountPaymentListener) {
        DebugLog.log(getClass(), "PAYMENT", "voy a enviar ticket");
        DebugLog.log(getClass(), "saveAndSendTicket",  Utils.toJSONString(STicket));
        this.mSTicket = STicket;
        this.accountPaymentListener = accountPaymentListener;

        //guardar el ticket
        int idTicket = saveSuperTicket(_Contexto, mSTicket);

        PaymentSendTicketFragment paymentSendTicketFragment = new PaymentSendTicketFragment();
        paymentSendTicketFragment.showFragment(_Contexto, idTicket, R.id.paymentHolder, mSTicket, accountPaymentListener); //R.id.fragmentHolder, R.id.brio_component_root no deja ver las pestanas
    }

    /**
     * Agregar un pago al ticket.
     *
     * @param TIPO_PAGO
     * @param pagoAttachListener
     */
    public void addPago(int TIPO_PAGO, final PagoAttachListener pagoAttachListener) {
        DebugLog.log(getClass(), "PAYMENT", "Creo nuevo TicketTipoPago");

//        managerSession.update();

        switch (TIPO_PAGO) {
            case TIPO_PAGO_EFECTIVO:
                managerTeclado.openKeyboard(true, TecladoManager2.TIPO_TECLADO.NUMERICO,
                        Utils.getString(R.string.payment_efectivo, _Contexto),
                        "0.00",
                        new TecladoOnClickListener() {
                            @Override
                            public void onAcceptResult(Object... results) {
                                MediaUtils.hideSystemUI(_Contexto);

                                Double monto = (Double) results[0];

                                DebugLog.log(getClass(), "PAYMENT", "Efectivo monto: " + monto);

                                final TicketTipoPago nuevoPago = new TicketTipoPago();
                                nuevoPago.setMonto(monto);
                                nuevoPago.setIdTipoPago(TIPO_PAGO_EFECTIVO);
                                nuevoPago.setDescripcion("EFECTIVO");
                                nuevoPago.setIdMoneda(1); //PESOS

                                managePago(nuevoPago, pagoAttachListener);
                            }

                            @Override
                            public void onCancelResult(Object... results) {
                                MediaUtils.hideSystemUI(_Contexto);
                            }
                        });
                break;

            case TIPO_PAGO_TARJETA:
                /**
                 * cuando se hace un pago con tarjeta con billpocket, se invoca la app de billpocket con un intent,
                 * y lo que regresa la transacción de billpocket llega por el método onActivityResult(), que es donde se procesa el pago.
                 *
                 * mientras qye cuando se hace un pago por bbpos(dongle), se instancia Dongle y se comunica por la interfaz IDongle
                 * por medio d ela cual indica el proceso de el pago a BBPOS
                 */
                managerTeclado.openKeyboard(true, TecladoManager2.TIPO_TECLADO.NUMERICO,
                        Utils.getString(R.string.payment_tarjeta, _Contexto),
                        String.format("%.2f", mSTicket.getTicket().getImporteNeto()),
                        new TecladoOnClickListener() {
                            @Override
                            public void onAcceptResult(Object... results) {
                                MediaUtils.hideSystemUI(_Contexto);
                                final Double monto = (Double) results[0];
                                final String Monto = String.valueOf(results[0]);
                                SessionManager sessionManager = SessionManager.getInstance(_Contexto);
                                if (monto <= mSTicket.getTicket().getImporteNeto()) {
                                    // Tipo de pago
                                    String tipoDongle = "1";
                                    try {
                                        tipoDongle = managerModel.settings.getByNombre("TIPO_DONGLE").getValor();
                                    }catch(Exception Ex){
                                        Ex.printStackTrace();
                                    }

                                    if(tipoDongle.equals("0")) {
                                        // Bill Pocket
                                        DebugLog.log(getClass(), "PAYMENT", "BillPocket monto: " + monto);

                                        BillPocketChicken chicken = new BillPocketChicken();

                                        chicken.setPagoAttachListener(pagoAttachListener);
                                        chicken.setPin(BillPocketService.ARG_PIN);
                                        chicken.setAmount(monto);
                                        chicken.setIdentifier("1"); // TODO: poner random aqui


                                        String comercio = sessionManager.readString("descripcionComercio");
                                        chicken.setReference(comercio);

                                        serviceBillPocket.sendPayment(chicken);
                                    }else{
                                        // Desactivar alertas durante el proceso de pago
//                                        ((BrioActivityMain)_Contexto).Alertas(false);

                                        // Inicializar tracker GPS
                                        GPSTracker gpsTracker = new GPSTracker(_Contexto);
                                        if(gpsTracker.canGetLocation()){
                                            // Conectar con el dispositivo

                                            if(_Dongle==null){
                                                _Dongle = new Dongle(_Contexto);

                                            }
                                            String comercio = "" + sessionManager.readInt("idComercio");

                                            _Dongle.Conectar(comercio, idDongle, new IDongle() {
                                                @Override
                                                public void alError(Error Error) {
                                                    Toast.makeText(_Contexto, "Error: " + Error.getMessage(), Toast.LENGTH_LONG).show();
                                                    // Log.w("PaymentServiceClass", Error.getMessage());
                                                    // TODO: showAllBanners un mensaje y finalizar proceso
                                                    // _Dongle.Finalizar();
                                                    MediaUtils.hideSystemUI(_Contexto);
                                                    BrioAlertDialog bad = new BrioAlertDialog(_Contexto, "Advertencia", "Error: " + Error.getMessage());
                                                    bad.show();
                                                    _Dongle.Finalizar();
                                                }

                                                @Override
                                                public void alConectar(String ID) {
                                                    // Aqui ya se conectó
                                                    // Limpiar los datos
                                                    mSTicket.Voucher = "";
                                                    mSTicket.TipoTarjeta = "";
                                                    mSTicket.Emisor = "";
                                                    mSTicket.Tarjeta = "";
                                                    mSTicket.Autorizacion = "";
                                                    mSTicket.Transaccion = "";
                                                    mSTicket.Afiliacion = "";
                                                    mSTicket.Referencia = "";
                                                    mSTicket.Titular = "";
                                                    mSTicket.Firma = "";
                                                    mSTicket.Arqc = "";
                                                    mSTicket.Aid = "";
                                                    mSTicket.Tvr = "";
                                                    mSTicket.Tsi = "";
                                                    mSTicket.Apn = "";
                                                    mSTicket.Expira = "";
                                                    // Iniciar lectura de tarjeta
                                                    _Dongle.Leer(Monto);
                                                }

                                                @Override
                                                public void alAutorizar(){
                                                    // Solicitar firma
                                                    _Dongle.Firmar();
                                                }


                                                @Override
                                                public void alRechazar(String Codigo, String Mensaje) {
                                                    BrioAlertDialog bad = new BrioAlertDialog(_Contexto, "Rechazado", Mensaje);
                                                    bad.show();
                                                }

                                                @Override
                                                public void alDeclinar(String Codigo, String Mensaje) {
                                                    BrioAlertDialog bad = new BrioAlertDialog(_Contexto, "Declinado", Mensaje);
                                                    bad.show();
                                                }

                                                @Override
                                                public void alCambiar(String Estado) {

                                                }


                                                @Override
                                                public void alConcluir(OPago Pago) {
                                                    // Aqui se indica que finalizo
                                                    final TicketTipoPago nuevoPago = new TicketTipoPago();
                                                    nuevoPago.setMonto(monto);
                                                    nuevoPago.setIdTipoPago(TIPO_PAGO_TARJETA);

                                                    nuevoPago.setIdMoneda(1); //PESOS

                                                    DebugLog.log(getClass(), "PAYMENT", "Brio Pago monto aceptado: " + nuevoPago.getMonto());

                                                    CardPaymentRequest cprPago = new CardPaymentRequest();
                                                    cprPago.setCardtype(Pago.Valor("Cuenta").toString());
                                                    cprPago.setCreditcard(Pago.Valor("Tarjeta").toString());
                                                    cprPago.setAuthorization(Pago.Valor("Autorizacion").toString());

                                                    // Cargar el pago
                                                    nuevoPago.setDescripcion(Utils.toJSON(cprPago));

                                                    // Por hacer: Embeber la firma en html
                                                    mSTicket.Voucher = Pago.Valor("Voucher").toString();
                                                    mSTicket.TipoTarjeta = Pago.Valor("Cuenta").toString();
                                                    mSTicket.Emisor = Pago.Valor("Emisor").toString();
                                                    mSTicket.Tarjeta = Pago.Valor("Tarjeta").toString();
                                                    mSTicket.Autorizacion = Pago.Valor("Autorizacion").toString();
                                                    mSTicket.Transaccion = Pago.Valor("Transaccion").toString();
                                                    mSTicket.Afiliacion = Pago.Valor("Afiliacion").toString();
                                                    mSTicket.Referencia = Pago.Valor("Referencia").toString();
                                                    mSTicket.Firma = Pago.Valor("Firma").toString();
                                                    mSTicket.Titular = Pago.Valor("Titular").toString();
                                                    mSTicket.Arqc = Pago.Valor("Arqc").toString();
                                                    mSTicket.Aid = Pago.Valor("Aid").toString();
                                                    mSTicket.Tvr = Pago.Valor("Tvr").toString();
                                                    mSTicket.Tsi = Pago.Valor("Tsi").toString();
                                                    mSTicket.Apn = Pago.Valor("Apn").toString();
                                                    mSTicket.Expira = Pago.Valor("Expira").toString();


                                                    // Meto el pago al ticket
                                                    managePago(nuevoPago, pagoAttachListener);
                                                }

                                                @Override
                                                public void alConcluir(OPago Pago, String ARPC) {
                                                    // Aqui se indica que finalizo
                                                    final TicketTipoPago nuevoPago = new TicketTipoPago();
                                                    nuevoPago.setMonto(monto);
                                                    nuevoPago.setIdTipoPago(TIPO_PAGO_TARJETA);

                                                    nuevoPago.setIdMoneda(1); //PESOS

                                                    DebugLog.log(getClass(), "PAYMENT", "Brio Pago monto aceptado: " + nuevoPago.getMonto());

                                                    CardPaymentRequest cprPago = new CardPaymentRequest();
                                                    cprPago.setCardtype(Pago.Valor("Cuenta").toString());
                                                    cprPago.setCreditcard(Pago.Valor("Tarjeta").toString());
                                                    cprPago.setAuthorization(Pago.Valor("Autorizacion").toString());

                                                    // Cargar el pago
                                                    nuevoPago.setDescripcion(Utils.toJSON(cprPago));

                                                    // Por hacer: Embeber la firma en html
                                                    mSTicket.Voucher = Pago.Valor("Voucher").toString();
                                                    mSTicket.TipoTarjeta = Pago.Valor("Cuenta").toString();
                                                    mSTicket.Emisor = Pago.Valor("Emisor").toString();
                                                    mSTicket.Tarjeta = Pago.Valor("Tarjeta").toString();
                                                    mSTicket.Autorizacion = Pago.Valor("Autorizacion").toString();
                                                    mSTicket.Transaccion = Pago.Valor("Transaccion").toString();
                                                    mSTicket.Afiliacion = Pago.Valor("Afiliacion").toString();
                                                    mSTicket.Referencia = Pago.Valor("Referencia").toString();
                                                    mSTicket.Titular = Pago.Valor("Titular").toString();
                                                    mSTicket.Firma = Pago.Valor("Firma").toString();
                                                    mSTicket.Arqc = Pago.Valor("Arqc").toString();
                                                    mSTicket.Aid = Pago.Valor("Aid").toString();
                                                    mSTicket.Tvr = Pago.Valor("Tvr").toString();
                                                    mSTicket.Tsi = Pago.Valor("Tsi").toString();
                                                    mSTicket.Apn = Pago.Valor("Apn").toString();
                                                    mSTicket.Expira = Pago.Valor("Expira").toString();

                                                    // Meto el pago al ticket
                                                    managePago(nuevoPago, pagoAttachListener);
                                                }
                                            });
                                        }else{
                                            MediaUtils.hideSystemUI(_Contexto);
                                            BrioAlertDialog bad = new BrioAlertDialog(_Contexto, "Advertencia", "Por favor active los servicios de ubicación e intentelo nuevamente.");
                                            bad.show();
                                        }
                                    }
                                } else {
                                    MediaUtils.hideSystemUI(_Contexto);

                                    BrioAlertDialog bad = new BrioAlertDialog(_Contexto,
                                            "Pago con tarjeta",
                                            String.format("El monto introducido para pago con tarjeta\nno puede ser mayor que $%.2f", mSTicket.getTicket().getImporteNeto()));
                                    bad.show();
                                }
                            }

                            @Override
                            public void onCancelResult(Object... results) {

                            }
                        });
                break;

            default:
                BrioAlertDialog bad = new BrioAlertDialog(_Contexto, "Pagos", "Forma de pago no disponible");
                bad.show();
                break;
        }
    }

    /**
     * Procesar el pago agregado.
     *
     * @param ticketTipoPago
     * @param pagoAttachListener
     */
    private void managePago(TicketTipoPago ticketTipoPago, final PagoAttachListener pagoAttachListener) {
        DebugLog.log(getClass(), "PAYMENT", "pago aceptado: " + Utils.pojoToString(ticketTipoPago));

//        managerSession.update();

        //Agrego el pago
        switch (paymentDialogFragment.getPagosController().addPago(ticketTipoPago)) {
            case PAGO_COMPLETADO:
                DebugLog.log(getClass(), "PAYMENT", "El pago esta completo");

                pagoAttachListener.onPagoAttached(ticketTipoPago);

                //saveSuperTicket();

                paymentDialogFragment.onVentaFinalizada(mSTicket.getTicket().getCambio());
                break;

            case INCOMPLETO:
                accountPaymentListener.onAccountPaymentStatusChange(ACCOUNT_STATUS.INCOMPLETO);
                break;
        }
    }

    /**
     * Este metodo se encarga de guardar el Ticket de la venta actual en la base de datos,
     * incluyendo la lista de ItemsTicket y la lista de TicketTipoPago
     *
     * @param Contexto
     * @param superTicket
     * @return
     */
    public static int saveSuperTicket(Context Contexto, SuperTicket superTicket) {
        DebugLog.log(PaymentService.class, "PAYMENT", "Guardar el superTicket a la base de datos");
        Log.i("saveAndSendTicket",  Utils.toJSONString(superTicket));

        SessionManager managerSession = SessionManager.getInstance(Contexto);
        ModelManager managerModel = new ModelManager(Contexto);

        superTicket.getTicket().setIdCaja(managerSession.readInt("idCaja"));
        superTicket.getTicket().setIdComercio(managerSession.readInt("idComercio"));
        superTicket.getTicket().setIdUsuario(managerSession.readInt("idUsuario"));
        superTicket.getTicket().setDescripcion(superTicket.getTicket().getDescripcion()); //FIXME poner descripcion

        ArrayList<Integer> idTicketTipoPagoBillPocket = new ArrayList<>();

        String msg;
        Long idTicket = -1L;

        try {
            ArrayList<TicketTipoPago> pagos = new ArrayList<>();
            idTicket = managerModel.tickets.save(superTicket.getTicket());
            superTicket.getTicket().setIdTicket(idTicket.intValue());

            Ticket tmp = managerModel.tickets.getByIdTicket(idTicket.intValue());


            superTicket.getTicket().setTimestamp(tmp.getTimestamp());

            DebugLog.log(PaymentService.class, "FECHA", Utils.pojoToString(tmp));

            DebugLog.log(PaymentService.class, "PAYMENT", "**********************************************\nTicket guardado con id: " + idTicket.intValue() + "\n" + Utils.pojoToString(superTicket.getTicket()));

            /*----------------------------------------------------------------------------
             GUARDANDO PAGOS
             -----------------------------------------------------------------------------*/
            double sumEfectivo = 0.0;
            TicketTipoPago ticketTipoPagoSumEfectivo = new TicketTipoPago();
            ticketTipoPagoSumEfectivo.setIdTipoPago(1);
            ticketTipoPagoSumEfectivo.setIdMoneda(1);
            ticketTipoPagoSumEfectivo.setDescripcion("EFECTIVO");
            ticketTipoPagoSumEfectivo.setIdTicket(idTicket.intValue());

            DebugLog.log(PaymentService.class, "PAYMENT", "*********************TICKET PAGOS**********************");
            for(TicketTipoPago ticketTipoPago : superTicket.ticketTipoPago) {
                switch (ticketTipoPago.getIdTipoPago()) {
                    case TIPO_PAGO_EFECTIVO:
                        sumEfectivo += ticketTipoPago.getMonto();
                        break;

                    default:
                        pagos.add(ticketTipoPago);
                        break;

                }
            }

            //redondeando totales para evitar perdida de precision double
            sumEfectivo = sumEfectivo - superTicket.getTicket().getCambio();

            sumEfectivo = Utils.getDoubleRounded(sumEfectivo);

            ticketTipoPagoSumEfectivo.setMonto(sumEfectivo);

            //Guardo pagos en efectivo
            if(ticketTipoPagoSumEfectivo.getMonto() > 0) {
                managerModel.ticketTipoPago.save(ticketTipoPagoSumEfectivo);

                DebugLog.log(PaymentService.class, "PAYMENT", "TicketTipoPago tipo '" + ticketTipoPagoSumEfectivo.getIdTipoPago() + "' EFECTIVO guardado:\n" + Utils.pojoToString(ticketTipoPagoSumEfectivo));
            }

            //Guardo los demás pagos
            for(TicketTipoPago pago : pagos) {

                CardPaymentRequest cardPaymentRequest = null;
                boolean encolaPagoTarjeta = true;
                if(pago.getIdTipoPago() == TIPO_PAGO_TARJETA) {
                    try {
                        cardPaymentRequest = (CardPaymentRequest)Utils.fromJSON(pago.getDescripcion(), CardPaymentRequest.class);
                        if(cardPaymentRequest != null) {
                            cardPaymentRequest.setIdTicket(idTicket.intValue());
                            pago.setDescripcion(Utils.toJSON(cardPaymentRequest));

                            encolaPagoTarjeta = !cardPaymentRequest.getReference().contains("<E>");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                pago.setIdTicket(idTicket.intValue());
                Long idTicketTipoPago = managerModel.ticketTipoPago.save(pago);

                if(pago.getIdTipoPago() == TIPO_PAGO_TARJETA && encolaPagoTarjeta) {
                    idTicketTipoPagoBillPocket.add(idTicketTipoPago.intValue());
                }

                DebugLog.log(PaymentService.class, "PAYMENT", "TicketTipoPago tipo '" + pago.getIdTipoPago() + "' guardado:\n" + Utils.pojoToString(pago));
            }

            /*----------------------------------------------------------------------------
             GUARDANDO ITEMS TICKET
             -----------------------------------------------------------------------------*/
            DebugLog.log(PaymentService.class, "PAYMENT", "*********************TICKET ITEMS**********************");
            for (ItemsTicketController itemsTicket : superTicket.itemsTicket) { // iterar sobre mis items
                itemsTicket.setIdTicket(idTicket.intValue());
                managerModel.itemsTicket.save(itemsTicket);

                if(superTicket.getTicket().getIdTipoTicket() == 1 && itemsTicket.getIdArticulo() > 0) {
                    //solo cuando es venta de pos actualizo el inventario : tabla inventario
                    //Solo cuando no es articulo de VARIOS
                    updateInventarioForIdViewArticulo(itemsTicket, managerModel);
                }

                DebugLog.log(PaymentService.class, "PAYMENT", "itemsTicket guardado:\n" + Utils.pojoToString(itemsTicket));
            }
            // Poner el id de ticket en el voucher ?
            msg = "Ticket guardado en bd con id " + idTicket;

        } catch (Exception e) {
            e.printStackTrace();

            msg = "No se pudo guardar el superTicket con id " + idTicket + " en bd: " + e.getCause();

            //En caso de ocurrir un error, se escribe la traza en el archivo de excepciones globales.
            //Al siguiente error no controlado de la app, se enviará este error.
            try {
                BrioErrorReportPojo report = Utils.getBrioErrorReport(Contexto, e, managerSession);
                FileOutputStream trace = Contexto.openFileOutput(GlobalExceptionHandler.TRACE_FILE, Context.MODE_PRIVATE);
                trace.write(("BRIO_ERROR_SAVETICKET=>" + Utils.toJSON(report) + "<=BRIO_END\n").getBytes());
                trace.close();
            } catch(IOException ioe) { }
        }

        DebugLog.log(PaymentService.class, "PAYMENT", "saveSuperTicket: " + msg);

        //guardo el ultimo ticket en la sesión
        managerSession.saveInt(SessionManager.SESSION_KEY_PAYMENT_INT_LAST_TICKET_ID, idTicket.intValue());

        //Encolo los pagos de tarjeta
        BuzonSalidaBillPocketService buzonSalidaBillPocketService = BuzonSalidaBillPocketService.getInstance(Contexto);
        buzonSalidaBillPocketService.queueIdTicketTipoPagos(idTicketTipoPagoBillPocket);

        return idTicket.intValue();
    }

    /**
     * Actualizar el numero de articulos de este tipo en el inventario (restar los vendidos).
     *
     * @param item
     * @param managerModel
     */
    private static void updateInventarioForIdViewArticulo(ItemsTicketController item, ModelManager managerModel) {
        Inventario inventario = managerModel.inventario.getByIdArticulo(item.getIdArticulo());
        inventario.setExistencias(item.getExistenciasActual());
        managerModel.inventario.save(inventario);

        DebugLog.log(PaymentService.class, "PAYMENT", "Inventario actualizado para articulo con id = " + item.getIdArticulo());
    }

    /**
     * Cuando se realiza un cobro por BillPocket via intent a la app, la respuesta llega por este método.
     *
     * @param requestCode
     * @param resultCode
     * @param intent - la respuesta de la app BillPocket
     * @return
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        DebugLog.log(getClass(), "PAYMENT", "regresa de billpocket");
        boolean atendido = true;
        // FileLogger fileLogger = FileLogger.get(context, BillPocketService.FL_KEY_BILLPOCKET);
        switch (requestCode) {
            case BillPocketService.BP_INTENT_CODE:

                BillPocketChicken chicken = serviceBillPocket.managePayment(intent);
                String msg = BillPocketChicken.RESULT_ERROR;

                if(chicken != null) {
                    msg = chicken.getResult();

                    // fileLogger.log(chicken, "PaymentService.onActivityResult: regresa de billpocket");

                    switch (chicken.getResult()) {
                        case BillPocketChicken.RESULT_APPROVED_INVALIDO:
                        case BillPocketChicken.RESULT_APPROVED:
                            msg = chicken.getResult();

                            CardPaymentRequest cardPaymentRequest = BillPocketService.getCardPaymentRequest(_Contexto, chicken);

                            //Instertar el pago en BD
                            final TicketTipoPago nuevoPago = new TicketTipoPago();
                            nuevoPago.setMonto(Double.parseDouble(chicken.getAmount()));
                            nuevoPago.setIdTipoPago(TIPO_PAGO_TARJETA);

                            if(chicken.getResult().equals(BillPocketChicken.RESULT_APPROVED_INVALIDO)) {
                                cardPaymentRequest.setReference("<E>" +  cardPaymentRequest.getReference());
                            }

                            nuevoPago.setDescripcion(Utils.toJSON(cardPaymentRequest));
                            nuevoPago.setIdMoneda(1); //PESOS

                            DebugLog.log(getClass(), "PAYMENT", "BillPocket monto aceptado: " + nuevoPago.getMonto());

                            //Meto el pago al ticket
                            managePago(nuevoPago, chicken.getPagoAttachListener());
                            break;

                        default:
                            msg += "\nRazón: " + chicken.getStatusinfo();
                            break;
                    }
                } else {
                    DebugLog.log(getClass(), "PAYMENT", "  :'(");
                    msg += "\nRazón: No fue posible conectarse a BillPocket.";

                    // fileLogger.log("PaymentService.onActivityResult: pojo null");
                }

                BrioAcceptDialog bad = new BrioAcceptDialog(mainActivity, "BillPocket", msg, "Aceptar", new DialogListener() {
                    @Override
                    public void onAccept() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
                bad.show();
                break;

            default:
                atendido = false;
                break;
        }

        return atendido;
    }

    public long getIdTicket() {
        return idTicket;
    }

    /**
     * Este listener se utiliza para informar
     * el estatus de la cuenta (venta)
     */
    public interface AccountPaymentListener {
        void onAccountPaymentStatusChange (ACCOUNT_STATUS status);
    }

    /**
     * Saber cuando se agrego un pago
     */
    public interface PagoAttachListener {
        void onPagoAttached (TicketTipoPago ticketTipoPago);
    }
}
