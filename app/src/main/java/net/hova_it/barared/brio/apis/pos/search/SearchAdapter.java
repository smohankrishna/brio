package net.hova_it.barared.brio.apis.pos.search;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.views.ViewInventario;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de la lista de búsqueda de artículos. Contiene algunos métodos
 * que permiten animar la aparición de los elementos de la búsqueda mientras
 * se buscan los artículos.
 * <p>
 * http://stackoverflow.com/questions/30398247/how-to-filter-a-recyclerview-with-a-searchview
 * <p>
 * Created by Herman Peralta on 09/02/2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Holder> implements Filterable {

    private final static int ncols = 3;

    private Context context;
    private List<String[]> items, listBk, listF;

    /**
     * Listener para indicar cuando el usuario ha hecho click en un artículo de la lista para agregarlo a la venta actual en el POS
     */
    private FragmentListButtonListener fragmentListButtonListener;



    public SearchAdapter(Context context, FragmentListButtonListener listener,List<String[]> itemsList) {
        this.context = context;
        this.items = itemsList;
        this.fragmentListButtonListener = listener;
        listBk = new ArrayList<>();
        listBk.clear();
        listBk.addAll(itemsList);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.search_item, null, false);

        return new Holder(item);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        String[] row = items.get(position);

        holder.bind(row);
    }

    /**
     * Establecer los items a mostrar (los cuales se cargan en el Loader)
     *
     * @param data
     */
    public void setItems(List<String[]> data) {
        if (data != null) {
            items = new ArrayList<>();
            items.addAll(data);
            listBk = new ArrayList<>();
            listBk.clear();
            listBk.addAll(data);
        }
        this.notifyDataSetChanged();
    }


    /**
     * Obtener el número actual de items
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
    
                if (constraint != null)
                {
                    constraint = constraint.toString().trim();
                    constraint = Utils.toNoPalatals(constraint.toString());
                }

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = listBk;
                    results.count = listBk.size();
                }else{
                    final List<String[]> filteredModelList = new ArrayList<>();
                    final String[] keywords;

                    constraint = constraint.toString().toLowerCase();

                    if (constraint.toString().contains(" ")) {
                        keywords = constraint.toString().split(" ");
                    } else {
                        keywords = new String[1];
                        keywords[0] = constraint.toString();
                    }

                    // Hacer match matricial de cada palabra ingresada con el row
                    for (final String[] model : listBk) {

                        final String desc = model[0];
                        final String codBarras = model[2];
                        final String rowText = (desc + codBarras).replace(" ", "").toLowerCase();


                        boolean matchAll = true;
                        for (final String word : keywords) {
                            if (!rowText.contains(word)) {
                                matchAll = false;
                                break;
                            }
                        }

                        if (matchAll) {
                            filteredModelList.add(model);
                        }
                    }
                    results.values = filteredModelList;
                    results.count = filteredModelList.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listF = (ArrayList<String[]>) results.values;
                //  Log.e("publish", listF.size() + "");
                items.clear();
                items.addAll(listF);
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Controlador de cada item de la lista, incluye la llamada
     * al POSManager cuando el usuario toca un artículo para agregarlo a la venta actual
     */
    public class Holder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private View clickedView;

        public final int[] ids = {R.id.txt1, R.id.txt2, R.id.txt3};

        public TextView[] texts;

        public Holder(View itemView) {
            super(itemView);

            //reference views
            texts = new TextView[ncols];
            for (int i = 0; i < ncols; i++) {
                texts[i] = (TextView) itemView.findViewById(ids[i]);
            }

            //listeners
            itemView.setOnClickListener(this);
        }

        public void bind(String[] row) {
            itemView.setTag(row);

            for (int i = 0; i < ncols; i++) {
                texts[i].setText(row[i]);
            }
        }

        @Override
        public void onClick(View view) {
            clickedView = view;
            BrioConfirmDialog bcd = new BrioConfirmDialog((AppCompatActivity) context, null, Utils.getString(R.string.search_add_article, context), null, new DialogListener() {
                @Override
                public void onAccept() {
                    fragmentListButtonListener.onListButtonClicked(clickedView, clickedView.getTag());
                }

                @Override
                public void onCancel() {

                }
            });
            bcd.show();
        }
    }
}
