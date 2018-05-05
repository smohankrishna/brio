package net.hova_it.barared.brio.apis.utils.teclado;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.AnimationUtils2;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.views.BrioView;

/**
 * Manejador de funciones basicas de los teclados (control de teclas, borrar, despliegue de teclado)
 * Created by Herman Peralta on 23/02/2016.
 */
public abstract class TecladoBase extends DialogFragment implements View.OnClickListener, View.OnTouchListener {
    public final static String TAG_FRAGMENT = TecladoBase.class.getSimpleName();
    public final static int BACKSPACE_TIMEOUT = 250;
    public final static int BACKSPACE_DELETE = 100;



    protected final static int RES_ID_ALFANUMERICO = R.layout.teclado_alfa;
    protected final static int RES_ID_NUMERICO = R.layout.teclado_numerico;

    protected boolean is_dialog = false;
    protected boolean is_text_default = false;
    protected static int[] btnRes;
    protected TextView[] btns;

    protected int layout_res_id = -1;

    protected View rootView;
    private FragmentManager fragmentManager;

    protected String textToShow = "";

    protected AudioManager audioManager;

    protected TextView textViewExterior;
    public TextView textViewInterior;
    public RelativeLayout textAndButton;

    protected TecladoOnClickListener tecladoOnClickListener;

    private boolean hasExternalTecladoOnClickListener = false;

    /**
     * Informa si se a ingresado un texto predefinido para el campo de texto que tiene el foco del
     * teclado
     * @param is_text_default
     */
    public void setIs_Text_default(boolean is_text_default){
        this.is_text_default = is_text_default;
    }

