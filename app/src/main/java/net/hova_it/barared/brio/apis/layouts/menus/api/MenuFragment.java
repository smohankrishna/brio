package net.hova_it.barared.brio.apis.layouts.menus.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.adapters.MenuMiBrioAdapter;
import net.hova_it.barared.brio.apis.models.DLMenuMiBrio;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.DebugLog;

import java.util.ArrayList;

/**
 * Fragment abstracto que se utiliza para mostrar los menus de la aplicación.
 *
 * Este fragment muestra una lista de opciones, cuyos textos (labels) e íconos (drawables)
 * se mapean por posición en un array de strings y de ids de drawables (respectivamente)
 * en los resources xml.
 *
 * En los fragments que extienden de esta clase, al agregar o quitar elementos de los array
 * de recursos xml resid_array_menuitems y resid_array_drawable_menuiconsoff se modifican las
 * opciones que muestra el menú.
 *
 * Todos los menus de la aplicación deben extender de esta clase.
 *
 * Created by Herman Peralta on 09/03/2016.
 * Api de los menus
 */
public abstract class MenuFragment extends OptionMenuFragment {
    
    protected View rootView;
    protected int num_columnas = 1;
    protected int resid_layout = - 1;
    protected int resid_array_menuitems = - 1;
    protected int resid_array_drawable_menuiconsoff = - 1;
    
    protected RecyclerView menu_list;
    protected OpcionesAdapter adapter;
    protected MenuMiBrioAdapter miBrioAdapter;
    
    private FragmentListButtonListener menulistener;
    
    protected ArrayList<DLMenuMiBrio> itemsMenuDinamico;
    protected boolean mibrio = false;
    
    
    /**
     * Se deben setear previamente las variables
     * resid_layout, resid_array_menuitems, resid_array_drawable_menuiconsoff
     * para indicar el layout xml del menu, el array de strings que se mapea a
     * cada opción y el array de iconos que se mapea a acada opción, respectivamente.
     * @param inflater
     * @param container
     * @param savedInstanceState
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance (true);
        
        rootView = inflater.inflate (resid_layout, container, false);
        rootView.setClickable (true);
    
       
        if (mibrio)
            configureListMiBrio ();
        else
            configureList ();
    
        configureUI ();
        
        return rootView;
    }
    
    
    private void configureList () {
        adapter = new OpcionesAdapter (getActivity (), resid_array_menuitems, resid_array_drawable_menuiconsoff);
        adapter.setFragmentListButtonListener (menulistener);
        // configurando recycler view
        setAdapter (adapter);
    }
    
    /**
     * Se crea nuevo adapter para mostrar el ordenamiento de los botones
     */
    public void configureListMiBrio () {
        miBrioAdapter = new MenuMiBrioAdapter (getActivity (), itemsMenuDinamico);
        miBrioAdapter.setFragmentListButtonListener (menulistener);
        setAdapter (miBrioAdapter);
    }
    
    
    
    private void setAdapter (Adapter adapter) {
        menu_list = (RecyclerView) rootView.findViewById (R.id.menu_list);
        if (num_columnas == 1) {
            LinearLayoutManager llm = new LinearLayoutManager (getActivity ());
            llm.setOrientation (LinearLayoutManager.VERTICAL);
            menu_list.setLayoutManager (llm);
        } else {
            GridLayoutManager glm = new GridLayoutManager (getActivity (), num_columnas);
            menu_list.setLayoutManager (glm);
        }
        menu_list.setHasFixedSize (true);
        menu_list.setAdapter (adapter);
    }
    
    public void setMenuListener (FragmentListButtonListener menulistener) {
        this.menulistener = menulistener;
        if (adapter != null) {
            adapter.setFragmentListButtonListener (menulistener);
        }
    }
    
    public abstract void configureUI ();
    
    @Override
    protected View getRootView () {
        return rootView;
    }
    
}
