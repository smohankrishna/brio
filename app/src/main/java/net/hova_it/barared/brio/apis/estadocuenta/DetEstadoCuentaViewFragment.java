package net.hova_it.barared.brio.apis.estadocuenta;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que genera el estado de cuenta
 */

public class DetEstadoCuentaViewFragment extends OptionMenuFragment {
    private final String TAG = "BARA_LOG";

    private DetEstadoCuentaViewManager detEstadoCuentaViewManager;
    private DetEstadoCuentaViewFragmentListener detEstadoCuentaViewFragmentListener;

    public static DetEstadoCuentaViewFragment newInstance() {
        Bundle args = new Bundle();

        DetEstadoCuentaViewFragment detEstadoCuentaViewFragment = new DetEstadoCuentaViewFragment();
        detEstadoCuentaViewFragment.setArguments(args);

        return detEstadoCuentaViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        rootView = inflater.inflate(R.layout.services_view_fragment, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "searchAlarmStart EstadoCuentaViewFragment");

        if(detEstadoCuentaViewManager != null) {
            detEstadoCuentaViewFragmentListener = detEstadoCuentaViewManager;
            detEstadoCuentaViewFragmentListener.onFragmentCreated();
        }

    }

    /**
     * Mapero del webview para ocuparlo en el manager
     * @return retorna el webview
     */
    public Map<String, Object> getFields() {
        WebView estadoCuentaWebView = (WebView)rootView.findViewById(R.id.servicesWebView);

        Map<String, Object> fields = new HashMap<>();
        fields.put("estadoCuentaWebView", estadoCuentaWebView);

        return fields;
    }

    /**
     * Setea el estado de cuenta en el manager
     * @param detEstadoCuentaViewManager  vista del estado de cuenta
     */
    public void setEstadoCuentaViewManager(DetEstadoCuentaViewManager detEstadoCuentaViewManager) {
        this.detEstadoCuentaViewManager = detEstadoCuentaViewManager;
    }

    @Override
    protected View getRootView() {
        return rootView;
    }

    @Override
    protected void beforeRemove() {

    }

    @Override
    public void onPause() {
        ((BrioActivityMain)getActivity()).setDispatchKey(true);
       // Toast.makeText(getActivity(), "entra en onpause y activa la pistola", Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    @Override
    public void onStop() {
        ((BrioActivityMain)getActivity()).setDispatchKey(true);
       // Toast.makeText(getActivity(), "entra en onstop y activa la pistola", Toast.LENGTH_SHORT).show();
        super.onStop();
    }

    public interface DetEstadoCuentaViewFragmentListener {
        void onFragmentCreated();
    }
}
