package net.hova_it.barared.brio.apis.payment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Controlador y Adapter que lleva la lista de pagos realizados en una venta.
 * Los pagos pueden ser EFECTIVO, TARJETA, VALES y FIADO (los ultimos dos no se utilizan).
 *
 * Created by Herman Peralta on 10/02/2016.
 */
public class PaymentControllerAdapter extends RecyclerView.Adapter<PaymentControllerAdapter.ViewHolder> {
    private double
            /*totalPagos,*/
            totalPagosEfectivo;

    private Context context;
    private SuperTicket mSTicket;
    private PaymentControllerAdapterListener paymentControllerAdapterListener;

    public PaymentControllerAdapter(Context context, SuperTicket STicket, PaymentControllerAdapterListener paymentControllerAdapterListener) {
        this.context = context;
        this.mSTicket = STicket;

        DebugLog.log(getClass(), "PAYMENT", "Adapter recibe ticket\n"  + Utils.pojoToString(STicket));

        setPaymentControllerAdapterListener(paymentControllerAdapterListener);
    }

    /**
     * Agregar un pago a la lista de pagos.
     *
     * @param ticketTipoPago
     * @return
     */
    public PaymentService.ACCOUNT_STATUS addPago(TicketTipoPago ticketTipoPago) {

        mSTicket.ticketTipoPago.add(ticketTipoPago);
        notifyItemInserted(mSTicket.ticketTipoPago.size() - 1);

        updateTotalPagos();

        //Verificando el cobro
        DebugLog.log(getClass(), "PAYMENT", "mSTicket.montoPagado < mSTicket.getTicket().getImporteNeto() : " + mSTicket.montoPagado + " < " + mSTicket.getTicket().getImporteNeto());

        PaymentService.ACCOUNT_STATUS status = PaymentService.ACCOUNT_STATUS.PAGO_COMPLETADO;
        if(mSTicket.montoPagado < mSTicket.getTicket().getImporteNeto()) {
            status = PaymentService.ACCOUNT_STATUS.INCOMPLETO;
        }

        DebugLog.log(getClass(), "PAYMENT", "agrego pago TicketTipoPago\n" + Utils.pojoToString(mSTicket));

        return status;
    }

    /**
     * Realiza la sumatoria de los pagos ingresados y verifica si ya se cubrio el importe de la venta.
     *
     * Se guarda la suma del efectivo para calcular el cambio
     */
    private void updateTotalPagos() {
        mSTicket.montoPagado = 0;
        totalPagosEfectivo = 0;

        for(TicketTipoPago pago : mSTicket.ticketTipoPago) {
            mSTicket.montoPagado += pago.getMonto();

            if(pago.getIdTipoPago() == PaymentService.TIPO_PAGO_EFECTIVO) { // asi estaba antes: pago.getIdTipoPago() == 1
                totalPagosEfectivo += pago.getMonto();
            }
        }

        double restante =  mSTicket.getTicket().getImporteNeto() - mSTicket.montoPagado ;
        BigDecimal dbRestante = new BigDecimal(restante);
        dbRestante = dbRestante.setScale(2, RoundingMode.HALF_UP);
        restante = dbRestante.doubleValue();

        double restanteEfectivo = mSTicket.getTicket().getImporteNeto() - (mSTicket.montoPagado - totalPagosEfectivo);
        dbRestante = new BigDecimal(restanteEfectivo);
        dbRestante.setScale(2,RoundingMode.HALF_UP);
        restanteEfectivo = dbRestante.doubleValue();

        mSTicket.getTicket().setCambio(totalPagosEfectivo - restanteEfectivo);

        if(paymentControllerAdapterListener != null) { paymentControllerAdapterListener.onPaymentTotalChanged(restante); }
    }

    public void setPaymentControllerAdapterListener(PaymentControllerAdapterListener paymentControllerAdapterListener) {
        this.paymentControllerAdapterListener = paymentControllerAdapterListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.payment_item, null, true);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TicketTipoPago pago = mSTicket.ticketTipoPago.get(position);

        String desc = "";
        switch (pago.getIdTicketTiposPago()) {
            case PaymentService.TIPO_PAGO_EFECTIVO:
                desc = "EFECTIVO";
                break;

            case PaymentService.TIPO_PAGO_TARJETA:
                desc = "TARJETA";
                break;

            case PaymentService.TIPO_PAGO_FIADO:
                desc = "FIADO";
                break;

            case PaymentService.TIPO_PAGO_VALES:
                desc = "VALES";
                break;
        }

        viewHolder.txts[0].setText(desc);
        viewHolder.txts[1].setText("MONTO");
        viewHolder.txts[2].setText(String.format("%.2f", pago.getMonto()));
    }

    @Override
    public int getItemCount() {
        return mSTicket.ticketTipoPago.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView[] txts;

        public ViewHolder(View itemView) {
            super(itemView);

            txts = new TextView[3];
            txts[0] = (TextView)itemView.findViewById(R.id.txt1);
            txts[1] = (TextView)itemView.findViewById(R.id.txt2);
            txts[2] = (TextView)itemView.findViewById(R.id.txt3);
        }

        @Override
        public void onClick(View view) {

        }
    }

    /**
     * Saber cuando cambia el restante de la cuenta la agregar un pago
     */
    public interface PaymentControllerAdapterListener {
        void onPaymentTotalChanged (double restante);
    }
}
