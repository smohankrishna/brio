package net.hova_it.barared.brio.apis.utils.dialogs;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.views.BrioButton2;

/**
 * Created by Herman Peralta on 30/03/2016.
 * Dialogo de confirmación
 */
public class BrioConfirmDialog implements View.OnClickListener {

    private View rootView;
    private AppCompatActivity activity;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    public TextView mensaje;

    private BrioButton2
            btnAccept,
            btnCancel;

    private DialogListener dialogListener;

    /**
     *
     * @param activity
     * @param title
     * @param msg
     * @param cancel_accept_text
     * @param dialogListener
     */
    public BrioConfirmDialog(AppCompatActivity activity, String title, String msg, String[] cancel_accept_text, final DialogListener dialogListener) {
        this.dialogListener = dialogListener;
        this.activity = activity;

        LayoutInflater inflater = activity.getLayoutInflater();
        rootView = inflater.inflate(R.layout.brio_dialog_confirm, null);
        btnAccept = (BrioButton2)rootView.findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(this);
        btnCancel = (BrioButton2)rootView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        mensaje = (TextView) rootView.findViewById(R.id.dialog_msg);

        if(title != null) { ((TextView) rootView.findViewById(R.id.dialog_title)).setText(title); } else { rootView.findViewById(R.id.dialog_title).setVisibility(View.GONE); }
        ((TextView) rootView.findViewById(R.id.dialog_msg)).setText(msg);
        if(cancel_accept_text != null) { btnCancel.setText(cancel_accept_text[0]); btnAccept.setText(cancel_accept_text[1]); }

        builder = new AlertDialog.Builder(activity);
        builder.setView(rootView);
        builder.setCancelable(false);
        builder.create();
    }

    /**
     *
     * @param activity
     * @param title
     * @param msg
     * @param dialogListener
     * @param msgAlign
     */
    public BrioConfirmDialog(AppCompatActivity activity, String title, String msg, final DialogListener dialogListener, int msgAlign) {
        this.dialogListener = dialogListener;
        this.activity = activity;


        LayoutInflater inflater = activity.getLayoutInflater();
        rootView = inflater.inflate(R.layout.brio_dialog_confirm, null);
        rootView.findViewById(R.id.btnAccept).setOnClickListener(this);
        rootView.findViewById(R.id.btnCancel).setOnClickListener(this);
        mensaje = (TextView) rootView.findViewById(R.id.dialog_msg);

        mensaje.setGravity(msgAlign);

        if(title != null) { ((TextView) rootView.findViewById(R.id.dialog_title)).setText(title); } else { rootView.findViewById(R.id.dialog_title).setVisibility(View.GONE); }
        ((TextView) rootView.findViewById(R.id.dialog_msg)).setText(msg);

        builder = new AlertDialog.Builder(activity);
        builder.setView(rootView);
        builder.setCancelable(false);
        builder.create();
    }

    /**
     * Muestra la el dialogo y pone la ventana en transparente
     */
    public void show() {
        alertDialog = builder.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    /**
     * @param v btn aceptar que te permite confirmar la accion y continuar manda un listener o cancelar la cual te regresa al fragmento actual
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccept:
                if(dialogListener != null) {
                    dialogListener.onAccept();
                }
                break;

            case R.id.btnCancel:
                if(dialogListener != null) {
                    dialogListener.onCancel();
                }
                break;
        }

        MediaUtils.hideSystemUI(activity);
        alertDialog.dismiss();
    }

    public void dismiss() {
        if (alertDialog != null)
            alertDialog.dismiss();
    }
}
