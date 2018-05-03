package net.hova_it.barared.brio.apis.pos.search;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.apis.hw.barcodescanner.ScannerManager;
import net.hova_it.barared.brio.apis.interfaces.OnFragmentListener;
import net.hova_it.barared.brio.apis.pos.POSManager;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.views.BrioSearchView;

/**
 * Clase encargada de la configuracion e inicializacion de busquedas de productos.
 * Created by HermanPeralta on 18/01/2016.
 * Updated on: 03/03/2016
 */
public class SearchManager implements SearchFragment.FragmentVisibleListener, OnFragmentListener {
    private static final String KEY_LOG = SearchManager.class.getSimpleName();
    
    private static SearchManager daManager;
    
    private Context context;
    
    private SearchFragment fgmtResults;
    private POSManager menuPOS;
    private BrioSearchView posSearch;
    private boolean resultsOpened =false;
    private POSManager posManager;
    private OnFragmentListener fragmentListener;
    
    
    private FragmentListButtonListener searchAddItemListener = new FragmentListButtonListener() {
        @Override
        public void onListButtonClicked(View btn, Object value) {
            DebugLog.log(SearchManager.this.getClass(), "Search", "Envio articulo a POSManager");
            
            String codigoBarras = ((String[]) value)[2];
            
            //regreso el control del scanner al modulo de pos
            posSearch.disableBarcodeScanner();
            
            ((BrioActivityMain) context).managerScanner.startScannerListening(menuPOS, ScannerManager.KEYCODE_DELIM_ENTER);
            
            ((BrioActivityMain) context).managerTeclado.closeKeyboard();
            fgmtResults.remove();
            
            menuPOS.onInputLineMatch(codigoBarras);
        }
    };
    
    // Función agregada por: Manuel Delgadillo - 21/FEB/2017
    public boolean esVisible() {
        if (fgmtResults != null) {
            return fgmtResults.isAdded();
        }
        return false;
    }
    /**
     * Obtinen instancia del manager (singleton).
     * @param context
     * @return
     */
    public static SearchManager getInstance(Context context) {
        if (daManager == null) {
            daManager = new SearchManager(context);
        }
        
        return daManager;
    }
    
    private SearchManager(final Context context) {
        this.context = context;
        
        fgmtResults = SearchFragment.newInstance();
        fgmtResults.setFragmentListButtonListener(searchAddItemListener);
        fgmtResults.setFragmentVisibleListener(this);
        fgmtResults.setRemoveListener(new SearchFragment.RemoveListener() {
            @Override
            public void onRemove() {
                //regreso el control del scanner al modulo de pos
                posSearch.disableBarcodeScanner();
                ((BrioActivityMain) SearchManager.this.context).managerScanner.startScannerListening(menuPOS, ScannerManager.KEYCODE_DELIM_ENTER);
               
                resultsOpened = false;
                resetSearch ();
               
            }
        });
        
        menuPOS = ((BrioActivityMain) context).menuPOS;
    }
    
    private void resetSearch(){
        posSearch.getTextView().setText("");
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(posSearch.getWindowToken(), 0);
        }
        disabledView (posSearch, false);
    }
    
    /**
     * Inflado y configuracion de fragment de busqueda.
     *
     * @param resIdBrioSearchView
     * @param resIdLayout
     */
    public void configure(int resIdBrioSearchView, final int resIdLayout) {
        posSearch = (BrioSearchView) ((BrioBaseActivity) context).findViewById(resIdBrioSearchView);

        if(fragmentListener!=null){
            fragmentListener=this;
        }

        disabledView (posSearch, false);
    
        if (posSearch != null) {
            posSearch.addOnQueryTextListener(fgmtResults);
        }

        posSearch.setTextButtonViewListener(new BrioSearchView.TextButtonViewListener() {
            @Override
            public void onCancelPressed() {
            
            }
            
            @Override
            public void onButtonPressed(String text) {
                if (! resultsOpened) {
                    resultsOpened = true;
                    
                    posSearch.enableBarcodeScanner();
                    fgmtResults.show(((BrioBaseActivity) context).getSupportFragmentManager(), resIdLayout,fragmentListener);
                }

            }
        });
    }

    public void disabledView (BrioSearchView editText, boolean enable) {
        editText.setEnabled (enable);
        editText.setFocusable (enable);
        editText.setFocusableInTouchMode(enable);
        if(!enable){
            editText.clearFocus ();
        }
    }
    
    @Override
    public void onFragmentVisible() {
        // INI: Agregado por: Manuel Delgadillo - 23/FEB/2017 -->
        resultsOpened = true;   // Se agrego esta linea para notificar que la busqueda esta abierta
        // FIN: Agregado por: Manuel Delgadillo - 23/FEB/2017 -->
    }
    
    // INI: Agregado por: Manuel Delgadillo - 22/FEB/2017 -->
    public void Cerrar() {
        resultsOpened = false;
        this.fgmtResults.remove();
        resetSearch();
    }
    // FIN: Agregado por: Manuel Delgadillo - 22/FEB/2017 -->

    public void setPOSManager(POSManager posManager) {
        this.posManager = posManager;
    }
    
    @Override
    public void onFragment (int requestCode, String msj, Bundle params) {
    
        resetSearch();
        
    }
}
