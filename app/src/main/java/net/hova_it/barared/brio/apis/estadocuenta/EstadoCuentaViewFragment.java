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

public class EstadoCuentaViewFragment extends OptionMenuFragment {
    private final String TAG = "BARA_LOG";

    private EstadoCuentaViewManager estadoCuentaViewManager;
    private EstadoCuentaViewFragmentListener estadoCuentaViewFragmentListener;

    public static EstadoCuentaViewFragment newInstance() {
        Bundle args = new Bundle();

        EstadoCuentaViewFragment estadoCuentaViewFragment = new EstadoCuentaViewFragment();
        estadoCuentaViewFragment.setArguments(args);

        return estadoCuentaViewFragment;
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

        if(estadoCuentaViewManager != null) {
            estadoCuentaViewFragmentListener = estadoCuentaViewManager;
            estadoCuentaViewFragmentListener.onFragmentCreated();
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
     * @param estadoCuentaViewManager  vista del estado de cuenta
     */
    public void setEstadoCuentaViewManager(EstadoCuentaViewManager estadoCuentaViewManager) {
        this.estadoCuentaViewManager = estadoCuentaViewManager;
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

    public interface EstadoCuentaViewFragmentListener {
        void onFragmentCreated();
    }
}
