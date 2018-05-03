package net.hova_it.barared.brio.apis.pos.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.entities.Cliente;
import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.utils.AnimationUtils2;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.views.BrioButton2;

/**
 * Fragment de ticket de venta.
 *
 * Created by Herman Peralta on 02/12/2015.
 */
public class POSTicketFragment extends POSFragment implements POSSuperTicketController.TicketUIActions {
    public final static String KEY_LOG = POSTicketFragment.class.getSimpleName();

    private ViewHolder viewHolder;
    private boolean enabled = true;

    private POSSuperTicketController controller;

    public static POSTicketFragment newInstance(int position) {
        Bundle args = new Bundle();

        args.putInt("page_position", position);

        POSTicketFragment fgmt = new POSTicketFragment();
        fgmt.setArguments(args);

        return fgmt;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DebugLog.log(POSTicketFragment.this.getClass(), "POSManager", "");

        recoverArgs();

//        setRetainInstance(true);

        View root = inflater.inflate(R.layout.pos_venta_fragment_ticket, container, false);
        viewHolder = new ViewHolder(root);

        DebugLog.log(POSTicketFragment.this.getClass(), "POSManager", "creando POSSuperTicketController");

        //controller = new POSSuperTicketController();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        DebugLog.log(getClass(), "POS TICKET", "Ticket " + page_position);
        controller.onResume(this, viewHolder.rvList);
    }

    public void disableTicket() {
        enabled = false;
        viewHolder.rvList.setEnabled(enabled);
    }

    public void enableTicket() {
        enabled = true;
        viewHolder.rvList.setEnabled(enabled);
    }

    public boolean isTicketEnabled() {
        return enabled;
    }

    /**
     * Reinicia la venta (cuando se termina la venta ya sea por que se completó pagando o
     * se canceló
     */
    public void peelTicketAndShowMainCarritoFragment() {
        viewHolder.btnNuevo.setVisibility(View.GONE);
        viewHolder.layTools.setVisibility(View.VISIBLE);

        final Animation animTicketArrancar = AnimationUtils.loadAnimation(getActivity(), R.anim.ticket_arrancar);
        animTicketArrancar.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                DebugLog.log(POSTicketFragment.this.getClass(), "POS", "Reinicio el controlador");

                controller.initTicket(); ///fixme limpiar el ticket antes de quitarlo
                //controller.searchAlarmStart(POSTicketFragment.this, viewHolder.rvList);
                pageSwapListener.muestraTicket(page_position);

                Animation animTicketNuevo = AnimationUtils.loadAnimation(getActivity(), R.anim.ticket_nuevo);
                viewHolder.layTicketContainer.startAnimation(animTicketNuevo);

                ((BrioActivityMain)getActivity()).enableMenus();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewHolder.layTicketContainer.startAnimation(animTicketArrancar);

        //newTicketListener.onNewTicket();


        ((BrioActivityMain)getActivity()).menuPOS.granelFragment.reload();
    }

    public void setCliente(Cliente cliente) {
        viewHolder.tvCliente.setText(cliente.getNombreCliente() + " " + cliente.getNombreCliente());
    }

    @Override
    protected void recoverArgs() {
        page_position = getArguments().getInt("page_position");
    }

    /**
     * Cuando se requiere cambiar el total de la venta al agregar un artículo
     * @param detail
     * @param total
     */
    @Override
    public void onUITotalChanged(String detail, String total) {
        int animtime = 500; // El mismo tiempo a todos los TV para que se vea en paralelo

        viewHolder.tvDetail.setText(detail);
        AnimationUtils2.fadeTextView(viewHolder.tvTotal, total, animtime);
    }

    /**
     * Cuando se agrega un pago a la venta
     * @param pago
     */
    @Override
    public void onUIVentaDone(TicketTipoPago pago) {
        viewHolder.layTools.setVisibility(View.GONE);
        viewHolder.btnNuevo.setVisibility(View.VISIBLE);

        AnimationUtils2.fadeTextView(viewHolder.tvTotal, viewHolder.tvTotal.getText().toString() + "\n" + pago.toString(), 500);
        MediaUtils.playCash(getActivity());
    }

