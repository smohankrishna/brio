package net.hova_it.barared.brio.apis.utils.teclado;

import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.views.BrioCloseButton;

/**
 * Fragment de teclado numerico
 * Created by Mauricio Cerón on 23/02/2016.
 */
public class TecladoNumerico extends TecladoBase {

    protected static int[] btnRes;
    private double valor;
    private boolean isdecimal = false;
    private boolean isEntero = false;
    private boolean isPass = false;
    private boolean isCp = false;
    private int decimales = 0;
    static final double CERO = 0;
    static final String CERO_FLOTANTE = String.format("%.2f", CERO);
    private String CERO_STRING= "0.00";
    private int limiteNumeros;
    private double cantidad = 0;
    boolean up = false;
    private LinearLayout background;
    private TextView showTitulo;
    private BrioCloseButton cancelar;
    private Handler handler = new Handler();
    private String titulo = null;
    private String textDefault = null;

    Runnable runnable;

    int btnBackPosicion;

    static {
        btnRes = new int[]{R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btn00 /*10*/,
                R.id.btnDot, R.id.btnBack, R.id.btnClr, R.id.btnIntro};
    }

    public TecladoNumerico() {
        layout_res_id = RES_ID_NUMERICO;
    }

    /**
     * Propiedad para seleccionar eltipo de teclado a mostrar.
     * @param entero true = Numerico_Entero | false = Numerico
     */
    public void setEntero(boolean entero){
        try {//quitar
            this.isEntero = entero;
            if (isEntero) {
                limiteNumeros = 7;
            }
        }catch (Exception e){
            String traza = Log.getStackTraceString(e);
            e.printStackTrace();
        }

    }

    /**
     * Propiedad para seleccionar teclado especial para ingresar exclusivamente codigo postal
     * @param entero
     */
    public void setIsCP(boolean entero){
        try {//quitar
            this.isCp = entero;
            if (isCp) {
                limiteNumeros = 5;
            } else {
                limiteNumeros = 7;
            }
            Log.w("numerico", "setIsCP" + isCp);
        }catch (Exception e){
            e.printStackTrace();
            String traza = Log.getStackTraceString(e);
        }
    }

    /**
     * Asignar encabezado al teclado.
     * Nota: solo se mostrara el encabezado si el teclado se muestra como fragment dialog
     * @param titulo
     */
    public void setTitulo(String titulo){
        try{//quitar
            this.titulo = titulo;
            Log.w("teclado numerico","titulo en setTitulo "+this.titulo );
        }catch (Exception e){
            e.printStackTrace();
            String traza = Log.getStackTraceString(e);

        }
    }

    /**
     * Propiedad para asignar un texto default que mostrara el teclado al desplegarse
     * @param textDefault
     */
    public void setTextDefault(String textDefault){
        try{//quitar

            this.textDefault = textDefault;
            Log.w("teclado numerico","titulo en setTextDefault "+this.textDefault );
        }catch (Exception e){
            e.printStackTrace();
            String traza = Log.getStackTraceString(e);

        }
    }
    /**
     * Infla los componentes del fragment (Teclas y TextView). Configura el tipo de "cero" que se utilizara.
     * 0.00 para Numerico y 0 para Numerico_Entero.
     */
    @Override
    public void configureUI() {
        textViewInterior = (TextView) rootView.findViewById(R.id.tvDetallesPago);
        textAndButton = (RelativeLayout) rootView.findViewById(R.id.numbreToShow);
        textToShow = textToShow.replace(",",".");

        try {
            Log.w("configureUI","try textToShow ="+textToShow);
            //if(textDefault != null) {
            //textToShow = textDefault;
            cantidad = Double.parseDouble(textToShow);
            Log.w("configureUI","try cantidad ="+cantidad);
            if(cantidad == 0){
                Log.w("configureUI","if");
                textToShow = "0.00";
            }else{
                Log.w("configureUI","else ="+textToShow);
            }
            //}

        } catch (NumberFormatException e) {
            cantidad = 0;
            if(!isCp) {
                textToShow = "" + CERO_STRING;//cantidad;
            }
            textViewInterior.setText(textToShow);
            Log.w("TECLADO NUMERICO","catch "+textToShow);
            if (textViewExterior != null && textDefault== null)//TODO:experimento
            {
                textViewExterior.setText(textToShow);
            }

        }

        try{//quitar
            if(textDefault != null) {
                resetValues();
                Log.w("textDefault", "textDefault no es null= " + textDefault);
                textViewInterior.setText(this.textDefault);
                textToShow = textViewInterior.getText().toString();
                //textViewExterior.setText(this.textDefault);//FIXME: experimento
                Log.w("textToShow", "textDefault no es null, seteando textToShow " + textToShow);
            }

            if(!isEntero) {
                validacion(textToShow);//Double.parseDouble(textToShow));
            }
        }catch (Exception e){
            String traza = Log.getStackTraceString(e);
            e.printStackTrace();
        }
    }

