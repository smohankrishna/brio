package net.hova_it.barared.brio.apis.utils.teclado;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.views.BrioView;

/**
 * Gestiona el tipo de teclado a mostrar y el manejo al posicionar como fragment o fragment dialog
 * Created by Herman Peralta on 23/02/2016.
 */
public class TecladoManager2 {
    private static TecladoManager2 daManager;

    private TecladoBase tecladoActual;
    private TecladoAlfanumerico tecladoAlfanumerico;
    private TecladoNumerico tecladoNumerico;

    private Context context;
    private FragmentManager fragmentManager;

    private TIPO_TECLADO mTipoActual = null;
    private Boolean mAsDialogActual = null;
    private BrioView callingViewActual = null;
    private boolean isopen = false;

    private boolean isopenType2 = false;

    private int layoutocultable = -1;

    private ViewGroup panelTeclado;

    public enum TIPO_TECLADO {
        ALFANUMERICO, NUMERICO, FECHA, NUMERICO_ENTERO ,ALFANUMERICO_PASS, NUMERICO_ENTERO_CP
    }

    /**
     * Obtener instancia del manager, ya que la clase es singleton
     * @param context
     * @return
     */
    public static TecladoManager2 getInstance(Context context) {
        if(daManager == null) {
            daManager = new TecladoManager2(context);
        }

        return daManager;
    }

    private TecladoManager2(Context context) {
        this.context = context;
        this.fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

        tecladoAlfanumerico = new TecladoAlfanumerico();
        tecladoNumerico = new TecladoNumerico();

        panelTeclado = (ViewGroup)((AppCompatActivity) this.context).findViewById(R.id.panel_teclado);
    }

    /**
     * Gestiona el tipo de teclado que se quiera mostrar y sus configuraciones.
     * asDialog = true para mostrar como fragmen dialog, false para mostrar como fragment.
     * tipo = ALFANUMERICO = 1 | NUMERICO = 2 | NUMERICO_ENTERO = 4
     * titulo = Encabezado para el teclado
     * textDefault = texto a mostrar al abrir el teclado
     * @param asDialog
     * @param tipo
     * @param titulo
     * @param textDefault
     * @param tecladoOnClickListener
     */
    public void openKeyboard(boolean asDialog, TIPO_TECLADO tipo, String titulo, String textDefault, TecladoOnClickListener tecladoOnClickListener) {
        DebugLog.log(getClass(), "TECLADO", "abriendo tipo 2: " + isopenType2);
        if(!isopenType2) {

            DebugLog.log(getClass(), "TECLADO", "tipo 2: si abre");
            isopenType2 = true;

            switch (tipo) {
                case ALFANUMERICO:
                    tecladoAlfanumerico.setTitulo(titulo);
                    tecladoAlfanumerico.setTextDefault(textDefault);
                    tecladoActual = tecladoAlfanumerico;
                    break;

                case NUMERICO:
                    tecladoNumerico.setTitulo(titulo);
                    tecladoNumerico.setTextDefault(textDefault);
                    tecladoNumerico.setEntero(false);
                    tecladoNumerico.setIsCP(false);
                    tecladoActual = tecladoNumerico;
                    Log.w("TECLADO","text defaul= "+textDefault);
                    if(textDefault != null){
                        tecladoActual.setIs_Text_default(true);
                    }else {
                        tecladoActual.setIs_Text_default(false);
                    }
                    break;

                case NUMERICO_ENTERO:
                    tecladoNumerico.setTitulo(titulo);
                    tecladoNumerico.setTextDefault(textDefault);
                    tecladoNumerico.setEntero(true);
                    tecladoNumerico.setIsCP(false);
                    tecladoActual = tecladoNumerico;
                    break;

                default:
                    tecladoActual = tecladoAlfanumerico;
                    break;
            }

            tecladoActual.setTecladoOnClickListener(tecladoOnClickListener);

            if (asDialog) {
                DebugLog.log(getClass(), "TECLADO", "como dialog");
                tecladoActual.showAsDialog(fragmentManager, null);
            } else {
                DebugLog.log(getClass(), "TECLADO", "empotrado");
                //si se pide mostrar empotrado, entonces muestra el panel de teclado
                panelTeclado.setVisibility(View.VISIBLE);
                tecladoActual.showAsFragment(fragmentManager, null);

                layoutocultable = -1;
            }

            //Configurar variables de teclado
            DebugLog.log(getClass(), "TECLADO", "ya lo abri!!!");
            isopen = true;
            mTipoActual = tipo;
            mAsDialogActual = asDialog;
        }
    }

