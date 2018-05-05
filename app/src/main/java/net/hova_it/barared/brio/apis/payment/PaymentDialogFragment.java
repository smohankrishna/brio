package net.hova_it.barared.brio.apis.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.hw.barcodescanner.ScannerManager;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.utils.AnimationUtils2;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.views.BrioButton2;
import net.hova_it.barared.brio.apis.views.BrioEditText;

/**
 * Fragment de pago de ventas.
 *
 * Este fragment presenta las opciones de pago disponibles de acuerdo a la venta realizada (POS o Pago de servicios).
 *
 * Created by Herman Peralta on 10/02/2016.
 */
public class PaymentDialogFragment extends Fragment
        implements View.OnClickListener, PaymentControllerAdapter.PaymentControllerAdapterListener, PaymentService.PagoAttachListener {

    private final static int[] btnsPago_ids = {R.id.btnTipoPagoEfectivo, R.id.btnTipoPagoTarjeta, R.id.btnTipoPagoFiado};
    private PaymentService servicePayment;

    private SuperTicket mSTicket;
    private PaymentControllerAdapter pagosController;
    private PaymentService.AccountPaymentListener accountPaymentListener;

    private int title;
    private int tipoTicket;

    private AppCompatActivity activity;
    private ViewHolder viewHolder;

    private final View.OnClickListener navigationListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnClose:
                case R.id.btnBack:
                    if(tipoTicket == PaymentService.TIPO_TICKET_POS) {
                        removeFragment();
                    } else {
                        BrioConfirmDialog bad = new BrioConfirmDialog((AppCompatActivity) getActivity(),
                                "Pago de servicios",
                                "¿Desea cancelar el pago de servicio?",
                                new String[]{"No", "Si"},
                                new DialogListener() {
                            @Override
                            public void onAccept() {
                                accountPaymentListener.onAccountPaymentStatusChange(PaymentService.ACCOUNT_STATUS.CANCELADO);

                                removeFragment();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                        bad.show();
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();

        viewHolder = new ViewHolder();
        viewHolder.rootView = inflater.inflate(R.layout.payment_dialog_fragment, container, false);

        servicePayment = PaymentService.getInstance((BrioActivityMain)getActivity());

        pagosController = new PaymentControllerAdapter(getActivity(), mSTicket, this);

        //init views
        viewHolder.panelBotonesPago = viewHolder.rootView.findViewById(R.id.panelBotonesPago);
        viewHolder.panelBotonesPago.setVisibility(View.VISIBLE);

        viewHolder.panelRestanteCambio = viewHolder.rootView.findViewById(R.id.panelRestanteCambio);
        viewHolder.panelRestanteCambio.setVisibility(View.VISIBLE);

        viewHolder.panelInfo = viewHolder.rootView.findViewById(R.id.panelInfo);
        viewHolder.panelInfo.setVisibility(View.VISIBLE);

        for(int i=0 ; i<btnsPago_ids.length ; i++) {
            viewHolder.rootView.findViewById(btnsPago_ids[i]).setOnClickListener(this);
        }

        viewHolder.tvImporteNeto = (TextView) viewHolder.rootView.findViewById(R.id.tvImporteNeto);
        viewHolder.tvImporteNeto.setText(String.format("$%.2f", mSTicket.getTicket().getImporteNeto()));

        viewHolder.btnBack = (BrioButton2) viewHolder.rootView.findViewById(R.id.btnBack);
        viewHolder.btnBack.setVisibility(View.VISIBLE);
        viewHolder.btnBack.setOnClickListener(navigationListener);

        viewHolder.btnClose = viewHolder.rootView.findViewById(R.id.btnClose);
        viewHolder.btnClose.setVisibility(View.VISIBLE);
        viewHolder.btnClose.setOnClickListener(navigationListener);

        if(tipoTicket != PaymentService.TIPO_TICKET_POS) {
            viewHolder.rootView.findViewById(R.id.btnTipoPagoTarjeta).setVisibility(View.GONE);
        }

        viewHolder.edMail = (BrioEditText) viewHolder.rootView.findViewById(R.id.edMail);

        viewHolder.tvRestante = (TextView) viewHolder.rootView.findViewById(R.id.tvCambio);
        viewHolder.tvRestanteTitle = (TextView) viewHolder.rootView.findViewById(R.id.tvRestanteTitle);
        onPaymentTotalChanged(mSTicket.getTicket().getImporteNeto() - mSTicket.montoPagado);

        TextView tvTitle = (TextView) viewHolder.rootView.findViewById(R.id.tvTitle);
        tvTitle.setText(Utils.getString(R.string.payment_title, getActivity()) + ": " + Utils.getString(title, getActivity()));

        //init list
        viewHolder.list = (RecyclerView) viewHolder.rootView.findViewById(R.id.pos_pagos_list);
        viewHolder.list.setAdapter(pagosController);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        viewHolder.list.setLayoutManager(llm);

        if (tipoTicket != PaymentService.TIPO_TICKET_POS) {
            viewHolder.btnBack.setText(Utils.getString(R.string.brio_cancelar, getActivity()));
        }

        return viewHolder.rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((BrioActivityMain) activity).managerScanner.stopScannerListening(((BrioActivityMain) activity).menuPOS);
    }

    /**
     * Gestiona el click en los distintos tipos de pago disponibles.
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        DebugLog.log(PaymentDialogFragment.this.getClass(), "PAYMENT", "click en tipo pao");

       int TIPO_PAGO;

        switch (view.getId()) {
            case R.id.btnTipoPagoEfectivo:
                TIPO_PAGO = PaymentService.TIPO_PAGO_EFECTIVO;
                break;

            case R.id.btnTipoPagoTarjeta:
                TIPO_PAGO = PaymentService.TIPO_PAGO_TARJETA;
                break;

            case R.id.btnTipoPagoVales:
                TIPO_PAGO = PaymentService.TIPO_PAGO_VALES;
                break;

            case R.id.btnTipoPagoFiado:
                TIPO_PAGO = PaymentService.TIPO_PAGO_FIADO;
                break;

            default:
                TIPO_PAGO = PaymentService.TIPO_PAGO_EFECTIVO;
                break;
        }

        servicePayment.addPago(TIPO_PAGO, this);
    }

    @Override
    public void onPaymentTotalChanged(double restante) {
        restante = restante <0 ? restante*-1 : restante;
        viewHolder.tvRestante.setText(String.format("$%.2f", restante));
        AnimationUtils2.animateViewPush(viewHolder.panelRestanteCambio, 0.80f, 400);
    }

    @Override
    public void onPagoAttached(TicketTipoPago ticketTipoPago) {
        pagosController.notifyItemInserted(pagosController.getItemCount() - 1);
        viewHolder.list.smoothScrollToPosition(pagosController.getItemCount());

        MediaUtils.playCash(getActivity());

        viewHolder.btnBack.setVisibility(View.GONE);
        viewHolder.btnClose.setVisibility(View.GONE);
    }

    public void onVentaFinalizada(double qcambio) {
        DebugLog.log(getClass(), "PAYMENT", "Venta finalizada");

        removeWhenVentaFinished();
    }

    /**
     * Establecer el tipo de ticket de acuerdo al tipo de venta.
     *
     * @param tipoTicket
     */
    public void setTipoTicket(int tipoTicket) {
        this.tipoTicket = tipoTicket;
        title = R.string.payment_tipo_ticket_pos;
        switch (tipoTicket) {
            case PaymentService.TIPO_TICKET_POS:
                title = R.string.payment_tipo_ticket_pos;
                break;

            case PaymentService.TIPO_TICKET_SERVICIO:
                title = R.string.payment_tipo_ticket_servicio;
                break;

            case PaymentService.TIPO_TICKET_TAE:
                title = R.string.payment_tipo_ticket_tae;
                break;

            case PaymentService.TIPO_TICKET_INTERNET:
                title = R.string.payment_tipo_ticket_internet;
                break;

            case PaymentService.TIPO_TICKET_BANCO_ENTRADA:
                title = R.string.payment_tipo_ticket_banco_entrada;
                break;

            case PaymentService.TIPO_TICKET_BANCO_SALIDA:
                title = R.string.payment_tipo_ticket_banco_salida;
                break;

        }
    }

    public void showFragment(AppCompatActivity activity, int layoutId, SuperTicket superTicket, PaymentService.AccountPaymentListener accountPaymentListener) {
        this.activity = activity;
        this.mSTicket = superTicket;

        setTipoTicket(superTicket.getTicket().getIdTipoTicket());
        setAccountPaymentListener(accountPaymentListener);

        activity.getSupportFragmentManager()
            .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .add(layoutId, this)
            .commit();
    }

    public void removeFragment() {
        DebugLog.log(getClass(), "PAYMENT", "quito el fragment");
        this.activity.getSupportFragmentManager()
            .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .remove(this)
            .commit();

        ((BrioActivityMain) getActivity()).managerScanner.startScannerListening(((BrioActivityMain) getActivity()).menuPOS, ScannerManager.KEYCODE_DELIM_ENTER);

        ((BrioActivityMain)getActivity()).enableMenus();
        MediaUtils.hideSystemUI((AppCompatActivity) getActivity());
    }

    public void setAccountPaymentListener(PaymentService.AccountPaymentListener accountPaymentListener) {
        this.accountPaymentListener = accountPaymentListener;
    }

    public PaymentControllerAdapter getPagosController() {
        return pagosController;
    }

    private void removeWhenVentaFinished() {
        viewHolder.rootView.setEnabled(false);

        MediaUtils.playCash(getActivity());
        accountPaymentListener.onAccountPaymentStatusChange(PaymentService.ACCOUNT_STATUS.PAGO_COMPLETADO);
        removeFragment();
    }

    /**
     * Contenedor de los views del fragment
     */
    private class ViewHolder {
        RecyclerView list;

        BrioButton2
                btnBack;

        View
                rootView,
                btnClose,
                panelBotonesPago,
                panelRestanteCambio,
                panelInfo;

        TextView
                tvImporteNeto,
                tvRestante,
                tvRestanteTitle;

        BrioEditText
                edMail;
    }
}