    /**
     * Mapeo del texto mostrado en la tecla con el valor que ingresa la tecla. Configuracion
     * especifica la animacion y funcionamiento del backspace para los teclados
     * Numerico, Numerico_Entero y Codigo_Postal.
     * Configuracion del limite maximo de numeros que se puede ingresar.
     */
    @Override
    public void configureKeys() {

        background = (LinearLayout) rootView.findViewById(R.id.background);
        showTitulo = (TextView) rootView.findViewById(R.id.textTitle);

        DebugLog.log(getClass(), "configureKeys", "textViewInterior es null :(?: " + (textViewInterior==null) );

        keyBackSpaceRunnable = new Runnable() {
            //@see http://www.mopri.de/2010/timertask-bad-do-it-the-android-way-use-a-handler/

            @Override
            public void run() {


                if(isEntero) {
                    if (textToShow.length() > 0) {
                        textToShow = textToShow.substring(0, textToShow.length() - 1);
                    }
                    if(!isCp) {
                        if (textToShow.equals("")) {
                            textToShow = "0";
                        }
                    }

                }else {
                    textToShow = deleteNumber(textToShow);
                }

                DebugLog.log(TecladoNumerico.class, "TEST", "textToShow: '" + textToShow + "'");
                textViewInterior.setText(textToShow);
                if (textViewExterior != null) {
                    Log.w("TECLADO NUMERICO","entreeeeeeee y no me importa nada ");
                    textViewExterior.setText(textToShow);
                }

                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);

                //endOfEditionText(strText);
                if(!keyBackSpaceUp) { keyBackSpaceHandler.postDelayed(this, BACKSPACE_DELETE); }
            }
        };


        btns = new TextView[btnRes.length];
        for (int i = 0; i < btns.length; i++) {
            btns[i] = (TextView) rootView.findViewById(btnRes[i]);
            btns[i].setOnClickListener(this);
            btns[i].setWidth(60);
            btns[i].setTag(btns[i].getText().toString());
            if(btnRes[i]==R.id.btnBack){
                btnBackPosicion=i;
                btns[i].setOnTouchListener(this);

            }
        }

        cancelar = (BrioCloseButton) rootView.findViewById(R.id.btnClose);
        cancelar.setOnClickListener(this);

        if(isEntero){
            btns[11].setVisibility(View.GONE);
            /*if(isCp){limiteNumeros = 5;}
            else{limiteNumeros = 7;}*/
            //limiteNumeros = 7;
            CERO_STRING = "0";
            Log.w("soy entero","soy entero");
        }else{
            btns[11].setVisibility(View.VISIBLE);
            limiteNumeros = 15;
            CERO_STRING = CERO_FLOTANTE;
        }

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
     * Limpieza de variables y reseteo de configuraciones del teclado.
     */
    @Override
    public void resetKeyboard() {
        try{//quitar
            resetValues();
        }catch (Exception e){
            e.printStackTrace();
            String traza = Log.getStackTraceString(e);

        }
    }