    public void setPOSSuperTicketController(POSSuperTicketController controller) {
        this.controller = controller;
    }

    /**
     * Lógica de los botones y componentes gráficos del ticket
     */
    private class ViewHolder implements View.OnClickListener {
        public RelativeLayout layTicketContainer; // contenedor del ticket
        public View layTools;
        public RecyclerView rvList;

        public TextView tvCliente;
        public TextView tvDetail;
        public TextView tvTotal;
        public BrioButton2
                btnCobrar,
                    btnIdentificar,
                    btnCancelar,
                    btnNuevo;

        public ViewHolder(View root) {
            layTicketContainer = (RelativeLayout) root.findViewById(R.id.ticketcontainer);
            layTools = root.findViewById(R.id.layTicketTools);

            rvList = (RecyclerView) root.findViewById(R.id.rvList);

            tvCliente = (TextView) root.findViewById(R.id.tvCliente);
            tvDetail = (TextView) root.findViewById(R.id.tvDetail);
            tvTotal = (TextView) root.findViewById(R.id.tvImporteNeto);
            btnCobrar = (BrioButton2) root.findViewById(R.id.btnCobrar);
            btnIdentificar = (BrioButton2) root.findViewById(R.id.btnIdentificar);
            btnCancelar = (BrioButton2) root.findViewById(R.id.btnCancelar);
            btnNuevo = (BrioButton2) root.findViewById(R.id.btnNuevo);

            btnCobrar.setOnClickListener(this);
            btnIdentificar.setOnClickListener(this);
            btnCancelar.setOnClickListener(this);
            btnNuevo.setOnClickListener(this);

            //Animator
            DefaultItemAnimator listAnimator = new DefaultItemAnimator() {
                @Override
                public void onAddStarting(RecyclerView.ViewHolder item) {
                    disableTicket();
                    super.onAddStarting(item);
                }

                @Override
                public void onRemoveStarting(RecyclerView.ViewHolder item) {
                    disableTicket();
                    super.onRemoveStarting(item);
                }

                @Override
                public void onChangeStarting(RecyclerView.ViewHolder item, boolean oldItem) {
                    disableTicket();
                    super.onChangeStarting(item, oldItem);
                }

                @Override
                public void onMoveStarting(RecyclerView.ViewHolder item) {
                    disableTicket();
                    super.onMoveStarting(item);
                }

                @Override
                public void onAddFinished(RecyclerView.ViewHolder item) {
                    enableTicket();
                    super.onAddFinished(item);
                }

                @Override
                public void onRemoveFinished(RecyclerView.ViewHolder item) {
                    enableTicket();
                    super.onRemoveFinished(item);
                }

                @Override
                public void onChangeFinished(RecyclerView.ViewHolder item, boolean oldItem) {
                    enableTicket();
                    super.onChangeFinished(item, oldItem);
                }

                @Override
                public void onMoveFinished(RecyclerView.ViewHolder item) {
                    enableTicket();
                    super.onMoveFinished(item);
                }
            };
            listAnimator.setAddDuration(100);
            listAnimator.setRemoveDuration(100);
            listAnimator.setMoveDuration(100);
            listAnimator.setChangeDuration(100);
            rvList.setItemAnimator(listAnimator);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            //llm.setStackFromEnd(true); // hacer que se manden hacia abajo los items
            rvList.setLayoutManager(llm);
            //rvList.getItemAnimator().setSupportsChangeAnimations(false);
            rvList.setHasFixedSize(true);
        }

        @Override
        public void onClick(final View btn) {
            switch (btn.getId()) {
                case R.id.btnCobrar:
                    controller.pagar();
                    break;

                case R.id.btnIdentificar:
                    pageSwapListener.muestraCliente(page_position);
                    break;

                case R.id.btnCancelar:
                case R.id.btnNuevo:
                    ((BrioActivityMain)getActivity()).disableMenus();
                    peelTicketAndShowMainCarritoFragment();
                    break;
            }
        }
    }
}
