package net.hova_it.barared.brio.apis.services;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;

import java.util.HashMap;
import java.util.Map;


/**
 * Clase que manda a llamar el webview de servicios en la aplicacion
 */
public class ServicesViewFragment extends Fragment {
    private final String TAG = "BARA_LOG";

    private View view;
    private ServicesViewManager servicesViewManager;
    private ServicesViewFragmentListener servicesViewFragmentListener;

    private View btnSaveUsPlease;

    public static ServicesViewFragment newInstance() {
        Bundle args = new Bundle();

        ServicesViewFragment servicesViewFragment = new ServicesViewFragment();
        servicesViewFragment.setArguments(args);

        return servicesViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final View view = inflater.inflate(R.layout.services_view_fragment, container, false);
        this.view = view;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "searchAlarmStart ServicesViewFragment");

        if(servicesViewManager != null) {
            servicesViewFragmentListener = servicesViewManager;
            servicesViewFragmentListener.onFragmentCreated();
        }

    }

    //se vuelve habilitar la pistola para el cambio de pestaña con el POS
    @Override
    public void onPause() {
        ((BrioActivityMain)getActivity()).setDispatchKey(true);
        super.onPause();
    }

    /**
     * Mapea el webview para poder ocuparlo en el manager
     * @return retorna el webview
     */
    public Map<String, Object> getFields() {
        WebView servicesWebView = (WebView)view.findViewById(R.id.servicesWebView);

        Map<String, Object> fields = new HashMap<>();
        fields.put("servicesWebView", servicesWebView);

        return fields;
    }

    public void setServicesViewManager(ServicesViewManager servicesViewManager) {
        this.servicesViewManager = servicesViewManager;
    }

    public interface ServicesViewFragmentListener {
        void onFragmentCreated();
    }

}
