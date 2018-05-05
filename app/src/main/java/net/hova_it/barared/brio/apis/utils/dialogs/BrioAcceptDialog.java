package net.hova_it.barared.brio.apis.utils.dialogs;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.MediaUtils;

/**
 * Created by Herman Peralta on 30/03/2016.
 * Dialogo de continuar muestra el titulo y un mensaje
 */
public class BrioAcceptDialog implements View.OnClickListener {

    private View rootView;
    private AppCompatActivity activity;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private DialogListener listener;

    public BrioAcceptDialog(AppCompatActivity activity, String title, String msg, String acceptStr, final DialogListener listener) {
        this.listener = listener;
        this.activity = activity;

        LayoutInflater inflater = activity.getLayoutInflater();
        rootView = inflater.inflate(R.layout.brio_dialog_accept, null);

        Button btnAccept = (Button)rootView.findViewById(R.id.btnAccept);
        if(acceptStr != null) { btnAccept.setText(acceptStr); }
        btnAccept.setOnClickListener(this);

        if(title != null) { ((TextView) rootView.findViewById(R.id.dialog_title)).setText(title); } else { rootView.findViewById(R.id.dialog_title).setVisibility(View.GONE); }
        ((TextView) rootView.findViewById(R.id.dialog_msg)).setText(msg);

        builder = new AlertDialog.Builder(activity);
        builder.setView(rootView);
        builder.setCancelable(false);
        builder.create();
    }

    /**
     * Te muestra el dialogo en transparente
     */
    public void show() {
        alertDialog = builder.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    /**
     * @param v boton aceptar que te saca del dialogo
     */
    @Override
    public void onClick(View v) {
        MediaUtils.hideSystemUI(activity);

        alertDialog.dismiss();

        switch (v.getId()) {
            case R.id.btnAccept:
                MediaUtils.hideSystemUI(activity);
                alertDialog.dismiss();
                if(listener!= null) { listener.onAccept(); }
                break;
        }
    }
}
