package lat.brio.api.dialogo;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.views.BrioCloseButton;

/**
 * Created by guillermo.ortiz on 13/03/18.
 */


public class BrioSyncRememberDialog implements View.OnClickListener {
    private final int mes = 24*7*4;
    
    private View rootView;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private TextView txtTitle;
    
    private String title;
    private Context context;
    private BrioCloseButton _btCerrar;
    
    
    public BrioSyncRememberDialog( Context Context) {
    
        context=Context;
        LayoutInflater inflater = ((AppCompatActivity)context).getLayoutInflater();
        rootView = inflater.inflate(R.layout.brio_dialog_sync, null);
        
        txtTitle = (TextView) rootView.findViewById(R.id.txtTitle);
        _btCerrar = (BrioCloseButton)rootView.findViewById(R.id.btnCancelar);
        _btCerrar.setOnClickListener(this);
        
        title = Utils.getString(R.string.dialog_ban_sync, context);
        
        builder = new AlertDialog.Builder(context);
        builder.setView(rootView);
        builder.setCancelable(false);
        builder.create();
    }
    
    public void show(final int horas) {
        
        if (!((BrioActivityMain) context).isFinishing()) {
            
            dismiss();
            
            alertDialog = builder.show();
            
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable (android.graphics.Color.TRANSPARENT));
            
            if (horas > 3 * mes) {
                txtTitle.setText(Utils.getString(R.string.dialog_ban_no_sync, context));
            } else if (horas >= 24) {
                txtTitle.setText(Utils.getString(R.string.dialog_ban_no_sync_days, context).replace("{0}", String.valueOf(horas / 24)));
            } else {
                txtTitle.setText(title.replace("?0", String.valueOf(horas)));
            }
            
            int size[] = Utils.getDisplaySize((AppCompatActivity) context);
            alertDialog.getWindow().setLayout(4 * size[0] / 5, size[1]);
        }
    }
    public void Cerrar(View.OnClickListener Evento){
        _btCerrar.setOnClickListener(Evento);
    }
    
    public void dismiss() {
        if(alertDialog!=null && alertDialog.isShowing()) {
            try {
                alertDialog.dismiss();
                
                MediaUtils.hideSystemUI((AppCompatActivity) context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancelar:
                dismiss();
                break;
        }
    }
}