    /**
     * Funcion utilizada para inflar el teclado como fragment dialog
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(layout_res_id == -1) {
            DebugLog.log(this.getClass(), "TECLADO", "La clase hija no seteo el layout del teclado, usando alfanumerico por default");

        }
        is_dialog = true;
        rootView = getActivity().getLayoutInflater().inflate(layout_res_id, null);

        //Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setView(rootView);
        setCancelable(false);

        return builder.create();
    }

    /**
     * Funcion utilizada para inflar el teclado como fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(!is_dialog) {
            rootView = inflater.inflate(layout_res_id, container, false);
        }

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        try{//quita
            configureKeys();

            configureUI();
        }catch (Exception e){
            e.printStackTrace();
            String traza = Log.getStackTraceString(e);
        }

        textViewInterior.setText(textToShow);

        // Esto va al final
        if(is_dialog) {
            return super.onCreateView(inflater, container, savedInstanceState);
        } else {
            textViewInterior.setVisibility(View.GONE);
            //textAndButton.setVisibility(View.GONE);
            return rootView;
        }
    }

    /**
     * Asignacion de texto a procesar y fondo
     */
    @Override
    public void onStart() {
        super.onStart();

        textViewInterior.setText(textToShow);

        if(is_dialog) {
            // solamente si se esta mostrando como dialogo, poner el fondo del dialog transparente
            Window window = getDialog().getWindow();
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    /**
     * Ingreso de configuraciones previas al despliegue del teclado
     */
    public abstract void configureUI();

    /**
     * Mapeo de valores correspondientes a cada tecla
     */
    public abstract void configureKeys();

    /**
     * Limpieza de parametros y asignaciones
     */
    public abstract void resetKeyboard();

    /**
     * Mostrar teclado como fragment dialog
     * @param fragmentManager
     * @param brioView
     */
    public void showAsDialog(FragmentManager fragmentManager, BrioView brioView) {
        DebugLog.log(this.getClass(), "TECLADO", "");

        init(fragmentManager, brioView);

        is_dialog = true;

        show(fragmentManager, TAG_FRAGMENT);
    }

    /**
     * Mostrar teclado como fragment
     * @param fragmentManager
     * @param brioView
     */
    public void showAsFragment(FragmentManager fragmentManager, BrioView brioView) {
        DebugLog.log(this.getClass(), "TECLADO", "");
        init(fragmentManager, brioView);

        is_dialog = false;

        this.fragmentManager
                .beginTransaction()
                /*.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)*/
                .replace(R.id.panel_teclado, this, TAG_FRAGMENT)
                .commit();
    }

    /**
     * Quitar teclado
     */
    public void remove() {
        if(!is_dialog) {
            this.fragmentManager
                    .beginTransaction()
                    /*.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)*/
                    .remove(this)
                    .commit();
        } else {
            DebugLog.log(getClass(), "TECLADO", "antes dismiss");
            dismiss();
            DebugLog.log(getClass(), "TECLADO", "despues dismiss");
        }
    }

    /**
     * Inicializacion de teclado
     * @param fragmentManager
     * @param callingView
     */
    private void init(FragmentManager fragmentManager, BrioView callingView) {
        this.fragmentManager = fragmentManager;

        setCallingView(callingView);
    }


    /**
     * Vinculacion de teclado con el TextView que lo desplego
     * @param callingView
     */
    public void setCallingView(BrioView callingView) {

        if(callingView != null) {
            resetKeyboard();
            textViewExterior = callingView.getTextView();
            textToShow = textViewExterior.getText().toString();

            if (textViewInterior != null) {
                textViewInterior.setText(textToShow);
            }

            tecladoOnClickListener = callingView.getTecladoOnClickListener();
        } else {
            //cuando se invoca sin callingView
            textToShow = "";
            if (textViewInterior != null) {
                textViewInterior.setText(textToShow);
            }
        }
    }


    protected void onCharKeyPress(View btn) {
        textToShow += btn.getTag();
        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);

        textViewInterior.setText(textToShow);
        if(!hasExternalTecladoOnClickListener) {
            if(!is_text_default) {
                textViewExterior.setText(textToShow);
            }
        }
        AnimationUtils2.animateViewPush(btn, 0.75f, 100);
    }

    protected void onEnterKeyPress() {
        DebugLog.log(getClass(), "TECLADO", "onEnterKeyPress..........");

        ((BrioBaseActivity)getActivity()).managerTeclado.closeKeyboard();
        if(tecladoOnClickListener!=null) { tecladoOnClickListener.onAcceptResult(textToShow); }

        hasExternalTecladoOnClickListener = false;
        tecladoOnClickListener = null;
    }

    /**
     * Asignacion de listener
     * @param tecladoOnClickListener
     */
    public void setTecladoOnClickListener(TecladoOnClickListener tecladoOnClickListener) {
        hasExternalTecladoOnClickListener = true;

        this.tecladoOnClickListener = tecladoOnClickListener;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // Manejo de la tecla backspace
    //////////////////////////////////////////////////////////////////////////////////////

    /**
     * Manejo de tecla backspace.
     * Gestiona el tiempo que la tecla se mantiene presionada para realizar un borrado continuo.
     * Gestion de animacion de tecla al ser presionada
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(v.getId()== R.id.btnBack || v.getId()== R.id.btnBack2) {
            Log.w("teclado numerico","onTouch text default = "+(is_text_default==true));
            if(!is_text_default) {//TODO:experimento
                Log.w("teclado numerico","onTouch text default true, entreeee y valio");
                textToShow = textViewExterior != null ? textViewExterior.getText().toString() : textToShow;
                //textViewInterior.setText(textToShow);//TODO:experimento
            }

            AnimationUtils2.animateViewPush(v, 0.7f, 50);


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    keyBackSpaceUp = false;
                    keyBackSpaceHandler.postDelayed(keyBackSpaceRunnable, BACKSPACE_TIMEOUT);
                    break;

                case MotionEvent.ACTION_UP:
                    keyBackSpaceUp = true;
                    break;
            }
        }


        return true;
    }

    boolean keyBackSpaceUp = false;
    protected Handler keyBackSpaceHandler = new Handler();
    protected Runnable keyBackSpaceRunnable = new Runnable() {
        //@see http://www.mopri.de/2010/timertask-bad-do-it-the-android-way-use-a-handler/

        @Override
        public void run() {
            //String strText = editor.getText().toString();
            if (textToShow.length() > 0) {
                textToShow = textToShow.substring(0, textToShow.length() - 1);
            }
            Log.w("teclado numerico","keyBackSpaceRunnable textTOShow= "+textToShow+"textViewInterior= "+textViewInterior);
            textViewInterior.setText(textToShow);//TODO:experimento
            if(!hasExternalTecladoOnClickListener) {
                Log.w("teclado numerico","text default = "+(is_text_default==true));
                if(!is_text_default){
                    Log.w("teclado numerico","onTouch text default true, entreeee y valio");
                textViewExterior.setText(textToShow); }
            }
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);

            //endOfEditionText(strText);
            if(!keyBackSpaceUp) { keyBackSpaceHandler.postDelayed(this, BACKSPACE_DELETE); }
        }
    };

}