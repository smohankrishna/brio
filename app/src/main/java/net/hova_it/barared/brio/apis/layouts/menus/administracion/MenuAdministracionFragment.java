package net.hova_it.barared.brio.apis.layouts.menus.administracion;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuFragment;

/**
 * Created by Herman Peralta on 09/03/2016.
 * Menu de la pestaña de administración
 */
public class MenuAdministracionFragment extends MenuFragment {

    public static MenuAdministracionFragment newInstance() {
        MenuAdministracionFragment fgmt = new MenuAdministracionFragment();
        fgmt.resid_layout = R.layout.menu_administracion;
        fgmt.resid_array_menuitems = R.array.menu_administracion;
        fgmt.resid_array_drawable_menuiconsoff = R.array.menu_administracion_iconos_off;
        fgmt.num_columnas = 2;

        return fgmt;
    }

    @Override
    public void configureUI() {
        //showSubMenuLayout(false);
    }

    @Override
    protected void beforeRemove() {

    }
}
