package lat.brio.api.alerta;

import android.os.Handler;

/**
 * Created by Delgadillo on 29/06/17.
 */

public class OAlerta {
    private final String _Anuncio;
    private final Integer _Intervalo;
    private Boolean _Activo = true;
    private Boolean _Mostrando = false;
    private Handler _Manejador;
    public lat.brio.api.dialogo.Alerta Alerta;

    public OAlerta(String Anuncio, Integer Intervalo){
        _Activo = true;
        _Anuncio = Anuncio;
        _Intervalo = Intervalo;
    }

    public Boolean Activo(){
        return _Activo;
    }

    public Boolean Activo(Boolean Activo){
        _Activo = Activo;
        return _Activo;
    }

    public Boolean Mostrando(){
        return _Mostrando;
    }

    public Boolean Mostrando(Boolean Mostrando){
        _Mostrando = Mostrando;
        return _Mostrando;
    }

    public String Anuncio(){
        return _Anuncio;
    }

    public Integer Intervalo(){
        return _Intervalo;
    }

    public void Programar(Runnable Executable){
        if(_Activo) {
            _Manejador = new Handler();
            _Manejador.postDelayed(Executable, ((_Intervalo) * 60 * 1000));
        }
    }
}
