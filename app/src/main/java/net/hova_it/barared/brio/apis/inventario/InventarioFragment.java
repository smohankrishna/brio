package net.hova_it.barared.brio.apis.inventario;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.interfaces.OnFragmentListener;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.daos.ViewInventarioDAO;
import net.hova_it.barared.brio.apis.models.entities.Inventario;
import net.hova_it.barared.brio.apis.models.views.ViewInventario;
import net.hova_it.barared.brio.apis.sqlite.SQLiteService;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.views.BrioSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lat.brio.core.BrioGlobales;
import lat.brio.core.bussines.BrCustomer;
import lat.brio.core.bussines.BrInventario;
import lat.brio.core.interfaces.IBasicMethods;

/**
 * Clase que muestra el inventario de los productos
 * Created by Mauricio Cerón on 08/02/2016.
 */
public class InventarioFragment extends OptionMenuFragment implements View.OnClickListener,
        BrioSearchView.OnQueryTextListener, InventarioAdapter.InventarioAdapterListener, IBasicMethods,
        LoaderManager.LoaderCallbacks<List<ViewInventario>>, OnFragmentListener {
    
    public final static String KEY_LOG = InventarioFragment.class.getSimpleName ();
    
    
    private RecyclerView mRecyclerView;
    private TextView textSinArticulos;
    private View mPBar;
    private InventarioAdapter mAdapter;
    
    
    private ViewInventarioDAO VA;
    private List<ViewInventario> listaOriginal;
    
    private FragmentListButtonListener mListener;
    SQLiteService sql;
    
    private Button producto;
    private Button marca;
    private Button presentacion;
    private Button categoria;
    private Button precio;
    private Button precioCompra;
    private Button existencias;
    
    private BrioSearchView busqueda;
    private List<ViewInventario> filteredModelList;
    private String TAG = "InventarioFragment";
    private BrInventario brInventario;
    private InventarioFragment inventarioFragment;
    private ProgressBar pbProgressBarGenerico;
    private String filterAdapter = "";
    private LinearLayout ll_btn_sort;
    private boolean isSort = false;
    
    
    public static InventarioFragment newInstance () {
        return new InventarioFragment ();
    }
    
    
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        setRetainInstance (true);
        
        rootView = inflater.inflate (R.layout.admin_inventario_list, container, false);
        inventarioFragment = this;
        loadBussines ();
        getLoaderManager ().restartLoader (InventarioListLoader.ORDER_BY_ARTICULO, null, this);
        loadViews ();
        loadData ();
        
        return rootView;
    }
    
    @Override
    public void onResume () {
        super.onResume ();
        
        busqueda.enableBarcodeScanner ();
    }
    
    @Override
    public void onPause () {
        super.onPause ();
        
        busqueda.disableBarcodeScanner ();
    }
    
    @Override
    public void loadViews () {
        try {
            mRecyclerView = (RecyclerView) rootView.findViewById (R.id.listInventario);
            
            producto = (Button) rootView.findViewById (R.id.btnProducto);
            marca = (Button) rootView.findViewById (R.id.btnMarca);
            precioCompra = (Button) rootView.findViewById (R.id.btnPrecioCompra);
            categoria = (Button) rootView.findViewById (R.id.btnCategoria);
            precio = (Button) rootView.findViewById (R.id.btnPrecioVenta);
            existencias = (Button) rootView.findViewById (R.id.btnExistencias);
            busqueda = (BrioSearchView) rootView.findViewById (R.id.busqueda);
            textSinArticulos = (TextView) rootView.findViewById (R.id.textSinArticulos);
            pbProgressBarGenerico = (ProgressBar) rootView.findViewById (R.id.pbProgressBarGenerico);
            ll_btn_sort = (LinearLayout) rootView.findViewById (R.id.ll_btn_sort);
            
            busqueda.setEnabled (false);
            producto.setOnClickListener (this);
            marca.setOnClickListener (this);
            precioCompra.setOnClickListener (this);
            categoria.setOnClickListener (this);
            precio.setOnClickListener (this);
            existencias.setOnClickListener (this);
            busqueda.addOnQueryTextListener (this);
            
            disabledView (busqueda, false);
            //mPBar = root.findViewById(R.id.granel_pb_cargando);
            
            LinearLayoutManager llm = new LinearLayoutManager (getActivity ());
            llm.setOrientation (LinearLayoutManager.VERTICAL);
            
            mRecyclerView.setLayoutManager (llm);
            mRecyclerView.setHasFixedSize (true);
            
            SimpleItemAnimator animator = new SimpleItemAnimator () {
                
                @Override
                public boolean animateAppearance (RecyclerView.ViewHolder holder, @Nullable ItemHolderInfo preLayoutInfo, ItemHolderInfo postLayoutInfo) {
                    //Log.d("ANIMATE", "animateAppearance");
                    
                    ViewCompat.animate (holder.itemView)
                            .translationY (holder.itemView.getHeight ())
                            .alpha (0)
                            .setDuration (1000)
                            .setInterpolator (new AccelerateDecelerateInterpolator ())
                            .start ();
                    
                    dispatchAnimationFinished (holder);
                    
                    return true;
                }
                
                
                @Override
                public boolean animateRemove (RecyclerView.ViewHolder holder) {
                    return false;
                }
                
                @Override
                public boolean animateAdd (RecyclerView.ViewHolder holder) {
                    //Log.d("ANIMATE", "animateAdd");
                    return false;
                }
                
                @Override
                public boolean animateMove (RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
                    return false;
                }
                
                @Override
                public boolean animateChange (RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
                    return false;
                }
                
                @Override
                public void runPendingAnimations () {
                
                }
                
                @Override
                public void endAnimation (RecyclerView.ViewHolder item) {
                
                }
                
                @Override
                public void endAnimations () {
                
                }
                
                @Override
                public boolean isRunning () {
                    return false;
                }
            };
            animator.setSupportsChangeAnimations (true);
            
            
        } catch (Exception e) {
            BrioGlobales.writeLog (TAG, "loadViews", e.getMessage ());
        }
    }
    
    public void disabledView (BrioSearchView editText, boolean enable) {
        editText.setEnabled (enable);
        editText.setFocusable (enable);
    }
    
    @Override
    public void loadBundle () {
    
    }
    
    public void loadBussines () {
        try {
            brInventario = BrInventario.getInstance (getActivity ().getApplicationContext (), BrioGlobales.getAccess ());
            VA = ((BrioActivityMain) getActivity ()).modelManager.viewInventario;
        } catch (Exception e) {
            BrioGlobales.writeLog (TAG, "LoadBussines", e.getMessage ());
        }
    }
    
    @Override
    public void loadData () {
        try {
            //new ConsultaInventarioTask ().execute ();
            disabledEnabledButtons (false);
            getLoaderManager ().initLoader (0, null, this);
        } catch (Exception e) {
            BrioGlobales.writeLog (TAG, "loadData", e.getMessage ());
        }
    }
    
    @Override
    public Loader<List<ViewInventario>> onCreateLoader (int id, Bundle args) {
        //mPDialog = ProgressDialog.show(getActivity(), "", "Cargando, espere un momento", true);
       
        return new InventarioListLoader (getActivity (), brInventario, id);
    }
    
    @Override
    public void onLoadFinished (Loader<List<ViewInventario>> loader, List<ViewInventario> viewInventarios) {
        // Log.d("es3", "ya regreso :P");
        
        // mPBar.setVisibility(View.GONE);
        
        if (viewInventarios != null) {
            textSinArticulos.setVisibility (View.GONE);
            pbProgressBarGenerico.setVisibility (View.GONE);
            mRecyclerView.setVisibility (View.VISIBLE);
            mAdapter = new InventarioAdapter (getActivity (), mListener, viewInventarios, this);
            mAdapter.setInventarioFragment (inventarioFragment);
            mRecyclerView.post (() -> {
                mRecyclerView.setAdapter (mAdapter);
                disabledEnabledButtons (true);
                if (! filterAdapter.equals ("")) {
                    filterAdapter (filterAdapter);
                }
            });
            
        } else {
            textSinArticulos.setVisibility (View.VISIBLE);
            pbProgressBarGenerico.setVisibility (View.GONE);
            mRecyclerView.setVisibility (View.GONE);
        }
        
        
    }
    
    @Override
    public void onLoaderReset (Loader<List<ViewInventario>> loader) {
        mAdapter.setData (null);
    }
    
    /**
     * al dar click al boton correspondiente hace un filtro para ordenarlo
     * @param v botones
     */
    @Override
    public void onClick (View v) {
        
        resetButtons ();
        
        switch (v.getId ()) {
            
            case R.id.btnProducto:
                sortListAdapter (ViewInventario.ComparatorNombreArticulo);
                producto.setBackgroundResource (R.drawable.view_bkg_pleca_activa);
                break;
            
            case R.id.btnMarca:
                sortListAdapter (ViewInventario.ComparatorNombreMarca);
                marca.setBackgroundResource (R.drawable.view_bkg_pleca_activa);
                break;
            
            case R.id.btnPrecioCompra:
                sortListAdapter (ViewInventario.ComparatorPrecioCompra);
                precioCompra.setBackgroundResource (R.drawable.view_bkg_pleca_activa);
                break;
            
            case R.id.btnCategoria:
                sortListAdapter (ViewInventario.ComparatorCodigoBarras);
                categoria.setBackgroundResource (R.drawable.view_bkg_pleca_activa);
                break;
            
            case R.id.btnPrecioVenta:
                sortListAdapter (ViewInventario.ComparatorPrecioVenta);
                precio.setBackgroundResource (R.drawable.view_bkg_pleca_activa);
                break;
            
            case R.id.btnExistencias:
                sortListAdapter (ViewInventario.ComparatorExistencias);
                existencias.setBackgroundResource (R.drawable.view_bkg_pleca_activa);
                break;
        }
        
    }
    
    private void sortListAdapter (Comparator<? super ViewInventario> comparator) {
        
        if (mAdapter != null) {
            disabledEnabledButtons (false);
            pbProgressBarGenerico.setVisibility (View.VISIBLE);
            if (! isSort) {
                isSort = true;
                mAdapter.sortListAdapter (comparator);
            }
        }
    }
    
    private void disabledEnabledButtons (boolean enabled) {
        if (ll_btn_sort != null)
            for (int i = 0; i < ll_btn_sort.getChildCount (); i++) {
                View view = ll_btn_sort.getChildAt (i);
                view.setEnabled (enabled); // Or whatever you want to do with the view.
            }
    }
    
    public void setListener (FragmentListButtonListener listener) {
        this.mListener = listener;
    }
    
    
    @Override
    public void onQueryTextChange (String query) {
        filterAdapter = query;
        if (mAdapter != null) {
            if (query != null && ! query.trim ().equals ("")) {
                filterAdapter (query);
            } else {
                filterAdapter (null);
            }
        }
    }
    
    private void filterAdapter (String query) {
        mAdapter.getFilter ().filter (query);
    }
    
    
    /**
     * Reset list
     */
    public void reset () {
        mAdapter.getFilter ().filter (null);
        textSinArticulos.setVisibility (View.VISIBLE);
        mRecyclerView.setVisibility (View.GONE);
        
    }
    
    /**
     * Reset of buttons
     */
    private void resetButtons () {
        producto.setBackgroundResource (R.drawable.view_bkg_pleca_inactiva);
        marca.setBackgroundResource (R.drawable.view_bkg_pleca_inactiva);
        precioCompra.setBackgroundResource (R.drawable.view_bkg_pleca_inactiva);
        categoria.setBackgroundResource (R.drawable.view_bkg_pleca_inactiva);
        precio.setBackgroundResource (R.drawable.view_bkg_pleca_inactiva);
        existencias.setBackgroundResource (R.drawable.view_bkg_pleca_inactiva);
    }
    
    
    @Override
    public void onEdit () {
        Log.w ("onEdit", "editandooooooo");
        //        reset ();
        //        mAdapter.getFilter ().filter (null);
    }
    
    @Override
    public void onDelete () {
        reset ();
    }
    
    @Override
    protected View getRootView () {
        return rootView;
    }
    
    @Override
    protected void beforeRemove () {
    
    }
    
    @Override
    public void onFragment (int requestCode, String msj, Bundle params) {
        isSort = false;
        disabledEnabledButtons (true);
        pbProgressBarGenerico.setVisibility (View.GONE);
    }
}
