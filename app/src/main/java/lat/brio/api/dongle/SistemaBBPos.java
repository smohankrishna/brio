package lat.brio.api.dongle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bbpos.bbdevice.BBDeviceController;
import com.bbpos.bbdevice.CAPK;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogoEstadoDispositivo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

/**
 * Created by Delgadillo on 06/06/17.
 */

public class SistemaBBPos {

    protected enum Status {
        GETTING_PSE, READING_RECORD, READING_AID, GETTING_PROCESS_OPTION, READING_DATA
    }

    // Variables de propiedad privada
    private static String _Estatus = "Inicializando, por favor espere...";
    private Boolean _Conectado = false;
    private Double _Monto;
    private Boolean _Conectando = false;
    private String _Dispositivo = "CHB108639000002";
    protected static DialogoEstadoDispositivo _Dialogo;
    private String AIDs = "";
    private static BBDeviceController _Controlador;
    protected static BBPosEscuchador _Escuchador;
    private static Context _Contexto;
    private static ISistemaBBPos _ISistemaBBPos;

    // Parametros de dongle
    protected static BBDeviceController.CheckCardMode _Modo;
    protected static String ksn = "";
    protected static boolean isPKCS7 = false;
    private static String[] aids = new String[] { "A0000000031010", "A0000000041010", "A00000002501", "A0000000651010", "A0000001523010", "A0000003241010", "A0000000032010", "A0000000043060", "A0000000044010"};
    protected static final String CBC = "CBC";
    protected static String encryptionMode = CBC; // Modo de encripcion
    protected static boolean isApduEncrypted = true; // Encripcion apdu
    private static int aflCounter = 0;
    // Id de formato
    protected String _IDFormato = "FID61";
    // Clave funcional para el formato con el ID 65
    protected static String _ClaveFuncionalFID65 = "A1223344556677889900AABBCCDDEEFF";
    protected static String _ClaveMaestraFID65 = "0123456789ABCDEFFEDCBA9876543210";

    // Variables reciclables
    private Boolean _Cancelado = false;
    private static int aidCounter = 0;
    protected static final String DATA_KEY = "Data Key";
    protected static final String DATA_KEY_VAR = "Data Key Var";
    protected static String keyMode = DATA_KEY;
    protected static Status state = null;
    private static String[] afls = null;
    private static int readingFileIndex = 0;
    private static int total = 0;
    private static String sfi = "";
    protected static long startTime;

    // Variables de lectura
    protected static ArrayList<byte[]> receipts;

    protected static String cardholderName;
    protected static String cardNumber;
    protected static String expiryDate;
    protected static String track2 = "";
    protected static String pan = "";
    protected static String aid;
    protected static String appLabel;
    protected static String tc;
    protected static String batchNum;
    protected static String tid;
    protected static String mid;
    protected static String transactionDateTime;
    protected static boolean signatureRequired;

    // Enumerador de estado de dispositivo
    private enum ESTADO_DISPOSITIVO {
        DISPOSITIVO_BUSCANDO,
        DISPOSITIVO_CONECTANDO,
        DISPOSITIVO_LECTURA;

