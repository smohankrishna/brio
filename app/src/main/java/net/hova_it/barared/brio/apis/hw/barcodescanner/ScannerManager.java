package net.hova_it.barared.brio.apis.hw.barcodescanner;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.apis.utils.MediaUtils;

/**
 * Clase de la funcionalidad de la lectora de codigo de barras
 *
 * Created by herman on 01/03/16.
 */
public class ScannerManager {

    private static ScannerManager daManager;

    private static final String pattern = "[^0-9a-zA-Z]";
    public static final int BUFFER_LIMIT = 256;
    public static final int KEYCODE_DELIM_ENTER = 66;

    private static char[] mInputBuffer = new char[BUFFER_LIMIT];

    private Context context;
    private int mBufferIndex = 0;
    private ScannerListener mScannerListener = null;
    private boolean mListenInput = false;
    private int mKeycodeDelim = KEYCODE_DELIM_ENTER;

    public static ScannerManager getInstance(Context context) {
        if(daManager == null) {
            daManager = new ScannerManager(context);
        }
        return daManager;
    }

    private ScannerManager(Context context) {
        this.context = context;

    }

    /**
     * Gestionar la entrada: Lectora de código de barras
     *
     * Invocar este metodo en dispatchKeyEvent del activity principal
     *
     * http://stackoverflow.com/questions/11273243/android-get-keyboard-key-press
     * @param KEvent - evento de entrada
     */
    public boolean dispatchKeyEvent(KeyEvent KEvent) {

        if(mListenInput) {
            Log.e ("ScannerManager", "Entra el Scanner y valida las teclas ");
            
            int keyaction = KEvent.getAction();
                int keycode = KEvent.getKeyCode();
                int keyunicode = KEvent.getUnicodeChar();//getUnicodeChar(KEvent.getMetaState());
                char character = (char) keyunicode;

            if (keyaction != KeyEvent.ACTION_DOWN) {
                
                if (keycode != mKeycodeDelim) {
                    if(mBufferIndex == BUFFER_LIMIT) {
                        Log.e("BrioBaseActivity", "dispatchKeyEvent(): ERROR, el buffer ha sido sobrepasado, se ignorarán los demás caracteres entrantes. El límite es " + BUFFER_LIMIT);
                        mBufferIndex = BUFFER_LIMIT - 1;
                    } else {
                        mInputBuffer[mBufferIndex] = character;
                        mBufferIndex++;
                    }
                } else {
                    // Enter --> CR '\n'
                    String line = String.copyValueOf(mInputBuffer, 0, mBufferIndex);


                    String antes = line;


                    line = line.replaceAll(pattern, "");


                    if (BrioActivityMain.DEBUG_SHOW_TOASTS) {
                        Toast.makeText (context, "antes: '" + antes + "', despues: '" + line + "'", Toast.LENGTH_SHORT).show ();
                    }

                    mBufferIndex = 0;
                    if(line.length() > 0 ) {
                        MediaUtils.playScannerBeep(context);
                        mScannerListener.onInputLineMatch(line);
                        
//                        Reiniciamos el buffer
                        mInputBuffer = new char[BUFFER_LIMIT];
                    }
                }
            }/* else {
                if (keycode != mKeycodeDelim) {
                    if (mBufferIndex == BUFFER_LIMIT) {
                        Log.e ("BrioBaseActivity", "dispatchKeyEvent(): ERROR, el buffer ha sido sobrepasado, se ignorarán los demás caracteres entrantes. El límite es " + BUFFER_LIMIT);
                        mBufferIndex = BUFFER_LIMIT - 1;
                    } else {
                        mInputBuffer[mBufferIndex] = character;
                        mBufferIndex++;
            }
        }
            }*/
        }

        return mListenInput;
    }

    /*
    Metodo encargado de reiniciar el bufer que contiene los caracteres escaneados
     */
    public static void resetBufferScanner(){
    
    }
    
    /**
     * Activa el listener de la pistola
     * @param ilistener listener
     * @param keycodeDelim
     */
    public void startScannerListening(ScannerListener ilistener, int keycodeDelim) {
        mKeycodeDelim = keycodeDelim;
        mScannerListener = ilistener;

        mListenInput = true;

    }

    /**
     * Desactiva el listener de la pistola
     * @param ilistener listener
     */
    public void stopScannerListening(ScannerListener ilistener) {
        if(mScannerListener != null && mScannerListener == ilistener) {
            //todo: solo detener cuando el listener es el activo, i.e. si alguien más toma el control antes del stop, cuando el primero hace el stop, ya no se detiene para el segundo
            mListenInput = false;
        } else {
        }
    }

    /**
     * Listener para indicar el codigo de barras leído
     */
    public interface ScannerListener {
        /**
         * @param codBarras - El codigo de barras leído
         */
        void onInputLineMatch (String codBarras);
    }
}
