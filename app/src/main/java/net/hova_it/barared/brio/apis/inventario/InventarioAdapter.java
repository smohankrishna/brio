package net.hova_it.barared.brio.apis.inventario;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.interfaces.OnFragmentListener;
import net.hova_it.barared.brio.apis.models.daos.ArticulosDAO;
import net.hova_it.barared.brio.apis.models.daos.InventarioDAO;
import net.hova_it.barared.brio.apis.models.daos.ViewInventarioDAO;
import net.hova_it.barared.brio.apis.models.entities.Articulo;
import net.hova_it.barared.brio.apis.models.entities.Inventario;
import net.hova_it.barared.brio.apis.models.views.ViewInventario;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoOnClickListener;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.AnimationUtils2;
import net.hova_it.barared.brio.apis.views.BrioEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import lat.brio.core.BrioGlobales;


/**
 * Muestra la lista de articulos que hay en el inventario, te permite editar precio y existencias
 * Created by Mauricio Cerón on 08/02/2016.
 */
public class InventarioAdapter extends RecyclerView.Adapter<InventarioAdapter.Holder> implements Filterable {
    private final static String KEY_LOG = InventarioAdapter.class.getSimpleName();

    public static List<ViewInventario> items, listBk, listF;
    //public static List<ViewInventario> articulo;
    private Context context;
    private int mPreviousPosition = 0;

    private ArticulosDAO AR;
    private InventarioDAO IN;
    private ViewInventarioDAO VIN;
    private BrioConfirmDialog notificacion;
    private FragmentListButtonListener mListener;
    private InventarioFragment inventarioFragment;

    private Comparator<? super ViewInventario> comparatorInventario;
    private OnFragmentListener fragmentListener;
    public boolean isClickable = true;


    public InventarioAdapter(Context context, FragmentListButtonListener listener, List<ViewInventario> itemsList, OnFragmentListener fragmentListener) {
        this.context = context;
        items = itemsList;
        this.fragmentListener = fragmentListener;
        listBk = new ArrayList<>();
        listBk.clear();
        listBk.addAll(itemsList);

        mListener = listener;
        AR = ((BrioActivityMain) context).modelManager.articulos;
        IN = ((BrioActivityMain) context).modelManager.inventario;
        VIN = ((BrioActivityMain) context).modelManager.viewInventario;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.admin_inventario_row, parent, false);
        return new Holder(item);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ViewInventario item = items.get(position);


        holder.posicion = position;
        holder.itemView.setTag(item);
        holder.producto.setText(item.getNombreArticulo());
        holder.marca.setText(item.getNombreMarca());

        if (item.getContenido() % 1 == 0) {
            holder.contenido.setText((int) item.getContenido() + " " + item.getUnidad());
        } else {
            holder.contenido.setText(String.format("%.2f", item.getContenido()) + " " + item.getUnidad());
        }

        holder.precioCompra.setText(String.format("%.2f", item.getPrecioCompra()));
        holder.categoria.setText(item.getCodigoBarras());
        holder.precioVenta.setText(String.format("%.2f", item.getPrecioVenta()));
        holder.existencias.setText(String.format("%.2f", item.getExistencias()));
        //holder.tvNombre.setText(item.getName());
        //holder.ivFoto.setImageResource(item.getPhoto());
        //if(imgtest!=null) {holder.ivFoto.setImageBitmap(imgtest);} else {holder.ivFoto.setImageResource(item.getPhoto());}


        if (position > mPreviousPosition) {
            //Todo: animar salida
        } else {
            //Todo: animar entrada
        }
        mPreviousPosition = position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static List<ViewInventario> getItems() {
        return items;
    }

    //Regresa la lista de articulos en inventario
    public void setData(List<ViewInventario> data) {
        if (data != null) {
            items = new ArrayList<>();
            items.addAll(data);

        }
        this.notifyDataSetChanged();
    }

    public void setListener(FragmentListButtonListener listener) {
        this.mListener = listener;
    }


