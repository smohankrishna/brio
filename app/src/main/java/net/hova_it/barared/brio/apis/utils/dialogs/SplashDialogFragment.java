package net.hova_it.barared.brio.apis.utils.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.MediaUtils;

import lat.brio.core.BrioGlobales;

import static brio.sdk.logger.util.BrioUtilsFechas.obtenerFechaDiaHoraFormat;


/**
 * Created by Herman Peralta on 08/03/2016.
 * Clase que muestra el splash al inicializar la aplicación, el cual  tambien te muestra la version que tienes
 */
public class SplashDialogFragment extends DialogFragment {

    private View rootView;
    private TextView tvMsg, tvLog;
    private String strlog = "";

    SplashDialogFragment instance;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        rootView = getActivity().getLayoutInflater().inflate(R.layout.brio_dialog_versioncheck, null);

        tvMsg = (TextView) rootView.findViewById(R.id.tvMsg);
        tvLog = (TextView) rootView.findViewById(R.id.tvLog);

        if (instance == null)
            instance = this;

        //Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(rootView);
        setCancelable(false);

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MediaUtils.setDialogTransparentBackground(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (strlog != null && strlog.length() > 0) {
            tvLog.setText(strlog);
        }
    }

    /**
     * Agrega un mensaje al dialogo.
     *
     * @param line
     */
    public void publish(String line) {
        if (strlog != null) {
            strlog += "\n" + line;
        } else {
            strlog = line;
        }

        if (tvLog != null) {
            tvLog.setText(strlog);
        }
    }

    /**
     * http://stackoverflow.com/questions/12265611/dialogfragment-nullpointerexception-support-library
     *
     * @param arg0
     */
    @Override
    public void onActivityCreated(Bundle arg0) {
        if (getDialog() == null) {  // Returns the dialog
            // Tells DialogFragment to not use the fragment as a dialog, and so won't try to use mDial
            setShowsDialog(false);
        }
        super.onActivityCreated(arg0);  // Will now complete and not crash
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void dismissAllowingStateLoss() {

        try {
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                   //  SplashDialogFragment.super.dismissAllowingStateLoss();
                     if (instance != null && instance.getActivity() != null){
                         instance.dismiss();
                     }
                }
            }, 100);
            //instance.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(obtenerFechaDiaHoraFormat() + getClass() + "->dismissAllowingStateLoss" + e.getMessage());
        }
    }
}
