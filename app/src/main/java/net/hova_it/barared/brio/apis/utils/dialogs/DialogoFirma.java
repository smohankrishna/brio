package net.hova_it.barared.brio.apis.utils.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
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

import lat.brio.api.vistas.VistaFirma;
import lat.brio.api.vistas.VistaGIF;

public class DialogoFirma extends DialogFragment {

    private View _wvVista;
    //private TextView _tvEstado;
    private VistaFirma _vfFirma;
    private String _Estado;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        _wvVista = getActivity().getLayoutInflater().inflate(R.layout.brio_dialog_firma, null);
        //_tvEstado = (TextView) _wvVista.findViewById(R.id.tvEstado);
        _vfFirma = (VistaFirma) _wvVista.findViewById(R.id.vfFirma);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(_wvVista);
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
        // _tvEstado.setText(_Estado);
    }

    /**
     * Cambia el estado de el mensaje
     * @param Estado
     */
    public void Estado(String Estado) {
        //if(BrioActivityMain.DEBUG_SHOW_TOASTS){
        //    if(_Estado != null) {
        //        _Estado += "\n" + Estado;
        //    } else {
        //        _Estado = Estado;
        //    }
        //}else{
           // _Estado = Estado;
        //}

        //if(_tvEstado != null) {
        //    _tvEstado.setText(_Estado);
        //}
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        if (getDialog() == null) {
            setShowsDialog(false);
        }
        super.onActivityCreated(arg0);
    }

    public Bitmap Firma(){
        if(_vfFirma!=null){
            return _vfFirma.Firma();
        }else{
            return null;
        }
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
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogoFirma.super.dismissAllowingStateLoss();
            }
        }, 1000);
    }
}
