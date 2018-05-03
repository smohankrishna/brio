package net.hova_it.barared.brio.apis.utils.dialogs;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.MediaUtils;

/**
 * Created by Herman Peralta on 30/03/2016.
 * Dialogo de alerta cuando algo no es correcto
 */
public class BrioAlertDialog implements View.OnClickListener {

    private View rootView;
    private AppCompatActivity activity;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    public BrioAlertDialog(AppCompatActivity activity, String title, String msg) {
        this.activity = activity;

        LayoutInflater inflater = activity.getLayoutInflater();
        rootView = inflater.inflate(R.layout.brio_dialog_alert, null);
        rootView.findViewById(R.id.btnCancelar).setOnClickListener(this);

        if(title != null) { ((TextView) rootView.findViewById(R.id.dialog_title)).setText(title); } else { rootView.findViewById(R.id.dialog_title).setVisibility(View.GONE); }
        ((TextView) rootView.findViewById(R.id.dialog_msg)).setText(msg);

        builder = new AlertDialog.Builder(activity);
        builder.setView(rootView);
        builder.setCancelable(false);
        builder.create();
    }

    /**
     * Muestra el dialogo y hace que la ventana se vea transparente
     */
    public void show() {
        alertDialog = builder.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancelar:
                alertDialog.dismiss();

                MediaUtils.hideSystemUI(activity);
                break;
        }
    }
}
