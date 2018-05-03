package lat.brio.api.dongle;

/*
 * Created by Delgadillo on 11/07/17.
 */

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

import com.bbpos.bbdevice.BBDeviceController;
import com.bbpos.bbdevice.BBDeviceController.CurrencyCharacter;
import com.bbpos.bbdevice.BBDeviceController.CheckCardMode;
import com.bbpos.bbdevice.BBDeviceController.TransactionType;

import com.bbpos.bbdevice.CAPK;

import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import lat.brio.api.dialogo.PagoDongle;
import lat.brio.api.pagos.IPago;
import lat.brio.api.pagos.Pago;

import static com.bbpos.bbdevice.BBDeviceController.getInstance;
import static com.bbpos.bbdevice.BBDeviceController.setDebugLogEnabled;

public class Dongle {

    // Interfaces en general
    private IDongle _IDongle;
    private Bitmap _Firma;

    // Variables de transaxión
    private String _Monto;

    // Variables de entorno de sistema
    private Context _Contexto;

    // Variables de operacion de dongle
    private static BBDeviceController.CheckCardMode _Modo;
    private static Boolean _Conectado = false;
    protected static BBDeviceController _Dongle;
    protected static Escucha _Escucha; // Interfaz de escucha para dongle
    protected static String _ID = "";  // Id de dongle al que se deberá conectar
    private Pago _Pago; // Procesador de pagos
    CurrencyCharacter[] _Caracteres; // Caracteres de uso

    // Objetos de interaccion con el usuario
    protected static PagoDongle _DlgDongle;



    // Nombres de dispositivos para buscar en el escaneo
    protected static final String[] DONGLE_NOMBRES = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
    private String _Comercio;

