package net.hova_it.barared.brio.apis.utils.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.preferencias.Preferencias;
import net.hova_it.barared.brio.apis.utils.preferencias.PreferenciasCol;

import brio.sdk.logger.util.BrioUtils;
import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioGlobales;

/**
 * Created by Guillermo Ortiz  on 09/02/2018.
 * Notifica que cuenta con una versión beta de la aplicación
 */
@SuppressLint("ValidFragment")
public class BrioDialogVersionBeta extends DialogFragment implements View.OnClickListener {
    
    private View rootView;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private DialogListener listener;
    private CheckBox cb_ocultar;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createSimpleDialog();
    }
    
    public BrioDialogVersionBeta(final DialogListener listener) {
        this.listener = listener;
        
    }
    
    public AlertDialog createSimpleDialog() {
        
        try {
            
            
            LayoutInflater inflater = getActivity().getLayoutInflater();
            rootView = inflater.inflate(R.layout.brio_dialog_version_beta, null);
            
            Button btnAccept = (Button) rootView.findViewById(R.id.btnAccept);
            cb_ocultar = (CheckBox) rootView.findViewById(R.id.cb_ocultar);
            btnAccept.setOnClickListener(this);
            
            cb_ocultar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Preferencias.i.setShowDialogBeta(! isChecked);
                }
            });
            
            builder = new AlertDialog.Builder(getActivity());
            builder.setView(rootView);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
            BrioGlobales.ArchivoLog.escribirLineaFichero(BrioUtilsFechas.obtenerFechaDiaFormat() + "BrioDialogVersionBeta->createSimpleDialog ->" + e.getMessage());
        }
        
        return alertDialog;
    }
    
    /**
     * Te muestra el dialogo en transparente
     */
//    public void show() {
//        alertDialog = builder.show();
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//    }
    
    /**
     * @param v boton aceptar que te saca del dialogo
     */
    @Override
    public void onClick(View v) {
        MediaUtils.hideSystemUI((BrioActivityMain) getActivity());
        
        alertDialog.dismiss();
        
        switch (v.getId()) {
            case R.id.btnAccept:
                MediaUtils.hideSystemUI((BrioActivityMain) getActivity());
                alertDialog.dismiss();
                if (listener != null) {
                    listener.onAccept();
                }
                break;
        }
    }
}
