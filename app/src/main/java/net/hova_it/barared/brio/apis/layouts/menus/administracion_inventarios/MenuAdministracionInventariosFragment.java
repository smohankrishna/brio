package net.hova_it.barared.brio.apis.layouts.menus.administracion_inventarios;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuFragment;

/**
 * Created by Herman Peralta on 09/03/2016.
 * submenu de la pestaña administracion de inventarios
 */
public class MenuAdministracionInventariosFragment extends MenuFragment {

    public static MenuAdministracionInventariosFragment newInstance() {
        MenuAdministracionInventariosFragment fgmt = new MenuAdministracionInventariosFragment();
        fgmt.resid_layout = R.layout.menu_administracion_inventarios;
        fgmt.resid_array_menuitems = R.array.menu_administracion_inventarios;
        fgmt.resid_array_drawable_menuiconsoff = R.array.menu_administracion_inventarios_iconos_off;
        fgmt.num_columnas = 2;

        return fgmt;
    }

    @Override
    public void configureUI() {

    }

    @Override
    protected void beforeRemove() {

    }
}