    // Constructor único
    public Dongle(Context Contexto){
        _Contexto = Contexto;
        _DlgDongle = new PagoDongle(_Contexto);

        _DlgDongle.EventoCerrar(new View.OnClickListener() {
            @Override
            public void onClick(View Vista) {
                switch(_DlgDongle.Vista()){
                    case CONECTAR:
                        Texto("Cerrar(); { CONECTAR::_Dongle->stopBTScan(); }");
                        // Detener escaneo bluetooth
                        if(!_Conectado){
                            try{
                                _Dongle.stopBTScan();
                            }catch(Exception Ex){
                                Ex.printStackTrace();
                            }
                        }
                        break;
                    case PROCESAR:
                        Texto("Cerrar(); { PROCESAR::_Dongle->cancelCheckCard(); }");
                        // Deetener lectura de tarjeta
                        try {
                            _Dongle.cancelCheckCard();
                        }catch(Exception Ex){
                            Ex.printStackTrace();
                        }
                        break;
                }
            }
        });

        _DlgDongle.EventoAceptar(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                // Ajustar a 250x100 px
                Bitmap firma = Bitmap.createBitmap(256, 100, Bitmap.Config.ARGB_8888);

                float ancho = _Firma.getWidth();
                float alto = _Firma.getHeight();

                Canvas canvas = new Canvas(firma);

                float escala = 250 / ancho;

                float TX = 0.0f;
                float TY = (100 - alto * escala) / 2.0f;

                Matrix transformacion = new Matrix();
                transformacion.postTranslate(TX, TY);
                transformacion.preScale(escala, escala);

                Paint pinta = new Paint();
                pinta.setFilterBitmap(true);
                canvas.drawBitmap(_Firma, transformacion, pinta);
                */


                _Firma = ajustar(256, 100, _DlgDongle.Firma());

                // Intentar guardar archivo de firma
                FileOutputStream archivo = null;
                String nombre = "Firma_" + Utils.dateToTimeatamp(Utils.getBrioDate()) + ".png";
                File firma = null;
                try {
                    firma = new File(_Contexto.getFilesDir(), nombre);
                    archivo = new FileOutputStream(firma);
                } catch (FileNotFoundException Ex) {
                    Ex.printStackTrace();
                }

                // Agregar marca de agua por seguridad
                Bitmap firmaFinal = Bitmap.createBitmap (_Firma.getWidth(), _Firma.getHeight(), _Firma.getConfig());

                Canvas canvas = new Canvas(firmaFinal);
                canvas.drawBitmap(_Firma, 0, 0, null);

                // Ubicacion de la marca
                Point ubicacion = new Point(0, 0);

                Paint marcador = new Paint();
                marcador.setColor(Color.MAGENTA);
                marcador.setAlpha(80);
                marcador.setTextSize(12);
                marcador.setAntiAlias(true);

                boolean opera = false;
                for(int e = 0;e<16;e++) {
                    canvas.drawText("Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío Brío", ubicacion.x, ubicacion.y, marcador);
                    ubicacion.y+=20;
                    if(opera){
                        ubicacion.x =  (ubicacion.x + 40);
                        opera = false;
                    }else{
                        ubicacion.x = (ubicacion.x - 40);
                        opera = true;
                    }
                }
                // Texto(firma.getAbsolutePath());
                if(archivo!=null){
                    //firmaFinal.setWidth(256);
                    //firmaFinal.setHeight(100);
                    firmaFinal.compress(Bitmap.CompressFormat.PNG, 100, archivo);
                }

                _Pago.Valor("Firma", nombre);
                _IDongle.alConcluir(_Pago.OPago());
                _Pago = null;

            }
        });

        _DlgDongle.EventoCancelar(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _Firma = _DlgDongle.Firma();
            }
        });

        if(_Dongle == null){
            // Enlazar clase que escucha
            _Escucha = new Escucha();
            _Dongle = getInstance(_Contexto, _Escucha);
            setDebugLogEnabled(true);
            _Dongle.setDetectAudioDevicePlugged(true);
        }
    }

    public Bitmap ajustar(int ancho, int alto, Bitmap bitmap){
        Bitmap fondo = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888);
        float originalWidth = bitmap.getWidth(), originalHeight = bitmap.getHeight();
        Canvas canvas = new Canvas(fondo);
        float scale, xTranslation = 0.0f, yTranslation = 0.0f;
        if (originalWidth > originalHeight) {
            scale = alto/originalHeight;
            xTranslation = (ancho - originalWidth * scale)/2.0f;
        }
        else {
            scale = ancho / originalWidth;
            yTranslation = (alto - originalHeight * scale)/2.0f;
        }
        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, transformation, paint);
        return fondo;
    }

    public Bitmap Firma(){
        return _DlgDongle.Firma();
    }

    // Finaliza el proceso con el dongle y libera todas las variales TODO <
    public void Finalizar(){
        // Cerrar dialogo de dongle
        if(_DlgDongle != null){
            if(_DlgDongle.isShowing()){
                _DlgDongle.dismiss();
            }
        }
        _Pago = new Pago(_Contexto);


        // Liberar controlador de escucha
        //_Dongle.releaseBBDeviceController();
        //_Escucha = null;
        //_Dongle = null;

    }

    public Boolean Firmar(){
        // Vista: Firmar
        _DlgDongle.Vista(PagoDongle.Vista.FIRMAR);
        return true;
    }

    public void Leer(String Monto){
        // Parametros de lectura
        Hashtable<String, Object> Parametros = new Hashtable<String, Object>();

        // Tiempo del terminal
        String tiempo = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());

        // Formato de moneda
        _Caracteres = new CurrencyCharacter[] {CurrencyCharacter.M, CurrencyCharacter.X, CurrencyCharacter.N};

        // Modo de lectura
        _Modo = CheckCardMode.SWIPE_OR_INSERT;

        // Monto de transaccion
        _Monto = Monto;

        // Cargar parametros
        Parametros.put("checkCardMode", _Modo);
        Parametros.put("terminalTime", tiempo);
        Parametros.put("currencyCharacters", _Caracteres);
        Parametros.put("currencyCode", "840");

        // Actualizar proceso
        _DlgDongle.Vista(PagoDongle.Vista.PROCESAR);

        // Iniciar proceso
        _Dongle.startEmv(Parametros);

    }

    // Detectar el modo de tarjeta
    public void Leer(Double Monto){

        // Parametros de lector
        Hashtable<String, Object> parametros = new Hashtable<String, Object>();

        // Establecer el monto
        _Monto = Monto.toString();

        // Asignar parametros
        parametros.put("checkCardMode", BBDeviceController.CheckCardMode.SWIPE_OR_INSERT);
        parametros.put("checkCardTimeout", 300);

        // Iniciar lector
        _Dongle.checkCard(parametros);

        // Actualizar proceso
        _DlgDongle.Vista(PagoDongle.Vista.PROCESAR);
    }
    // Método para esperar a que el dispositivo este listo
    public void Esperar(){

    }

    // Método para conectar con el dispositivo
    public void Conectar(String Comercio, String ID, IDongle IDongle){
        // Reestablecer el proceso de pago
        _Pago = new Pago(_Contexto);

        // ID de comercio del cual se recibe el pago
        _Comercio = Comercio;

        // Establecer comercio
        _Pago.Comercio(Comercio);

        // ID de dispositivo al cual conectar
        _ID = ID;

        // showAllBanners dialogo de conexión
        _DlgDongle.Vista(PagoDongle.Vista.CONECTAR);

        // Mensaje inicial
        Texto("Buscando dispositivo brío");

        // Establecer la interfaz
        _IDongle = IDongle;

        // Si ya esta conectado retornarlo
        if(_Conectado && _ID.equals(ID)){
            _IDongle.alConectar(ID);
            return;
        }else{
            // Si no, traer objetos emparejados y verificar si ya esta conectado
            /*
            Object[] objetos = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
            // BUSCAR EN LOS DISPOSITIVOS YA VINCULADOS
            for(Object objeto : objetos){
                BluetoothDevice dispositivo = (BluetoothDevice) objeto;
                if(dispositivo.getName().startsWith(ID)){
                    _Dongle.connectBT(dispositivo);
                    return;
                }
            }*/

            // Si no esta conectado, escanear
            _Dongle.startBTScan(DONGLE_NOMBRES, 120); // ESCANEAR POR 2 MINUTOS
        }
    }

    // Funcion que actualiza el texto de estado
    public void Texto(String Texto){
        if(_DlgDongle != null){
            if(_DlgDongle.isShowing()){
                _DlgDongle.Estado(Texto);
            }
        }

        if(BrioBaseActivity.DEBUG_SHOW_TOASTS){
            // Toast.makeText(_Contexto, Texto, Toast.LENGTH_LONG).show();
        }

        Log.w("Dongle.java", Texto);
    }

    private static String Hex2Asc(String Hexadecimal){
        if (Hexadecimal == null)
            return "";

        Hexadecimal = Hexadecimal.replaceAll(" ", "");

        if (Hexadecimal.length() % 2 != 0) {
            return "";
        }

        StringBuilder constructor = new StringBuilder();
        for (int i = 0; i < Hexadecimal.length(); i+=2) {
            String tmp = Hexadecimal.substring(i, i+2);
            constructor.append((char)Integer.parseInt(tmp, 16));
        }
        return constructor.toString();
    }

    class Escucha implements BBDeviceController.BBDeviceControllerListener {
        @Override
        public void onWaitingForCard(BBDeviceController.CheckCardMode Modo) { //Texto("onWaitingForCard(" + Modo.toString() + ")");
            switch(Modo){
                case INSERT:Texto("Favor de insertar tarjeta");break;
                case SWIPE:Texto("Favor de deslizar tarjeta");break;
                case SWIPE_OR_INSERT:Texto("Favor de insertar o deslizar tarjeta");break;
                default:Texto("Favor de correr y gritar en circulos");break;
            }

        }

        @Override
        public void onWaitingReprintOrPrintNext() {
            Texto("onWaitingReprintOrPrintNext()");
            //Texto("Favor de presionar 'Re-Print' o 'Print Next'");
        }

        @Override
        public void onBTConnected(BluetoothDevice Dispositivo) {//Texto("onBTConnected(" + Dispositivo.toString() + ")");
            _Conectado = true;
            _IDongle.alConectar(Dispositivo.getName());
        }

        @Override
        public void onBTDisconnected() { // Texto("onBTDisconnected()");
            _Conectado = false;
        }

        @Override
        public void onBTReturnScanResults(List<BluetoothDevice> Dispositivos) {// Texto("onBTReturnScanResults(" + Dispositivos.toString() + ")");
            for (BluetoothDevice dispositivo : Dispositivos) {
                if(dispositivo.getName().startsWith(_ID)){
                    // Detener scaneo
                    _Dongle.stopBTScan();
                    //Texto("Conectando con dispositivo...");
                    // conectar con dispositivo coincidente
                    _Dongle.connectBT(dispositivo);
                    break;
                }
            }
        }

        @Override
        public void onBTScanStopped() { // Texto("onBTScanStopped()");

        }

        @Override
        public void onBTScanTimeout() {
            if(_DlgDongle.isShowing() && _DlgDongle.Vista().equals(PagoDongle.Vista.CONECTAR)){
                _Dongle.startBTScan(DONGLE_NOMBRES, 120); // ESCANEAR POR OTROS 2 MINUTOS
            }
            //Texto("onBTScanTimeout()");
        }

        @Override
        public void onReturnCheckCardResult(BBDeviceController.CheckCardResult Resultado, Hashtable<String, String> Datos) {
            Texto("onReturnCheckCardResult(" + Resultado + "," + Datos.toString() + ")");

            String track1, track2;
            switch (Resultado){
                case BAD_SWIPE:
                    // Texto("Tarjeta no valida, Intente de nuevo por favor");
                    Leer(Double.parseDouble(_Monto));
                    break;
                case NO_CARD: // No hay una tarjeta, buscar de nuevo
                    // Texto("Intente de nuevo por favor");
                    Leer(Double.parseDouble(_Monto));
                    break;
                case MSR: // Lectura de Banda
                    // Hashtable<String, Object> datos = new Hashtable<>();
                    Object[] claves = Datos.keySet().toArray();
                    Arrays.sort(claves);

                    for(Object clave : claves){
                        if(!(clave.equals("c5") || clave.equals("C5"))){
                            String valor = Datos.get(clave);
                            //datos.put(clave.toString(), Datos.get(clave));
                            _Pago.Valor(clave.toString(), valor);
                        }else{
                            Texto("Se omitio la TLV con Clave: " + clave + " Valor: " + Datos.get(clave));
                        }
                    }

                    Texto("Procesando pago");

                    // Preparar datos de pago
                    _Pago.Comercio(_Comercio);
                    _Pago.Valor("mPan", Datos.get("maskedPAN"));
                    //String trk = tlv.get("C5");
                    //String ksn = tlv.get("C3");
                    //Texto("TRK:" + trk);
                    //Texto("KSN:" + ksn);

                    //trk = TripleDES.decrypt(trk,ksn);
                    //_Pago.Track2(trk);

                    //texto = tlv.get("c5");

                    //Texto(texto);
                    //_Pago.TLV(TLV.replace(texto, ""));


                    // Remover el tag que contiene el TLV 57 (C5/c5) con los datos equivalentes a track2
                    //tlv.remove(tlv.get("C5"));
                    //tlv.remove(tlv.get("c5"));

                    _Pago.VentaMSR(new IPago() {
                        @Override
                        public void alAutorizar(String Autorizacion) {
                            //_DlgDongle.dismiss();
                            _Dongle.sendOnlineProcessResult("8A023030"); //
                            _IDongle.alAutorizar();
                        }

                        @Override
                        public void alAutorizar(String Autorizacion, String ARPC) {
                            _IDongle.alAutorizar();
                        }

                        @Override
                        public void alRechazar(String ID, String Mensaje) {
                            _DlgDongle.dismiss();
                            //_Dongle.sendOnlineProcessResult("8A023035");
                            _IDongle.alRechazar(ID, Mensaje);
                        }

                        @Override
                        public void alDeclinar(String ID, String Mensaje) {
                            _DlgDongle.dismiss();
                            //_Dongle.sendOnlineProcessResult("8A023035");
                            _IDongle.alDeclinar(ID, Mensaje);
                        }

                        @Override
                        public void alError(Error Error) {
                            _DlgDongle.dismiss();
                            //Texto("Error: " + Error.getMessage());
                            Error.printStackTrace();
                            _IDongle.alError(Error);
                        }

                    });
                    break;
                case INSERTED_CARD: // Inserción de Tarjeta
                    //Texto("Procesando pago");
                    _Dongle.getEmvCardData();

                    Hashtable<String, Object> parametros = new Hashtable<String, Object>();
                    parametros.put("emvOption", BBDeviceController.EmvOption.START_WITH_FORCE_ONLINE);

                    String tiempo = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
                    parametros.put("terminalTime", tiempo);

                    // Caracteres y codigo de moneda
                    //BBDeviceController.CurrencyCharacter[] caracteres = new BBDeviceController.CurrencyCharacter[] {BBDeviceController.CurrencyCharacter.M, BBDeviceController.CurrencyCharacter.X, BBDeviceController.CurrencyCharacter.N};
                    //parametros.put("currencyCharacters", caracteres);
                    //parametros.put("currencyCode", "840");

                    if(_Modo == null) {
                        _Modo = BBDeviceController.CheckCardMode.SWIPE_OR_INSERT;
                    }

                    parametros.put("checkCardMode", BBDeviceController.CheckCardMode.SWIPE_OR_INSERT);
                    _Dongle.startEmv(parametros);

                    // IniciarEmv();
                    break;
            }
            /*
            String texto = "";
            String datos = "";

            final String mascaraPAN, expira, manejador;
            String idFormato, PAN, claveSerie,codigoServicio, lonTrack1, lonTrack2, encTracks,
                    encTrack1, encTrack2, estTrack1, estTrack2,partTrack, tipoProducto,
                    codificacion, numeroAleatorio, mensajeFinal , encClaveTrabajo, mac, numeroSerie,
                    idNegocio, modoEntrada;

            switch (Resultado){
                case NO_CARD:
                    Texto("No hay una tarjeta");
                break;
                case INSERTED_CARD:
                    Texto("Procesando, por favor espere...");
                    IniciarEmv();
                    //_Dongle.getEmvCardData();
                break;
                case NOT_ICC:
                    Texto("Esta tarjeta no cuenta con Chip");
                break;
                case BAD_SWIPE:
                    Texto("Por favor deslice nuevamente");
                break;
                case MSR: // Magnetic Srtipe Read (Lectura de Banda Magnetica - LBM)

                    if((Datos != null) && (Datos.containsKey("data"))){
                        datos = Datos.get("data");
                    }else{
                        datos = "";
                    }

                    idFormato = Datos.get("formatID");
                    mascaraPAN = Datos.get("maskedPAN");
                    PAN = Datos.get("PAN");
                    expira = Datos.get("expiryDate");
                    manejador = Datos.get("cardholderName");
                    claveSerie = Datos.get("ksn");
                    codigoServicio = Datos.get("serviceCode");
                    lonTrack1 = Datos.get("track1Length");
                    lonTrack2 = Datos.get("track2Length");
                    encTracks = Datos.get("encTracks");
                    encTrack1 = Datos.get("encTrack1");
                    encTrack2 = Datos.get("encTrack2");
                    estTrack1 = Datos.get("track1Status");
                    estTrack2 = Datos.get("track2Status");
                    partTrack = Datos.get("partialTrack");
                    tipoProducto = Datos.get("productType");
                    codificacion = Datos.get("trackEncoding");
                    numeroAleatorio = Datos.get("randomNumber");
                    mensajeFinal = Datos.get("finalMessage");
                    encClaveTrabajo = Datos.get("encWorkingKey");
                    mac = Datos.get("mac");
                    numeroSerie = Datos.get("serialNumber");
                    idNegocio = Datos.get("bID");
                    modoEntrada = Datos.get("posEntryMode");

                    texto = Resultado.toString() + "\n";
                    texto += "ID Formato: " + idFormato + "\n";
                    texto += "Máscara PAN: " + mascaraPAN + "\n";
                    texto += "PAN: " + PAN + "\n";
                    texto += "Expira: " + expira + "\n";
                    texto += "Manejador: " + manejador + "\n";
                    texto += "Clave de Serie: " + claveSerie + "\n";
                    texto += "Código de Servicio: " + codigoServicio + "\n";
                    texto += "Longitud Track1: " + lonTrack1 + "\n";
                    texto += "Longitud Track2: " + lonTrack2 + "\n";
                    texto += "Encripcion de Tracks: " + encTracks + "\n";
                    texto += "Encripcion de Track1: " + encTrack1 + "\n";
                    texto += "Encripcion de Track2: " + encTrack2 + "\n";
                    texto += "Estado de Track1: " + estTrack1 + "\n";
                    texto += "Estado de Track2: " + estTrack2 + "\n";
                    texto += "Parte del Track: " + partTrack + "\n";
                    texto += "Tipo de producto: " + tipoProducto + "\n";
                    texto += "Codificacion: " + codificacion + "\n";
                    texto += "Número aleatorio: " + numeroAleatorio + "\n";
                    texto += "Mensaje Final: " + mensajeFinal + "\n";
                    texto += "Encripcion Clave de Trabajo: " + encClaveTrabajo + "\n";
                    texto += "MAC: " + mac + "\n";

                    if((numeroSerie != null) && (!numeroSerie.equals("")) ){
                        texto += "Número de Serie: " + numeroSerie + "\n";
                    }

                    if((idNegocio != null) && (!idNegocio.equals("")) ){
                        texto += "ID de negocio: " + idNegocio + "\n";
                    }

                    if((modoEntrada != null) && (!modoEntrada.equals("")) ){
                        texto += "Modo de entrada: " + modoEntrada + "\n";
                    }
                    Texto(texto);
                break;
                case MAG_HEAD_FAIL:break;
                case USE_ICC_CARD:
                    if(Datos!=null) {
                        idFormato = Datos.get("formatID");
                        mascaraPAN = Datos.get("maskedPAN");
                        PAN = Datos.get("PAN");
                        expira = Datos.get("expiryDate");
                        manejador = Datos.get("cardholderName");
                        claveSerie = Datos.get("ksn");
                        codigoServicio = Datos.get("serviceCode");
                        lonTrack1 = Datos.get("track1Length");
                        lonTrack2 = Datos.get("track2Length");
                        encTracks = Datos.get("encTracks");
                        encTrack1 = Datos.get("encTrack1");
                        encTrack2 = Datos.get("encTrack2");
                        estTrack1 = Datos.get("track1Status");
                        estTrack2 = Datos.get("track2Status");
                        partTrack = Datos.get("partialTrack");
                        tipoProducto = Datos.get("productType");
                        codificacion = Datos.get("trackEncoding");
                        numeroAleatorio = Datos.get("randomNumber");
                        encClaveTrabajo = Datos.get("encWorkingKey");
                        mac = Datos.get("mac");

                        texto = Resultado.toString() + "\n";
                        texto += "ID Formato: " + idFormato + "\n";
                        texto += "Máscara PAN: " + mascaraPAN + "\n";
                        texto += "PAN: " + PAN + "\n";
                        texto += "Expira: " + expira + "\n";
                        texto += "Manejador: " + manejador + "\n";
                        texto += "Clave de Serie: " + claveSerie + "\n";
                        texto += "Código de Servicio: " + codigoServicio + "\n";
                        texto += "Longitud Track1: " + lonTrack1 + "\n";
                        texto += "Longitud Track2: " + lonTrack2 + "\n";
                        texto += "Encripcion de Tracks: " + encTracks + "\n";
                        texto += "Encripcion de Track1: " + encTrack1 + "\n";
                        texto += "Encripcion de Track2: " + encTrack2 + "\n";
                        texto += "Estado de Track1: " + estTrack1 + "\n";
                        texto += "Estado de Track2: " + estTrack2 + "\n";
                        texto += "Parte del Track: " + partTrack + "\n";
                        texto += "Tipo de producto: " + tipoProducto + "\n";
                        texto += "Codificacion: " + codificacion + "\n";
                        texto += "Número aleatorio: " + numeroAleatorio + "\n";
                        texto += "Encripcion Clave de Trabajo: " + encClaveTrabajo + "\n";
                        texto += "MAC: " + mac + "\n";
                    }
                    Texto(texto);
                break;
                case TAP_CARD_DETECTED:break;
                case MANUAL_PAN_ENTRY:break;
            }
            */
        }

        @Override
        public void onReturnCancelCheckCardResult(boolean Sucedido) {
            Texto("onReturnCancelCheckCardResult(" + Sucedido + ")");
            /*if(Sucedido){
                Texto("Se canceló la verificación de la tarjeta");
            }else{
                Texto("Error al cancelar la verificación de la tarjeta");
            }*/
        }

        @Override
        public void onReturnDeviceInfo(Hashtable<String, String> Datos) {
            // Texto("onReturnDeviceInfo(" + Datos.toString() + ")");
            /*
            String isSupportedTrack1 = Datos.get("isSupportedTrack1");
            String isSupportedTrack2 = Datos.get("isSupportedTrack2");
            String isSupportedTrack3 = Datos.get("isSupportedTrack3");
            String bootloaderVersion = Datos.get("bootloaderVersion");
            String firmwareVersion = Datos.get("firmwareVersion");
            String mainProcessorVersion = Datos.get("mainProcessorVersion");
            String coprocessorVersion = Datos.get("coprocessorVersion");
            String coprocessorBootloaderVersion = Datos.get("coprocessorBootloaderVersion");
            String isUsbConnected = Datos.get("isUsbConnected");
            String isCharging = Datos.get("isCharging");
            String batteryLevel = Datos.get("batteryLevel");
            String batteryPercentage = Datos.get("batteryPercentage");
            String hardwareVersion = Datos.get("hardwareVersion");
            String productId = Datos.get("productId");
            String pinKsn = Datos.get("pinKsn");
            String emvKsn = Datos.get("emvKsn");
            String trackKsn = Datos.get("trackKsn");
            String terminalSettingVersion = Datos.get("terminalSettingVersion");
            String deviceSettingVersion = Datos.get("deviceSettingVersion");
            String formatID = Datos.get("formatID");
            String vendorID = Datos.get("vendorID");
            String csn = Datos.get("csn");
            String uid = Datos.get("uid");
            String serialNumber = Datos.get("serialNumber");
            String modelName = Datos.get("modelName");
            String macKsn = Datos.get("macKsn");
            String nfcKsn = Datos.get("nfcKsn");
            String messageKsn = Datos.get("messageKsn");
            String bID = Datos.get("bID");
            String publicKeyVersion = Datos.get("publicKeyVersion");

            String vendorIDAscii = "";
            if ((vendorID != null) && (!vendorID.equals(""))) {
                if (!vendorID.substring(0, 2).equalsIgnoreCase("00")) {
                    vendorIDAscii = Hex2Asc(vendorID);
                }
            }

            String texto = "";
            texto += "ID Formato: " + formatID + "\n";
            texto += "Fabricante HEX: " + vendorID + "\n";
            texto += "Fabricante ASC: " + vendorIDAscii + "\n";
            texto += "Versión de cargador de inicio:" + bootloaderVersion + "\n";
            texto += "Versión de firmware: " + firmwareVersion + "\n";
            texto += "Versión del procesador principal: " + mainProcessorVersion + "\n";
            texto += "Versión del co-procesador: " + coprocessorVersion + "\n";
            texto += "Versión de cargador de co-procesador: " + coprocessorBootloaderVersion + "\n";
            texto += "Conectado por USB: " + isUsbConnected + "\n";
            texto += "Cargando: " + isCharging + "\n";
            texto += "Nivel de bateria: " + batteryLevel + "\n";
            texto += "Porcentaje de la bateria: " + batteryPercentage + "\n";
            texto += "Versión de hardware: " + hardwareVersion + "\n";
            texto += "Soportado Track1: " + isSupportedTrack1 + "\n";
            texto += "Soportado Track2: " + isSupportedTrack2 + "\n";
            texto += "Id de producto: " + productId + "\n";
            texto += "PIN KSN: " + pinKsn + "\n";
            texto += "EMV KSN: " + emvKsn + "\n";
            texto += "MSR KSN: " + trackKsn + "\n";
            texto += "Ajuste de version de terminal: " + terminalSettingVersion + "\n";
            texto += "Ajuste de version de dispositivo: " + deviceSettingVersion + "\n";
            texto += "getString(R.string.csn)" + csn + "\n";
            texto += "Identificador Unico de Dispositivo:" + uid + "\n";
            texto += "Numero de serie: " + serialNumber + "\n";
            if ((modelName != null) && (!modelName.equals(""))) {
                texto += "Nombre de modelo: " + modelName + "\n";
            }

            if ((macKsn != null) && (!macKsn.equals(""))) {
                texto += "getString(R.string.mac_ksn)" + macKsn + "\n";
            }
            if ((nfcKsn != null) && (!nfcKsn.equals(""))) {
                texto += "getString(R.string.nfc_ksn)" + nfcKsn + "\n";
            }
            if ((messageKsn != null) && (!messageKsn.equals(""))) {
                texto += "getString(R.string.message_ksn)" + messageKsn + "\n";
            }
            if ((bID != null) && (!bID.equals(""))) {
                texto += "getString(R.string.b_id)" + "  :" + bID + "\n";
            }
            if ((publicKeyVersion != null) && (!publicKeyVersion.equals(""))) {
                texto += "getString(R.string.public_key_version)" + "  :" + publicKeyVersion + "\n";
            }

            Texto(texto);*/

            /*
            if (formatID.equals("46")) {
                fidSpinner.setSelection(2);
            } else if (formatID.equals("61")) {
                fidSpinner.setSelection(6);
            } else if (formatID.equals("65")) {
                fidSpinner.setSelection(8);
            } else {
                fidSpinner.setSelection(5);
            }
            */
        }



        @Override
        public void onReturnTransactionResult(BBDeviceController.TransactionResult Resultado) {
            //Texto("onReturnTransactionResult(" + Resultado + ")");
        }

        @Override
        public void onReturnBatchData(String TLV) {
            if(BrioBaseActivity.DEBUG_SHOW_TOASTS){
                //Texto("onReturnBatchData(" + TLV + ")");
            }


            /*String texto = "Datos Batch: \n";
            Hashtable<String, String> datos = _Dongle.decodeTlv(TLV);

            Object[] claves = datos.keySet().toArray();
            Arrays.sort(claves);

            for(Object clave : claves){
                String valor = datos.get(clave);
                texto += clave + ":" + valor + "\n";
            }

            Texto(texto);*/
        }

        @Override
        public void onReturnReversalData(String TLV) {
            if(BrioBaseActivity.DEBUG_SHOW_TOASTS){
                Texto("onReturnReversalData(" + TLV + ")");
            }
            /* Texto("onReturnReversalData(" + TLV + ")");
            String texto = "Datos de reversa: \n";
            texto += TLV;
            Texto(texto); */
        }

        @Override
        public void onReturnAmountConfirmResult(boolean Sucedido) {
            Texto("onReturnAmountConfirmResult(" + Sucedido + ")");
            /*if(Sucedido) {
                Texto("Monto confirmado");
            }else{
                Texto("Monto cancelado");
            }*/
        }

        @Override
        public void onReturnPinEntryResult(BBDeviceController.PinEntryResult Resultado, Hashtable<String, String> Datos) {
            Texto("onReturnPinEntryResult(" + Resultado + "," + Datos.toString() + ")");
            /*
            String texto = "";
            switch(Resultado){
                case ENTERED:
                    texto = "PIN Entrado";
                    if(Datos.containsKey("epb")){
                        texto += "\nEPB: " + Datos.get("epb");
                    }

                    if(Datos.containsKey("ksn")){
                        texto += "\nKSN: " + Datos.get("ksn");
                    }

                    if(Datos.containsKey("randomNumber")){
                        texto += "\nNúmero aleatorio: " + Datos.get("randomNumber");
                    }

                    if(Datos.containsKey("encWorkingKey")){
                        texto += "\nLlave de cifrado: " + Datos.get("encWorkingKey");
                    }
                break;
                case BYPASS:texto ="PIN Brincado";break;
                case CANCEL:texto ="PIN Cancelado";break;
                case TIMEOUT:texto ="PIN No introducido";break;
            }
            Texto(texto);*/
        }

        @Override
        public void onReturnPrintResult(BBDeviceController.PrintResult Resultado) {
            Texto("onReturnPrintResult(" + Resultado + ")");
            //Texto(Resultado.toString());
        }

        @Override
        public void onReturnAmount(Hashtable<String, String> Datos) {
            Texto("onReturnAmount(" + Datos + ")");
            /*
            String monto = Datos.get("amount");
            String moneda = Datos.get("currencyCode");

            Texto("Monto: " + monto + "\nMoneda: " + moneda);
            */
        }

        @Override
        public void onReturnUpdateTerminalSettingResult(BBDeviceController.TerminalSettingStatus Estado) {
            Texto("onReturnUpdateTerminalSettingResult(" + Estado + ")");
            /*
            switch (Ajustes){
                case SUCCESS:
                    Texto("Actualizacion de ajustes en terminal correcto");
                break;
                case TAG_NOT_FOUND:
                    Texto("Error al actualizar ajustes de terminal: Etiqueta no encontrada");
                break;
                case LENGTH_INCORRECT:
                    Texto("Error al actualizar ajustes de terminal: Longitud incorrecta");
                break;
                case TLV_INCORRECT:
                    Texto("Error al actualizar ajustes de terminal: TLV no validos");
                break;
                case BOOTLOADER_NOT_SUPPORT:
                    Texto("Error al actualizar ajustes de terminal: Cargador de inicio no soportado");
                break;
                case TAG_NOT_ALLOWED_TO_ACCESS:
                    Texto("Error al actualizar ajustes de terminal: Etiqueta de solo lectura");
                break;
                case USER_DEFINED_DATA_NOT_ENALBLED:
                    Texto("Error al actualizar ajustes de terminal: Etiqueta definida por usuario, no habilitada");
                break;
                case TAG_NOT_WRITTEN_CORRECTLY:
                    Texto("Error al actualizar ajustes de terminal: Etiqueta no se grabó");
                break;
            }*/

        }

        @Override
        public void onReturnReadTerminalSettingResult(BBDeviceController.TerminalSettingStatus Estado, String Valor) {
            Texto("onReturnReadTerminalSettingResult(" + Estado + "," + Valor + ")");
            /*
            switch (Ajustes){
                case SUCCESS:
                    Texto("Etiqueta con el valor :" + Valor);
                break;
                case TAG_NOT_FOUND:
                    Texto("Etiqueta no encontrada");
                break;
                case LENGTH_INCORRECT:
                    Texto("Etiqueta con longitud incorrecta");
                break;
                case TLV_INCORRECT:
                    Texto("TLV no valido");
                break;
                case BOOTLOADER_NOT_SUPPORT:
                    Texto("Cargador de inicio no soportado");
                break;
                case TAG_NOT_ALLOWED_TO_ACCESS:
                    Texto("Etiqueta no permitida");
                break;
                case USER_DEFINED_DATA_NOT_ENALBLED:
                    Texto("Etiqueta definida por usuario no permitida");
                break;
                case TAG_NOT_WRITTEN_CORRECTLY:
                    Texto("Etiqueta no grabada correctamente");
                break;
            }*/

        }

        @Override
        public void onReturnEnableInputAmountResult(boolean Sucedido) {
            Texto("onReturnEnableInputAmountResult(" + Sucedido + ")");
            /*
            if(Sucedido){
                Texto("Entrada de monto habilitada");
            }else{
                Texto("Error en habilitación");
            }*/
        }


        @Override
        public void onReturnDisableInputAmountResult(boolean Sucedido) {
            Texto("onReturnDisableInputAmountResult(" + Sucedido + ")");
            /*if(Sucedido){
                Texto("Entrada de monto deshabilitada");
            }else{
                Texto("Error en deshabilitación");
            }*/
        }

        @Override
        public void onReturnPhoneNumber(BBDeviceController.PhoneEntryResult Resultado, String Telefono) {
            Texto("onReturnPhoneNumber(" + Resultado + "," + Telefono + ")");
            /*
            switch(Resultado){
                case ENTERED:Texto("No. Tel: " + Telefono);break;
                case TIMEOUT:Texto("Tiempo limitado");break;
                case CANCEL:Texto("Cancelado");break;
                case WRONG_LENGTH:Texto("Longitud invalida");break;
                case BYPASS:Texto("Pasado por alto");break;

            }*/
        }

        @Override
        public void onReturnEmvCardDataResult(boolean Sucedido, String TLV) {
            Texto("onReturnEmvCardDataResult(" + Sucedido + "," + TLV + ")");
        }

        @Override
        public void onReturnEmvCardNumber(boolean Sucedido, String PAN) {
            Texto("onReturnEmvCardNumber(" + Sucedido + "," + PAN + ")");
            /*
            if(Sucedido){
                Texto("PAN: " + PAN);
            }else {
                Texto("Error al obtener el PAN");
            }*/
        }

        @Override
        public void onReturnEncryptPinResult(boolean Sucedido, Hashtable<String, String> Datos) {
            Texto("onReturnEncryptPinResult(" + Sucedido + "," + Datos.toString() + ")");
            /*
            String ksn = Datos.get("ksn");
            String epb = Datos.get("epb");
            String rnd = Datos.get("randomNumber");
            String ewk = Datos.get("encWorkingKey");
            String ems = Datos.get("errorMessage");

            Texto("KSN: " + ksn + "\nEPB: " + epb + "\nRND: " + rnd + "\nEWK: " + ewk + "\nEMS: " + ems);*/
        }

        @Override
        public void onReturnEncryptDataResult(boolean Sucedido, Hashtable<String, String> Datos) {
            // Texto("onReturnEncryptDataResult(" + Sucedido + "," + Datos.toString() +  ")");
            /*
            Texto("onReturnEncryptDataResult(" + Sucedido + "," + Datos + ")");
            String texto = "Error al encriptar datos";
            if(Sucedido){
                texto = "";
                if(Datos.containsKey("ksn")){
                    texto = "KSN: " + Datos.get("ksn");
                }

                if(Datos.containsKey("randomNumber")){
                    texto = "RND: " + Datos.get("randomNumber");
                }

                if(Datos.containsKey("encData")){
                    texto = "EDT: " + Datos.get("encData");
                }

                if(Datos.containsKey("mac")){
                    texto = "MAC: " + Datos.get("mac");
                }
            }
            Texto(texto);*/
        }

        @Override
        public void onReturnInjectSessionKeyResult(boolean Sucedido, Hashtable<String, String> Datos) {
            Texto("onReturnInjectSessionKeyResult(" + Sucedido + "," + Datos.toString() +  ")");
            /*
            String texto = "";
            if(Sucedido){
                texto = "Clave de sesion correctamente injectada";
                if(Datos.size( ) == 0){
                    Inyectar();
                }
            }else{
                texto = "Error en la clave de sesion:" + Datos.get("errorMessage");
            }
            Texto(texto);*/
        }

        @Override
        public void onReturnUpdateAIDResult(Hashtable<String, BBDeviceController.TerminalSettingStatus> Datos) {
            Texto("onReturnUpdateAIDResult(" + Datos.toString() + ")");
            /*String texto = "" ;
            Object[] claves = Datos.keySet().toArray();
            Arrays.sort(claves);
            for(Object clave : claves){
                texto += "\n" + clave.toString() + " : ";
                Object valor = Datos.get(clave);
                if(valor instanceof String){
                    texto += valor.toString();
                }else if(valor instanceof Boolean){
                    texto += ((Boolean)valor);
                }else if(valor instanceof TerminalSettingStatus){
                    texto += ((TerminalSettingStatus)valor);
                }
            }

            Texto(texto);*/
        }

        @Override
        public void onReturnUpdateGprsSettingsResult(boolean Sucedido, Hashtable<String, BBDeviceController.TerminalSettingStatus> Datos) {
            Texto("onReturnUpdateGprsSettingsResult(" + Sucedido + "," + Datos.toString() + ")");
            /*if(Sucedido){
                Texto("Actualizacion GPRS correcta");
            }else{
                Texto("Actualizacion GPRS fallo con: " + Datos.get("gprs"));
            }*/
        }


        @Override
        public void onReturnUpdateWiFiSettingsResult(boolean Sucedido, Hashtable<String, BBDeviceController.TerminalSettingStatus> Datos) {
            Texto("onReturnUpdateWiFiSettingsResult(" + Sucedido + "," + Datos.toString() + ")");
            /*
            String texto = "Se actualizaron los ajustes WiFi";
            if(!Sucedido){
                texto = "Falló la actualización de ajustes WiFi";
                Object[] claves = Datos.keySet().toArray();
                Arrays.sort(claves);
                for(Object clave : claves){
                    texto += "\n" + (String)clave + " : ";
                    TerminalSettingStatus estado = (TerminalSettingStatus)Datos.get(clave);
                    switch (estado){
                        case SUCCESS:texto += "Lectura de ajuste correcta";break;
                        case LENGTH_INCORRECT:texto += "Longitud incorrecta";break;
                        case TLV_INCORRECT:texto += "TLV incorrecto";break;
                        case TAG_NOT_FOUND:texto += "etiqueta no encontrada";break;
                        case BOOTLOADER_NOT_SUPPORT:texto += "Cargador de arranque no soportado";break;
                        case TAG_NOT_ALLOWED_TO_ACCESS:texto += "Etiqueta no permitida";break;
                        case USER_DEFINED_DATA_NOT_ENALBLED:texto += "No permitido el cambio de etiqueta definida por usuario";break;
                        case TAG_NOT_WRITTEN_CORRECTLY:texto += "Etiqueta no grabada";break;
                    }
                }
            }
            Texto(texto);*/
        }

        @Override
        public void onReturnReadAIDResult(Hashtable<String, Object> Datos) {
            Texto("onReturnReadAIDResult(" + Datos.toString() + ")");
            /*String texto = "Lectura AID sucedida";

            Object[] claves = Datos.keySet().toArray();
            Arrays.sort(claves);

            for(Object clave : claves){
                texto += "\n" + clave.toString() + " : ";
                Object valor = Datos.get(clave);
                if(valor instanceof String){
                    texto += valor.toString();
                }else if(valor instanceof Boolean){
                    texto += ((Boolean)valor).toString();
                }else if(valor instanceof TerminalSettingStatus){
                    texto += ((TerminalSettingStatus)valor).toString();
                }
            }
            Texto(texto);*/
        }

        @Override
        public void onReturnReadGprsSettingsResult(boolean Sucedido, Hashtable<String, Object> Datos) {
            Texto("onReturnReadGprsSettingsResult(" + Sucedido + "," + Datos.toString() + ")");
            /*
            String texto = "";
            if (Sucedido) {
                texto = "Lectura de ajustes GPRS sucedida";
                texto += "\nOperador: " + Datos.get("operator");
                texto += "\nAPN: " + Datos.get("apn");
                texto += "\nUsuario: " + Datos.get("username");
                texto += "\nClave: " + Datos.get("password");
            } else {
                texto = "Falló la lectura de ajustes GPRS";
                TerminalSettingStatus estado = (TerminalSettingStatus)Datos.get("gprs");
                switch (estado) {
                    case SUCCESS:
                        Texto("Lectura de ajustes de GPRS sucedida");
                        break;
                    case LENGTH_INCORRECT:
                        Texto("Longitud invalida");
                        break;
                    case TLV_INCORRECT:
                        Texto("TLV invalidas");
                        break;
                    case TAG_NOT_FOUND:
                        Texto("Etiqueta no encontrada");
                        break;
                    case BOOTLOADER_NOT_SUPPORT:
                        Texto("Cargador de inicio no soportado");
                        break;
                    case TAG_NOT_ALLOWED_TO_ACCESS:
                        Texto("No se puede acceder a la etiqueta");
                        break;
                    case USER_DEFINED_DATA_NOT_ENALBLED:
                        Texto("Datos definidos por usuario, no es posible cambiar");
                        break;
                    case TAG_NOT_WRITTEN_CORRECTLY:
                        Texto("Etiqueta no grabada");
                        break;
                    default:
                        break;
                }
            }
            Texto(texto);*/
        }

        @Override
        public void onReturnReadWiFiSettingsResult(boolean Sucedido, Hashtable<String, Object> Datos) {
            Texto("onReturnReadWiFiSettingsResult(" + Sucedido + "," + Datos.toString() +  ")");
            /*
            String texto = "";
            if (Sucedido) {
                texto = "Lectura de ajustes WiFi sucedida";
                texto += "\nSSID: " + Datos.get("ssid");
                texto += "\nClave: " + Datos.get("password");
                texto += "\nURL: " + Datos.get("url");
                texto += "\nPuerto: " + Datos.get("portNumber");
            } else {
                texto = "Falló la lectura de ajustes WiFi";
                Object[] claves = Datos.keySet().toArray();
                Arrays.sort(claves);
                for (Object clave : claves) {
                    texto += "\n" + clave.toString() + " : ";
                    TerminalSettingStatus terminalSettingStatus = (TerminalSettingStatus)Datos.get(clave);
                    switch (terminalSettingStatus) {
                        case SUCCESS:
                            texto += "Lectura de ajustes de terminal sucedida";
                            break;
                        case LENGTH_INCORRECT:
                            texto += "Longitud incorrecta";
                            break;
                        case TLV_INCORRECT:
                            texto += "TLV incorrectas";
                            break;
                        case TAG_NOT_FOUND:
                            texto += "Etiqueta no encontrada";
                            break;
                        case BOOTLOADER_NOT_SUPPORT:
                            texto += "Cargador de arranque no soportado";
                            break;
                        case TAG_NOT_ALLOWED_TO_ACCESS:
                            texto += "Etiqueta no se permite el acceso";
                            break;
                        case USER_DEFINED_DATA_NOT_ENALBLED:
                            texto += "Datos definidos por el usuario no permitido cambiar";
                            break;
                        case TAG_NOT_WRITTEN_CORRECTLY:
                            texto += "Etiqueta no grabada";
                            break;
                        default:
                            break;
                    }
                }
            }
            Texto(texto);*/
        }

        @Override
        public void onReturnCAPKList(List<CAPK> Datos) {
            Texto("onReturnCAPKList(" + Datos.toString() + ")");
            /*String texto = "CAPK: ";
            Integer a = 0;
            for(CAPK dato : Datos){
                texto += "\n" + a;
                texto += "\nUbicación: " + dato.location;
                texto += "\nRID: " + dato.rid;
                texto += "\nIndice: " + dato.index;
                a++;
            }

            Texto(texto);*/
        }

        @Override
        public void onReturnCAPKDetail(CAPK Dato) {
            Texto("onReturnCAPKDetail(" + Dato .toString()+ ")");
            /*
            String texto = "CAPK: ";
            if (Dato != null) {
                texto += "\nUbicación: " + Dato.location;
                texto += "\nRID: " + Dato.rid;
                texto += "\nIndice: " + Dato.index;
                texto += "\nExponente: " + Dato.exponent;
                texto += "\nModulo: " + Dato.modulus;
                texto += "\nValidación: " + Dato.checksum;
                texto += "\nTamaño: " + Dato.size;
                texto += "\n";
            } else {
                texto += "\nnull \n";
            }
            Texto(texto);*/
        }

        @Override
        public void onReturnCAPKLocation(String Ubicacion) {
            Texto("onReturnCAPKLocation(" + Ubicacion + ")");
        }

        @Override
        public void onReturnUpdateCAPKResult(boolean Sucedido) {
            Texto("onReturnUpdateCAPKResult(" + Sucedido + ")");
            // Texto(Sucedido ? "Actualización de CAPK sucedida" : "Falló la actualización de CAPK");
        }

        @Override
        public void onReturnEmvReportList(Hashtable<String, String> Datos) {
            Texto("onReturnEmvReportList(" + Datos.toString() + ")");
            /*
            String texto = "Reporte de lista EMV: \n";
            Object[] claves = Datos.keySet().toArray();
            Arrays.sort(claves);
            for(Object clave : claves){
                texto += clave + " : " + Datos.get(clave) + "\n";
            }
            Texto(texto);
            */
        }

        @Override
        public void onReturnEmvReport(String TLV) {
            Texto("onReturnEmvReport(" + TLV + ")");
            /*
            String texto =  "Reporte EMV: \n";

            Hashtable<String, String> datos = decodeTlv(TLV);
            Object[] claves = datos.keySet().toArray();
            Arrays.sort(claves);
            for (Object clave : claves) {
                if (((String) clave).matches(".*[a-z].*") && datos.containsKey(((String) clave).toUpperCase(Locale.ENGLISH))) {
                    continue;
                }
                String valor = datos.get(clave);
                texto += clave + " : " + valor + "\n";

                if (((String) clave).toUpperCase(Locale.ENGLISH).equalsIgnoreCase(TagList.EMV_REPORT_TEMPLATE)) {
                    Hashtable<String, String> indatos = decodeTlv(valor);
                    Object[] inclaves = indatos.keySet().toArray();
                    Arrays.sort(inclaves);
                    for (Object inclave : inclaves) {
                        if (((String) inclave).matches(".*[a-z].*") && indatos.containsKey(((String) inclave).toUpperCase(Locale.ENGLISH))) {
                            continue;
                        }
                        texto += "\n" + inclave + " : " + indatos.get(inclave);
                    }
                }
            }
            Texto(texto);
            */
        }

        @Override
        public void onReturnPowerOnIccResult(boolean Sucedido, String s, String s1, int i) {
            Texto("onReturnPowerOnIccResult(" + Sucedido + "," + s + "," + s1 + "," + i + ")");
        }

        @Override
        public void onReturnPowerOffIccResult(boolean Sucedido) {
            Texto("onReturnPowerOffIccResult(" + Sucedido + ")");
        }

        @Override
        public void onReturnApduResult(boolean Sucedido, Hashtable<String, Object> Datos) {
            Texto("onReturnApduResult(" + Sucedido + "," + Datos.toString() + ")");
        }

        @Override
        public void onRequestSelectApplication(ArrayList<String> Datos) {
            Texto("onRequestSelectApplication(" + Datos.toString() + ")");
            _Dongle.selectApplication(Datos.indexOf(0));
        }

        @Override
        public void onRequestSetAmount() {//Texto("onRequestSetAmount()");
            _Pago.Valor("Monto", _Monto);
            _Dongle.setAmount(_Monto, "0", "840", TransactionType.PAYMENT, _Caracteres);
        }

        @Override
        public void onRequestPinEntry(BBDeviceController.PinEntrySource Fuente) {
            // Actualizar proceso
            _DlgDongle.Vista(PagoDongle.Vista.FIRMAR);
            // Texto("onRequestPinEntry(" + Fuente.toString() + ")");
        }

        @Override
        public void onRequestOnlineProcess(final String TLV) {
            _Pago.Valor("TLV", TLV);
            _Pago.Valor("bID", _ID);
            _Pago.VentaEMV(new IPago(){
                @Override
                public void alAutorizar(String Autorizacion) {

                }
                @Override
                public void alAutorizar(String Autorizacion, String ARPC) {
                    _IDongle.alAutorizar();
                    _Dongle.sendFinalConfirmResult(true);
                }

                @Override
                public void alRechazar(String ID, String Mensaje) {
                    _DlgDongle.dismiss();
                    _Dongle.sendOnlineProcessResult("8A023035");
                    _IDongle.alRechazar(ID, Mensaje);
                }

                @Override
                public void alDeclinar(String ID, String Mensaje) {
                    _DlgDongle.dismiss();
                    _Dongle.sendOnlineProcessResult("8A023035");
                    _IDongle.alDeclinar(ID, Mensaje);
                }

                @Override
                public void alError(Error Error) {
                    Texto("Error: " + Error.getMessage());
                    Error.printStackTrace();
                    _IDongle.alError(Error);
                }

            });
            Texto("Esperando autorización");
            // Invocar los datos EMV
            // _Dongle.getEmvCardData();
            /*
            Log.w("TLV", TLV);
            Hashtable<String, String> datos = BBDeviceController.decodeTlv(TLV);
            Object[] claves = datos.keySet().toArray();
            Arrays.sort(claves);

            // Recorrer las etiquetas
            for (Object clave : claves) {
                switch(clave.toString()){
                    case "": break;
                    case "d": break;
                }

                String valor = datos.get(clave);
                Log.w("TLV", clave + " : " + valor);
            }

            Texto("onRequestOnlineProcess(" + TLV + ")");
              */
        }

        @Override
        public void onRequestTerminalTime() {
            Texto("onRequestTerminalTime()");
        }

        int intentos = 0;
        @Override
        public void onRequestDisplayText(BBDeviceController.DisplayText Texto) {

            switch(Texto){
                case PROCESSING:
                    Texto("Procesando espere...");
                    break;
                case REMOVE_CARD:
                    // Texto("Favor de retirar tarjeta");
                    break;
                case DECLINED:
                    Texto("Declinada por el comercio");
                    break;
                case INSERT_SWIPE_OR_TRY_ANOTHER_CARD:
                    Texto("Error de tarjeta, favor de deslizar");
                    break;
                case NOT_ICC_CARD:
                    if(intentos>1){
                        _Pago.Valor("FALLBACK", "1");
                        intentos = 0;
                    }else{
                        intentos++;
                    }
                    break;
                case TIMEOUT: // El tiempo de espera se ha excedido

                    break;
                default:
                    Texto("onRequestDisplayText(" + Texto + ")");
            }
        }

        @Override
        public void onRequestDisplayAsterisk(int Cantidad) {
            Texto("onRequestDisplayAsterisk(" + Cantidad + ")");
        }

        @Override
        public void onRequestDisplayLEDIndicator(BBDeviceController.ContactlessStatus Estado) {
            Texto("onRequestDisplayLEDIndicator(" + Estado + ")");
        }

        @Override
        public void onRequestProduceAudioTone(BBDeviceController.ContactlessStatusTone Tono) {
            Texto("onRequestProduceAudioTone(" + Tono.toString() + ")");
        }

        @Override
        public void onRequestClearDisplay() {
            Texto("onRequestClearDisplay()");
        }

        @Override
        public void onRequestFinalConfirm() {
            Texto("onRequestFinalConfirm()");
            _Dongle.sendFinalConfirmResult(true);
        }

        @Override
        public void onRequestPrintData(int Indice, boolean Valor) {
            Texto("onRequestPrintData(" + Indice + "," + Valor + ")");
        }

        @Override
        public void onPrintDataCancelled() {
            Texto("onPrintDataCancelled()");
        }

        @Override
        public void onPrintDataEnd() {
            Texto("onPrintDataEnd()");
        }

        @Override
        public void onBatteryLow(BBDeviceController.BatteryStatus Estado) {
            Texto("onBatteryLow(" + Estado.toString() + ")");
        }

        @Override
        public void onAudioDevicePlugged() {
            Texto("onAudioDevicePlugged()");
        }

        @Override
        public void onAudioDeviceUnplugged(){
            Texto("onAudioDeviceUnplugged()");
        }

        @Override
        public void onError(BBDeviceController.Error Error, String Mensaje) {
            switch(Error){
                case FAIL_TO_START_BT: // Error al iniciar el bluetooth

                    break;
                case INVALID_FUNCTION_IN_CURRENT_CONNECTION_MODE:

                    break;
            }
            Texto("onError(" + Error.toString() + "," + Mensaje + ")");
        }

        @Override
        public void onSessionInitialized() {
            Texto("onSessionInitialized()");
        }

        @Override
        public void onSessionError(BBDeviceController.SessionError Error, String Mensaje) {
            Texto("onSessionError(" + Error.toString() + "," + Mensaje + ")");
        }

        @Override
        public void onAudioAutoConfigProgressUpdate(double Valor) {
            Texto("onAudioAutoConfigProgressUpdate(" + Valor + ")");
        }

        @Override
        public void onAudioAutoConfigCompleted(boolean Sucedido, String Mensaje) {
            Texto("onAudioAutoConfigCompleted(" + Sucedido + "," + Mensaje + ")");
        }

        @Override
        public void onAudioAutoConfigError(BBDeviceController.AudioAutoConfigError Error) {
            Texto("onAudioAutoConfigError(" + Error.toString() + ")");
        }

        @Override
        public void onNoAudioDeviceDetected() {
            Texto("onNoAudioDeviceDetected()");
        }

        @Override
        public void onDeviceHere(boolean Aqui) {
            Texto("onDeviceHere(" + Aqui + ")");
            if(Aqui){
                _Conectado = true;
            }
        }

        @Override
        public void onPowerDown() {// Al apagar, obviamente desconactar
            Texto("onPowerDown()");
        }

        @Override
        public void onPowerButtonPressed() {
            Texto("onPowerButtonPressed()");
        }

        @Override
        public void onDeviceReset() {
            Texto("onDeviceReset()");
        }

        @Override
        public void onReturnNfcDataExchangeResult(boolean Sucedido, Hashtable<String, String> Datos) {
            Texto("onReturnNfcDataExchangeResult(" + Sucedido + "," + Datos.toString() + ")");
        }

        @Override
        public void onReturnNfcDetectCardResult(BBDeviceController.NfcDetectCardResult Resultado, Hashtable<String, Object> Datos) {
            Texto("onReturnNfcDetectCardResult(" + Resultado + "," + Datos + ")");
        }

        @Override
        public void onUsbConnected() {
            Texto("onUsbConnected()");
        }

        @Override
        public void onUsbDisconnected() {
            Texto("onUsbDisconnected()");
        }

        @Override
        public void onSerialConnected() {
            Texto("onSerialConnected()");
        }

        @Override
        public void onSerialDisconnected() {
            Texto("onSerialDisconnected()");
        }

        @Override
        public void onBarcodeReaderConnected() {
            Texto("onBarcodeReaderConnected()");
        }

        @Override
        public void onBarcodeReaderDisconnected() {
            Texto("onBarcodeReaderDisconnected()");
        }

        @Override
        public void onReturnBarcode(String Valor) {
            Texto("onReturnBarcode(" + Valor + ")");
        }
    }
}