    /**
     * Configuracion de listener de teclas al ser tocadas.
     * @param btn
     */
    @Override
    public void onClick(View btn) {
        //try{//quitar
            if(!isCp) {


                    textToShow = textToShow.replace(",",".");

                try {
                    if (Double.parseDouble(textToShow) == 0) {
                        Log.w("textToShow!!!", "null");
                    }
                } catch (Exception e) {
                    //editor.setText(String.format("%.2f", CERO));
                    Log.w("textToShow catch!!!",CERO_STRING);
                    textToShow = CERO_STRING;//editor.getText().toString();
                }



                if (!textToShow.equals("0.") && !textToShow.equals("0.0")) {
                    //validacion(Double.parseDouble(currVal));
                    textToShow = textToShow.replace(",",".");
                    if (Double.parseDouble(textToShow) == 0) {//if (currVal.equals("0.00")) {
                        textToShow = "";
                    }
                }
            }
        /*if(!isEntero) {
            Log.w("onClick","teclado doble validando");
            validacion(cantidad);
        }*/
            Log.w("textToShow", "al teclear" + textToShow);
            Log.w("decimales","al teclear"+decimales);
            Log.w("decimales","al teclear"+isdecimal);
            switch (btn.getId()) {

                case R.id.btnIntro:
                    if (textToShow.equals(".") || textToShow.equals("")) {
                        Log.w("textToShow catch!!!",CERO_STRING);
                        if(!isCp) {
                            textToShow = CERO_STRING;//String.format("%.2f", CERO);
                        }
                    }

                    DebugLog.log(TecladoNumerico.class, "TEST", "antes de volar textToShow: '" + textToShow + "'");


                    //Double newcant = Double.parseDouble(textToShow);


                    //Log.e("TEST", "numero: '" + newcant + "'");
                    resetValues();
                    //this.setCantInicial(CERO);
                    /*if(!isEntero) {
                        textToShow = Double.toString(newcant);
                    }*/
                    if (tecladoOnClickListener != null) {
                        if(!isEntero) {
                            tecladoOnClickListener.onAcceptResult(Double.parseDouble(textToShow));
                        }else {
                            if(Double.parseDouble(textToShow)== 0){ textToShow="0";}
                            tecladoOnClickListener.onAcceptResult(Integer.parseInt(textToShow));
                        }
                    }

                    Log.w("ISDECIMAL: ", "" + isdecimal);
                /*if (is_dialog) {
                    getDialog().dismiss();
                } else {
                    ((BrioBaseActivity) getActivity()).managerTeclado.closeKeyboard();
                }*/
                    ((BrioBaseActivity) getActivity()).managerTeclado.closeKeyboard();
                    break;

                case R.id.btn00:

                    if(isEntero){
                        textToShow = dobleCeroEntero(textToShow,limiteNumeros);
                    }else{
                        textToShow = dobleCeroFlotante(textToShow,limiteNumeros);
                    }

                    break;

                case R.id.btnDot:
                    if (textToShow.length() < limiteNumeros) {
                        if (textToShow.equals("") || textToShow.equals(".")) {
                            textToShow = "0.";
                        }
                        Log.e("CURRVAL: ", textToShow);
                        if (Double.parseDouble(textToShow) == 0 ) {
                            textToShow = "0.";
                            isdecimal = true;
                            Log.e("TEST", "Punto: '" + textToShow + "'");
                        } else {
                            if (!isdecimal) {
                                isdecimal = true;
                                textToShow = textToShow + ".";
                            }
                        }
                    }
                    break;

                case R.id.btnClr:
                    resetValues();
                    //textToShow = String.format("%.2f", CERO);
                    Log.w("CLR",isCp+"");
                    if(!isCp) {
                        textToShow = CERO_STRING;
                    }else{
                        textToShow = "";
                    }

                    break;

                case R.id.btnClose:
                    resetValues();
                    if (tecladoOnClickListener != null) {

                        if (textToShow.equals("")) {
                            textToShow = "0";
                        }

                        if (!isEntero) {
                            tecladoOnClickListener.onCancelResult();
                        } else {
                            if (Double.parseDouble(textToShow) == 0) {
                                textToShow = "0";
                            }
                            tecladoOnClickListener.onCancelResult();
                        }
                    }
                    DebugLog.log(TecladoNumerico.this.getClass(), "TECLADO", "Mando cerrar");
                    ((BrioActivityMain) getActivity()).managerTeclado.closeKeyboard();

                    break;


                default:
                    if (textToShow.length() < limiteNumeros) {
                        if (!isdecimal) {
                            textToShow = textToShow + btn.getTag();
                        } else {
                            if (decimales < 2) {
                                textToShow = textToShow + btn.getTag();
                                decimales++;
                            }
                        }
                    }
                    break;

            }

            if(!isCp) {

                try {

                    if (Double.parseDouble(textToShow) == 0 && !isdecimal) {
                        Log.w("textToShow try teclear", CERO_STRING);
                        textToShow = CERO_STRING;//String.format("%.2f", CERO)
                    }

                } catch (Exception e) {
                    Log.w("textToShowcatch teclear", CERO_STRING);
                }

            }

            if(!isCp) {

                if (textToShow.equals("")) {
                    Log.w("textToShow == _!!!", CERO_STRING);
                    textToShow = CERO_STRING;//String.format("%.2f", CERO);
                }
            }

            if (textViewExterior != null && textDefault==null) {//TODO: experimento
                Log.w("teclado numerico","me vale entreee text default = null"+textDefault);
                textViewExterior.setText(textToShow);
            }

            Log.w("teclado numerico","textToShow = null"+textToShow);
            textViewInterior.setText(textToShow);
            Log.w("onClick", textToShow.length() + "");
            Log.w("variables", isdecimal + " " + decimales);
    }

