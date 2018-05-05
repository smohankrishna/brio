package net.hova_it.barared.brio.apis.pos.api;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.entities.Cliente;
import net.hova_it.barared.brio.apis.views.BrioButton2;
import net.hova_it.barared.brio.apis.views.BrioSearchView;


/**
 *
 * Fragment para buscar cliente (Actualmente no usado)
 *
 * Created by Herman Peralta.
 */
public class POSClienteFragment extends POSFragment {
    public static final String KEY_LOG = POSClienteFragment.class.getSimpleName();

    private ViewHolder holder;
    private Cliente mCliente;

    public static POSClienteFragment newInstance(int position) {
        Bundle args = new Bundle();

        args.putInt("page_position", position);

        POSClienteFragment fgmt = new POSClienteFragment();
        fgmt.setArguments(args);

        return fgmt;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recoverArgs();

//        setRetainInstance(true);

        View root = inflater.inflate(R.layout.pos_venta_fragment_cliente, container, false);

        holder = new ViewHolder(root);

        holder.tbvCodigoCliente.setText("" + page_position);

        return root;
    }

    @Override
    protected void recoverArgs() {
        page_position = getArguments().getInt("page_position");
    }

    public void setCliente(Cliente cliente) {
        mCliente = cliente;
        if(mCliente != null) {
            //todo: usar scene transition
            holder.tvMsg2.setText("Nombre del cliente: " + mCliente.getNombreCliente() + " " + mCliente.getNombreCliente());
            holder.tbvCodigoCliente.setText(mCliente.getNumeroTarjeta());
            holder.btnNoCliente.setVisibility(View.GONE);
            holder.btnContinuar.setVisibility(View.VISIBLE);
        } else {
            holder.tvMsg2.setText("No se encontró el cliente");
        }
    }

    public Cliente getCliente() {
        return mCliente;
    }

    private class ViewHolder implements View.OnClickListener {
        public TextView tvMsg;
        public TextView tvMsg2;
        public BrioSearchView tbvCodigoCliente;
        public BrioButton2 btnNoCliente, btnContinuar;

        public ViewHolder (View root) {
            tvMsg = (TextView) root.findViewById(R.id.tvMsg);
            tvMsg2 = (TextView) root.findViewById(R.id.tvMsg2);
            btnNoCliente = (BrioButton2) root.findViewById(R.id.btnNoCliente);
            btnContinuar = (BrioButton2) root.findViewById(R.id.btnContinuar);
            tbvCodigoCliente = (BrioSearchView) root.findViewById(R.id.tbvCodigoCliente);
            //tbvCodigoCliente.setFragmentManager((getActivity()).getSupportFragmentManager());

            btnNoCliente.setOnClickListener(this);
            btnContinuar.setOnClickListener(this);
            tbvCodigoCliente.setTextButtonViewListener(new BrioSearchView.TextButtonViewListener() {
                @Override
                public void onButtonPressed(String codigoBarras) {
                    Toast.makeText(getActivity(), "Buscar cliente...", Toast.LENGTH_SHORT).show();

                    ((BrioActivityMain) getActivity()).menuPOS.findClienteByCodigoBarras(codigoBarras);
                }

                @Override
                public void onCancelPressed() {
                    reset();
                }
            });
        }

        public void reset() {
            tvMsg2.setText("");
            holder.btnNoCliente.setVisibility(View.VISIBLE);
            holder.btnContinuar.setVisibility(View.GONE);
            holder.tbvCodigoCliente.setText("");
        }

        @Override
        public void onClick(final View btn) {
            switch (btn.getId()) {
                case R.id.btnNoCliente:
                    Log.e(KEY_LOG, "btnNoCliente page " + page_position);
                    pageSwapListener.muestraTicket(page_position);

                    break;

                case R.id.btnContinuar:
                    pageSwapListener.muestraTicket(page_position);
                    holder.reset();
                    break;
            }
        }
    }
}