    /**
     *Gestiona el tipo de teclado que se quiera mostrar y sus configuraciones.
     * asDialog = true para mostrar como fragmen dialog, false para mostrar como fragment.
     * tipo = ALFANUMERICO = 1 | NUMERICO = 2 | NUMERICO_ENTERO = 4
     * callingView = TextView al que se vinculara el teclado
     * titulo = Encabezado para el teclado
     * textDefault = texto a mostrar al abrir el teclado
     * @param asDialog
     * @param tipo
     * @param callingView
     * @param titulo
     * @param textDefault
     */
    public void openKeyboard(boolean asDialog, TIPO_TECLADO tipo, BrioView callingView, String titulo, String textDefault) {
        boolean reload = shouldReloadKeyboard(asDialog, tipo, callingView);
        DebugLog.log(getClass(), "TECLADO", "asDialog? " + asDialog + ", tipo: " + tipo.toString() + ", shouldOpen: " + reload);
        Log.w("TECLEDO", "textDefault = " + textDefault);

        if(reload) {

            switch (tipo) {
                case ALFANUMERICO:
                    tecladoAlfanumerico.setTitulo(titulo);
                    tecladoAlfanumerico.setTextDefault(textDefault);
                    Log.w("ALPHA", "textDefault " + (textDefault == null));
                    //tecladoActual.setIs_Text_default(textDefault!=null);
                    tecladoActual = tecladoAlfanumerico;
                    break;

                case ALFANUMERICO_PASS:
                    tecladoAlfanumerico.setPass(true);
                    tecladoAlfanumerico.setTitulo(titulo);
                    tecladoAlfanumerico.setTextDefault(textDefault);
                    //tecladoActual.setIs_Text_default(textDefault != null);
                    tecladoActual = tecladoAlfanumerico;
                    break;


                case NUMERICO:
                    Log.w("NUMERICO_ENTERO","FLOTANTE");
                    tecladoNumerico.setTitulo(titulo);
                    tecladoNumerico.setTextDefault(textDefault);
                    Log.w("NUMERICO_ENTERO", "FLOTANTE textDefauld" + textDefault);
                    tecladoNumerico.setEntero(false);
                    tecladoNumerico.setIsCP(false);
                    //tecladoActual.setIs_Text_default(textDefault != null);
                    tecladoActual = tecladoNumerico;
                    if(textDefault != null){
                        tecladoActual.setIs_Text_default(true);
                    }else {
                        tecladoActual.setIs_Text_default(false);
                    }
                    break;

                case NUMERICO_ENTERO:
                    Log.w("NUMERICO_ENTERO", "ENTERO");
                    tecladoNumerico.setTitulo(titulo);
                    tecladoNumerico.setTextDefault(textDefault);
                    tecladoNumerico.setEntero(true);
                    tecladoNumerico.setIsCP(false);
                    //tecladoActual.setIs_Text_default(textDefault != null);
                    tecladoActual = tecladoNumerico;
                    break;

                case NUMERICO_ENTERO_CP:
                    Log.w("NUMERICO_ENTERO_CP","ENTERO");
                    tecladoNumerico.setTitulo(titulo);
                    tecladoNumerico.setTextDefault(textDefault);
                    tecladoNumerico.setEntero(true);
                    tecladoNumerico.setIsCP(true);
                    //tecladoActual.setIs_Text_default(textDefault != null);
                    tecladoActual = tecladoNumerico;
                    break;


                default:
                    tecladoActual = tecladoAlfanumerico;
                    break;
            }

            if (asDialog) {
                tecladoActual.showAsDialog(fragmentManager, callingView);
            } else {
                //si se pide mostrar empotrado, entonces muestra el panel de teclado
                panelTeclado.setVisibility(View.VISIBLE);
                tecladoActual.showAsFragment(fragmentManager, callingView);

                layoutocultable = callingView.getIdLayoutOcultable();
                if(layoutocultable != -1) {
                    ((BrioActivityMain) context).findViewById(layoutocultable).setVisibility(View.GONE);
                }
            }

            //Configurar variables de teclado
            isopen = true;
            DebugLog.log(getClass(), "TECLADO", "ya lo abri!!!");

            mTipoActual = tipo;
            mAsDialogActual = asDialog;

            ///Todo: cambiar el fondo o hacer algo para resaltar el textview
        } //if(reload)
    }

