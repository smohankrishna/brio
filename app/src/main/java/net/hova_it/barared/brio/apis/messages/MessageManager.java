package net.hova_it.barared.brio.apis.messages;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAcceptDialog;

/**
 * Created by Herman Peralta on 18/02/2016.
 */
public class MessageManager {

    private static MessageManager daManager;

    private Context context;

    public static MessageManager getInstance(Context context) {
        if(daManager == null) {
            daManager = new MessageManager(context);
        }

        return daManager;
    }

    private MessageManager(Context context) {
        this.context = context;
    }

    public void show(final String m) {
        try {
            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //todo: limpiar esto para saber la causa del error
                    //todo: poner drawables de icono de error por modulo
                    //todo: message manager
                    /*AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setPositiveButton("OK", null);
                    AlertDialog alert = dialog.create();
                    alert.setTitle("ErrorManager");
                    alert.setMessage(m);
                    alert.setCancelable(false);
                    alert.show();*/

                    BrioAcceptDialog bad = new BrioAcceptDialog((AppCompatActivity) context, "ErrorManager", m, Utils.getString(R.string.brio_aceptar, context), null);
                    bad.show();
                }
            });
        } catch (Exception e) {

        }
    }
}
