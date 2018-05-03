package net.hova_it.barared.brio.apis.pos.api;

import android.support.v4.app.Fragment;
import android.view.ViewGroup;


/**
 * Clase abstracta que contiene un listener para recargar fragments del POS
 * cuando se cambia la venta actual en el viewpager.
 *
 * Created by Herman Peralta on 13/01/2016.
 */
public abstract class POSFragment extends Fragment {

    protected ViewGroup lay_root;
    protected POSMainFragment.PageSwapListener pageSwapListener;

    //args
    public int page_position = -1;

    public void setPageSwapListener(POSMainFragment.PageSwapListener listener) {
        pageSwapListener = listener;
    }

    protected abstract void recoverArgs();
}