    /**
     * Gestion de cierre de teclado sin importar de que forma fue desplegado (fragment, fragment dialog)
     */
    public void closeKeyboard() {
        DebugLog.log(getClass(), "TECLADO", "cerrar? " + isOpen());
        if(isOpen()) {
            tecladoActual.remove();

            if (!mAsDialogActual) {
                DebugLog.log(getClass(), "TECLADO", "no era dialog");
                //si se estaba mostrando empotrado, entonces oculta el panel de teclado
                panelTeclado.setVisibility(View.GONE);

                if(layoutocultable != -1) {
                    ((BrioActivityMain) context).findViewById(layoutocultable).setVisibility(View.VISIBLE);
                    layoutocultable = -1;
                }
            } else {
                DebugLog.log(getClass(), "TECLADO", "si era dialog");
            }

            isopen = false;

            DebugLog.log(getClass(), "TECLADO", "isopen = false");
        }

        isopenType2 = false;
        MediaUtils.hideSystemUI((AppCompatActivity) context);
    }

    public boolean isOpen(){
        return isopen;
    }

    /**
     * Gestion de teclado al cambiar de TextView.
     * En caso de que el TextView que muestra el teclado requiera un tipo de teclado diferente,
     * se gestiona el reinflado del fragment. En el caso contrario solo se reasigna el TextView
     * al que debe estar vinculado
     * @param asDialog
     * @param tipo
     * @param callingView
     * @return
     */
    private boolean shouldReloadKeyboard(final boolean asDialog, final TIPO_TECLADO tipo, final BrioView callingView) {
        boolean reload;

        DebugLog.log(getClass(), "TECLADO", "");

        if(!isOpen()) {
            //Si no esta abierto, abrelo
            //verificar si la configuracion cambia aunque este cerrado, para no tener que recargar el teclado
            reload = true;

            DebugLog.log(getClass(), "TECLADO", "estaba cerrado...");

        } else {
            //Ya esta abierto, checa config

            if(asDialog && mAsDialogActual) {
                //Ya esta abierto como dialog y piden abrir como dialog
                //Este caso no deberia existir por que no es posible abrir el teclado en dialog si ya esta abierto en dialog
                DebugLog.log(getClass(), "TECLADO", "Esto no debio pasar, no hay forma de que se abra el teclado en dialog si ya esta abierto en dialog.");
                if(callingViewActual == callingView) {
                    //Piden mostrarlo como dialog desde el mismo callingview

                    reload = asDialog; //solo recarga si pidieron como dialog
                } else {
                    //Piden mostrarlo como dialog pero el callingView es otro, no cierres nada, solo cambia la referencia

                    tecladoActual.setCallingView(callingView);
                    callingViewActual = callingView;

                    reload = false; // no recargar, debido a que solo cambio el callingView pero ya esta abierto
                }
            } else
            if(!asDialog && !mAsDialogActual) {
                //estaba empotrado y lo quieren empotrado
                DebugLog.log(getClass(), "TECLADO", "estaba empotrado y lo quieren empotrado");

                if (callingViewActual != callingView) {
                    tecladoActual.setCallingView(callingView);
                    callingViewActual = callingView;
                }

                reload = false; //no es necesario recargar por que solo cambio el callingview
            } else {
                //Ya esta abierto pero la configuraciÃ³n de vista cambia...
                DebugLog.log(getClass(), "TECLADO", "Ya esta abierto pero la configuraciÃ³n de vista cambia");

                closeKeyboard();

                //Verificar si el view es el mismo
                if(callingViewActual != callingView) {
                    //cambia la configuracion de vista, pero NO es el mismo view
                    //guarda la referencia del nuevo view
                    tecladoActual.setCallingView(callingView);
                    callingViewActual = callingView;
                }

                reload = true; //como si cambio la configuraciÃ³n, entonces si hay que recargar...
            }
        }

        if(!reload) {
            //Si la vista no cambio, pero si cambiÃ³ el tipo de teclado, recarga.
            reload = (mTipoActual != tipo);
        }

        return reload;
    }
}
