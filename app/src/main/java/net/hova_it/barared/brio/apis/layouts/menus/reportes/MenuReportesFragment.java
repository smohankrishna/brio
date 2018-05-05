package net.hova_it.barared.brio.apis.layouts.menus.reportes;

import android.view.View;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuFragment;
import net.hova_it.barared.brio.apis.network.NetworkStatus;
import net.hova_it.barared.brio.apis.utils.DebugLog;

/**
 * Created by Herman Peralta on 09/03/2016.
 * Clase que crea el menu de reportes
 */
public class MenuReportesFragment extends MenuFragment {

    public static MenuReportesFragment newInstance() {
        MenuReportesFragment fgmt = new MenuReportesFragment();
        fgmt.resid_layout = R.layout.menu_reportes;
        fgmt.resid_array_menuitems = R.array.menu_reportes;
        fgmt.resid_array_drawable_menuiconsoff = R.array.menu_reportes_iconos_off;
        fgmt.num_columnas = 2;

        DebugLog.log(MenuReportesFragment.class, "MiBrio", "");

        return fgmt;
    }

    @Override
    public void onResume() {
        super.onResume();

        View panelInternet = rootView.findViewById(R.id.panelInternet);
        View panelMenu = rootView.findViewById(R.id.panelMenu);

        if(NetworkStatus.hasInternetAccess(getActivity())) {
            panelInternet.setVisibility(View.GONE);
            panelMenu.setVisibility(View.VISIBLE);
        } else {
            panelMenu.setVisibility(View.GONE);
            panelInternet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void configureUI() {

    }

    @Override
    protected void beforeRemove() {

    }
}
