package net.hova_it.barared.brio.apis.videollamada;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;

/**
 * Fragment de la VideoLlamada que utiliza el framework OpenTok para
 * conectarse al servicio de Video Llamada de Barared.
 *
 * Created by Herman Peralta on 08/04/2016.
 */
public class VideoLlamadaFragment extends OptionMenuFragment
        implements View.OnClickListener {
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.videollamada_fragment, container, false);

        rootView.findViewById(R.id.btnAccept).setOnClickListener(this);

        return rootView;
    }

    @Override
    protected View getRootView() {
        return rootView;
    }

    @Override
    protected void beforeRemove() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccept:
                VideoLlamadaManager videoLlamadaManager = VideoLlamadaManager.getInstance(getActivity());
                videoLlamadaManager.iniciar();
                break;
        }
    }
}
