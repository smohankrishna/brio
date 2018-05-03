package net.hova_it.barared.brio.apis.pos.api;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hova_it.barared.brio.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de ItemsTicketController
 * i.e. la lista de items en el ticket.
 *
 * Created by Herman Peralta on 27/11/2015.
 */
public class ItemsTicketControllerRecyclerAdapter
        extends RecyclerView.Adapter<ItemsTicketControllerRecyclerAdapter.Holder> {

    private final static String KEY_LOG = ItemsTicketControllerRecyclerAdapter.class.getSimpleName();

    private List<ItemsTicketController> mItemList;
    private List<Integer> mIdsArticulos;

    public TicketItemClickListener mItemClickListener;

    public ItemsTicketControllerRecyclerAdapter(TicketItemClickListener itemClickListener) {

        mItemList = new ArrayList<>();
        mIdsArticulos = new ArrayList<>();
        mItemClickListener = itemClickListener;
    }

    public void setItems(List<ItemsTicketController> list) {
        this.mItemList = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_venta_ticket_item, parent, false);

        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        ItemsTicketController item = mItemList.get(position);

        holder.btnCant.setText(String.format("%.2f", item.getCantidad()));
        holder.tvDescripcion.setText(item.getDescripcion());
        holder.tvImporteUnitario.setText(String.format("$%.2f", item.getImporteUnitario()));

        holder.tvImporteTotal.setText(String.format("Subtotal: $%.2f", item.getImporteTotal()));

        /*
        if(item.getCantidad() > 1) {
            holder.tvImporteTotal.setVisibility(View.VISIBLE);
            holder.tvImporteTotal.setText(String.format("Subtotal: $%.2f", item.getImporteTotal()));
        } else {
            holder.tvImporteTotal.setVisibility(View.GONE);
        }
        */

        holder.itemView.setTag(position);
    }

    public void addItem(ItemsTicketController newItem, final int idArticulo) {
        if(mIdsArticulos.contains(idArticulo)) {
            // Si ya tengo un item en el recyclerview con este artículo, lo remuevo y lo agrego al final de nuevo
            // http://stackoverflow.com/questions/32354917/recyclerview-corrupts-view-using-notifyitemmoved#

            int position = mIdsArticulos.indexOf(idArticulo);

            ItemsTicketController item = mItemList.get(position);

            item.setCantidad(item.getCantidad() + newItem.getCantidad());
            mItemList.remove(position);
            mItemList.add(item);
            notifyItemMoved(position, mItemList.size() - 1);
            notifyItemChanged(mItemList.size() - 1);

            mIdsArticulos.remove(position);
            mIdsArticulos.add(idArticulo);
        } else {
            //Si no tengo el artículo lo agrego
            mIdsArticulos.add(idArticulo);
            mItemList.add(newItem);
            notifyItemInserted(mItemList.size());
        }
    }

    public void removeItem(int position) {
        mItemList.remove(position);
        mIdsArticulos.remove(position);
        notifyItemRemoved(position);
    }

    public void cambioCantidad(int position, double newcant) {
        ItemsTicketController item = mItemList.get(position);
        if ( newcant <= 0 ) {
            removeItem(position);
        } else {
            item.setCantidad(newcant);
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public List<ItemsTicketController> getItems() {
        return mItemList;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvDescripcion;
        private TextView tvImporteUnitario;
        private TextView tvImporteTotal;

        //Botones
        private TextView btnCant;
        private TextView tvBtnRemove;
        private TextView tvBtnRemove1;
        private TextView tvBtnAdd1;

        public Holder(View itemView) {
            super(itemView);

            tvDescripcion = (TextView) itemView.findViewById(R.id.tvDescripcion);
            tvImporteUnitario = (TextView) itemView.findViewById(R.id.tvImporteUnitario);
            tvImporteTotal = (TextView) itemView.findViewById(R.id.tvImporteTotal);

            //Botones
            btnCant = (TextView) itemView.findViewById(R.id.btnCant);
            tvBtnRemove = (TextView) itemView.findViewById(R.id.tvBtnRemove);
            tvBtnRemove1 = (TextView) itemView.findViewById(R.id.tvBtnRemove1);
            tvBtnAdd1 = (TextView) itemView.findViewById(R.id.tvBtnAdd1);

            btnCant.setOnClickListener(this);
            tvBtnRemove.setOnClickListener(this);
            tvBtnRemove1.setOnClickListener(this);
            tvBtnAdd1.setOnClickListener(this);
            tvImporteUnitario.setOnClickListener(this);
        }

        @Override
        public void onClick(View btn) {
            mItemClickListener.onTicketItemClick(btn, getAdapterPosition(), mItemList);
        }
    }

    /**
     * Interfaz para implementar el click en un item de la lista del ticket (recyclerview)
     */
    public interface TicketItemClickListener {
        /**
         * Implementar el onclick listener de cada botón de acción presente en la lista del ticket (recyclerview)
         * @param itemBtn - el botón de acción presionado
         * @param position - la posicion del item en el adapter del recyclerview
         * @param ticketItems - la lista de items del recyclerview
         */
        void onTicketItemClick(View itemBtn, int position, List<ItemsTicketController> ticketItems);
    }
}

