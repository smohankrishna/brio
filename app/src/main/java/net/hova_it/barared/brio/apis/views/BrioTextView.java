package net.hova_it.barared.brio.apis.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoManager2;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoOnClickListener;

/**
 * TextView personalizado para la aplicación.
 *
 * Permite gestionar el teclado personalizado para escribir sobre el elemento.
 *
 * Created by Herman on 19/01/2016.
 */
public class BrioTextView extends TextView
        implements BrioView, View.OnClickListener {
    private final static String KEY_LOG = BrioTextView.class.getSimpleName();

    private Context context;
    private BrioBaseActivity mainActivity;

    // attrs @see res/values/attrs.xml/declare-styleable/BrioTextView
    private int attr_id_layout_ocultable_por_teclado = -1;
    private boolean attr_mostrar_como_dialogo;
    private TecladoManager2.TIPO_TECLADO attr_tipo_teclado = TecladoManager2.TIPO_TECLADO.ALFANUMERICO;

    public BrioTextView(Context context) {
        super(context);

        init(context);
    }

    public BrioTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
        recoverCustomAttrs(attrs);
    }

    public BrioTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public BrioTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs);
    }

    @Override
    public void onClick(View this_TextView) {
        //mainActivity.managerTeclado.openKeyboard(getId(), (View) this_TextView.getParent(), attr_id_layout_ocultable_por_teclado, attr_tipo_teclado);
        ///Todo: cambiar el fondo o hacer algo para resaltar
        if(Utils.shouldPerformClick()) {
            mainActivity.managerTeclado.openKeyboard(attr_mostrar_como_dialogo, attr_tipo_teclado, this, "", "");
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        Log.e(KEY_LOG, "No se puede fijar click listener en este componente");
    }

    private void init(Context context) {
        this.context = context;
        if(!isInEditMode()) { this.mainActivity = (BrioBaseActivity) context; /*Si no esta siendo rendereado por android studio http://stackoverflow.com/questions/19085336/how-to-fix-java-lang-classcastexception-com-android-layoutlib-bridge-android-b*/}

        if(getId() != NO_ID) {
            super.setOnClickListener(this);
        } else {
            Log.e(KEY_LOG, "Se debe fijar un id a este " + KEY_LOG + ". No funcionara el evento onClick ");
        }
    }

    /**
     * Esta funcion se encuentra duplicada en
     * BrioTextView
     * BrioEditText
     * @param attrs
     */
    private void recoverCustomAttrs(AttributeSet attrs) {
        //Obtener los atributos custom desde el xml
        TypedArray ta = context.getTheme().obtainStyledAttributes( attrs, R.styleable.BrioTextView, 0, 0 );
        try {
            attr_id_layout_ocultable_por_teclado = ta.getResourceId(R.styleable.BrioTextView_id_layout_ocultable_por_teclado, -1);
            attr_mostrar_como_dialogo = ta.getBoolean(R.styleable.BrioTextView_mostrar_como_dialogo, false);
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
            }
        } finally {
            ta.recycle();
        }
    }

    @Override
    public TextView getTextView() {
        return this;
    }

    @Override
    public TecladoOnClickListener getTecladoOnClickListener() {
        return null;
    }

    @Override
    public int getIdLayoutOcultable() {
        return attr_id_layout_ocultable_por_teclado;
    }
}
