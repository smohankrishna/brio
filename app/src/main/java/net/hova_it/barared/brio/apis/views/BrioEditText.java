package net.hova_it.barared.brio.apis.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoManager2;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoOnClickListener;
import net.hova_it.barared.brio.apis.utils.DebugLog;

/**
 * EditText personalizado.
 * Esta clase se comunica con el TecladoManager2 para abrir el teclado
 * personalizado de forma automática.
 *
 * @see TecladoManager2
 *
 * Created by Herman on 19/01/2016.
 */
public class BrioEditText extends EditText
        implements View.OnClickListener, View.OnFocusChangeListener, BrioView {

    private final static String KEY_LOG = BrioEditText.class.getSimpleName();

    private Context context;
    private BrioBaseActivity mainActivity;
    private String TAG = "Brio_EditText";
    //attrs @see res/values/attrs.xml/declare-styleable/BrioEditText
    private int attr_id_layout_ocultable_por_teclado = -1;
    private boolean attr_cerrar_teclado_en_enter = false;
    private boolean attr_mostrar_como_dialogo = false;
    private String attr_titulo_teclado = null;
    private String attr_valor_default_teclado = null;
    private TecladoManager2.TIPO_TECLADO attr_tipo_teclado = TecladoManager2.TIPO_TECLADO.ALFANUMERICO;


    private OnFocusChangeListener onFocusChangeListener;

    //pruebas
    private TecladoOnClickListener tecladoOnClickListener;

    public BrioEditText(Context context) {
        super(context);
        init(context);
    }

    public BrioEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        recoverCustomAttrs(attrs);
    }

    public BrioEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
        recoverCustomAttrs(attrs);
    }

    public BrioEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        init(context);
        recoverCustomAttrs(attrs);
    }

    /**
     * Para que el onclick funcione, se debe fijar un id único
     * @param this_EditText
     */
    @Override
    public void onClick(View this_EditText) {
        if(Utils.shouldPerformClick()) { performOnClick(this_EditText); }
    }

    @Override
    public void onFocusChange(View this_EditText, boolean hasFocus) {
        if (hasFocus) {
            this.setBackgroundResource(R.drawable.ap_bkg_pink);
            performOnClick(this_EditText);
        } else {
            this.setBackgroundResource(R.drawable.ap_bkg_blue);
            if(attr_cerrar_teclado_en_enter) {
                mainActivity.managerTeclado.closeKeyboard();
            }
        }
        //TODOse regreso a la version anterior porq afecto a todos
        if(onFocusChangeListener != null) { onFocusChangeListener.onFocusChange(this_EditText, hasFocus); }
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    private void performOnClick(View this_EditText) {
        //recoverCustomAttrs
        mainActivity.managerTeclado.openKeyboard(attr_mostrar_como_dialogo, attr_tipo_teclado, this, attr_titulo_teclado, attr_valor_default_teclado);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
    }

    /**
     * Inicializa el view, deshabilita el teclado nativo para usar el propio, est lo hace con el
     * focus y con el click listener.
     *
     * Para que funcione el onClick listener, este view debe tener un id
     *
     * @param context
     */
    private void init(Context context) {
        this.context = context;
        if(!isInEditMode()) { this.mainActivity = (BrioBaseActivity) context; /*Si no esta siendo rendereado por android studio http://stackoverflow.com/questions/19085336/how-to-fix-java-lang-classcastexception-com-android-layoutlib-bridge-android-b*/}

        this.setInputType(InputType.TYPE_NULL);
        if(getId() != NO_ID) {
            super.setOnFocusChangeListener(this);
            super.setOnClickListener(this);

            this.setBackgroundResource(R.drawable.view_editable_inactivo);
        } else {
           //"Se debe fijar un id a este " + KEY_LOG + ". No funcionara el evento onClick ");
        }
    }

    private void recoverCustomAttrs(AttributeSet attrs) {
        //Obtener los atributos custom desde el xml
        TypedArray ta = context.getTheme().obtainStyledAttributes( attrs, R.styleable.BrioEditText, 0, 0 );
        try {
            attr_id_layout_ocultable_por_teclado = ta.getResourceId(R.styleable.BrioEditText_id_layout_ocultable_por_teclado, -1);
            attr_cerrar_teclado_en_enter = ta.getBoolean(R.styleable.BrioEditText_cerrar_teclado_en_enter, false);
            attr_mostrar_como_dialogo = ta.getBoolean(R.styleable.BrioEditText_mostrar_como_dialogo, false);
            attr_titulo_teclado = ta.getString(R.styleable.BrioEditText_titulo_teclado);
            attr_valor_default_teclado = ta.getString(R.styleable.BrioEditText_valor_default_teclado);

            int attr_tmp = ta.getInt(R.styleable.BrioEditText_tipo_teclado, 0);
            switch (attr_tmp) {
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

                case 4:
                    attr_tipo_teclado = TecladoManager2.TIPO_TECLADO.ALFANUMERICO_PASS;
                    this.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    break;

                case 5:
                    attr_tipo_teclado = TecladoManager2.TIPO_TECLADO.NUMERICO_ENTERO_CP;
                    break;
            }

        } finally {
            ta.recycle();
        }
    }

    public void setTecladoOnClickListener(TecladoOnClickListener listener){
        this.tecladoOnClickListener = listener;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {

        setError(null);

        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    public EditText getTextView() {
        return this;
    }

    @Override
    public TecladoOnClickListener getTecladoOnClickListener() {
        return tecladoOnClickListener;
    }

    @Override
    public int getIdLayoutOcultable() {
        return attr_id_layout_ocultable_por_teclado;
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        setCompoundDrawables(null, null, icon, null);
    }
}