    /**
     * control de numeros ingresados despues del punto decimal.
     * Nota: Esta configuracion solo funciona para el teclado Numerico
     */
    public void resetValues() {
        try{//quitar
            this.isdecimal = false;
            this.decimales = 0;
            //this.titulo = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Configuracion y validacion de la tecla "00".
     * Funciona para el teclado Numerico.
     * @param num
     * @param limiteNumeros
     * @return
     */
    private String dobleCeroFlotante(String num, int limiteNumeros){
        try{//quitar
            if (num.length() < limiteNumeros) {
                if (!isdecimal) {
                    Log.w("dobleCeroFlotante", "is decimal false");
                    if(num.length() < limiteNumeros-1){
                        Log.w("dobleCeroFlotante","if 00");
                        num = num + "00";
                    }else {
                        if (num.length() < limiteNumeros) {
                            Log.w("dobleCeroFlotante","if 0");
                            num = num + "0";
                        }
                    }

                } else {
                    Log.w("dobleCeroFlotante","is decimal true");
                    if (decimales == 0) {
                        Log.w("dobleCeroFlotante","if 00");
                        num = num + "00";
                        decimales += 2;
                    }
                    if (decimales == 1) {
                        Log.w("dobleCeroFlotante","if 0");
                        num = num + "0";
                        decimales++;
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            String traza = Log.getStackTraceString(e);
        }//quitar

        return  num;
    }

    /**
     * Configuracion y validacion de la tecla "00".
     * Funciona para el teclado Numerico_Entero.
     * @param num
     * @param limiteNumeros
     * @return
     */
    private String dobleCeroEntero(String num, int limiteNumeros){

        try{//quitar

            if(num.length() < limiteNumeros-1){
                num = num + "00";
            }else {
                if (num.length() < limiteNumeros) {
                    num = num + "0";
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            String traza = Log.getStackTraceString(e);
        }//quitar
        return num;
    }

    /**
     * Validaciones del manejo del punto decimal.
     * Nota: Esto funciona unicamente para el teclado Numerico.
     * @param cantInicial
     */
    private void validacion(String cantInicial) {
        int numDecimales = 0;
        this.cantidad = Double.parseDouble(cantInicial);
        Log.w("Cantidad inicial: ", cantidad + "cantInicial "+cantInicial);
        char[] cadena = cantInicial.toCharArray();//Double.toString(cantidad).toCharArray();
        if (cantidad > 0) {
            if (cadena.length > 0) {
                //isdecimal = true;
                for (int i = 0; i < cadena.length; i++) {
                    if (cadena[i] == '.') {
                        isdecimal = true;
                        numDecimales = (cadena.length - 1) - i;
                        break;
                    }
                }
                decimales = numDecimales;
            }
        }
    }

    /**
     * Validaciones de eliminacion de caracteres y manejo de variables de validacion el punto
     * decimal, sustictucion de "," por "." en caso de tener una configuracion le lenguaje erronea
     * en el dispositivo.
     * Validacion de numeros negativos.
     * @param num
     * @return
     */
    public String deleteNumber(String num){
        num = num.replace(",",".");
        char[] cadena = num.toCharArray();
        StringBuilder newCad = new StringBuilder();

        if(num.equals("-")) {
            num = String.format("%.2f", CERO);
            resetValues();
        }else {
            if (num.equals("") || Double.parseDouble(num) == 0) {
                num = String.format("%.2f", CERO);
                resetValues();
            } else {
                for (int i = 0; i < cadena.length; i++) {
                    Log.e("TEST", "caracter en la posicion " + i + ": '" + cadena[i] + "'");
                }
                Log.e("TEST", "caracter en la posicion " + cadena.length + ": '" + cadena[cadena.length - 1] + "'");
                if (isdecimal) {
                    decimales -= 1;
                }

                if (cadena[cadena.length - 1] == '.') {
                    isdecimal = false;
                    decimales = 0;
                }

                for (int i = 0; i < cadena.length - 1; i++) {
                    newCad.append(cadena[i]);
                }
                Log.e("TEST", "Cadena nueva: '" + newCad + "'");
                //TODO revisar
                if (num.equals("")) {
                    num = String.format("%.2f", CERO);
                }
                num = newCad.toString();
            }
        }
        if (num.equals("")) {
            num = String.format("%.2f", CERO);
        }
        DebugLog.log(this.getClass(), "TEST", "num es: '" + num + "'");
        return num;
    }
}