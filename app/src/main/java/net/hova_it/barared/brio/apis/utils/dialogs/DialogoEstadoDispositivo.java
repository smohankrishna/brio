package net.hova_it.barared.brio.apis.utils.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.views.BrioCloseButton;

import lat.brio.api.vistas.VistaGIF;

public class DialogoEstadoDispositivo extends DialogFragment {

    private View _wvVista;
    private TextView _tvEstado;
    private VistaGIF _vgAnimacion;
    private BrioCloseButton _CerrarBoton;
    private int _Gif = -1;
    private String _Estado;
    private Boolean _CanceloUsuario = false;
    private View.OnClickListener _Evento;

    public void BotonCancelar(View.OnClickListener Evento){
        _Evento = Evento;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstadnceState) {
        _wvVista = getActivity().getLayoutInflater().inflate(R.layout.brio_dialog_devicecheck, null);
        _tvEstado = (TextView) _wvVista.findViewById(R.id.tvEstado);
        _vgAnimacion = (VistaGIF) _wvVista.findViewById(R.id.vgAnimacion);
        _CerrarBoton = (BrioCloseButton) _wvVista.findViewById(R.id.btnCerrarDialogo);

        _CerrarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_Evento!=null){
                    _Evento.onClick(v);
                    _CanceloUsuario = true;
                    getDialog().dismiss();
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(_wvVista);
        setCancelable(false);
        return builder.create();
    }
    public Boolean CanceloUsuario(){
        return _CanceloUsuario;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MediaUtils.setDialogTransparentBackground(this);
        //if(_Gif>0){
            // _vgAnimacion.setGifResource(_Gif);
        //}

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        _tvEstado.setText(_Estado);
    }

    /**
     * Cambia el estado de el mensaje
     * @param Estado
     */
    public void Estado(String Estado) {
        Log.w("DED", Estado);
        //if(BrioActivityMain.DEBUG_SHOW_TOASTS){
        //    if(_Estado != null) {
        //        _Estado += "\n" + Estado;
        //    } else {
        //        _Estado = Estado;
        //    }
        //}else{
            _Estado = Estado;
        //}

        if(_tvEstado != null) {
            _tvEstado.setText(_Estado);
        }
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        if (getDialog() == null) {
            setShowsDialog(false);
        }
        super.onActivityCreated(arg0);
    }

    public void Gif(int RecursoGif){
        _Gif = RecursoGif;
        try {
            _vgAnimacion.setGifResource(_Gif);
        }catch(Exception Ex){
            Ex.printStackTrace();
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
                DialogoEstadoDispositivo.super.dismissAllowingStateLoss();
            }
        }, 1000);
    }
}
