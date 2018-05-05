package net.hova_it.barared.brio.apis.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.hw.barcodescanner.ScannerManager;
import net.hova_it.barared.brio.apis.utils.AnimationUtils2;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoManager2;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoOnClickListener;


import org.slf4j.helpers.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * View para realizar búsquedas. Consta de un BrioTextView y un botón
 * para simular el comportamiento del SearchView nativo de android,
 * pero utilizando el teclado personalizado de la aplicación.
 *
 * Created by Herman on 23/12/2015.
 */
public class BrioSearchView extends RelativeLayout implements BrioView, ScannerManager.ScannerListener {
    
    
    private Holder holder;
    private Context context;
    private TextButtonViewListener mlistener;
    private BrioBaseActivity mainActivity;
    private static final int[] ICONS_RES = { android.R.drawable.ic_menu_search, android.R.drawable.ic_delete };
    
    private List<OnQueryTextListener> onQueryTextListenerList;
    private CharSequence query;
    
    private TecladoOnClickListener tecladoOnClickListener;
    
    private ScannerManager managerScanner;
    
    //attrs @see res/values/attrs.xml/declare-styleable/BrioSearchView
    private int attr_id_layout_ocultable_por_teclado = - 1;
    private boolean attr_mostrar_como_dialogo = false;
    private String attr_hint = null;
    private TecladoManager2.TIPO_TECLADO attr_tipo_teclado = TecladoManager2.TIPO_TECLADO.ALFANUMERICO;
    private boolean isShowkeyboard = false;
    private boolean attr_nativo = false;
    
    public BrioSearchView (Context context) {
        super (context);
        
        init (context, null);
    }
    
    public BrioSearchView (Context context, AttributeSet attrs) {
        super (context, attrs);
        
        init (context, attrs);
    }
    
    public BrioSearchView (Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        
        init (context, attrs);
    }
    
    public BrioSearchView (Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super (context, attrs, defStyleAttr);
        
        init (context, attrs);
    }
    
    public void setTextButtonViewListener (TextButtonViewListener listener) {
        if (listener != null) {
            mlistener = listener;
        }
    }
    
    public void setText (String text) {
        if (text != null && text.length () > 0) {
            holder.showDeleteButton = true;
            holder.tvText.setText (text);
            holder.ivBtn.setImageResource (ICONS_RES[1]);
        }
    }
    
    private void init (Context context, AttributeSet attrs) {
        this.context = context;
        if (! isInEditMode ()) {
            this.mainActivity = (BrioBaseActivity) context; /*Si no esta siendo rendereado por android studio http://stackoverflow.com/questions/19085336/how-to-fix-java-lang-classcastexception-com-android-layoutlib-bridge-android-b*/
        }
        
        LayoutInflater.from (this.context).inflate (R.layout.view_briosearchview, this);
        
        onQueryTextListenerList = new ArrayList<> ();
        holder = new Holder (this);
        managerScanner = ScannerManager.getInstance (context);
        
        if (attrs != null) {
            recoverCustomAttrs (attrs);
            holder.config ();
        }
        
        
    }
    
    private void recoverCustomAttrs (AttributeSet attrs) {
        //Obtener los atributos custom desde el xml
        TypedArray ta = context.getTheme ().obtainStyledAttributes (attrs, R.styleable.BrioSearchView, 0, 0);
        try {
            attr_id_layout_ocultable_por_teclado = ta.getResourceId (R.styleable.BrioSearchView_id_layout_ocultable_por_teclado, - 1);
            attr_hint = ta.getString (R.styleable.BrioSearchView_hint);
            attr_mostrar_como_dialogo = ta.getBoolean (R.styleable.BrioSearchView_mostrar_como_dialogo, false);
            attr_nativo = ta.getBoolean (R.styleable.BrioSearchView_nativosearch, false);
            
            int attr_tmp = ta.getInt (R.styleable.BrioEditText_tipo_teclado, 0);
        /*    switch (attr_tmp) {
                case 0:
                    attr_tipo_teclado = TecladoManager2.TIPO_TECLADO.ALFANUMERICO;
                    break;
                
                case 1:
                    attr_tipo_teclado = TecladoManager2.TIPO_TECLADO.NUMERICO;
                    break;
                
                case 2:
                    attr_tipo_teclado = TecladoManager2.TIPO_TECLADO.FECHA;
                    break;
                
                case 3:
                    attr_tipo_teclado = TecladoManager2.TIPO_TECLADO.NUMERICO_ENTERO;
                    break;
                
                default:
                    attr_tipo_teclado = TecladoManager2.TIPO_TECLADO.ALFANUMERICO;
                    break;
            }*/
        } finally {
            ta.recycle ();
        }
    }
    