        ESTADO_DISPOSITIVO (){

        }
    }
    private void IniciarEMV(){ // TODO: Indicar HORA y MONTO

        Hashtable<String, Object> data = new Hashtable<String, Object>();
        data.put("emvOption", BBDeviceController.EmvOption.START);
        if(_IDFormato.equals("FID61")) {
            data.put("orderID", "0123456789ABCDEF0123456789ABCDEF");
            data.put("randomNumber", "012345");
        } else if(_IDFormato.equals("FID46")) {
            data.put("randomNumber", "0123456789ABCDEF");
        } else if(_IDFormato.equals("FID65")) {
            String encWorkingKey = TripleDES.encrypt( _ClaveFuncionalFID65, _ClaveMaestraFID65);
            String workingKeyKcv = TripleDES.encrypt("0000000000000000", _ClaveFuncionalFID65);

            data.put("encPinKey", encWorkingKey + workingKeyKcv);
            data.put("encDataKey", encWorkingKey + workingKeyKcv);
            data.put("encMacKey", encWorkingKey + workingKeyKcv);
        }
        if(_Modo != null) {
            data.put("checkCardMode", _Modo);
        }

        String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
        data.put("terminalTime", terminalTime);
        _Controlador.startEmv(data);

    }
    public void IniciarLectura(Double Monto){
        _Monto = Monto;
        IniciarEMV();
        _Dialogo.Gif(R.drawable.pasar_tarjeta);
        _Dialogo.Estado("Favor insertar/deslizar la tarjeta");

        _Dialogo.BotonCancelar(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(_Controlador != null){
                    Toast.makeText(_Contexto, "Detener lectura de tarjeta", Toast.LENGTH_LONG).show();
                    _Controlador.cancelCheckCard();
                }
            }
        });
    }

    private static String toHexString(byte[] b) {
        if (b == null) {
            return "null";
        }
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xFF) + 0x100, 16).substring(1);
        }
        return result;
    }
    private static byte[] hexToByteArray(String s) {
        if (s == null) {
            s = "";
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (int i = 0; i < s.length() - 1; i += 2) {
            String data = s.substring(i, i + 2);
            bout.write(Integer.parseInt(data, 16));
        }
        return bout.toByteArray();
    }
    protected static String toHexString(byte b) {
        return Integer.toString((b & 0xFF) + 0x100, 16).substring(1);
    }
    protected static void sendApdu(String command) {
        try {
            if (isApduEncrypted) {
                String key;
                if (keyMode.equals(DATA_KEY)) {
                    key = DUKPTServer.GetDataKey(ksn, "0123456789ABCDEFFEDCBA9876543210");
                } else if (keyMode.equals(DATA_KEY_VAR)) {
                    key = DUKPTServer.GetDataKeyVar(ksn, "0123456789ABCDEFFEDCBA9876543210");
                } else {
                    key = DUKPTServer.GetPinKeyVar(ksn, "0123456789ABCDEFFEDCBA9876543210");
                }

                String temp = command;
                if (isPKCS7) {
                    int padding = 8 - (temp.length() / 2) % 8;
                    for (int i = 0; i < padding; ++i) {
                        temp += "0" + padding;
                    }
                } else {
                    while ((temp.length() / 2) % 8 != 0) {
                        temp += "00";
                    }
                }

                String encryptedCommand;

                if (encryptionMode.equals(CBC)) {
                    encryptedCommand = TripleDES.encrypt_CBC(temp, key);
                } else {
                    encryptedCommand = TripleDES.encrypt(temp, key);
                }

                Hashtable<String, Object> apduInput = new Hashtable<String, Object>();
                apduInput.put("apdu", encryptedCommand);
                if (isPKCS7) {
                    _Controlador.sendApdu(apduInput);
                } else {
                    apduInput.put("apduLength", command.length() / 2);
                    _Controlador.sendApdu(apduInput);
                }

                Estatus(R.string.sending, command);
            } else {
                Hashtable<String, Object> apduInput = new Hashtable<String, Object>();
                apduInput.put("apdu", command);
                apduInput.put("apduLength", command.length() / 2);
                _Controlador.sendApdu(apduInput);
            }
        } catch (Exception e) {
            Estatus(e.getMessage());
            StackTraceElement[] elements = e.getStackTrace();
            for (int i = 0; i < elements.length; ++i) {
                Estatus(elements[i].toString());
            }
        }
    }
    public static void handleApduResult(boolean isSuccess, String apdu, int apduLength) {
        // dismissDialog();
        try {
            if (isSuccess) {
                if (isApduEncrypted) {
                    String key;
                    if (keyMode.equals(DATA_KEY)) {
                        key = DUKPTServer.GetDataKey(ksn, "0123456789ABCDEFFEDCBA9876543210");
                    } else if (keyMode.equals(DATA_KEY_VAR)) {
                        key = DUKPTServer.GetDataKeyVar(ksn, "0123456789ABCDEFFEDCBA9876543210");
                    } else {
                        key = DUKPTServer.GetPinKeyVar(ksn, "0123456789ABCDEFFEDCBA9876543210");
                    }

                    if (encryptionMode.equals(CBC)) {
                        apdu = TripleDES.decrypt_CBC(apdu, key);
                    } else {
                        apdu = TripleDES.decrypt(apdu, key);
                    }

                    if (apduLength == 0) {
                        int padding = Integer.parseInt(apdu.substring(apdu.length() - 2));
                        apdu = apdu.substring(0, apdu.length() - padding * 2);
                    } else {
                        apdu = apdu.substring(0, apduLength * 2);
                    }
                }

                Estatus(R.string.apdu_result, apdu);

                if (apdu.startsWith("61") && apdu.length() == 4) {
                    sendApdu("00C00000" + apdu.substring(2));
                    return;
                }

                if (state == Status.GETTING_PSE) {
                    if (apdu.endsWith("9000")) {
                        List<TLV> tlvList = TLVParser.parse(apdu.substring(0, apdu.length() - 4));
                        TLV tlv = TLVParser.searchTLV(tlvList, "88");
                        if (tlv != null && tlv.value.equals("01")) {
                            state = Status.READING_RECORD;
                            sendApdu("00B2010C00");
                            // setStatus("Reading record...");
                        }
                    } else if (apdu.equalsIgnoreCase("6A82")) {
                        aidCounter = 0;
                        state = Status.READING_AID;
                        sendApdu("00A40400" + toHexString((byte) (aids[aidCounter].length() / 2)) + aids[aidCounter]);
                        //Estatus("Error al obtener el PSE");
                        // Estatus(R.string.read_aid, aids[aidCounter]);
                    }
                } else if (state == Status.READING_RECORD) {
                    if (apdu.endsWith("9000")) {
                        List<TLV> tlvList = TLVParser.parse(apdu.substring(0, apdu.length() - 4));
                        TLV tlv = TLVParser.searchTLV(tlvList, "4F");
                        if (tlv != null) {
                            state = Status.READING_AID;
                            sendApdu("00A40400" + tlv.length + tlv.value);
                            // setStatus("Reading AID...");
                        }
                    }
                } else if (state == Status.READING_AID) {
                    if (apdu.endsWith("9000")) {
                        List<TLV> tlvList = TLVParser.parse(apdu.substring(0, apdu.length() - 4));
                        TLV tlv = TLVParser.searchTLV(tlvList, "9F38");
                        state = Status.GETTING_PROCESS_OPTION;
                        String command = "80A800000283";
                        if (tlv != null) {
                            int len = 0;
                            List<TLV> challenges = TLVParser.parseWithoutValue(tlv.value);
                            for (int i = 0; i < challenges.size(); ++i) {
                                len += Integer.parseInt(challenges.get(i).length);
                            }

                            command = "80A80000" + toHexString((byte) (len + 2)) + "83" + toHexString((byte) len);
                            for (int i = 0; i < len; ++i) {
                                command += "00";
                            }
                        } else {
                            command += "00";
                        }

                        sendApdu(command);
                        // setStatus("Getting Process Option...");
                    } else if (apdu.equalsIgnoreCase("6A82")) {
                        ++aidCounter;
                        if (aidCounter < aids.length) {
                            sendApdu("00A40400" + toHexString((byte) (aids[aidCounter].length() / 2)) + aids[aidCounter]);
                        } else {
                            Estatus(R.string.no_aid_matched);
                        }
                        // setStatus("Read AID failed");
                        // setStatus("Trying to read AID " + aids[aidCounter] +
                        // "...");
                    }
                } else if (state == Status.GETTING_PROCESS_OPTION) {
                    if (apdu.endsWith("9000")) {
                        List<TLV> tlvList = TLVParser.parse(apdu.substring(0, apdu.length() - 4));
                        TLV tlv = TLVParser.searchTLV(tlvList, "94");
                        if (tlv != null) {
                            aflCounter = 0;
                            afls = new String[tlv.value.length() / 8];
                            for (int i = 0; i < afls.length; ++i) {
                                afls[i] = tlv.value.substring(i * 8, i * 8 + 8);
                            }
                            readingFileIndex = Integer.parseInt(afls[aflCounter].substring(2, 4), 16);
                            total = Integer.parseInt(afls[aflCounter].substring(4, 6), 16);
                            sfi = toHexString((byte) (((Integer.parseInt(afls[aflCounter].substring(0, 2), 16) & 0xF8) | 0x04)));

                            state = Status.READING_DATA;

                            sendApdu("00B2" + toHexString((byte) readingFileIndex) + sfi + "00");

                            // setStatus("Reading record...");
                        } else if (apdu.startsWith("80")) {
                            afls = new String[(apdu.length() - 12) / 8];
                            for (int i = 0; i < afls.length; ++i) {
                                afls[i] = apdu.substring(i * 8 + 8, i * 8 + 16);
                            }

                            aflCounter = 0;
                            readingFileIndex = Integer.parseInt(afls[aflCounter].substring(2, 4), 16);
                            total = Integer.parseInt(afls[aflCounter].substring(4, 6), 16);
                            sfi = toHexString((byte) (((Integer.parseInt(afls[aflCounter].substring(0, 2), 16) & 0xF8) | 0x04)));

                            state = Status.READING_DATA;

                            sendApdu("00B2" + toHexString((byte) readingFileIndex) + sfi + "00");
                            // setStatus("Reading record...");
                        }
                    }
                } else if (state == Status.READING_DATA) {
                    if (apdu.endsWith("9000")) {
                        List<TLV> tlvList = TLVParser.parse(apdu.substring(0, apdu.length() - 4));
                        TLV tlv;
                        tlv = TLVParser.searchTLV(tlvList, "5F20");
                        if (tlv != null) {
                            cardholderName = new String(hexToByteArray(tlv.value));
                        }

                        tlv = TLVParser.searchTLV(tlvList, "5F24");
                        if (tlv != null) {
                            expiryDate = tlv.value;
                        }

                        tlv = TLVParser.searchTLV(tlvList, "57");
                        if (tlv != null) {
                            track2 = tlv.value;
                        }

                        tlv = TLVParser.searchTLV(tlvList, "5A");
                        if (tlv != null) {
                            pan = tlv.value;
                        }

                        if (!cardholderName.equals("") && !expiryDate.equals("") && !track2.equals("") && !pan.equals("")) {
                            Estatus("");
                            Estatus("Cardholder Name: " + cardholderName);
                            Estatus("Expire Date: " + expiryDate);
                            Estatus("Track 2: " + track2);
                            Estatus("PAN: " + pan);
                            if (startTime != 0) {
                                Estatus((System.currentTimeMillis() - startTime) + "ms");
                                startTime = 0;
                            }
                            return;
                        }

                        ++readingFileIndex;
                        if (readingFileIndex <= total) {
                            sendApdu("00B2" + toHexString((byte) readingFileIndex) + sfi + "00");
                        } else if (aflCounter < afls.length - 1) {
                            ++aflCounter;
                            readingFileIndex = Integer.parseInt(afls[aflCounter].substring(2, 4), 16);
                            total = Integer.parseInt(afls[aflCounter].substring(4, 6), 16);
                            sfi = toHexString((byte) (((Integer.parseInt(afls[aflCounter].substring(0, 2), 16) & 0xF8) | 0x04)));

                            state = Status.READING_DATA;

                            sendApdu("00B2" + toHexString((byte) readingFileIndex) + sfi + "00");
                            // setStatus("Reading record...");
                        }
                    }
                }
				/*
				 * ++count; if(count < apduCommands.length) {
				 *
				 * //setStatus(getString(R.string.sending) + apduCommands[count]); //emvSwipeController.sendApdu(apduCommands[count], apduCommands[count].length() / 2);
				 *
				 * setStatus(getString(R.string.sending) + apduCommands[count]);
				 *
				 * String command = apduCommands[count]; while((command.length() / 2) % 8 != 0) { command = command + "00"; } String encryptedCommand = TripleDES.encrypt_CBC(command, key); emvSwipeController.sendApdu(encryptedCommand, apduCommands[count].length() / 2); }
				 */
            } else {
                Estatus(R.string.apdu_failed);
            }
        } catch (Exception e) {
            Estatus(e.getMessage());
            StackTraceElement[] elements = e.getStackTrace();
            for (int i = 0; i < elements.length; ++i) {
                Estatus(elements[i].toString());
            }
        }
    }

    // PIN
    private static String _PIN_COMPONENTE = "5220D75FECCB3D6A7D921DC8DD112646";
    private static String _PIN_VERIFICA01 = "03D4CF";
    private static String _PIN_VERIFICA02 = "2A7049"; // Combinado

    // EMV
    private static String _EMV_COMPONENTE = "B6CC4487BF8B8F96BCA8DD7DCEBCE3C1";
    private static String _EMV_VERIFICA01 = "74E078";
    private static String _EMV_VERIFICA02 = "303969"; // Combinado

    // MSR
    private static String _MSR_COMPONENTE = "16C070F56912CD9E22E282FC3D929A71";
    private static String _MSR_VERIFICA01 = "69F00C";
    private static String _MSR_VERIFICA02 = "5856F9"; // Combinado

    // MAC
    private static String _MAC_COMPONENTE = ""; // 16 Bytes A
    private static String _MAC_VERIFICA01 = ""; // 3 Bytes A
    private static String _MAC_VERIFICA02 = ""; // 3 Bytes A - Combinado



    public static void injectNextSessionKey() {

        if (!_PIN_COMPONENTE.equals("")) {
            Hashtable<String, String> datos = new Hashtable<String, String>();
            datos.put("index", "1");
            datos.put("encSK", _PIN_COMPONENTE);
            datos.put("kcv", _PIN_VERIFICA01);
            Estatus(R.string.sending_encrypted_pin_session_key);
            _PIN_COMPONENTE = "";
            _Controlador.injectSessionKey(datos);
            return;
        }

        if (!_EMV_COMPONENTE.equals("")) {
            Hashtable<String, String> datos = new Hashtable<String, String>();
            datos.put("index", "2");
            datos.put("encSK", _EMV_COMPONENTE);
            datos.put("kcv", _EMV_VERIFICA01);
            Estatus(R.string.sending_encrypted_data_session_key);
            _EMV_COMPONENTE = "";
            _Controlador.injectSessionKey(datos);
            return;
        }

        if (!_MSR_COMPONENTE.equals("")) {
            Hashtable<String, String> datos = new Hashtable<String, String>();
            datos.put("index", "3");
            datos.put("encSK", _MSR_COMPONENTE);
            datos.put("kcv", _MSR_VERIFICA01);
            Estatus(R.string.sending_encrypted_track_session_key);
            _MSR_COMPONENTE = "";
            _Controlador.injectSessionKey(datos);
            return;
        }

        if (!_MAC_COMPONENTE.equals("")) {
            Hashtable<String, String> datos = new Hashtable<String, String>();
            datos.put("index", "4");
            datos.put("encSK", _MAC_COMPONENTE);
            datos.put("kcv", _MAC_VERIFICA01);
            Estatus(R.string.sending_encrypted_mac_session_key);
            _MAC_COMPONENTE = "";
            _Controlador.injectSessionKey(datos);
            return;
        }

    }
    public static String hexString2AsciiString(String hexString) {
        if (hexString == null)
            return "";
        hexString = hexString.replaceAll(" ", "");
        if (hexString.length() % 2 != 0) {
            return "";
        }
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hexString.length(); i+=2) {
            String str = hexString.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    // METODO PRINCIPAL DE CONEXION
    public void Conectar(String Dispositivo, ISistemaBBPos Interfaz){
        if(_Dialogo!=null){
            if(!_Dialogo.isVisible()){
                _Dialogo.show(((AppCompatActivity) _Contexto).getSupportFragmentManager(), _Dispositivo);
            }
        }


        if(Interfaz!=null){
            _ISistemaBBPos = Interfaz;
        }

        if(_Conectado != true){
            Estatus("Antes de conectar; No esta conectado.");
            // return;
        }else{
            // si ya esta conectado, probar si esta disponible
            Estatus("Antes de conectar; Ya esta conectado.");
            _ISistemaBBPos.alConectarDispositivo(_Dispositivo);
            return;
        }

        _Dispositivo = Dispositivo;

        Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
        // Aqui se busca entre los dispositivos ya emparejados
        for (int i = 0; i < pairedObjects.length; ++i) {
            BluetoothDevice dispositivo = (BluetoothDevice) pairedObjects[i];
            if(dispositivo.getName().equals(_Dispositivo) || dispositivo.getName().equals(_Dispositivo.concat("-LE")) ){
                _Controlador.connectBT(dispositivo);
                _Conectando = true;
                break;
            }
        }

        _Dialogo.Gif(R.drawable.switch_hand);
        _Dialogo.Estado("Esperando dispositivo");

        if(!_Conectado && !_Conectando){
            _Controlador.startBTScan(new String[] {_Dispositivo}, 120);
        }


    }

    // CONSTRUCTOR PRINCIPAL
    public SistemaBBPos(Context Contexto){
        _Contexto = Contexto;
        _Modo = BBDeviceController.CheckCardMode.SWIPE_OR_INSERT;
        if (_Controlador == null){
            _Escuchador = new BBPosEscuchador();
            _Controlador = BBDeviceController.getInstance(_Contexto, _Escuchador);
            BBDeviceController.setDebugLogEnabled(true);
            _Controlador.setDetectAudioDevicePlugged(true);
        }
        _Dialogo = new DialogoEstadoDispositivo();
    }

    public void IniciarPago(Float Monto){

    }

    public interface ISistemaBBPos {
        void alError (Error error);
        void alConectarDispositivo (String ID);
        void alLeerDispositivo (Hashtable<String, Object> Datos);
        void alCambiarEstatus (String Estatus);
    }

    public static String Estatus(){
        return _Estatus; // Retorna el historico de estatus
    }

    public static void Estatus(String Estatus) {
        // Agregar estatus al log historico
        _Estatus += "\n" + Estatus;
        if(_Dialogo!=null){
            if(_Dialogo.isVisible()){
                _Dialogo.Estado(Estatus);
            }

        }
        // showAllBanners estatus
        // Toast.makeText(_Contexto, Estatus, Toast.LENGTH_SHORT).show();
        if (_ISistemaBBPos != null){
            _ISistemaBBPos.alCambiarEstatus(Estatus);
        }
    }

    protected static void Estatus(Integer Recurso, String... Parametros){
        String estatus = _Contexto.getString(Recurso);
        Integer contador = 0;
        for (String parametro: Parametros) {
            estatus = estatus.replace("{" + contador.toString() + "}", parametro);
            contador += 1;
        }
        if(contador < Parametros.length){
            Log.w("BBPos", "Hay mas parametros que patrones" + Estatus());
        }
        Estatus(estatus);
    }

    class BBPosEscuchador implements BBDeviceController.BBDeviceControllerListener {

        @Override
        public void onWaitingForCard(BBDeviceController.CheckCardMode checkCardMode) {
            //dismissDialog();
            switch (checkCardMode) {
                case INSERT:
                    //Estatus(R.string.please_insert_card);
                    break;
                case SWIPE:
                    //Estatus(R.string.please_swipe_card);
                    break;
                case SWIPE_OR_INSERT:
                    //Estatus(R.string.please_swipe_insert_card);
                    break;
                case TAP:
                    //Estatus(R.string.please_tap_card);
                    break;
                case SWIPE_OR_TAP:
                    //Estatus(R.string.please_swipe_tap_card);
                    break;
                case INSERT_OR_TAP:
                    //Estatus(R.string.please_insert_tap_card);
                    break;
                case SWIPE_OR_INSERT_OR_TAP:
                    // Estatus(R.string.please_swipe_insert_tap_card);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onWaitingReprintOrPrintNext() {
            Estatus(R.string.please_press_reprint_or_print_next);
            // statusEditText.setText(statusEditText.getText() + "\n" + getString(R.string.please_press_reprint_or_print_next));
        }

        @Override
        public void onBTConnected(BluetoothDevice bluetoothDevice) {
            _Conectado = true;
            Estatus(R.string.dongle_conectado, bluetoothDevice.getAddress());
            if(_ISistemaBBPos != null){
                _ISistemaBBPos.alConectarDispositivo(bluetoothDevice.getName());
            }
        }

        @Override
        public void onBTDisconnected() {
            Estatus(R.string.bluetooth_disconnected);
        }

        @Override
        public void onBTReturnScanResults(List<BluetoothDevice> Dispositivos) {

            for (int i = 0; i < Dispositivos.size(); ++i) {
                if(Dispositivos.get(i).getName().equals(_Dispositivo) || Dispositivos.get(i).getName().equals(_Dispositivo.concat("-LE")) ){
                    _Controlador.stopBTScan();
                    _Controlador.connectBT(Dispositivos.get(i));
                    break;
                }
            }
        }

        @Override
        public void onBTScanStopped() {
            Estatus(R.string.bluetooth_scan_stopped);
        }

        @Override
        public void onBTScanTimeout() {
            Estatus(R.string.bluetooth_scan_timeout);
        }

        @Override
        public void onReturnCheckCardResult(BBDeviceController.CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
            // dismissDialog();
            // Estatus("" + checkCardResult + "\n");
            if(checkCardResult == BBDeviceController.CheckCardResult.NO_CARD) {
            } else if(checkCardResult == BBDeviceController.CheckCardResult.INSERTED_CARD) {
            } else if(checkCardResult == BBDeviceController.CheckCardResult.NOT_ICC) {
            } else if(checkCardResult == BBDeviceController.CheckCardResult.BAD_SWIPE) {
            } else if(checkCardResult == BBDeviceController.CheckCardResult.MSR) {
                String formatID = decodeData.get("formatID");
                final String maskedPAN = decodeData.get("maskedPAN");
                String PAN = decodeData.get("PAN");
                final String expiryDate = decodeData.get("expiryDate");
                final String cardHolderName = decodeData.get("cardholderName");
                String ksn = decodeData.get("ksn");
                String serviceCode = decodeData.get("serviceCode");
                String track1Length = decodeData.get("track1Length");
                String track2Length = decodeData.get("track2Length");
                String track3Length = decodeData.get("track3Length");
                String encTracks = decodeData.get("encTracks");
                String encTrack1 = decodeData.get("encTrack1");
                String encTrack2 = decodeData.get("encTrack2");
                String encTrack3 = decodeData.get("encTrack3");
                String track1Status = decodeData.get("track1Status");
                String track2Status = decodeData.get("track2Status");
                String track3Status = decodeData.get("track3Status");
                String partialTrack = decodeData.get("partialTrack");
                String productType = decodeData.get("productType");
                String trackEncoding = decodeData.get("trackEncoding");
                String randomNumber = decodeData.get("randomNumber");
                String finalMessage = decodeData.get("finalMessage");
                String encWorkingKey = decodeData.get("encWorkingKey");
                String mac = decodeData.get("mac");
                String serialNumber = decodeData.get("serialNumber");
                String bID = decodeData.get("bID");
                String posEntryMode = decodeData.get("posEntryMode");

                String content = "" + checkCardResult + "\n";
                content += _Contexto.getString(R.string.format_id) + " " + formatID + "\n";
                content += _Contexto.getString(R.string.masked_pan) + " " + maskedPAN + "\n";
                content += _Contexto.getString(R.string.pan) + " " + PAN + "\n";
                content += _Contexto.getString(R.string.expiry_date) + " " + expiryDate + "\n";
                content += _Contexto.getString(R.string.cardholder_name) + " " + cardHolderName + "\n";
                content += _Contexto.getString(R.string.ksn) + " " + ksn + "\n";
                content += _Contexto.getString(R.string.service_code) + " " + serviceCode + "\n";
                content += _Contexto.getString(R.string.track_1_length) + " " + track1Length + "\n";
                content += _Contexto.getString(R.string.track_2_length) + " " + track2Length + "\n";
                content += _Contexto.getString(R.string.track_3_length) + " " + track3Length + "\n";
                content += _Contexto.getString(R.string.encrypted_tracks) + " " + encTracks + "\n";
                content += _Contexto.getString(R.string.encrypted_track_1) + " " + encTrack1 + "\n";
                content += _Contexto.getString(R.string.encrypted_track_2) + " " + encTrack2 + "\n";
                content += _Contexto.getString(R.string.encrypted_track_3) + " " + encTrack3 + "\n";
                content += _Contexto.getString(R.string.track_1_status) + " " + track1Status + "\n";
                content += _Contexto.getString(R.string.track_2_status) + " " + track2Status + "\n";
                content += _Contexto.getString(R.string.track_3_status) + " " + track3Status + "\n";
                content += _Contexto.getString(R.string.partial_track) + " " + partialTrack + "\n";
                content += _Contexto.getString(R.string.product_type) + " " + productType + "\n";
                content += _Contexto.getString(R.string.track_encoding) + " " + trackEncoding + "\n";
                content += _Contexto.getString(R.string.random_number) + " " + randomNumber + "\n";
                content += _Contexto.getString(R.string.final_message) + " " + finalMessage + "\n";
                content += _Contexto.getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";
                content += _Contexto.getString(R.string.mac) + " " + mac + "\n";
                if ((decodeData != null) && (decodeData.containsKey("data"))) {
                    content += _Contexto.getString(R.string.data) + decodeData.get("data");
                }

                if ((serialNumber != null) && (!serialNumber.equals(""))) {
                    content += _Contexto.getString(R.string.serial_number) + serialNumber + "\n";
                }

                if ((bID != null) && (!bID.equals(""))) {
                    content += _Contexto.getString(R.string.b_id) + "  :" + bID + "\n";
                }

                if ((posEntryMode != null) && (!posEntryMode.equals(""))) {
                    content += _Contexto.getString(R.string.pos_entry_mode) + "  :" + posEntryMode + "\n";
                }

                Estatus(content);
            } else if(checkCardResult == BBDeviceController.CheckCardResult.MAG_HEAD_FAIL) {
            } else if(checkCardResult == BBDeviceController.CheckCardResult.USE_ICC_CARD) {
                String content = "" + checkCardResult + "\n";

                if(decodeData != null) {
                    String formatID = decodeData.get("formatID");
                    final String maskedPAN = decodeData.get("maskedPAN");
                    String PAN = decodeData.get("PAN");
                    final String expiryDate = decodeData.get("expiryDate");
                    final String cardHolderName = decodeData.get("cardholderName");
                    String ksn = decodeData.get("ksn");
                    String serviceCode = decodeData.get("serviceCode");
                    String track1Length = decodeData.get("track1Length");
                    String track2Length = decodeData.get("track2Length");
                    String track3Length = decodeData.get("track3Length");
                    String encTracks = decodeData.get("encTracks");
                    String encTrack1 = decodeData.get("encTrack1");
                    String encTrack2 = decodeData.get("encTrack2");
                    String encTrack3 = decodeData.get("encTrack3");
                    String track1Status = decodeData.get("track1Status");
                    String track2Status = decodeData.get("track2Status");
                    String track3Status = decodeData.get("track3Status");
                    String partialTrack = decodeData.get("partialTrack");
                    String productType = decodeData.get("productType");
                    String trackEncoding = decodeData.get("trackEncoding");
                    String randomNumber = decodeData.get("randomNumber");
                    String encWorkingKey = decodeData.get("encWorkingKey");
                    String mac = decodeData.get("mac");

                    content += _Contexto.getString(R.string.format_id) + " " + formatID + "\n";
                    content += _Contexto.getString(R.string.masked_pan) + " " + maskedPAN + "\n";
                    content += _Contexto.getString(R.string.pan) + " " + PAN + "\n";
                    content += _Contexto.getString(R.string.expiry_date) + " " + expiryDate + "\n";
                    content += _Contexto.getString(R.string.cardholder_name) + " " + cardHolderName + "\n";
                    content += _Contexto.getString(R.string.ksn) + " " + ksn + "\n";
                    content += _Contexto.getString(R.string.service_code) + " " + serviceCode + "\n";
                    content += _Contexto.getString(R.string.track_1_length) + " " + track1Length + "\n";
                    content += _Contexto.getString(R.string.track_2_length) + " " + track2Length + "\n";
                    content += _Contexto.getString(R.string.track_3_length) + " " + track3Length + "\n";
                    content += _Contexto.getString(R.string.encrypted_tracks) + " " + encTracks + "\n";
                    content += _Contexto.getString(R.string.encrypted_track_1) + " " + encTrack1 + "\n";
                    content += _Contexto.getString(R.string.encrypted_track_2) + " " + encTrack2 + "\n";
                    content += _Contexto.getString(R.string.encrypted_track_3) + " " + encTrack3 + "\n";
                    content += _Contexto.getString(R.string.track_1_status) + " " + track1Status + "\n";
                    content += _Contexto.getString(R.string.track_2_status) + " " + track2Status + "\n";
                    content += _Contexto.getString(R.string.track_3_status) + " " + track3Status + "\n";
                    content += _Contexto.getString(R.string.partial_track) + " " + partialTrack + "\n";
                    content += _Contexto.getString(R.string.product_type) + " " + productType + "\n";
                    content += _Contexto.getString(R.string.track_encoding) + " " + trackEncoding + "\n";
                    content += _Contexto.getString(R.string.random_number) + " " + randomNumber + "\n";
                    content += _Contexto.getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";
                    content += _Contexto.getString(R.string.mac) + " " + mac + "\n";
                }
                Estatus(content);
            } else if (checkCardResult == BBDeviceController.CheckCardResult.TAP_CARD_DETECTED) {
            } else if (checkCardResult == BBDeviceController.CheckCardResult.MANUAL_PAN_ENTRY) {
                String content = "" + checkCardResult + "\n";
                if (decodeData != null) {
                    Object[] keys = decodeData.keySet().toArray();
                    Arrays.sort(keys);
                    for (Object key : keys) {
                        content += "\n" + key + " : ";
                        Object obj = decodeData.get(key);
                        if (obj instanceof String) {
                            content += (String)obj;
                        } else if (obj instanceof Boolean) {
                            content += obj + "";
                        }
                    }
                    Estatus(content);
                }
            }
        }

        @Override
        public void onReturnCancelCheckCardResult(boolean isSuccess) {
            if (isSuccess) {
                Estatus(R.string.cancel_check_card_success);
            } else {
                Estatus(R.string.cancel_check_card_fail);
            }
        }

        @Override
        public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
            //dismissDialog();
            //uid = deviceInfoData.get("uid");
            String isSupportedTrack1 = deviceInfoData.get("isSupportedTrack1");
            String isSupportedTrack2 = deviceInfoData.get("isSupportedTrack2");
            String isSupportedTrack3 = deviceInfoData.get("isSupportedTrack3");
            String bootloaderVersion = deviceInfoData.get("bootloaderVersion");
            String firmwareVersion = deviceInfoData.get("firmwareVersion");
            String mainProcessorVersion = deviceInfoData.get("mainProcessorVersion");
            String coprocessorVersion = deviceInfoData.get("coprocessorVersion");
            String coprocessorBootloaderVersion = deviceInfoData.get("coprocessorBootloaderVersion");
            String isUsbConnected = deviceInfoData.get("isUsbConnected");
            String isCharging = deviceInfoData.get("isCharging");
            String batteryLevel = deviceInfoData.get("batteryLevel");
            String batteryPercentage = deviceInfoData.get("batteryPercentage");
            String hardwareVersion = deviceInfoData.get("hardwareVersion");
            String productId = deviceInfoData.get("productId");
            String pinKsn = deviceInfoData.get("pinKsn");
            String emvKsn = deviceInfoData.get("emvKsn");
            String trackKsn = deviceInfoData.get("trackKsn");
            String terminalSettingVersion = deviceInfoData.get("terminalSettingVersion");
            String deviceSettingVersion = deviceInfoData.get("deviceSettingVersion");
            String formatID = deviceInfoData.get("formatID");
            String vendorID = deviceInfoData.get("vendorID");
            String csn = deviceInfoData.get("csn");
            String uid = deviceInfoData.get("uid");
            String serialNumber = deviceInfoData.get("serialNumber");
            String modelName = deviceInfoData.get("modelName");
            String macKsn = deviceInfoData.get("macKsn");
            String nfcKsn = deviceInfoData.get("nfcKsn");
            String messageKsn = deviceInfoData.get("messageKsn");
            String bID = deviceInfoData.get("bID");
            String publicKeyVersion = deviceInfoData.get("publicKeyVersion");

            String vendorIDAscii = "";
            if ((vendorID != null) && (!vendorID.equals(""))) {
                if (!vendorID.substring(0, 2).equalsIgnoreCase("00")) {
                    vendorIDAscii = hexString2AsciiString(vendorID);
                }
            }

            String content = "";
            content += _Contexto.getString(R.string.format_id) + formatID + "\n";
            content += _Contexto.getString(R.string.vendor_id_hex) + vendorID + "\n";
            content += _Contexto.getString(R.string.vendor_id_ascii) + vendorIDAscii + "\n";
            content += _Contexto.getString(R.string.bootloader_version) + bootloaderVersion + "\n";
            content += _Contexto.getString(R.string.firmware_version) + firmwareVersion + "\n";
            content += _Contexto.getString(R.string.main_processor_version) + mainProcessorVersion + "\n";
            content += _Contexto.getString(R.string.coprocessor_version) + coprocessorVersion + "\n";
            content += _Contexto.getString(R.string.coprocessor_bootloader_version) + coprocessorBootloaderVersion + "\n";
            content += _Contexto.getString(R.string.usb) + isUsbConnected + "\n";
            content += _Contexto.getString(R.string.charge) + isCharging + "\n";
            content += _Contexto.getString(R.string.battery_level) + batteryLevel + "\n";
            content += _Contexto.getString(R.string.battery_percentage) + batteryPercentage + "\n";
            content += _Contexto.getString(R.string.hardware_version) + hardwareVersion + "\n";
            content += _Contexto.getString(R.string.track_1_supported) + isSupportedTrack1 + "\n";
            content += _Contexto.getString(R.string.track_2_supported) + isSupportedTrack2 + "\n";
            content += _Contexto.getString(R.string.track_3_supported) + isSupportedTrack3 + "\n";
            content += _Contexto.getString(R.string.product_id) + productId + "\n";
            content += _Contexto.getString(R.string.pin_ksn) + pinKsn + "\n";
            content += _Contexto.getString(R.string.emv_ksn) + emvKsn + "\n";
            content += _Contexto.getString(R.string.track_ksn) + trackKsn + "\n";
            content += _Contexto.getString(R.string.terminal_setting_version) + terminalSettingVersion + "\n";
            content += _Contexto.getString(R.string.device_setting_version) + deviceSettingVersion + "\n";
            content += _Contexto.getString(R.string.csn) + csn + "\n";
            content += _Contexto.getString(R.string.uid) + uid + "\n";
            content += _Contexto.getString(R.string.serial_number) + serialNumber + "\n";
            if ((modelName != null) && (!modelName.equals(""))) {
                content += _Contexto.getString(R.string.model_name) + modelName + "\n";
            }

            if ((macKsn != null) && (!macKsn.equals(""))) {
                content += _Contexto.getString(R.string.mac_ksn) + macKsn + "\n";
            }
            if ((nfcKsn != null) && (!nfcKsn.equals(""))) {
                content += _Contexto.getString(R.string.nfc_ksn) + nfcKsn + "\n";
            }
            if ((messageKsn != null) && (!messageKsn.equals(""))) {
                content += _Contexto.getString(R.string.message_ksn) + messageKsn + "\n";
            }
            if ((bID != null) && (!bID.equals(""))) {
                content += _Contexto.getString(R.string.b_id) + "  :" + bID + "\n";
            }
            if ((publicKeyVersion != null) && (!publicKeyVersion.equals(""))) {
                content += _Contexto.getString(R.string.public_key_version) + "  :" + publicKeyVersion + "\n";
            }

            Estatus(content);

            if (formatID.equals("46")) {
                //fidSpinner.setSelection(2);
            } else if (formatID.equals("61")) {
                //fidSpinner.setSelection(6);
            } else if (formatID.equals("65")) {
                //fidSpinner.setSelection(8);
            } else {
                //fidSpinner.setSelection(5);
            }
        }

        @Override
        public void onReturnTransactionResult(BBDeviceController.TransactionResult transactionResult) {
            // dismissDialog();
            //dialog = new Dialog(currentActivity);
            //dialog.setContentView(R.layout.alert_dialog);
            //dialog.setTitle(R.string.transaction_result);
            //TextView messageTextView = (TextView) dialog.findViewById(R.id.messageTextView);

            String message = "" + transactionResult + "\n";
            if (transactionResult == BBDeviceController.TransactionResult.APPROVED) {
                // message = _Contexto.getString(R.string.amount) + ": $" + amount + "\n";
                //if (!cashbackAmount.equals("")) {
                //    message += getString(R.string.cashback_amount) + ": $" + cashbackAmount;
                //}
            }

            //messageTextView.setText(message);

            //amount = "";
            //cashbackAmount = "";
            //amountEditText.setText("");
            /*
            dialog.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismissDialog();
                }
            });

            dialog.show();
            */
        }

        @Override
        public void onReturnBatchData(String tlv) {
            //dismissDialog();
            String content = _Contexto.getString(R.string.batch_data) + "\n";
            Hashtable<String, String> decodeData = BBDeviceController.decodeTlv(tlv);
            Object[] keys = decodeData.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                String value = decodeData.get(key);
                content += key + ": " + value + "\n";
            }
            Estatus(content);
        }

        @Override
        public void onReturnReversalData(String tlv) {
            //dismissDialog();
            String content = _Contexto.getString(R.string.reversal_data);
            content += tlv;
            Estatus(content);
        }

        @Override
        public void onReturnAmountConfirmResult(boolean isSuccess) {
            if (isSuccess) {
                Estatus(R.string.amount_confirmed);
            } else {
                Estatus(R.string.amount_canceled);
            }
        }

        @Override
        public void onReturnPinEntryResult(BBDeviceController.PinEntryResult pinEntryResult, Hashtable<String, String> data) {
            if (pinEntryResult == BBDeviceController.PinEntryResult.ENTERED) {
                String content = _Contexto.getString(R.string.pin_entered);
                if (data.containsKey("epb")) {
                    content += "\n" + _Contexto.getString(R.string.epb) + data.get("epb");
                }
                if (data.containsKey("ksn")) {
                    content += "\n" + _Contexto.getString(R.string.ksn) + data.get("ksn");
                }
                if (data.containsKey("randomNumber")) {
                    content += "\n" + _Contexto.getString(R.string.random_number) + data.get("randomNumber");
                }
                if (data.containsKey("encWorkingKey")) {
                    content += "\n" + _Contexto.getString(R.string.encrypted_working_key) + data.get("encWorkingKey");
                }

                Estatus(content);
            } else if (pinEntryResult == BBDeviceController.PinEntryResult.BYPASS) {
                Estatus(R.string.pin_bypassed);
            } else if (pinEntryResult == BBDeviceController.PinEntryResult.CANCEL) {
                Estatus(R.string.pin_canceled);
            } else if (pinEntryResult == BBDeviceController.PinEntryResult.TIMEOUT) {
                Estatus(R.string.pin_timeout);
            }
        }

        @Override
        public void onReturnPrintResult(BBDeviceController.PrintResult printResult) {
            Estatus("" + printResult);
        }

        @Override
        public void onReturnAmount(Hashtable<String, String> data) {
            String amount = data.get("amount");
            String cashbackAmount = data.get("cashbackAmount");
            String currencyCode = data.get("currencyCode");

            String text = "";
            text += _Contexto.getString(R.string.amount_with_colon) + amount + "\n";
            text += _Contexto.getString(R.string.cashback_with_colon) + cashbackAmount + "\n";
            text += _Contexto.getString(R.string.currency_with_colon) + currencyCode + "\n";

            Estatus(text);
        }

        @Override
        public void onReturnUpdateTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus) {
            //dismissDialog();
            if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.SUCCESS) {
                _Contexto.getString(R.string.update_terminal_setting_success);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_FOUND) {
                _Contexto.getString(R.string.update_terminal_setting_tag_not_found);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.LENGTH_INCORRECT) {
                _Contexto.getString(R.string.update_terminal_setting_length_incorrect);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TLV_INCORRECT) {
                _Contexto.getString(R.string.update_terminal_setting_tlv_incorrect);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.BOOTLOADER_NOT_SUPPORT) {
                _Contexto.getString(R.string.update_terminal_setting_bootloader_not_support);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_ALLOWED_TO_ACCESS) {
                _Contexto.getString(R.string.update_terminal_setting_tag_not_allowed_to_change);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.USER_DEFINED_DATA_NOT_ENALBLED) {
                _Contexto.getString(R.string.update_terminal_setting_user_defined_data_not_allowed_to_change);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_WRITTEN_CORRECTLY) {
                _Contexto.getString(R.string.update_terminal_setting_tag_not_written_correctly);
            }
        }

        @Override
        public void onReturnReadTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus, String value) {
            //dismissDialog();
            if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.SUCCESS) {
                Estatus(R.string.read_terminal_setting_success, "\n" + _Contexto.getString(R.string.value) + " " + value);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_FOUND) {
                Estatus(R.string.read_terminal_setting_tag_not_found);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.LENGTH_INCORRECT) {
                Estatus(R.string.read_terminal_setting_length_incorrect);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TLV_INCORRECT) {
                Estatus(R.string.read_terminal_setting_tlv_incorrect);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.BOOTLOADER_NOT_SUPPORT) {
                Estatus(R.string.read_terminal_setting_bootloader_not_support);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_ALLOWED_TO_ACCESS) {
                Estatus(R.string.read_terminal_setting_tag_not_allowed_to_access);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.USER_DEFINED_DATA_NOT_ENALBLED) {
                Estatus(R.string.read_terminal_setting_user_defined_data_not_allowed_to_change);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_WRITTEN_CORRECTLY) {
                Estatus(R.string.read_terminal_setting_tag_not_written_correctly);
            }
        }

        @Override
        public void onReturnEnableInputAmountResult(boolean isSuccess) {
            if (isSuccess) {
                Estatus(_Contexto.getString(R.string.enable_input_amount_success));
            } else {
                Estatus(_Contexto.getString(R.string.enable_input_amount_fail));
            }
        }

        @Override
        public void onReturnDisableInputAmountResult(boolean isSuccess) {
            if (isSuccess) {
                Estatus(R.string.disable_input_amount_success);
            } else {
                Estatus(R.string.disable_input_amount_fail);
            }
        }

        @Override
        public void onReturnPhoneNumber(BBDeviceController.PhoneEntryResult phoneEntryResult, String phoneNumber) {
            if (phoneEntryResult == BBDeviceController.PhoneEntryResult.ENTERED) {
                Estatus(R.string.phone_number, phoneNumber);
            } else if (phoneEntryResult == BBDeviceController.PhoneEntryResult.TIMEOUT) {
                Estatus(R.string.timeout);
            } else if (phoneEntryResult == BBDeviceController.PhoneEntryResult.CANCEL) {
                Estatus(R.string.canceled);
            } else if (phoneEntryResult == BBDeviceController.PhoneEntryResult.WRONG_LENGTH) {
                Estatus(R.string.wrong_length);
            } else if (phoneEntryResult == BBDeviceController.PhoneEntryResult.BYPASS) {
                Estatus(R.string.bypass);
            }
        }

        @Override
        public void onReturnEmvCardDataResult(boolean isSuccess, String tlv) {
            if (isSuccess) {
                Estatus(R.string.emv_card_data_result, tlv);
            } else {
                Estatus(R.string.emv_card_data_failed);
            }
        }

        @Override
        public void onReturnEmvCardNumber(boolean isSuccess, String cardNumber) {
            Estatus(R.string.pan, cardNumber);
        }

        @Override
        public void onReturnEncryptPinResult(boolean isSuccess, Hashtable<String, String> data) {
            String ksn = data.get("ksn");
            String epb = data.get("epb");
            String randomNumber = data.get("randomNumber");
            String encWorkingKey = data.get("encWorkingKey");
            String errorMessage = data.get("errorMessage");
            String content = _Contexto.getString(R.string.ksn) + ksn + "\n";
            content += _Contexto.getString(R.string.epb) + epb + "\n";
            content += _Contexto.getString(R.string.random_number) + randomNumber + "\n";
            content += _Contexto.getString(R.string.encrypted_working_key) + encWorkingKey + "\n";
            content += _Contexto.getString(R.string.error_message) + errorMessage;
            Estatus(content);
        }

        @Override
        public void onReturnEncryptDataResult(boolean isSuccess, Hashtable<String, String> data) {
            if (isSuccess) {
                String content = "";
                if (data.containsKey("ksn")) {
                    content += _Contexto.getString(R.string.ksn) + data.get("ksn") + "\n";
                }
                if (data.containsKey("randomNumber")) {
                    content += _Contexto.getString(R.string.random_number) + data.get("randomNumber") + "\n";
                }
                if (data.containsKey("encData")) {
                    content += _Contexto.getString(R.string.encrypted_data) + data.get("encData") + "\n";
                }
                if (data.containsKey("mac")) {
                    content += _Contexto.getString(R.string.mac) + data.get("mac") + "\n";
                }
                Estatus(content);
            } else {
                Estatus(R.string.encrypt_data_failed);
            }
        }

        @Override
        public void onReturnInjectSessionKeyResult(boolean isSuccess, Hashtable<String, String> data) {
            String content;
            if (isSuccess) {
                content = _Contexto.getString(R.string.inject_session_key_success);
                if (data.size() == 0) {
                    injectNextSessionKey();
                }
            } else {
                content = _Contexto.getString(R.string.inject_session_key_failed);
                content += "\n" + _Contexto.getString(R.string.error_message) + data.get("errorMessage");
            }
            Estatus(content);
        }

        @Override
        public void onReturnApduResult(boolean isSuccess, Hashtable<String, Object> data) {
            try {
                String apdu = "";
                int apduLength = 0;

                if ((data != null) && (data.containsKey("apduLength")) && (data.get("apduLength") instanceof String)) {
                    apduLength = Integer.parseInt((String)data.get("apduLength"));
                } else if ((data != null) && (data.containsKey("apduLength")) && (data.get("apduLength") instanceof Integer)) {
                    apduLength = (Integer)data.get("apduLength");
                }

                if ((data != null) && (data.containsKey("apdu"))) {
                    apdu = (String)data.get("apdu");
                    handleApduResult(isSuccess, apdu, apduLength);
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void onReturnPowerOffIccResult(boolean isSuccess) {
            //dismissDialog();
            if (isSuccess) {
                Estatus(R.string.power_off_icc_success);
            } else {
                Estatus(R.string.power_off_icc_failed);
            }
        }

        @Override
        public void onReturnPowerOnIccResult(boolean isSuccess, String ksn, String atr, int atrLength) {
            // dismissDialog();
            if (isSuccess) {
                SistemaBBPos.ksn = ksn;

                Estatus(R.string.power_on_icc_success);
                Estatus(R.string.ksn, ksn);
                Estatus(R.string.atr, atr);
                Estatus(R.string.atr_length, Integer.valueOf(atrLength).toString() );
            } else {
                Estatus(R.string.power_on_icc_failed);
            }
        }

        @Override
        public void onRequestSelectApplication(ArrayList<String> appList) {
            //dismissDialog();

            //dialog = new Dialog(currentActivity);
            //dialog.setContentView(R.layout.application_dialog);
            //dialog.setTitle(R.string.please_select_app);

            String[] appNameList = new String[appList.size()];
            for (int i = 0; i < appNameList.length; ++i) {
                appNameList[i] = appList.get(i);
            }

            //appListView = (ListView) dialog.findViewById(R.id.appList);
            //appListView.setAdapter(new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1, appNameList));
            //appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //    @Override
            //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //       bbDeviceController.selectApplication(position);
            //        dismissDialog();
            //    }

            //});

            /*
            dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    bbDeviceController.cancelSelectApplication();
                    dismissDialog();
                }
            });
            dialog.show();
            */
        }

        @Override
        public void onRequestSetAmount() {
            String amount = _Monto.toString();

            BBDeviceController.TransactionType tipo = BBDeviceController.TransactionType.PAYMENT;
            BBDeviceController.CurrencyCharacter[] moneda = new BBDeviceController.CurrencyCharacter[] { BBDeviceController.CurrencyCharacter.M, BBDeviceController.CurrencyCharacter.X, BBDeviceController.CurrencyCharacter.N };

            String currencyCode = "840";

            if (_Controlador.setAmount(amount, "", currencyCode, tipo, moneda)) {
                Estatus("Monto por: $ " + amount);
            } else {
                onRequestSetAmount();
            }
        }

        @Override
        public void onRequestPinEntry(BBDeviceController.PinEntrySource pinEntrySource) {
            //dismissDialog();
            if (pinEntrySource == BBDeviceController.PinEntrySource.KEYPAD) {
                Estatus(R.string.enter_pin_on_keypad);
            } else {
                //dismissDialog();

                //dialog = new Dialog(currentActivity);
                //dialog.setContentView(R.layout.pin_dialog);
                //dialog.setTitle(getString(R.string.enter_pin));
                /*
                dialog.findViewById(R.id.confirmButton).setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                String pin = ((EditText) dialog.findViewById(R.id.pinEditText)).getText().toString();
                                bbDeviceController.sendPinEntryResult(pin);
                                dismissDialog();
                            }
                        });*/
                /*
                dialog.findViewById(R.id.bypassButton).setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                bbDeviceController.bypassPinEntry();
                                dismissDialog();
                            }
                        });

                dialog.findViewById(R.id.cancelButton).setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                isPinCanceled = true;
                                bbDeviceController.cancelPinEntry();
                                dismissDialog();
                            }
                        });

                dialog.show();
                */
            }
        }

        @Override
        public void onRequestOnlineProcess(String tlv) {
            String content = _Contexto.getString(R.string.request_data_to_server) + "\n";
            Hashtable<String, String> decodeData = BBDeviceController.decodeTlv(tlv);
            Object[] keys = decodeData.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                String value = decodeData.get(key);
                content += key + ": " + value + "\n";
            }
            _Controlador.sendOnlineProcessResult("8A023030");
            Estatus(content);

            // dismissDialog();
            // dialog = new Dialog(currentActivity);
            // dialog.setContentView(R.layout.alert_dialog);
            // dialog.setTitle(R.string.request_data_to_server);

            // if (isPinCanceled) {
            //     ((TextView) dialog.findViewById(R.id.messageTextView)).setText(R.string.replied_failed);
            // } else {
            //     ((TextView) dialog.findViewById(R.id.messageTextView)).setText(R.string.replied_success);
            // }

            /* dialog.findViewById(R.id.confirmButton).setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (isPinCanceled) {
                                bbDeviceController.sendOnlineProcessResult(null);
                            } else {
                                bbDeviceController.sendOnlineProcessResult("8A023030");
                            }
                            dismissDialog();
                        }
                    });

            dialog.show();*/
        }

        @Override
        public void onRequestTerminalTime() {
            String fechayhora = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
            _Controlador.sendTerminalTime(fechayhora);
        }

        @Override
        public void onRequestDisplayText(BBDeviceController.DisplayText displayText) {
            //dismissDialog();
            Estatus("" + displayText);
        }

        @Override
        public void onRequestClearDisplay() {
            //dismissDialog();
            Estatus("");
        }

        @Override
        public void onRequestFinalConfirm() {
            //dismissDialog();
            /*
            if (!isPinCanceled) {
                dialog = new Dialog(currentActivity);
                dialog.setContentView(R.layout.confirm_dialog);
                dialog.setTitle(getString(R.string.confirm_amount));

                String message = getString(R.string.amount) + ": $" + amount;
                if (!cashbackAmount.equals("")) {
                    message += "\n" + getString(R.string.cashback_amount) + ": $" + cashbackAmount;
                }

                ((TextView) dialog.findViewById(R.id.messageTextView)).setText(message);

                dialog.findViewById(R.id.confirmButton).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bbDeviceController.sendFinalConfirmResult(true);
                                dialog.dismiss();
                            }
                        });

                dialog.findViewById(R.id.cancelButton).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bbDeviceController.sendFinalConfirmResult(false);
                                dialog.dismiss();
                            }
                        });

                dialog.show();
            } else {
                bbDeviceController.sendFinalConfirmResult(false);
            }
            */
        }

        @Override
        public void onRequestPrintData(int index, boolean isReprint) {
            _Controlador.sendPrintData(receipts.get(index));
            if (isReprint) {
                Estatus(R.string.request_reprint_data, Integer.valueOf(index).toString());
            } else {
                Estatus(R.string.request_printer_data, Integer.valueOf(index).toString());
            }
        }

        @Override
        public void onPrintDataCancelled() {
            Estatus(R.string.printer_operation_cancelled);
        }

        @Override
        public void onPrintDataEnd() {
            Estatus(R.string.printer_operation_end);
        }

        @Override
        public void onBatteryLow(BBDeviceController.BatteryStatus Estado) {
            // Dar una advertencia de bateria baja con BrioAlertDialog
            if (Estado == BBDeviceController.BatteryStatus.LOW) {
                Estatus("Bateria dongle esta baja, favor de conectar USB");
            } else if (Estado == BBDeviceController.BatteryStatus.CRITICALLY_LOW) {
                Estatus("El dispositivo se apagará por que la bateria esta criticamente baja");
            }
        }

        @Override
        public void onAudioDevicePlugged() {}

        @Override
        public void onAudioDeviceUnplugged() {}

        @Override
        public void onError(BBDeviceController.Error errorState, String errorMessage) {

            //dismissDialog();
            /*
            if(progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            amountEditText.setText("");
            */
            String content = "";
            if (errorState == BBDeviceController.Error.CMD_NOT_AVAILABLE) {
                content = _Contexto.getString(R.string.command_not_available);
            } else if (errorState == BBDeviceController.Error.TIMEOUT) {
                content = _Contexto.getString(R.string.device_no_response);
            } else if (errorState == BBDeviceController.Error.UNKNOWN) {
                content = _Contexto.getString(R.string.unknown_error);
            } else if (errorState == BBDeviceController.Error.DEVICE_BUSY) {
                content = _Contexto.getString(R.string.device_busy);
            } else if (errorState == BBDeviceController.Error.INPUT_OUT_OF_RANGE) {
                content = _Contexto.getString(R.string.out_of_range);
            } else if (errorState == BBDeviceController.Error.INPUT_INVALID_FORMAT) {
                content = _Contexto.getString(R.string.invalid_format);
                Toast.makeText(_Contexto, _Contexto.getString(R.string.invalid_format), Toast.LENGTH_LONG).show();
            } else if (errorState == BBDeviceController.Error.INPUT_INVALID) {
                content = _Contexto.getString(R.string.input_invalid);
                Toast.makeText(_Contexto, _Contexto.getString(R.string.input_invalid), Toast.LENGTH_LONG).show();
            } else if (errorState == BBDeviceController.Error.CASHBACK_NOT_SUPPORTED) {
                content = _Contexto.getString(R.string.cashback_not_supported);
                Toast.makeText(_Contexto, _Contexto.getString(R.string.cashback_not_supported), Toast.LENGTH_LONG).show();
            } else if (errorState == BBDeviceController.Error.CRC_ERROR) {
                content = _Contexto.getString(R.string.crc_error);
            } else if (errorState == BBDeviceController.Error.COMM_ERROR) {
                content = _Contexto.getString(R.string.comm_error);
            } else if (errorState == BBDeviceController.Error.FAIL_TO_START_BT) {
                content = _Contexto.getString(R.string.fail_to_start_bluetooth);
            } else if (errorState == BBDeviceController.Error.FAIL_TO_START_AUDIO) {
                content = _Contexto.getString(R.string.fail_to_start_audio);
            } else if (errorState == BBDeviceController.Error.INVALID_FUNCTION_IN_CURRENT_CONNECTION_MODE) {
                content = _Contexto.getString(R.string.invalid_function);
            } else if (errorState == BBDeviceController.Error.COMM_LINK_UNINITIALIZED) {
                content = _Contexto.getString(R.string.comm_link_uninitialized);
            } else if (errorState == BBDeviceController.Error.BTV4_NOT_SUPPORTED) {
                content = _Contexto.getString(R.string.bluetooth_4_not_supported);
                Toast.makeText(_Contexto, _Contexto.getString(R.string.bluetooth_4_not_supported), Toast.LENGTH_LONG).show();
            } else if (errorState == BBDeviceController.Error.CHANNEL_BUFFER_FULL) {
                content = _Contexto.getString(R.string.channel_buffer_full);
            } else if (errorState == BBDeviceController.Error.BLUETOOTH_PERMISSION_DENIED) {
                content = _Contexto.getString(R.string.bluetooth_permission_denied);
            } else if (errorState == BBDeviceController.Error.VOLUME_WARNING_NOT_ACCEPTED) {
                content = _Contexto.getString(R.string.volume_warning_not_accepted);
            } else if (errorState == BBDeviceController.Error.FAIL_TO_START_SERIAL) {
                content = _Contexto.getString(R.string.fail_to_start_serial);
            } else if (errorState == BBDeviceController.Error.USB_DEVICE_NOT_FOUND) {
                content = _Contexto.getString(R.string.usb_device_not_found);
            } else if (errorState == BBDeviceController.Error.USB_DEVICE_PERMISSION_DENIED) {
                content = _Contexto.getString(R.string.usb_device_permission_denied);
            } else if (errorState == BBDeviceController.Error.USB_NOT_SUPPORTED) {
                content = _Contexto.getString(R.string.usb_not_supported);
            }

            if (errorMessage != null && !errorMessage.equals("")) {
                content += "\n" + _Contexto.getString(R.string.error_message) + errorMessage;
            }

            Estatus(content);
        }

        @Override
        public void onReturnCAPKList(List<CAPK> capkList) {
            String content = _Contexto.getString(R.string.capk);
            for (int i = 0; i < capkList.size(); ++i) {
                CAPK capk = capkList.get(i);
                content += "\n" + i + ": ";
                content += "\n" + _Contexto.getString(R.string.location) + capk.location;
                content += "\n" + _Contexto.getString(R.string.rid) + capk.rid;
                content += "\n" + _Contexto.getString(R.string.index) + capk.index;
                content += "\n";
            }
            Estatus(content);
        }

        @Override
        public void onReturnCAPKDetail(CAPK capk) {
            String content = _Contexto.getString(R.string.capk);
            if (capk != null) {
                content += "\n" + _Contexto.getString(R.string.location) + capk.location;
                content += "\n" + _Contexto.getString(R.string.rid) + capk.rid;
                content += "\n" + _Contexto.getString(R.string.index) + capk.index;
                content += "\n" + _Contexto.getString(R.string.exponent) + capk.exponent;
                content += "\n" + _Contexto.getString(R.string.modulus) + capk.modulus;
                content += "\n" + _Contexto.getString(R.string.checksum) + capk.checksum;
                content += "\n" + _Contexto.getString(R.string.size) + capk.size;
                content += "\n";
            } else {
                content += "\nnull \n";
            }
            Estatus(content);
        }

        @Override
        public void onReturnCAPKLocation(String location) {
            Estatus(R.string.location, location);
        }

        @Override
        public void onReturnUpdateCAPKResult(boolean isSuccess) {
            if (isSuccess) {
                Estatus(R.string.update_capk_success);
            } else {
                Estatus(R.string.update_capk_fail);
            }
        }

        @Override
        public void onReturnEmvReport(String tlv) {
            String content = _Contexto.getString(R.string.emv_report) + "\n";

            Hashtable<String, String> decodeData = BBDeviceController.decodeTlv(tlv);
            Object[] keys = decodeData.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                if (((String) key).matches(".*[a-z].*") && decodeData.containsKey(((String) key).toUpperCase(Locale.ENGLISH))) {
                    continue;
                }
                String value = decodeData.get(key);
                content += key + ": " + value + "\n";

                if (((String) key).toUpperCase(Locale.ENGLISH).equalsIgnoreCase(TagList.EMV_REPORT_TEMPLATE)) {
                    Hashtable<String, String> innerDecodeData = BBDeviceController.decodeTlv(value);
                    Object[] innerKeys = innerDecodeData.keySet().toArray();
                    Arrays.sort(innerKeys);
                    for (Object innerKey : innerKeys) {
                        if (((String) innerKey).matches(".*[a-z].*") && innerDecodeData.containsKey(((String) innerKey).toUpperCase(Locale.ENGLISH))) {
                            continue;
                        }
                        String innerValue = innerDecodeData.get(innerKey);
                        content += "\n" + innerKey + ": " + innerValue;
                    }
                }
            }

            Estatus(content);
        }

        @Override
        public void onReturnEmvReportList(Hashtable<String, String> data) {
            String content = _Contexto.getString(R.string.emv_report_list) + "\n";
            Object[] keys = data.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                String value = data.get(key);
                content += key + ": " + value + "\n";
            }

            Estatus(content);
        }

        @Override
        public void onSessionInitialized() {
            Estatus("Sesion inicializada");
        }

        @Override
        public void onSessionError(BBDeviceController.SessionError Error, String Mensaje) {
            switch(Error){
                case FIRMWARE_NOT_SUPPORTED:
                    Estatus("Firmware no soportado");
                    break;
                case INVALID_SESSION:
                    Estatus("Sesion invalida");
                    break;
                case INVALID_VENDOR_TOKEN:
                    Estatus("Token invalido");
                    break;
                case SESSION_NOT_INITIALIZED:
                    Estatus("Sesion iniciada");
                    break;
            }
            Estatus(Mensaje);
        }

        @Override
        public void onReturnReadGprsSettingsResult(boolean Sucedido, Hashtable<String, Object> Datos) {}

        @Override
        public void onReturnReadWiFiSettingsResult(boolean Sucedido, Hashtable<String, Object> Datos) {}

        @Override
        public void onReturnUpdateGprsSettingsResult(boolean Sucedido, Hashtable<String, BBDeviceController.TerminalSettingStatus> Datos) {}

        @Override
        public void onReturnUpdateWiFiSettingsResult(boolean Sucedido, Hashtable<String, BBDeviceController.TerminalSettingStatus> Datos) {}

        @Override
        public void onAudioAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings) {}

        @Override
        public void onAudioAutoConfigError(BBDeviceController.AudioAutoConfigError autoConfigError) {}

        @Override
        public void onAudioAutoConfigProgressUpdate(double percentage) {}

        @Override
        public void onDeviceHere(boolean arg0) {}

        @Override
        public void onNoAudioDeviceDetected() {}

        @Override
        public void onReturnNfcDataExchangeResult(boolean Sucedido, Hashtable<String, String> Datos) {}

        @Override
        public void onReturnNfcDetectCardResult(BBDeviceController.NfcDetectCardResult ResultadoNFC, Hashtable<String, Object> Datos) {}

        @Override
        public void onUsbConnected() {
            Estatus("USB Conectado");
        }

        @Override
        public void onUsbDisconnected() {
            Estatus("USB Desconectado");
        }

        @Override
        public void onRequestDisplayAsterisk(int Cantidad) {}

        @Override
        public void onSerialConnected() {}

        @Override
        public void onSerialDisconnected() {}

        @Override
        public void onBarcodeReaderConnected() {}

        @Override
        public void onBarcodeReaderDisconnected() {}

        @Override
        public void onReturnBarcode(String arg0) {}

        @Override
        public void onRequestDisplayLEDIndicator(BBDeviceController.ContactlessStatus arg0) {}

        @Override
        public void onRequestProduceAudioTone(BBDeviceController.ContactlessStatusTone arg0) {}

        @Override
        public void onReturnReadAIDResult(Hashtable<String, Object> Datos) {
            String texto = "Lectura de ID de Aplicacion correcta";
        }

        @Override
        public void onReturnUpdateAIDResult(Hashtable<String, BBDeviceController.TerminalSettingStatus> Datos) {
            String texto = "Actualizacion de ID de Aplicacion correcto";
        }

        @Override
        public void onDeviceReset() {
            Estatus("Dongle reseteado");
        }

        @Override
        public void onPowerButtonPressed() {
            Estatus("Boton de encendido presionado");
        }

        @Override
        public void onPowerDown() {
            Estatus("Boton de encendido abajo");
        }
    }
}