package net.hova_it.barared.brio.apis.layouts.menus.reportes;

import android.content.Context;
import android.util.Log;
import android.view.View;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.estadocuenta.DetEstadoCuentaViewManager;
import net.hova_it.barared.brio.apis.estadocuenta.EstadoCuentaViewManager;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuFragment;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuManager;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.reports.ReportesFragment;
import net.hova_it.barared.brio.apis.utils.DebugLog;

/**
 * Created by Herman Peralta on 09/03/2016.
 * Clase que manda llamar al submenu de reportes
 */
public class MenuReportesManager extends MenuManager {

    protected static MenuReportesManager daManager;

    private EstadoCuentaViewManager estadoCuentaViewManager;
    private DetEstadoCuentaViewManager detEstadoCuentaViewManager; // Agregado por Manuel Delgadillo - 25/02/2017

    public static MenuReportesManager getInstance(Context context) {
        if(daManager == null) {
            daManager = new MenuReportesManager(context);
        }

        return daManager;
    }

    private MenuReportesManager(Context context) {
        super(context, "REPORTES", R.array.menu_reportes, false);

        estadoCuentaViewManager = EstadoCuentaViewManager.getInstance(context);
        detEstadoCuentaViewManager = DetEstadoCuentaViewManager.getInstance(context);// Agregado por Manuel Delgadillo - 25/02/2017
    }

    @Override
    protected FragmentListButtonListener getMenulistener() {
        return menulistener;
    }

    @Override
    protected MenuFragment getMenuFragmentInstance() {
        return MenuReportesFragment.newInstance();
    }

    /** Estas opciones vienen de res/xml/arrays
              y se configuraron en el MenuMiBrioFragment */
    private FragmentListButtonListener menulistener = new FragmentListButtonListener() {
        @Override
        public void onListButtonClicked(View btn, Object value) {
            int position = (Integer) value;

            OptionMenuFragment opcion = null;


            Log.w("MenuReporte","posicion seleccionada: "+position);

            switch (position) {

                case 0: //Estado de cuenta
                    //Toast.makeText(context, "Estado de cuenta", Toast.LENGTH_SHORT).show();
                    opcion = (OptionMenuFragment) estadoCuentaViewManager.createFragments(fragmentManager, R.id.fragmentHolder);
                    break;

                case 1: //Reportes de ventas
                    Log.w("MenuReporte","entrando a reportes");
                      opcion = new ReportesFragment();
                    break;

                case 2: //Reportes estado de cuenta
                    Log.w("MenuReporte","entrando a reportes");
                    // Comentado por Manuel Delgadillo - 25/02/2017
                    // opcion = ReportesFragment.getInstance(5);
                    // INI: Agregado por Manuel Delgadillo - 25/02/2017
                    opcion = (OptionMenuFragment) detEstadoCuentaViewManager.createFragments(fragmentManager, R.id.fragmentHolder);
                    // FIN: Agregado por Manuel Delgadillo - 25/02/2017
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
