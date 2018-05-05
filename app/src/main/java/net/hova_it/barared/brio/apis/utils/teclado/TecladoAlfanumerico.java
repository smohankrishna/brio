package net.hova_it.barared.brio.apis.utils.teclado;

import android.content.res.ColorStateList;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.views.BrioCloseButton;

/**
 * Fragment de teclado alfanumerico
 * Created by Mauricio Cerón on 23/02/2016.
 */
public class TecladoAlfanumerico extends TecladoBase {
    protected static int[] btnRes;

    //Views
    private RelativeLayout lay_tecladoLetras;
    private RelativeLayout lay_tecladoSignos;
    private boolean shift = false;
    private boolean isPass = false;

    private BrioCloseButton cancelar;
    private LinearLayout background;
    private TextView showTitulo;
    private String titulo = null;
    private String textDefault = null;

    static {
        btnRes = new int[] {
                R.id.btnQ, R.id.btnW, R.id.btnE, R.id.btnR, R.id.btnT, R.id.btnY, R.id.btnU, R.id.btnI, R.id.btnO, R.id.btnP,
                R.id.btnA, R.id.btnS, R.id.btnD, R.id.btnF, R.id.btnG, R.id.btnH, R.id.btnJ, R.id.btnK, R.id.btnL, R.id.btnEN,
                R.id.btnZ, R.id.btnX, R.id.btnC, R.id.btnV, R.id.btnB, R.id.btnN, R.id.btnM,


                R.id.btnShift, R.id.btnBack, R.id.btnBack2,
                R.id.btnSignos, R.id.btnLetras, R.id.btnComma, R.id.btnSpace, R.id.btnSpace2, R.id.btnDot,

                R.id.btnEnter, R.id.btnEnter2,

                R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn0,


                R.id.btn10, R.id.btn11, R.id.btn12,
                R.id.btn13, R.id.btn14, R.id.btn15, R.id.btn16, R.id.btn17, R.id.btn18, R.id.btn19, R.id.btn20, R.id.btn21, R.id.btn22, R.id.btn23, R.id.btn24, R.id.btn25,
                R.id.btn26, R.id.btn27, R.id.btn28, R.id.btn29, R.id.btn30, R.id.btn31
        };
    }

    public TecladoAlfanumerico() {
        layout_res_id = RES_ID_ALFANUMERICO;
    }

    /**
     * Ingresar propiedad para enmascarar la informacion ingresada
     * @param pass
     */
    public void setPass(boolean pass){
        this.isPass = pass;
    }

    /**
     * Propiedad utilizada unicamente cuando el teclado se muestra como fragment dialog.
     * Ingresar encabezado al teclado
     * @param titulo
     */
    public void setTitulo(String titulo){
        this.titulo = titulo;
        Log.w("teclado alpha","titulo en setTitulo "+this.titulo );
    }

    /**
     * Propiedad utilizada para ingresar texto predefinido a un campo de texto que tenga el foco
     * del teclado
     * @param textDefault
     */
    public void setTextDefault(String textDefault){
        this.textDefault = textDefault;
        Log.w("teclado numerico","titulo en setTextDefault "+this.titulo );
    }

    /**
     * Infla los componentes del fragment (Teclas y TextView)
     */
    @Override
    public void configureUI() {
        lay_tecladoLetras = (RelativeLayout) rootView.findViewById(R.id.lay_tecladoLetras);
        lay_tecladoSignos = (RelativeLayout) rootView.findViewById(R.id.lay_tecladoNumeros);
        textAndButton = (RelativeLayout) rootView.findViewById(R.id.textToShow);
        textViewInterior = (TextView) rootView.findViewById(R.id.textoEscrito);
        /*if(isPass){
            textViewInterior.setVisibility(View.GONE);
            Log.w("isPass","oculto textViewInterior");
        }else{
            textViewInterior.setVisibility(View.VISIBLE);
            Log.w("isPass", "muestro textViewInterior");
        }*/
    }

    /**
     * Mapeo del texto mostrado en la tecla con el valor que ingresa la tecla
     */
    @Override
    public void configureKeys() {
        background = (LinearLayout) rootView.findViewById(R.id.background);
        showTitulo = (TextView) rootView.findViewById(R.id.textTitle);

        btns = new TextView[btnRes.length];
        for (int i = 0; i < btns.length; i++) {
            btns[i] = (TextView) rootView.findViewById(btnRes[i]);
            btns[i].setOnClickListener(this);
            btns[i].setWidth(60);
            btns[i].setTag(btns[i].getText().toString());
            if(btnRes[i]==R.id.btnBack || btnRes[i]==R.id.btnBack2){
                btns[i].setOnTouchListener(this);
            }
        }
        cancelar = (BrioCloseButton) rootView.findViewById(R.id.btnClose);
        cancelar.setOnClickListener(this);

        Log.w("teclado numerico", "configureKeys titulo antes del if " + this.titulo);
        if(titulo == null){
            background.setBackgroundResource(android.R.color.transparent);
            showTitulo.setText("");
            Log.w("teclado numerico", "configureKeys titulo null " + this.titulo +" "+ titulo);
        }else {
            showTitulo.setText(titulo);
            Log.w("teclado numerico", "titulo "+titulo);
        }

    }

    /**
     * Limpieza de texto
     */
    @Override
    public void resetKeyboard() {
        textToShow="";
//        textViewInterior.setText("");
    }

    /**
     * Manejo de listeners de teclas
     * @param btn
     */

    @Override
    public void onClick(View btn) {

        textToShow = textViewExterior!=null? textViewExterior.getText().toString() : textToShow;
        textViewInterior.setText(textToShow);

        switch (btn.getId()) {
            case R.id.btnShift:
                shift = !shift;
                if (shift) {
                    btn.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.shiftOn)));
                } else {
                    btn.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.shiftOff)));
                }

                shift();
                Log.w("Shift state:", " " + shift);

                break;
            case R.id.btnSignos:
                lay_tecladoLetras.setVisibility(View.GONE);
                lay_tecladoSignos.setVisibility(View.VISIBLE);
                break;
            case R.id.btnLetras:
                lay_tecladoSignos.setVisibility(View.GONE);
                lay_tecladoLetras.setVisibility(View.VISIBLE);
                break;
            case R.id.btnEnter:
            case R.id.btnEnter2:
                onEnterKeyPress();
                break;

            case R.id.btnClose:
                if (tecladoOnClickListener != null) {
                    tecladoOnClickListener.onCancelResult();
                }
                ((BrioActivityMain) getActivity()).managerTeclado.closeKeyboard();
                break;

            default:
                onCharKeyPress(btn);
                break;
        }
    }

    private void shift() {

        if (!shift) {
            for (int i = 0; i < 27; i++) {
                btns[i].setText(btns[i].getText().toString().toLowerCase());
                btns[i].setTag(btns[i].getText().toString().toLowerCase());
            }
        } else {
            for (int i = 0; i < 27; i++) {
                btns[i].setText(btns[i].getText().toString().toUpperCase());
                btns[i].setTag(btns[i].getText().toString().toUpperCase());
            }
        }

    }
}
