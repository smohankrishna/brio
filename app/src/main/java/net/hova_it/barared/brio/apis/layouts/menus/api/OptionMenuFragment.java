package net.hova_it.barared.brio.apis.layouts.menus.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import net.hova_it.barared.brio.BrioActivityMain;

/**
 * Fragment Abstracto para las opciones de un menu.
 *
 * Todos los fragments que se utilicen como respuesta a una opción de
 * menú deben extender de esta clase.
 *
 * Created by Herman Peralta on 14/03/16.
 */
public abstract class OptionMenuFragment extends Fragment {

    private MenuFragmentController menuFragmentController;
    protected View rootView;
    protected MenuManager menuManager;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getRootView().setClickable(true);

        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Este metodo se invoca automaticamente por el MenuManager cuando se va a quitar
     * este fragment de opción de menú.
     */
    public void remove(/*boolean confirm*/) {
        /*if(confirm) {
            if(!canRemove()) {
                return;
            }
        }*/
        beforeRemove();

        ((BrioActivityMain) getActivity()).managerTeclado.closeKeyboard();

        menuFragmentController.onRemoveOptionFragment();
    }

    public void setMenuFragmentController(MenuFragmentController menuFragmentController) {
        this.menuFragmentController = menuFragmentController;
    }

    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    /**
     * Es necesario que se le ponga .setClickable(true)
     * al layout root del fragment.
     * Por ejemplo en onCreateView(), inflan el fragment con:
     *
     *      inflater.inflate(R.layout.el_layout, container, false);
     *
     * Cambiar a:
     *
     *      rootView = inflater.inflate(R.layout.el_layout, container, false);
     *
     * y en la implementacion de setRootClickable() poner
     *    rootView.setClickable(true);
     */
    protected abstract View getRootView();

    /**
     * Este metodo se invoca justo antes de remover el fragment de opción de menú.
     * Por lo que este metodo contiene la lógica de limpieza del fragment de opción.
     */
    protected abstract void beforeRemove();

    //public abstract boolean canRemove();
}