    public CharSequence getQuery () {
        return query;
    }
    
    /**
     * Fijar el listener para usar el BrioSearchView para filtrar en una lista
     * @param onQueryTextListener
     */
    public void addOnQueryTextListener (OnQueryTextListener onQueryTextListener) {
        onQueryTextListenerList.add (onQueryTextListener);
    }
    
    @Override
    public EditText getTextView () {return holder.tvText;}
    
    @Override
    public TecladoOnClickListener getTecladoOnClickListener () {return tecladoOnClickListener;}
    
    @Override
    public int getIdLayoutOcultable () {
        return attr_id_layout_ocultable_por_teclado;
    }
    
    public void enableBarcodeScanner () {
        managerScanner.startScannerListening (BrioSearchView.this, ScannerManager.KEYCODE_DELIM_ENTER);
    }
    
    public void disableBarcodeScanner () {
        managerScanner.stopScannerListening (BrioSearchView.this);
    }
    
    public void cleanText () {
        holder.tvText.setText ("");
    }
    
    @Override
    public void onInputLineMatch (String codBarras) {
        setText (codBarras);
    }
    
    public void setTecladoOnClickListener (TecladoOnClickListener listener) {this.tecladoOnClickListener = listener;}
    
    
    public class Holder implements View.OnClickListener {
        
