package net.hova_it.barared.brio.apis.pos.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.interfaces.OnFragmentListener;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.views.BrioSearchView;

import java.util.ArrayList;
import java.util.List;

import lat.brio.core.BrioGlobales;
import lat.brio.core.bussines.BrInventario;

/**
 * Fragment para mostrar los articulos disponibles y aplicar busqueda sobre ellos.
 * Se cargan todos los productos de la DB en un Loader Asincrono.
 * Created by Herman Peralta on 09/02/2016.
 * Updated on: 03/03/2016
 */
public class SearchFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<String[]>>,
        View.OnClickListener, BrioSearchView.OnQueryTextListener {
    
    private static final String KEY_LOG = SearchFragment.class.getSimpleName ();
    
    private View
            rootView,
            panel_loading,
            panel_notresults;
    
    private RecyclerView rvProductos;
    private SearchAdapter daAdapter;
    private List<String[]> rows;
    
    private FragmentManager fragmentManager;
    private FragmentListButtonListener fragmentListButtonListener;
    private FragmentVisibleListener fragmentVisibleListener;
    
    private RemoveListener removeListener;
    
    private BrInventario brInventario;
    private String TAG = "SearchFragment";
    private String filterAdapter = "";
    private OnFragmentListener fragmentListener;
    
    public static SearchFragment newInstance () {
        Bundle args = new Bundle ();
        
        SearchFragment fgmt = new SearchFragment ();
        fgmt.setArguments (args);
        
        return fgmt;
    }
    
    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        rootView = inflater.inflate (R.layout.search_fragment_results, null);
        rootView.findViewById (R.id.btnClose).setOnClickListener (this);
        
        panel_loading = rootView.findViewById (R.id.loading);
        panel_notresults = rootView.findViewById (R.id.notresults);
        
        loadBussines ();
        loadViews ();
        loadData ();
        //configList();
        
        return rootView;
    }
    
    public void loadViews () {
        try {
            
            LinearLayoutManager llm = new LinearLayoutManager (getActivity ());
            llm.setOrientation (LinearLayoutManager.VERTICAL);
            
            rvProductos = (RecyclerView) rootView.findViewById (R.id.list);
            rvProductos.setLayoutManager (llm);
            rvProductos.setHasFixedSize (true);
            
        } catch (Exception e) {
            BrioGlobales.writeLog (TAG, "loadViews", e.getMessage ());
        }
    }
    
    public void loadBussines () {
        try {
            brInventario = BrInventario.getInstance (getActivity ().getApplicationContext (), BrioGlobales.getAccess ());
        } catch (Exception e) {
            BrioGlobales.writeLog (TAG, "LoadBussines", e.getMessage ());
        }
    }
    
    public void loadData () {
        try {
            getLoaderManager ().initLoader (SearchListLoader.LOADER_TYPE_ALL, null, this);
        } catch (Exception e) {
            BrioGlobales.writeLog (TAG, "loadData", e.getMessage ());
        }
    }
    
    @Override
    public void onResume () {
        super.onResume ();
        
        fragmentVisibleListener.onFragmentVisible ();
    }
    
    
    @Override
    public Loader<List<String[]>> onCreateLoader (int id, Bundle args) {
        Log.d (KEY_LOG, "onCreateLoader()");
        
        return new SearchListLoader (brInventario, getActivity ());
    }
    
    @Override
    public void onLoadFinished (Loader<List<String[]>> loader, List<String[]> data) {
        
        if (data != null) {
            
            panel_loading.setVisibility (View.GONE);
            rvProductos.setVisibility (View.VISIBLE);
            daAdapter = new SearchAdapter (getActivity (), fragmentListButtonListener, data);
            rows = data;
            rvProductos.post (new Runnable () {
                @Override
                public void run () {
                    rvProductos.setAdapter (daAdapter);
                    
                    if (! filterAdapter.equals ("")) {
                        filterAdapter (filterAdapter);
                    }
                }
            });
            
        } else {
            panel_loading.setVisibility (View.VISIBLE);
            rvProductos.setVisibility (View.GONE);
        }
        
        
    }
    
    @Override
    public void onLoaderReset (Loader<List<String[]>> loader) {
        // Clear the data in the adapter.
        // daAdapter.setItems(null);
    }
    
    @Override
    public void onClick (View btn) {
        switch (btn.getId ()) {
            case R.id.btnClose:
                remove ();
                break;
        }
    }
    
    public void Cerrar () {
        remove ();
    }
    
    /**
     * Inicia la busqueda en la DB.
     * @param query
     */
    @Override
    public void onQueryTextChange (String query) {
        filterAdapter = query;
        if (daAdapter != null) {
            
            if (this.isVisible ()) {
                if (query != null && ! query.trim ().equals ("")) {
                    filterAdapter (query);
                } else {
                    filterAdapter (null);
                }
            }
        }
    }
    
    private void filterAdapter (String query) {
        daAdapter.getFilter ().filter (query);
    }
    
    public void show (final FragmentManager fragmentManager, final int resIdLayout, OnFragmentListener fragmentListener) {
        //      if(this.fragmentManager == null) {
        this.fragmentManager = fragmentManager;
        this.fragmentListener = fragmentListener;
        
        this.fragmentManager
                .beginTransaction ()
                .setCustomAnimations (android.R.anim.fade_in, android.R.anim.fade_out)
                .replace (resIdLayout, this)
                .commit ();
        //    } else {
        //        DebugLog.log(getClass(), "Search", "No se pudo mostrar el fragment dado que ya esta visible.");
        //    }
    }
    
    public void remove () {
        //if(fragmentManager != null) {
        ((BrioActivityMain) getActivity ()).managerTeclado.closeKeyboard ();
        removeListener.onRemove ();
        
        fragmentManager
                .beginTransaction ()
                .remove (this)
                .commit ();
        
        fragmentManager = null;
        //        } else {
        //            DebugLog.log(getClass(), "Search", "No se pudo remover el fragment, dado que no se ha agregado anteriormente.");
        //        }
    }
    
    public void setFragmentListButtonListener (FragmentListButtonListener listener) {
        fragmentListButtonListener = listener;
    }
    
    public void setRemoveListener (RemoveListener removeListener) {
        this.removeListener = removeListener;
    }
    
    public void setFragmentVisibleListener (FragmentVisibleListener fragmentVisibleListener) {
        this.fragmentVisibleListener = fragmentVisibleListener;
    }
    
    public interface RemoveListener {
        void onRemove ();
    }
    
    public interface FragmentVisibleListener {
        void onFragmentVisible ();
    }
}
