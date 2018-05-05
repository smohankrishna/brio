package lat.brio.api.dialogo;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import net.hova_it.barared.brio.R;

import lat.brio.api.botones.Cerrar;

/**
 * Created by Manuel Delgadillo on 05/07/2017.
 */
public class Alerta {

    private View _Vista;
    private Cerrar _btCerrar;
    private View.OnClickListener _Evento;
    private AlertDialog.Builder _Builder;
    private AlertDialog _Dialogo;
    private WebView _WvVista;

    public Alerta(AppCompatActivity Actividad) {
        LayoutInflater inflater = Actividad.getLayoutInflater();
        _Vista = inflater.inflate(R.layout.dialogo_alerta, null);
        _WvVista = (WebView) _Vista.findViewById(R.id.WvVista);
        _btCerrar = (Cerrar)_Vista.findViewById(R.id.btnCerrar);
        _btCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                _Dialogo.dismiss();
                if(_Evento!=null){
                    _Evento.onClick(vista);
                }
            }
        });
        _Builder = new AlertDialog.Builder(Actividad);
        _Builder.setView(_Vista);
        _Builder.setCancelable(false);
        _Builder.create();

    }
    
    public void hideShowButton(Boolean show){
        _btCerrar.setVisibility (show?View.VISIBLE:View.GONE);
    }

    public void Cerrar(){
        if(_Dialogo != null) {
            _Dialogo.dismiss();
        }
    }
    public void Cerrar(View.OnClickListener Evento){
        _btCerrar.setOnClickListener(Evento);
    }

    public void show() {
        _Dialogo = _Builder.show();
        _Dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void showBanner(String URL) {
        _Dialogo = _Builder.show();
        _Dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _WvVista.getSettings().setJavaScriptEnabled(true);
        _WvVista.setBackgroundColor(0x00000000);
        _WvVista.loadUrl(URL);
        _Dialogo.getWindow().setLayout(800, 520);
    }
}
