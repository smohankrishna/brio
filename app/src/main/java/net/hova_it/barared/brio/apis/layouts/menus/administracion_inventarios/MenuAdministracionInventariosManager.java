package net.hova_it.barared.brio.apis.layouts.menus.administracion_inventarios;

import android.content.Context;
import android.view.View;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.admin.ArticleFormFragment;
import net.hova_it.barared.brio.apis.inventario.InventarioFragment;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuFragment;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuManager;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.DebugLog;

/**Submenu donde se encuantra el alta de articulos y el inventario
 * Created by Herman Peralta on 09/03/2016.
 */
public class MenuAdministracionInventariosManager extends MenuManager {

    protected static MenuAdministracionInventariosManager daManager;


    public static MenuAdministracionInventariosManager getInstance(Context context) {
        if(daManager == null) {
            daManager = new MenuAdministracionInventariosManager(context);
        }

        return daManager;
    }

    private MenuAdministracionInventariosManager(Context context) {
        super(context, "ADMINISTRACION_INVENTARIOS", R.array.menu_administracion_inventarios, true);


    }

    @Override
    protected FragmentListButtonListener getMenulistener() {
        return menulistener;
    }

    @Override
    protected MenuFragment getMenuFragmentInstance() {
        return MenuAdministracionInventariosFragment.newInstance();
    }

    /** Estas opciones vienen de res/xml/arrays
                  y se configuraron en el MenuMiBrioFragment */
    private FragmentListButtonListener menulistener = new FragmentListButtonListener() {
        @Override
        public void onListButtonClicked(View btn, Object value) {
            int position = (Integer) value;

            OptionMenuFragment opcion = null;


            switch (position) {
                case 0: //Alta de productos
                    opcion = new ArticleFormFragment();
                    //opcion = (OptionMenuFragment)((BrioActivityMain) context).managerArticleForm.createFragments(((AppCompatActivity)context).getSupportFragmentManager(), R.id.fragmentHolder);
                    break;

                case 1: //Inventario
                    opcion = new InventarioFragment();
                    break;

                default:
                    opcion = null;
                    break;
            }

            if(opcion != null) {
                onShowOptionFragment(opcion);
            }

            DebugLog.log(getClass(), "MENU", "Listener opcion " + position);
        }
    };
}
