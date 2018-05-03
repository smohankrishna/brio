package net.hova_it.barared.brio.apis.pos.api;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Cliente;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.models.views.ViewArticulo;
import net.hova_it.barared.brio.apis.payment.PaymentService;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoManager2;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoOnClickListener;

import java.util.List;

/**
 * Controlador de una venta (ticket) basado en un super POJO
 * SuperTicket, el fragment del ticket y un adaptador de ItemsTicketController.
 *
 * Cada ItemsTicketController lleva un subtotal de la venta. El total de la venta
 * es la suma de todos los subtotales almacenados en el adapter de ItemsTicketController.
 *
 * https://www.youtube.com/watch?v=MHqpR3yLNfk
 * Created by Herman on 27/11/2015.
 */
public class POSSuperTicketController
        implements PaymentService.PagoAttachListener, ItemsTicketControllerRecyclerAdapter.TicketItemClickListener, PaymentService.AccountPaymentListener {

    public final static String KEY_LOG = POSSuperTicketController.class.getSimpleName();

    private Context context;
    private SuperTicket mSTicket;

    private Cliente mCliente;

    private RecyclerView mRecyclerView;
    private BrioActivityMain mainActivity;

    private POSTicketFragment ticketFragment;
    private ItemsTicketControllerRecyclerAdapter mAdapter;

    private TicketTipoPago mPago;

    private PaymentService servicePayment;

    private int totalcantidadArticulos = 0;

    private ModelManager managerModel;

    public POSSuperTicketController(BrioActivityMain context) {
        DebugLog.log(getClass(), "POS", "SE CREA CONTROLLER");
        this.context = context;
        servicePayment = PaymentService.getInstance(context);
        managerModel = new ModelManager(context);


        initTicket();
    }

    public void onResume(POSTicketFragment fgmt, RecyclerView recyclerView) {
        DebugLog.log(getClass(), "POS", "Restauro el ticket");
        mRecyclerView = recyclerView;
        mainActivity = (BrioActivityMain) fgmt.getActivity();
        ticketFragment = fgmt;

        init();

        refreshPatch();

        updateImporteNeto();
    }

    public void initTicket() {
        mSTicket = new SuperTicket();
        mAdapter = new ItemsTicketControllerRecyclerAdapter(this);

        mSTicket.getTicket().setDescripcion(Utils.getString(R.string.payment_ticket_default_descripcion, context));
        mSTicket.getTicket().setIdTipoTicket(1); //TODO: aqui hay que poner el id de ticket de venta mostrador / general
        mSTicket.getTicket().setIdMoneda(1); // TODO: mandar ID moneda
    }

    public void init() {
        DebugLog.log(getClass(), "POSManager", "Creando el superTicket desde POSTICKETCONTROLLER");

        mAdapter.setItems(mSTicket.itemsTicket);
        mRecyclerView.setAdapter(mAdapter);
    }

    //todo: recibir codigo de barras
    public void addItem(final ViewArticulo articulo, final double cantidad, final double existencias) {
        DebugLog.log(getClass(), "POSManager", "agregar item");
        ticketFragment.disableTicket();

            mAdapter.addItem(new ItemsTicketController(mSTicket.getTicket().getIdTicket(), articulo, cantidad, existencias), articulo.getIdArticulo());
            updateImporteNeto();

        ticketFragment.enableTicket();

        Log.d(KEY_LOG, "addItem() valido");
    }

    private void onCambioCantidad(int position, double newcant) {
        Log.d(KEY_LOG, "onCambioCantidad " + position + " -> " + newcant);
        mAdapter.cambioCantidad(position, newcant);
        updateImporteNeto();
    }

    /**
     * Lógica de los botones de cada item del ticket
     *    Botón agregar 1
     *    Botón quitar 1
     *    Botón quitar todos
     *    Botón editar cantidad
     * @param listItemButton
     * @param position - la posicion del item en el adapter del recyclerview
     * @param ticketItems - la lista de items del adapter
     */
    @Override
    public void onTicketItemClick(View listItemButton, final int position, List<ItemsTicketController> ticketItems) {
        DebugLog.log(POSSuperTicketController.this.getClass(), "Click en boton de item de ticket", "");
        if(position == -1){return;}

        final ItemsTicketController item = mSTicket.itemsTicket.get(position);

        switch (listItemButton.getId()) {
            case R.id.tvBtnAdd1: // agregar un articulo ---> [ Botón (+) ]
                double newcant = item.getCantidad() + 1d;
                onCambioCantidad(position, newcant);
                break;

            case R.id.tvBtnRemove1: // remover un articulo ---> [ Botón (-) ]
                newcant = item.getCantidad() - 1d;
                onCambioCantidad(position, newcant);
                break;

            case R.id.tvBtnRemove: // remover todos los articulos del item ---> [ Botón bote basura ]
                onCambioCantidad(position, 0);
                break;

            case R.id.btnCant: //Cambiar la cantidad de articulos con teclado --> [ Texto de cantidad ]

                String
                        title,
                        cantidad;

                TecladoManager2.TIPO_TECLADO tipoTeclado;

                ItemsTicketController itemsTicketController = ticketItems.get(position);
                title = Utils.getString(R.string.pos_granel_change, context).replace("?", itemsTicketController.getUnidad());

                if(item.getGranel()) {
                    tipoTeclado = TecladoManager2.TIPO_TECLADO.NUMERICO;
                    cantidad = String.format("%.2f", item.getCantidad());
                } else {
                    tipoTeclado = TecladoManager2.TIPO_TECLADO.NUMERICO_ENTERO;
                    cantidad = String.format("%d", (int)item.getCantidad());
                }
                mainActivity.managerTeclado.openKeyboard(true, tipoTeclado,
                        title,
                        cantidad,
                        new TecladoOnClickListener() {
                            @Override
                            public void onAcceptResult(Object... results) {
                                MediaUtils.hideSystemUI((AppCompatActivity) context);

                                double nuevaCantidad;

                                if (item.getGranel()) {
                                    nuevaCantidad = (Double) results[0];
                                } else {
                                    nuevaCantidad = (Integer) results[0];
                                }

                                if (nuevaCantidad <= 0) {
                                    BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) context,
                                            Utils.getString(R.string.pos_error_cantidad_title, context),
                                            Utils.getString(R.string.pos_error_cantidad_msg, context));
                                    bad.show();
                                } else {
                                    onCambioCantidad(position, nuevaCantidad);
                                }
                            }

                            @Override
                            public void onCancelResult(Object... results) {
                                MediaUtils.hideSystemUI((AppCompatActivity) context);
                            }
                        });
                break;


            case R.id.tvImporteUnitario: //cambiar el precio manualmente
                //todo: cambiar el precio manualmente
                /*
                Toast.makeText(mainActivity, "DEBUG: Ingresa numero", Toast.LENGTH_SHORT).show();
                mainActivity.managerTeclado.openKeyboard(true, TecladoManager2.TIPO_TECLADO.ALFANUMERICO, new TecladoOnClickListener() {
                    @Override
                    public void onAcceptResult(Object... results) {
                        double newcant = 666;
                        try {
                            Double.parseDouble((String) results[0]);
                        } catch (Exception e) { }
                        item.setPrecioManual(newcant);
                        mAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onCancelResult(Object... results) {
                        item.removePrecioManual();
                        mAdapter.notifyItemChanged(position);
                    }
                });
                */

                break;
        }
    }

    public void pagar() {
        DebugLog.log(this.getClass(), "POSManager", "a partir de aqui atiende PaymentService, checar tag PAYMENT en log");
        if(totalcantidadArticulos == 0) {
            BrioAlertDialog bad = new BrioAlertDialog(mainActivity, Utils.getString(R.string.brio_error, mainActivity), Utils.getString(R.string.pos_empty, mainActivity));
            bad.show();
        } else {
            servicePayment.payTicket(mSTicket, this);
        }
    }

    /**
     * Actualiza el importe neto (total) del ticket
     */
    private void updateImporteNeto() {
        double
                importeTotal = 0d,
                importeBruto = 0d;

        //double impuestos = 0d;
        totalcantidadArticulos = 0;

        for(int i=0 ; i< mSTicket.itemsTicket.size() ; i++) {
            ItemsTicketController item = mSTicket.itemsTicket.get(i);

            importeTotal += item.getImporteTotal();
            //impuestos += item.getImpuestos();

            totalcantidadArticulos += item.getGranel()? 1 : item.getCantidad();
        }

        importeBruto = importeTotal;// - impuestos;

        //todo: aplicar todos los descuentos en cascada (Si hay)

        //guardo los totales del ticket
        mSTicket.getTicket().setImporteBruto(importeBruto);
        mSTicket.getTicket().setImporteNeto(importeTotal);
        mSTicket.getTicket().setImpuestos(0/*impuestos*/);

        String sarticulos = String.format("ARTICULOS: %d", totalcantidadArticulos);
        /*String ssubtotal  = String.format("SUBTOTAL: $%.2f", importeBruto);*/
        /*String simpuestos = String.format("IMPUESTOS: $%.2f", impuestos);*/
        String stotal = String.format("TOTAL: $%.2f", importeTotal);
        String sdetail = String.format("%10s\n", sarticulos/*, ssubtotal, simpuestos*/);

        ticketFragment.onUITotalChanged(sdetail, String.format("%10s", stotal));

        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }

    @Override
    public void onPagoAttached(TicketTipoPago pago) {
        Log.d(KEY_LOG, "onPagoAttached");
        mPago = pago;
        ticketFragment.onUIVentaDone(pago);
    }

    public void setCliente(Cliente cliente) {
        mCliente = cliente;

        mSTicket.getTicket().setIdCliente(cliente.getIdCliente());
    }

    public Cliente getCliente() {
        return mCliente;
    }

    public ItemsTicketControllerRecyclerAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAccountPaymentStatusChange(PaymentService.ACCOUNT_STATUS status) {
        DebugLog.log(POSSuperTicketController.this.getClass(), "PAYMENT", "onAccountPaymentStatusChange(): status: " + status);
        switch (status) {
            case PAGO_COMPLETADO:
                DebugLog.log(POSSuperTicketController.this.getClass(), "PAYMENT", "pagado completo");
                servicePayment.saveAndSendTicket(mSTicket, this);
                break;

            case ENVIADO:
                DebugLog.log(POSSuperTicketController.this.getClass(), "PAYMENT", "ticket enviado");
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ticketFragment.peelTicketAndShowMainCarritoFragment();
                    }
                }, 1000);
                break;

            default:
                DebugLog.log(POSSuperTicketController.this.getClass(), "PAYMENT", "onAccountPaymentStatusChange(): default");
                break;
        }
    }

    private void refreshPatch() {
        //todo: mandarle al adapter los datos nuevos

        if(mAdapter != null) {

            for (int i = 0; i < mSTicket.itemsTicket.size(); i++) {
                //todo actualizar los productos desde el indexer
                ItemsTicketController tmp = mSTicket.itemsTicket.get(i);
//
                ViewArticulo tmpArticulo = managerModel.viewArticulos.getByIdArticulo(tmp.getIdArticulo());
                if(tmpArticulo != null) {
                    //Actualizar los productos que no son de tipo VARIOS
                    mSTicket.itemsTicket.set(i, new ItemsTicketController(mSTicket.getTicket().getIdTicket(), tmpArticulo, tmp.getCantidad(), tmp.getExistenciasInventario()));
                }
            }

            mAdapter.setItems(mSTicket.itemsTicket);

            updateImporteNeto();
        }
    }

    /**
     * Acciones que debe cubrir el fragment del ticket
     */
    public interface TicketUIActions {
        /**
         * Cuando cambia el total de la cuenta
         * @param detail
         * @param total
         */
        void onUITotalChanged (String detail, String total);

        /**
         * Cuando se ha efectuado el cobro de la venta
         *
         * @param pago
         */
        void onUIVentaDone (TicketTipoPago pago);
    }
}