        //public TextView tvText;
        /*se cambio desde el layout el TextView por un EditText*/
        public EditText tvText;
        public ImageView ivBtn;
        public boolean showDeleteButton = false;
        
        
        public Holder (View root) {
            //tvText = (TextView) root.findViewById(R.id.view_briosearch_text);
            /*se cambio desde el layout el TextView por un EditText*/
            tvText = (EditText) root.findViewById (R.id.view_briosearch_text);
            //Linea para establcer el focus en el view_briosearch_text
            // tvText.requestFocus ();
            tvText.addTextChangedListener (new TextWatcher () {
                
                @Override
                public void beforeTextChanged (CharSequence s, int start, int count, int after) {
                
                }
                
                @Override
                public void onTextChanged (CharSequence s, int start, int before, int count) {
                    //                    Si el teclado está oculto, y el scanner escribe en el edit text, reiniciarlo
                    
                    if (! isInEditMode () && onQueryTextListenerList.size () > 0) {
                        for (OnQueryTextListener listener : onQueryTextListenerList) {
                            if (listener != null) {
                                listener.onQueryTextChange (s.toString ());
                            }
                        }
                    }
                    query = s;
                }
                
                @Override
                public void afterTextChanged (Editable s) {
                
                }
                
            });
            tvText.setOnClickListener (this);
            ivBtn = (ImageView) root.findViewById (R.id.view_briosearch_btn);
            ivBtn.setOnClickListener (this);
            tvText.setOnEditorActionListener (
                    new EditText.OnEditorActionListener () {
                        @Override
                        public boolean onEditorAction (TextView v, int actionId, KeyEvent event) {
                            // Identifier of the action. This will be either the identifier you supplied,
                            // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                            if (actionId == EditorInfo.IME_ACTION_SEARCH
                                    || actionId == EditorInfo.IME_ACTION_DONE
                                    || event.getAction () == KeyEvent.ACTION_DOWN
                                    && event.getKeyCode () == KeyEvent.KEYCODE_ENTER) {
                                //                                onSearchAction(v);
                                tvText.setFocusable (false);
                                InputMethodManager imm = (InputMethodManager) context.getSystemService (Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow (tvText.getWindowToken (), 0);
                                isShowkeyboard = false;
                            }
                            // Return true if you have consumed the action, else false.
                            return false;
                        }
                    });
            
            tvText.setOnFocusChangeListener (new OnFocusChangeListener () {
                @Override
                public void onFocusChange (View v, boolean hasFocus) {
                    if (hasFocus) {
                    /*    if (attr_nativo) {
                            
                            tvText.setInputType (InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            mainActivity.managerTeclado.closeKeyboard ();
                            
                        }*/
                        tvText.performClick ();
                        
                        
                    }
                }
            });
            
            reset ();
            
            
        }
        
        
        @Override
        public void onClick (View v) {
            if (Utils.shouldPerformClick ()) {
                
                switch (v.getId ()) {
                    
                    case R.id.view_briosearch_btn:
                        tvText.setFocusable (true);
                        tvText.setFocusableInTouchMode (true);
                        tvText.requestFocus ();
                        AnimationUtils2.animateViewPush (ivBtn, 0.75f, 300);
                        //////////mau////////////
                        tvText.setText ("");
                        tvText.setBackgroundResource (R.drawable.ap_bkg_pink);
                        ////////////////////////
                        if (! showDeleteButton) {
                            /*se comento la linea que apertura el teclado de la aplicacion y se agrego esta funcion
                            para aperturara el teclado nativo de android*/
                            ((InputMethodManager) context.getSystemService (Context.INPUT_METHOD_SERVICE))
                                    .toggleSoftInput (InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            //mainActivity.managerTeclado.openKeyboard(attr_mostrar_como_dialogo, attr_tipo_teclado, BrioSearchView.this, null, null);
                            if (mlistener != null) {
                                mlistener.onButtonPressed (tvText.getText ().toString ());
                                
                            }
                        } else {
                            AnimationUtils2.animateViewClear (tvText, 10, 300);
                            ivBtn.setImageResource (ICONS_RES[0]);
                            reset ();
                            if (mlistener != null) {
                                mlistener.onCancelPressed ();
                            }
                        }
                        break;
                    
                    //////mau/////////
                    case R.id.view_briosearch_text:
                        isShowkeyboard = true;
                        tvText.setFocusable (true);
                        tvText.setFocusableInTouchMode (true);
                        tvText.requestFocus ();
                        AnimationUtils2.animateViewPush (ivBtn, 0.75f, 300);
                        InputMethodManager imm = (InputMethodManager) context.getSystemService (Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput (tvText, InputMethodManager.SHOW_IMPLICIT);
                        //////////mau////////////
                        tvText.setText ("");
                        tvText.setBackgroundResource (R.drawable.ap_bkg_pink);
                        
                        ////////////////////////
                        if (! showDeleteButton) {
                            
                            if (mlistener != null) {
                                mlistener.onButtonPressed (tvText.getText ().toString ());
                                
                            }
                        } else {
                            //AnimationUtils2.animateViewClear(tvText, 10, 300);
                            ivBtn.setImageResource (ICONS_RES[0]);
                            reset ();
                            if (mlistener != null) {
                                mlistener.onCancelPressed ();
                            }
                        }
                        break;
                    //////////mau///////////////
                }
            }
        }
        
        public void config () {
            tvText.setHint (attr_hint);
        }
        
        public void reset () {
            showDeleteButton = false;
            tvText.setText ("");
            ivBtn.setImageResource (ICONS_RES[0]);
        }
    }
    
    public interface TextButtonViewListener {
        void onCancelPressed ();
        
        void onButtonPressed (String text);
        
    }
    
    public interface OnQueryTextListener {
        void onQueryTextChange (String query);
    }
}