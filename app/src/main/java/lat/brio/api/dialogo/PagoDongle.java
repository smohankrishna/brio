package lat.brio.api.dialogo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.views.BrioButton2;

import lat.brio.api.botones.Cerrar;
import lat.brio.api.vistas.VistaFirma;
import lat.brio.api.vistas.VistaGIF;

/**
 * Created by Delgadillo on 11/07/17.
 */


public class PagoDongle extends Dialog {
    // Contexto
    Context _Contexto;
    // Vista actual
    public enum Vista { // Me informan que el PIN es mutuamente excluyente de la FIRMA
        CONECTAR, PROCESAR, /*AUTORIZAR,*/ FIRMAR;

        Vista(){}
    }

    private Vista _Vista = Vista.CONECTAR;
    private LinearLayout _dlg_actual;
    private Integer _dlg_conectar = R.id.dlg_conectar;
    private Integer _dlg_procesar = R.id.dlg_procesar;
    private Integer _dlg_firmar = R.id.dlg_firmar;

    private View _vwVista; // Vista raíz
    private TextView _tvEstado; // Control indicador de estado
    private VistaGIF _vgAnimacion; // Control indicador de acción
    private Cerrar _btCerrar; // Botón para cerrar el dialogo de estado
    private BrioButton2 _btAceptar; // Botón para aceptar el dialogo de estado
    private BrioButton2 _btCancelar; // Botón para cancelar el dialogo de estado
    private Boolean _Cerro; // Bandera para saber si el usuario cerró
    private Boolean _Acepto; // Bandera para saber si el usuario aceptó
    private Boolean _Cancelo; // Bandera para saber si el usuario canceló
    private View.OnClickListener _evtCerrar; // Evento para el boton cerrar
    private View.OnClickListener _evtAceptar; // Evento para el boton aceptar
    private View.OnClickListener _evtCancelar; // Evento para el boton cancelar
    private ImageView _imgLogo; // Logotipo para ocular en la firma
    private Bitmap _Firma; // Objeto de firma para exportar a imagen
    private VistaFirma _vwFirma; // Vista para firmar
    private String _Estado; // Texto de estado


    public Bitmap Firma(){
        return _vwFirma.Firma();
    }

    public PagoDongle(@NonNull Context Contexto) {
        super(Contexto);
        _Contexto = Contexto;
    }

    @Override
    protected void onCreate(Bundle Instancia) {

    }

    public Vista Vista(){
        return _Vista;
    }

    public void Vista(Vista Vista){
        super.setContentView(R.layout.brio_dongle_dialogo);
        _tvEstado = (TextView) findViewById(R.id.tvEstado);
        _Vista = Vista;
        _btCerrar = (Cerrar) findViewById(R.id.btCerrar);
        _btAceptar = (BrioButton2) findViewById(R.id.btAcceptar);
        _btCancelar = (BrioButton2) findViewById(R.id.btCancelar);
        _imgLogo = (ImageView) findViewById(R.id.imgLogo);
        _vwFirma = (VistaFirma) findViewById(R.id.vfFirma);

        setCancelable(false);

        _btCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View Vista) {
                if(_evtCerrar!=null){
                    _evtCerrar.onClick(Vista);
                }
                _Cerro = true;
                dismiss();
            }
        });

        _btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View Vista) {
                if(_evtCancelar!=null){
                    _evtCancelar.onClick(Vista);
                    // Limpiar firma si es el caso
                    if(_Vista.equals(PagoDongle.Vista.FIRMAR)) {
                        _vwFirma.Limpiar();
                    }
                }
                // _Cancelo = true;
                //dismiss();
            }
        });

        // Aqui guardar la firma y enviarla
        _btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View Vista) {
                _Firma = _vwFirma.Firma();
                if(_Firma!=null){
                    if(_evtAceptar!=null){
                        _evtAceptar.onClick(Vista);
                    }
                    _Acepto = true;
                    dismiss();
                }else{
                    MediaUtils.hideSystemUI((AppCompatActivity) _Contexto);
                    BrioAlertDialog bad = new BrioAlertDialog((BrioActivityMain) _Contexto , "Advertencia", "Por favor solicite al cliente una firma valida.");
                    bad.show();
                }
            }
        });

        if(_dlg_actual!=null) {
            _dlg_actual.setVisibility(View.GONE);
            _dlg_actual = null;
        }
        switch (Vista){
            case CONECTAR:
                _dlg_actual = (LinearLayout) findViewById(_dlg_conectar);
                break;
            case PROCESAR:
                if(_btCerrar!= null ){
                    _btCerrar.setVisibility(View.VISIBLE);
                }
                _dlg_actual = (LinearLayout) findViewById(_dlg_procesar);
                break;
            case FIRMAR:
                _dlg_actual = (LinearLayout) findViewById(_dlg_firmar);
                if(_btCancelar!= null ){
                    _btCancelar.setVisibility(View.VISIBLE);
                }
                if(_btCerrar!= null ){
                    _btCerrar.setVisibility(View.GONE);
                }
                if(_btAceptar!= null ){
                    _btAceptar.setVisibility(View.VISIBLE);
                }
                if(_imgLogo!=null){
                    _imgLogo.setVisibility(View.GONE);
                }
                break;
        }
        _dlg_actual.setVisibility(View.VISIBLE);
        // Transparencia
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // showAllBanners el dialogo
        show();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // super.setContentView(layoutResID);
    }

    private PagoDongle(@NonNull Context Contexto, @StyleRes int themeResId) {
        super(Contexto, themeResId);
        _Contexto = Contexto;
    }

    private PagoDongle(@NonNull Context Contexto, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(Contexto, cancelable, cancelListener);
        _Contexto = Contexto;
    }

    public Boolean Cerro(){
        return _Cerro;
    }
    public void EventoCerrar(View.OnClickListener Evento){
        _evtCerrar = Evento;
    }
    public void EventoAceptar(View.OnClickListener Evento){
        _evtAceptar = Evento;
    }
    public void EventoCancelar(View.OnClickListener Evento){
        _evtCancelar = Evento;
    }
    public void Estado(String Estado){
        _tvEstado.setText(Estado);
    }
}