    public void sortListAdapter(final Comparator<? super ViewInventario> comparator) {

        comparatorInventario = comparator;

        new sortThread().start();

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
                } else {
                    ArrayList<ViewInventario> filteredContacts = new ArrayList<ViewInventario>();
                    for (ViewInventario c : listBk) {
                        if (c.getNombreArticulo().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                                c.getCodigoBarras().toUpperCase().contains(constraint.toString().toUpperCase())) {

                            filteredContacts.add(c);
                        }
                    }
                    // Finally set the filtered values and size/count
                    results.values = filteredContacts;
                    results.count = filteredContacts.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listF = (ArrayList<ViewInventario>) results.values;
                items.clear();
                items.addAll(listF);
                notifyDataSetChanged();
            }
        };

    }


    // Se declaran los campos
    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView producto;
        public TextView marca;
        public TextView contenido;
        //public TextView presentacion;
        public TextView categoria;
        public BrioEditText precioCompra;
        public BrioEditText precioVenta;
        public BrioEditText existencias;
        public ImageView btnBorrar;
        public TextView btnEditar;

        public int posicion;

        // se castean los campos y despues se realiza la logica de editar precios y existencias
        public Holder(View itemView) {
            super(itemView);


            producto = (TextView) itemView.findViewById(R.id.producto);
            marca = (TextView) itemView.findViewById(R.id.marca);
            contenido = (TextView) itemView.findViewById(R.id.contenido);
            categoria = (TextView) itemView.findViewById(R.id.categoria);
            btnBorrar = (ImageView) itemView.findViewById(R.id.borrar);
            precioCompra = (BrioEditText) itemView.findViewById(R.id.precioCompra);
            precioVenta = (BrioEditText) itemView.findViewById(R.id.precioVenta);
            existencias = (BrioEditText) itemView.findViewById(R.id.existencias);

            btnBorrar.setOnClickListener(this);

            // final ViewInventario item = items.get(posicion);
    
            precioCompra.setTecladoOnClickListener(new TecladoOnClickListener() {
                @Override
                public void onAcceptResult(Object... results) {
                    
                    if(!isClickable){
                        return;
                    }

                    double temporalD;//double
                    String mensaje = (String.format("%s%s %s %s %s?",
                            Utils.getString(R.string.inventario_editar_precio, context),
                            items.get(posicion).getNombreArticulo(),
                            items.get(posicion).getNombreMarca(),
                            items.get(posicion).getContenido(),
                            items.get(posicion).getUnidad()));

                    final Articulo articulo = AR.getByIdArticulo(items.get(posicion).getIdArticulo());
                    final Inventario inventario = IN.getByIdArticulo(items.get(posicion).getIdArticulo());

                    if (precioCompra.getText().toString().equals("")) {
                        precioCompra.setText("0");
                    }
                    temporalD = (double) results[0];//Double.parseDouble(existencias.getText().toString());
                    articulo.setPrecioCompra(temporalD);
                    inventario.setFechaModificacion(System.currentTimeMillis() / 1000);

                    DialogListener dialogListener = new DialogListener() {
                        @Override
                        public void onAccept() {
                            if (articulo.getImagen() == null) {
                                articulo.setImagen("");
                            }
                            AR.save(articulo);
                            IN.save(inventario);
                            updateItem(posicion, VIN.getByIdArticulo(items.get(posicion).getIdArticulo()));
                        }

                        @Override
                        public void onCancel() {
                            updateItem(posicion, items.get(posicion));
                        }
                    };

                    notificacion = new BrioConfirmDialog((AppCompatActivity) context, null, mensaje, null, dialogListener);
                    notificacion.show();
                }

                @Override
                public void onCancelResult(Object... results) {
                    precioCompra.post(new Runnable() {
                        @Override
                        public void run() {
                            precioCompra.setText(String.format(new Locale("ES", "MX"), "%.2f", items.get(posicion).getPrecioCompra()));
                        }
                    });
                }
            });

            precioVenta.setTecladoOnClickListener(new TecladoOnClickListener() {
                @Override
                public void onAcceptResult(Object... results) {
                    if(!isClickable){
                        return;
                    }

                    double temporalD;//double
                    String mensaje = (String.format("%s%s %s %s %s?",
                            Utils.getString(R.string.inventario_editar_precio, context),
                            items.get(posicion).getNombreArticulo(),
                            items.get(posicion).getNombreMarca(),
                            items.get(posicion).getContenido(),
                            items.get(posicion).getUnidad()));

                    final Articulo articulo = AR.getByIdArticulo(items.get(posicion).getIdArticulo());
                    final Inventario inventario = IN.getByIdArticulo(items.get(posicion).getIdArticulo());

                    if (precioVenta.getText().toString().equals("")) {
                        precioVenta.setText("0");
                    }
                    temporalD = (double) results[0];//Double.parseDouble(existencias.getText().toString());
                    articulo.setPrecioVenta(temporalD);
                    inventario.setFechaModificacion(System.currentTimeMillis() / 1000);


                    DialogListener dialogListener = new DialogListener() {
                        @Override
                        public void onAccept() {
                            if (articulo.getImagen() == null) {
                                articulo.setImagen("");
                            }
                            AR.save(articulo);
                            IN.save(inventario);
                            updateItem(posicion, VIN.getByIdArticulo(items.get(posicion).getIdArticulo()));
                        }

                        @Override
                        public void onCancel() {
                            updateItem(posicion, items.get(posicion));
                        }
                    };

                    notificacion = new BrioConfirmDialog((AppCompatActivity) context, null, mensaje, null, dialogListener);
                    notificacion.show();
                }

                @Override
                public void onCancelResult(Object... results) {

                    precioVenta.post(new Runnable() {
                        @Override
                        public void run() {
                            precioVenta.setText(String.format(new Locale("ES", "MX"), "%.2f", items.get(posicion).getPrecioVenta()));
                        }
                    });

                }
            });


            existencias.setTecladoOnClickListener(new TecladoOnClickListener() {
                @Override
                public void onAcceptResult(Object... results) {
                    if(!isClickable){
                        return;
                    }

                    //  final ViewInventario item = items.get(posicion);
                    double existenciasAgregadas;//double

                    String mensaje = (String.format("%s%s %s %s %s?",
                            Utils.getString(R.string.inventario_editar_existencias, context),
                            items.get(posicion).getNombreArticulo(), items.get(posicion).getNombreMarca(), items.get(posicion).getContenido(), items.get(posicion).getUnidad()));

                    final Inventario inventario = IN.getByIdInventario(items.get(posicion).getIdInventario());

                    if (existencias.getText().toString().equals("")) {
                        existencias.setText("0");
                    }
                    existenciasAgregadas = (double) results[0];//Double.parseDouble(existencias.getText().toString());

                    double existenciasOriginales = inventario.getExistencias();
                    double existenciasActualizadas = existenciasOriginales + existenciasAgregadas;
                    inventario.setExistencias(existenciasActualizadas);
                    inventario.setFechaModificacion(System.currentTimeMillis() / 1000);

                    DialogListener dialogListener = new DialogListener() {
                        @Override
                        public void onAccept() {
                            IN.save(inventario);
                            updateItem(posicion, VIN.getByIdArticulo(items.get(posicion).getIdArticulo()));
                        }

                        @Override
                        public void onCancel() {
                            updateItem(posicion, items.get(posicion));
                        }
                    };

                    if (existenciasAgregadas != 0) {
                        notificacion = new BrioConfirmDialog((AppCompatActivity) context, null, mensaje, null, dialogListener);
                        notificacion.show();
                    } else {
                        updateItem(posicion, items.get(posicion));
                    }
                }

                @Override
                public void onCancelResult(Object... results) {

                    existencias.post(new Runnable() {
                        @Override
                        public void run() {
                            existencias.setText(String.format(new Locale("ES", "MX"), "%.2f", items.get(posicion).getExistencias()));

                        }
                    });
                }
            });

        }


        /**
         * Anteriormente se borraba de inventario pero ya no se ocupa
         *
         * @param view
         */
        @Override
        public void onClick(View view) {

            //items.add(new GranelItem("Hola", R.drawable.test_granel));
            final ViewInventario item = items.get(posicion);
            AnimationUtils2.animateViewPush(view, 0.7f, 50);
            //notifyItemInserted(items.size() - 1);
            switch (view.getId()) {

                case R.id.borrar:

                    String mensaje = (Utils.getString(R.string.inventario_borrar, context) +
                            item.getNombreArticulo() + " " + item.getNombreMarca() + " " + item.getContenido() + " " + item.getUnidad() +
                            "?");

                    final Articulo articulo = AR.getByIdArticulo(item.getIdArticulo());
                    final Inventario inventario = IN.getByIdArticulo(item.getIdArticulo());
                    articulo.setFechaBaja(System.currentTimeMillis() / 1000);
                    //articulo.setImagen(" ");
                    inventario.setFechaModificacion(System.currentTimeMillis() / 1000);


                    DialogListener dialogListener = new DialogListener() {
                        @Override
                        public void onAccept() {
                            AR.save(articulo);
                            Long l = IN.save(inventario);
                            items.remove(posicion);
                            inventarioFragment.onDelete();
                            //InventarioAdapter.this.notifyItemRemoved(posicion);
                            InventarioAdapter.this.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancel() {

                        }
                    };

                    notificacion = new BrioConfirmDialog((AppCompatActivity) context, null, mensaje, null, dialogListener);
                    notificacion.show();

                    break;

            }

            if (mListener != null) {
                mListener.onListButtonClicked(view, view.getTag());
            } else {
                Log.e(KEY_LOG, "El listener es null");
            }
        }
    }

    /**
     * metodo que actualiza los elementos de la lista y los mantiene en cache
     */
    private void updateItem(int position, final ViewInventario item) {
        //Se ejecuta en un nuevo hilo, para que no se vea afectado el performance de la app
        new updateItemThread(position, item).start();
    }

    private class sortThread extends Thread {

        @Override
        public void run() {
            super.run();

            try {
    
                isClickable=false;

                //useLegacyMergeSort
                Collections.sort(items, comparatorInventario);
                ((BrioActivityMain) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        notifyDataSetChanged();
                        habilitaBotones();
                       
                    }
                });
                isClickable=true;
                //Despues de ordenar y mostrar el array list, se ordena la lista original
                Collections.sort(listBk, comparatorInventario);
            } catch (Exception e) {
                BrioGlobales.writeLog("InventarioAdapter", "updateItemThread", e.getMessage());
            }
        }
    }
    
    
    private void habilitaBotones(){
    
        try {
            Thread.sleep (1000);
    
            if (fragmentListener != null) {
                fragmentListener.onFragment(1, null, null);
            }
        } catch (InterruptedException e) {
            e.printStackTrace ();
            BrioGlobales.writeLog("InventarioAdapter", "habilitaBotones", e.getMessage());
    
        }
    
    
    }

    private class updateItemThread extends Thread {

        private final int position;
        private final ViewInventario item;

        public updateItemThread(int position, final ViewInventario item) {
            this.position = position;
            this.item = item;
        }

        @Override
        public void run() {

            //Actualizamos la lista del recyclerview primero
            items.set(position, item);
            ((BrioActivityMain) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

            /*
            Despues de actualizar la vista, se actualiza la lista original. Esto es para no se vea reflejado
             el proceso que se está ejecutando.
             */
            //Se hace busqueda del elemento en la lista original, se obtiene el index y despues se actualiza
            if (comparatorInventario == null)
                comparatorInventario = ViewInventario.ComparatorNombreArticulo;

            try {
                //Primero se ordena la lista
                Collections.sort(listBk, comparatorInventario);

                int i = -1;//= Collections.binarySearch(listBk, item, comparatorInventario);
                int b = 0;
                for (ViewInventario v : listBk) {
                    if (v.getCodigoBarras().equals(item.getCodigoBarras())) {
                        i = b;
                        listBk.set(i, item);
                        return;
                    }
                    b++;
                }

            } catch (Exception e) {
                BrioGlobales.writeLog("InventarioAdapter", "updateItemThread", e.getMessage());
            }
        }
    }

    public void setInventarioFragment(InventarioFragment inventarioFragment) {
        this.inventarioFragment = inventarioFragment;
    }

    public interface InventarioAdapterListener {
        void onEdit();

        void onDelete();
    }
}
