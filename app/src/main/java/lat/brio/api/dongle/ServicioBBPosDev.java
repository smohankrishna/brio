package lat.brio.api.dongle;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bbpos.bbdevice.BBDeviceController;
import com.bbpos.bbdevice.CAPK;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogoEstadoDispositivo;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class ServicioBBPosDev {

    // Para lso dispositivos validos en respuesta de escaneo
    // Ambiguedad: Aqui el api no especifica si son los nombres literales o caracteres validos en el nombre de los dispositivos
    protected static final String[] NOMBRE_DISPOSITIVOS = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

    // Indicador de si el usuario fue quien cancelo
    private Boolean _CanceloUsuario = false;

    // Modo en el que la tarjeta sera leido
    BBDeviceController.CheckCardMode _Modo;

    // Id de formato
    protected String _IDFormato = "FID60";

    // Clave funcional para el formato con el ID 65
    protected static String _ClaveFuncionalFID65 = "A1223344556677889900AABBCCDDEEFF";
    protected static String _ClaveMaestraFID65 = "0123456789ABCDEFFEDCBA9876543210";

    // Dialogo de estado de dispositivo
    protected static DialogoEstadoDispositivo _DialogoDispositivo;

    // Dispositivos nuevos escaneados
    protected static List<BluetoothDevice> _Dispositivos;

    // Contexto actual
    protected static Context _Contexto = null;

    // Bandera de etado de conexion de el dispositivo
    protected static Boolean _Conectado = false;

    // Controlador de dispositivo Dongle
    protected static BBDeviceController _Controlador;
    private final IServicioBBPos _IServicioPos;

    protected EscuchaDongle _EscuchaDongle = null;
    // Id de dispositivo a asignado
    protected static String _IdDongle = null;

    // Dispositivo Bluetooth
    protected static BluetoothDevice _Dongle;

    // Retorna la propiedad booleana _Conectado
    public Boolean Conectado(){
        return _Conectado;
    }

    // Retorna la propiedad booleana _CanceloUsuario
    public Boolean CanceloUsuario(){
        return _CanceloUsuario;
    }

    // Constructor
    public ServicioBBPosDev(Context Contexto, IServicioBBPos IServicioPos){
        _Contexto = Contexto;
        _IServicioPos = IServicioPos;
        if(_Controlador == null){
            _EscuchaDongle = new EscuchaDongle();
            _Controlador = BBDeviceController.getInstance(_Contexto, _EscuchaDongle);
        }
    }

    // EMV(american Express, Manstercard, Visa)
    private void IniciarEMV(){ // TODO: Inidicar HORA y MONTO
        // Establecer el evento de cancelacion de lectura de tarjeta

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

    // Metodo para leer tarjeta
    public void IniciarLectura(){
        IniciarEMV();
        _DialogoDispositivo.Gif(R.drawable.pasar_tarjeta);
        _DialogoDispositivo.Estado("Favor insertar/deslizar la tarjeta");

        _DialogoDispositivo.BotonCancelar(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(_Controlador != null){
                    Toast.makeText(_Contexto, "Detener lectura de tarjeta", Toast.LENGTH_LONG).show();
                    _Controlador.cancelCheckCard();
                }
            }
        });

        // Es valido pasar insertar o pegar la targeta
        _Modo = BBDeviceController.CheckCardMode.SWIPE_OR_INSERT_OR_TAP;
        _DialogoDispositivo.show( ((AppCompatActivity)_Contexto).getSupportFragmentManager(), _IdDongle );

    }

    // Metodo que expone la propiedad _IdDongle
    public String IdDongle(){
        return _IdDongle;
    }

    private static String hexString2AsciiString(String hexString) {
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
    // Metodo para conectar con dongle
    public void Conectar(String IdDongle){
        try {
            _IdDongle = IdDongle;
            _DialogoDispositivo = new DialogoEstadoDispositivo();
            _DialogoDispositivo.show(((AppCompatActivity) _Contexto).getSupportFragmentManager(), _IdDongle);

            // Obtener objetos bluetooth emparejados
            Object[] objetos = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
            final BluetoothDevice[] dispositivos = new BluetoothDevice[objetos.length];

            for (int i = 0; i < objetos.length; ++i) {
                dispositivos[i] = (BluetoothDevice) objetos[i];
            }

            if (_EscuchaDongle.Buscar(dispositivos)) {
                // Se encontro el dispositivo, ahora, conectar
                _EscuchaDongle.Conectar();
            } else {
                _Controlador.startBTScan(NOMBRE_DISPOSITIVOS, 120);
                _DialogoDispositivo.Gif(R.drawable.switch_hand);
                _DialogoDispositivo.Estado("Esperando dispositivo");
            }
        }catch(NullPointerException Ex){
            if(_DialogoDispositivo.CanceloUsuario()){
                // Se cancelo por el usuario
                _CanceloUsuario = true;
                return;
            }else{
                Ex.printStackTrace();
            }
            _Controlador.disconnectBT();
            _Controlador.releaseBBDeviceController();
            _Controlador = null;
            _EscuchaDongle = null;
        }
    }

    public interface IServicioBBPos {
        void alConectar ();
        void alLeerTarjeta ();
    }


    // Clase para escuchar Dispositivo Dongle
private class EscuchaDongle implements BBDeviceController.BBDeviceControllerListener {

        // Metodo para buscar en dispositivos emparejados
        public Boolean Buscar(BluetoothDevice[] Dispositivos){
            _DialogoDispositivo.Estado("Buscando dispositivo...");
            for (int i = 0; i < Dispositivos.length; ++i) {
                if(Dispositivos[i].getName().equals(_IdDongle.concat("-LE"))){
                    // El dispositivo es el indicado por control - Conectar
                    _DialogoDispositivo.Estado("Conectando con " + Dispositivos[i].getAddress() );
                    return true;
                }
            }
            return false;
        }

        // Metodo para buscar en dispositivos escaneados
        public Boolean Buscar(List<BluetoothDevice> Dispositivos){
            _Dispositivos = Dispositivos;
            for (int i = 0; i < Dispositivos.size(); ++i) {
                if(Dispositivos.get(i).getName().equals(_IdDongle)){
                    // Si el dispositivo es el indicado por control, conectar, sino, ignorar
                    _DialogoDispositivo.Estado("Conectando con " + Dispositivos.get(i).getAddress() );
                    _Dongle = Dispositivos.get(i);
                    return true;
                }
            }
            return false;
        }

        // Conectar a dongle encontrado por el metodo Buscar
        public void Conectar(){
            _Controlador.connectBT(_Dongle);
        }

        @Override
        public void onBTReturnScanResults(List<BluetoothDevice> Dispositivos) {
            _DialogoDispositivo.Estado("Buscando dispositivo");

            // Buscar dongle entre dispositivos
            // Escanear de nuevo
            // _Controlador.startBTScan(NOMBRE_DISPOSITIVOS, 120);
            if(_EscuchaDongle.Buscar(Dispositivos)){
                _EscuchaDongle.Conectar();
            }
        }

        @Override
        public void onBTConnected(BluetoothDevice Dispositivo) {
            _Conectado = true;
            _DialogoDispositivo.dismiss();
            _IServicioPos.alConectar();
        }

        @Override
        public void onBTDisconnected() {
            _DialogoDispositivo.Estado("Se desconecto el dispositivo");
            _Conectado = false;
        }

        @Override
        public void onWaitingForCard(BBDeviceController.CheckCardMode checkCardMode) {
            switch (_Modo) {
                case INSERT:
                    _DialogoDispositivo.Estado("Favor de insertar tarjeta");
                    break;
                case SWIPE:
                    _DialogoDispositivo.Estado("Favor de deslizar tarjeta");
                    break;
                case SWIPE_OR_INSERT:
                    _DialogoDispositivo.Estado("Favor de insertar/deslizar tarjeta");
                    break;
                case TAP:
                    _DialogoDispositivo.Estado("Favor de pegar tarjeta");
                    break;
                case SWIPE_OR_TAP:
                    _DialogoDispositivo.Estado("Favor de deslizar/pegar tarjeta");
                    break;
                case INSERT_OR_TAP:
                    _DialogoDispositivo.Estado("Favor de insertar/pegar tarjeta");
                    break;
                case SWIPE_OR_INSERT_OR_TAP:
                    _DialogoDispositivo.Estado("Favor de insertar/deslizar/pegar tarjeta");
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onWaitingReprintOrPrintNext() {
            //statusEditText.setText(statusEditText.getText() + "\n" + getString(R.string.please_press_reprint_or_print_next));
        }

        //@Override
        //public void onBTConnected(BluetoothDevice bluetoothDevice) {
        //statusEditText.setText(getString(R.string.bluetooth_connected) + ": " + bluetoothDevice.getAddress());
        //}

        //@Override
        //public void onBTDisconnected() {
        //statusEditText.setText(getString(R.string.bluetooth_disconnected));
        //}

        //@Override
        //public void onBTReturnScanResults(List<BluetoothDevice> foundDevices) {
            /*currentActivity.foundDevices = foundDevices;
            if (arrayAdapter != null) {
                arrayAdapter.clear();
                for (int i = 0; i < foundDevices.size(); ++i) {
                    arrayAdapter.add(foundDevices.get(i).getName());
                }
                arrayAdapter.notifyDataSetChanged();
            }*/
        //}

        @Override
        public void onBTScanStopped() {
            //statusEditText.setText(getString(R.string.bluetooth_scan_stopped));
        }

        @Override
        public void onBTScanTimeout() {
            //statusEditText.setText(getString(R.string.bluetooth_scan_timeout));
        }

        @Override
        public void onReturnCheckCardResult(BBDeviceController.CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
            //dismissDialog();
            //statusEditText.setText("" + checkCardResult + "\n");
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

                //String content = "" + checkCardResult + "\n";
                //content += getString(R.string.format_id) + " " + formatID + "\n";
                //content += getString(R.string.masked_pan) + " " + maskedPAN + "\n";
                //content += getString(R.string.pan) + " " + PAN + "\n";
                //content += getString(R.string.expiry_date) + " " + expiryDate + "\n";
                //content += getString(R.string.cardholder_name) + " " + cardHolderName + "\n";
                //content += getString(R.string.ksn) + " " + ksn + "\n";
                //content += getString(R.string.service_code) + " " + serviceCode + "\n";
                //content += getString(R.string.track_1_length) + " " + track1Length + "\n";
                //content += getString(R.string.track_2_length) + " " + track2Length + "\n";
                //content += getString(R.string.track_3_length) + " " + track3Length + "\n";
                //content += getString(R.string.encrypted_tracks) + " " + encTracks + "\n";
                //content += getString(R.string.encrypted_track_1) + " " + encTrack1 + "\n";
                //content += getString(R.string.encrypted_track_2) + " " + encTrack2 + "\n";
                //content += getString(R.string.encrypted_track_3) + " " + encTrack3 + "\n";
                //content += getString(R.string.track_1_status) + " " + track1Status + "\n";
                //content += getString(R.string.track_2_status) + " " + track2Status + "\n";
                //content += getString(R.string.track_3_status) + " " + track3Status + "\n";
                //content += getString(R.string.partial_track) + " " + partialTrack + "\n";
                //content += getString(R.string.product_type) + " " + productType + "\n";
                //content += getString(R.string.track_encoding) + " " + trackEncoding + "\n";
                //content += getString(R.string.random_number) + " " + randomNumber + "\n";
                //content += getString(R.string.final_message) + " " + finalMessage + "\n";
                //content += getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";
                //content += getString(R.string.mac) + " " + mac + "\n";
                if ((decodeData != null) && (decodeData.containsKey("data"))) {
                    //   content += getString(R.string.data) + decodeData.get("data");
                }

                if ((serialNumber != null) && (!serialNumber.equals(""))) {
                    //  content += getString(R.string.serial_number) + serialNumber + "\n";
                }

                if ((bID != null) && (!bID.equals(""))) {
                    //  content += getString(R.string.b_id) + "  :" + bID + "\n";
                }

                if ((posEntryMode != null) && (!posEntryMode.equals(""))) {
                    //  content += getString(R.string.pos_entry_mode) + "  :" + posEntryMode + "\n";
                }

                //statusEditText.setText(content);
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

                    //content += getString(R.string.format_id) + " " + formatID + "\n";
                    //content += getString(R.string.masked_pan) + " " + maskedPAN + "\n";
                    //content += getString(R.string.pan) + " " + PAN + "\n";
                    //content += getString(R.string.expiry_date) + " " + expiryDate + "\n";
                    //content += getString(R.string.cardholder_name) + " " + cardHolderName + "\n";
                    //content += getString(R.string.ksn) + " " + ksn + "\n";
                    //content += getString(R.string.service_code) + " " + serviceCode + "\n";
                    //content += getString(R.string.track_1_length) + " " + track1Length + "\n";
                    //content += getString(R.string.track_2_length) + " " + track2Length + "\n";
                    //content += getString(R.string.track_3_length) + " " + track3Length + "\n";
                    //content += getString(R.string.encrypted_tracks) + " " + encTracks + "\n";
                    //content += getString(R.string.encrypted_track_1) + " " + encTrack1 + "\n";
                    //content += getString(R.string.encrypted_track_2) + " " + encTrack2 + "\n";
                    //content += getString(R.string.encrypted_track_3) + " " + encTrack3 + "\n";
                    //content += getString(R.string.track_1_status) + " " + track1Status + "\n";
                    //content += getString(R.string.track_2_status) + " " + track2Status + "\n";
                    //content += getString(R.string.track_3_status) + " " + track3Status + "\n";
                    //content += getString(R.string.partial_track) + " " + partialTrack + "\n";
                    //content += getString(R.string.product_type) + " " + productType + "\n";
                    //content += getString(R.string.track_encoding) + " " + trackEncoding + "\n";
                    //content += getString(R.string.random_number) + " " + randomNumber + "\n";
                    //content += getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";
                    //content += getString(R.string.mac) + " " + mac + "\n";
                }
                //statusEditText.setText(content);
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
                    //statusEditText.setText(content);
                }
            }
        }

        @Override
        public void onReturnCancelCheckCardResult(boolean isSuccess) {
            if (isSuccess) {
                //statusEditText.setText(R.string.cancel_check_card_success);
            } else {
                //statusEditText.setText(R.string.cancel_check_card_fail);
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
            //content += getString(R.string.format_id) + formatID + "\n";
            //content += getString(R.string.vendor_id_hex) + vendorID + "\n";
            // content += getString(R.string.vendor_id_ascii) + vendorIDAscii + "\n";
            // content += getString(R.string.bootloader_version) + bootloaderVersion + "\n";
            //content += getString(R.string.firmware_version) + firmwareVersion + "\n";
            //content += getString(R.string.main_processor_version) + mainProcessorVersion + "\n";
            //content += getString(R.string.coprocessor_version) + coprocessorVersion + "\n";
            //content += getString(R.string.coprocessor_bootloader_version) + coprocessorBootloaderVersion + "\n";
            //content += getString(R.string.usb) + isUsbConnected + "\n";
            //content += getString(R.string.charge) + isCharging + "\n";
            //content += getString(R.string.battery_level) + batteryLevel + "\n";
            //content += getString(R.string.battery_percentage) + batteryPercentage + "\n";
            //content += getString(R.string.hardware_version) + hardwareVersion + "\n";
            // content += getString(R.string.track_1_supported) + isSupportedTrack1 + "\n";
            // content += getString(R.string.track_2_supported) + isSupportedTrack2 + "\n";
            //content += getString(R.string.track_3_supported) + isSupportedTrack3 + "\n";
            //content += getString(R.string.product_id) + productId + "\n";
            //content += getString(R.string.pin_ksn) + pinKsn + "\n";
            //content += getString(R.string.emv_ksn) + emvKsn + "\n";
            //content += getString(R.string.track_ksn) + trackKsn + "\n";
            //content += getString(R.string.terminal_setting_version) + terminalSettingVersion + "\n";
            //content += getString(R.string.device_setting_version) + deviceSettingVersion + "\n";
            //content += getString(R.string.csn) + csn + "\n";
            //content += getString(R.string.uid) + uid + "\n";
            //content += getString(R.string.serial_number) + serialNumber + "\n";
            if ((modelName != null) && (!modelName.equals(""))) {
                //content += getString(R.string.model_name) + modelName + "\n";
            }

            if ((macKsn != null) && (!macKsn.equals(""))) {
                //content += getString(R.string.mac_ksn) + macKsn + "\n";
            }
            if ((nfcKsn != null) && (!nfcKsn.equals(""))) {
                //content += getString(R.string.nfc_ksn) + nfcKsn + "\n";
            }
            if ((messageKsn != null) && (!messageKsn.equals(""))) {
                //content += getString(R.string.message_ksn) + messageKsn + "\n";
            }
            if ((bID != null) && (!bID.equals(""))) {
                //content += getString(R.string.b_id) + "  :" + bID + "\n";
            }
            if ((publicKeyVersion != null) && (!publicKeyVersion.equals(""))) {
                //content += getString(R.string.public_key_version) + "  :" + publicKeyVersion + "\n";
            }

            //statusEditText.setText(content);

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
            //dismissDialog();
            //dialog = new Dialog(currentActivity);
            //dialog.setContentView(R.layout.alert_dialog);
            //dialog.setTitle(R.string.transaction_result);
            //TextView messageTextView = (TextView) dialog.findViewById(R.id.messageTextView);

            String message = "" + transactionResult + "\n";
            if (transactionResult == BBDeviceController.TransactionResult.APPROVED) {
                //message = getString(R.string.amount) + ": $" + amount + "\n";
                //if (!cashbackAmount.equals("")) {
                //    message += getString(R.string.cashback_amount) + ": $" + cashbackAmount;
                //}
            }

            //messageTextView.setText(message);

            //amount = "";
            //cashbackAmount = "";
            //amountEditText.setText("");

            //dialog.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {

            // @Override
            // public void onClick(View v) {
            //        dismissDialog();
            //    }
            //});

            //dialog.show();
        }

        @Override
        public void onReturnBatchData(String tlv) {
            //dismissDialog();
            String content = "batch_data\n";
            Hashtable<String, String> decodeData = BBDeviceController.decodeTlv(tlv);
            Object[] keys = decodeData.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                String value = decodeData.get(key);
                content += key + ": " + value + "\n";
            }
            _DialogoDispositivo.Estado(content);
            // statusEditText.setText(content);
        }

        @Override
        public void onReturnReversalData(String tlv) {
            //dismissDialog();
            String content = "reversal_data";
            content += tlv;
            _DialogoDispositivo.Estado(content);
        }

        @Override
        public void onReturnAmountConfirmResult(boolean isSuccess) {
            if (isSuccess) {
                _DialogoDispositivo.Estado("amount_confirmed");
                //statusEditText.setText(getString(R.string.amount_confirmed));
            } else {
                _DialogoDispositivo.Estado("amount_cancelled");
            }
        }

        @Override
        public void onReturnPinEntryResult(BBDeviceController.PinEntryResult pinEntryResult, Hashtable<String, String> data) {
            if (pinEntryResult == BBDeviceController.PinEntryResult.ENTERED) {
                //String content = getString(R.string.pin_entered);
                if (data.containsKey("epb")) {
                    //content += "\n" + getString(R.string.epb) + data.get("epb");
                }
                if (data.containsKey("ksn")) {
                    //content += "\n" + getString(R.string.ksn) + data.get("ksn");
                }
                if (data.containsKey("randomNumber")) {
                    //content += "\n" + getString(R.string.random_number) + data.get("randomNumber");
                }
                if (data.containsKey("encWorkingKey")) {
                    //content += "\n" + getString(R.string.encrypted_working_key) + data.get("encWorkingKey");
                }

                //statusEditText.setText(content);
            } else if (pinEntryResult == BBDeviceController.PinEntryResult.BYPASS) {
                //statusEditText.setText(getString(R.string.pin_bypassed));
            } else if (pinEntryResult == BBDeviceController.PinEntryResult.CANCEL) {
                //statusEditText.setText(getString(R.string.pin_canceled));
            } else if (pinEntryResult == BBDeviceController.PinEntryResult.TIMEOUT) {
                //statusEditText.setText(getString(R.string.pin_timeout));
            }
        }

        @Override
        public void onReturnPrintResult(BBDeviceController.PrintResult printResult) {
            //statusEditText.setText("" + printResult);
            _DialogoDispositivo.Estado(printResult.toString());
        }

        @Override
        public void onReturnAmount(Hashtable<String, String> data) {
            String amount = data.get("amount");
            String cashbackAmount = data.get("cashbackAmount");
            String currencyCode = data.get("currencyCode");

            String text = "";
            text += "amount_with_colon" + amount + "\n";
            text += "cashback_with_colon" + cashbackAmount + "\n";
            text += "currency_with_colon" + currencyCode + "\n";

            _DialogoDispositivo.Estado(text);
        }

        @Override
        public void onReturnUpdateTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus) {
            //dismissDialog();
            if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.SUCCESS) {
                //statusEditText.setText(getString(R.string.update_terminal_setting_success));
                _DialogoDispositivo.Estado("update_terminal_setting_success");
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_FOUND) {
                //statusEditText.setText(getString(R.string.update_terminal_setting_tag_not_found));
                _DialogoDispositivo.Estado("update_teminal_setting_tag_not_found");
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.LENGTH_INCORRECT) {
                //statusEditText.setText(getString(R.string.update_terminal_setting_length_incorrect));
                _DialogoDispositivo.Estado("update_terminal_setting_length_incorrect");
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TLV_INCORRECT) {
                //statusEditText.setText(getString(R.string.update_terminal_setting_tlv_incorrect));
                _DialogoDispositivo.Estado("update_terminal_setting_tlv_incorrect");
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.BOOTLOADER_NOT_SUPPORT) {
                //statusEditText.setText(getString(R.string.update_terminal_setting_bootloader_not_support));
                _DialogoDispositivo.Estado("update_terminal_setting_bootloader_not_supported");
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_ALLOWED_TO_ACCESS) {
                //statusEditText.setText(getString(R.string.update_terminal_setting_tag_not_allowed_to_change));
                _DialogoDispositivo.Estado("update_terminal_setting_tag_not_allowed_to_change");
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.USER_DEFINED_DATA_NOT_ENALBLED) {
                //statusEditText.setText(getString(R.string.update_terminal_setting_user_defined_data_not_allowed_to_change));
                _DialogoDispositivo.Estado("update_temrinal_setting_user_defined_data_not_allowed_to_change");
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_WRITTEN_CORRECTLY) {
                //statusEditText.setText(getString(R.string.update_terminal_setting_tag_not_written_correctly));
                _DialogoDispositivo.Estado("update_terminal_setting_tag_not_written_correctly");
            }
        }

        @Override
        public void onReturnReadTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus, String value) {
            //dismissDialog();
            if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.SUCCESS) {
                //statusEditText.setText(getString(R.string.read_terminal_setting_success) + "\n" + getString(R.string.value) + " " + value);
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_FOUND) {
                //statusEditText.setText(getString(R.string.read_terminal_setting_tag_not_found));
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.LENGTH_INCORRECT) {
                //statusEditText.setText(getString(R.string.read_terminal_setting_length_incorrect));
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TLV_INCORRECT) {
                //statusEditText.setText(getString(R.string.read_terminal_setting_tlv_incorrect));
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.BOOTLOADER_NOT_SUPPORT) {
                //statusEditText.setText(getString(R.string.read_terminal_setting_bootloader_not_support));
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_ALLOWED_TO_ACCESS) {
                //statusEditText.setText(getString(R.string.read_terminal_setting_tag_not_allowed_to_access));
                //statusEditText.setText(getString(R.string.read_terminal_setting_tag_not_allowed_to_access));D
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.USER_DEFINED_DATA_NOT_ENALBLED) {
                //statusEditText.setText(getString(R.string.read_terminal_setting_user_defined_data_not_allowed_to_change));
            } else if (terminalSettingStatus == BBDeviceController.TerminalSettingStatus.TAG_NOT_WRITTEN_CORRECTLY) {
                //statusEditText.setText(getString(R.string.read_terminal_setting_tag_not_written_correctly));
            }
        }

        @Override
        public void onReturnEnableInputAmountResult(boolean isSuccess) {
            if (isSuccess) {
                //statusEditText.setText(getString(R.string.enable_input_amount_success));
            } else {
                //statusEditText.setText(getString(R.string.enable_input_amount_fail));
            }
        }

        @Override
        public void onReturnDisableInputAmountResult(boolean isSuccess) {
            if (isSuccess) {
                //statusEditText.setText(getString(R.string.disable_input_amount_success));
            } else {
                //statusEditText.setText(getString(R.string.disable_input_amount_fail));
            }
        }

        @Override
        public void onReturnPhoneNumber(BBDeviceController.PhoneEntryResult phoneEntryResult, String phoneNumber) {
            if (phoneEntryResult == BBDeviceController.PhoneEntryResult.ENTERED) {
                //statusEditText.setText(getString(R.string.phone_number) + " " + phoneNumber);
            } else if (phoneEntryResult == BBDeviceController.PhoneEntryResult.TIMEOUT) {
                //statusEditText.setText(getString(R.string.timeout));
            } else if (phoneEntryResult == BBDeviceController.PhoneEntryResult.CANCEL) {
                //statusEditText.setText(getString(R.string.canceled));
            } else if (phoneEntryResult == BBDeviceController.PhoneEntryResult.WRONG_LENGTH) {
                //statusEditText.setText(getString(R.string.wrong_length));
            } else if (phoneEntryResult == BBDeviceController.PhoneEntryResult.BYPASS) {
                //statusEditText.setText(getString(R.string.bypass));
            }
        }

        @Override
        public void onReturnEmvCardDataResult(boolean isSuccess, String tlv) {
            if (isSuccess) {
                _DialogoDispositivo.Estado(tlv);
                // statusEditText.setText(getString(R.string.emv_card_data_result) + tlv);
            } else {
                _DialogoDispositivo.Estado(tlv);
                //  statusEditText.setText(getString(R.string.emv_card_data_failed));
            }
        }

        @Override
        public void onReturnEmvCardNumber(boolean isSuccess, String cardNumber) {
            //statusEditText.setText(getString(R.string.pan) + cardNumber);
        }

        @Override
        public void onReturnEncryptPinResult(boolean isSuccess, Hashtable<String, String> data) {
            /*
            String ksn = data.get("ksn");
            String epb = data.get("epb");
            String randomNumber = data.get("randomNumber");
            String encWorkingKey = data.get("encWorkingKey");
            String errorMessage = data.get("errorMessage");
            String content = getString(R.string.ksn) + ksn + "\n";
            content += getString(R.string.epb) + epb + "\n";
            content += getString(R.string.random_number) + randomNumber + "\n";
            content += getString(R.string.encrypted_working_key) + encWorkingKey + "\n";
            content += getString(R.string.error_message) + errorMessage;
            statusEditText.setText(content);*/
        }

        @Override
        public void onReturnEncryptDataResult(boolean isSuccess, Hashtable<String, String> data) {
            /*if (isSuccess) {
                String content = "";
                if (data.containsKey("ksn")) {
                    content += getString(R.string.ksn) + data.get("ksn") + "\n";
                }
                if (data.containsKey("randomNumber")) {
                    content += getString(R.string.random_number) + data.get("randomNumber") + "\n";
                }
                if (data.containsKey("encData")) {
                    content += getString(R.string.encrypted_data) + data.get("encData") + "\n";
                }
                if (data.containsKey("mac")) {
                    content += getString(R.string.mac) + data.get("mac") + "\n";
                }
                statusEditText.setText(content);
            } else {
                statusEditText.setText(getString(R.string.encrypt_data_failed));
            }*/
        }

        @Override
        public void onReturnInjectSessionKeyResult(boolean isSuccess, Hashtable<String, String> data) {
            /*String content;
            if (isSuccess) {
                content = getString(R.string.inject_session_key_success);
                if (data.size() == 0) {
                    injectNextSessionKey();
                }
            } else {
                content = getString(R.string.inject_session_key_failed);
                content += "\n" + getString(R.string.error_message) + data.get("errorMessage");
            }
            setStatus(content);*/
        }

        @Override
        public void onReturnApduResult(boolean isSuccess, Hashtable<String, Object> data) {
            /*
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

            }*/
        }

        @Override
        public void onReturnPowerOffIccResult(boolean isSuccess) {
            /*dismissDialog();
            if (isSuccess) {
                setStatus(getString(R.string.power_off_icc_success));
            } else {
                setStatus(getString(R.string.power_off_icc_failed));
            }*/
        }

        @Override
        public void onReturnPowerOnIccResult(boolean isSuccess, String ksn, String atr, int atrLength) {
            /*dismissDialog();
            if (isSuccess) {
                BaseActivity.ksn = ksn;

                setStatus(getString(R.string.power_on_icc_success));
                setStatus(getString(R.string.ksn) + ksn);
                setStatus(getString(R.string.atr) + atr);
                setStatus(getString(R.string.atr_length) + atrLength);
            } else {
                setStatus(getString(R.string.power_on_icc_failed));
            }*/
        }

        @Override
        public void onRequestSelectApplication(ArrayList<String> appList) {
            /*
            dismissDialog();

            dialog = new Dialog(currentActivity);
            dialog.setContentView(R.layout.application_dialog);
            dialog.setTitle(R.string.please_select_app);

            String[] appNameList = new String[appList.size()];
            for (int i = 0; i < appNameList.length; ++i) {
                appNameList[i] = appList.get(i);
            }

            appListView = (ListView) dialog.findViewById(R.id.appList);
            appListView.setAdapter(new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1, appNameList));
            appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    bbDeviceController.selectApplication(position);
                    dismissDialog();
                }

            });

            dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    bbDeviceController.cancelSelectApplication();
                    dismissDialog();
                }
            });
            dialog.show();*/
        }

        @Override
        public void onRequestSetAmount() {
            // Monto, Cashback, (Codigo de moneda Solo 156 si fuera china) , tipo de servicio, Caracter de moneda
            if(_Controlador.setAmount("500", "0", "840", BBDeviceController.TransactionType.PAYMENT, new BBDeviceController.CurrencyCharacter[] { BBDeviceController.CurrencyCharacter.DOLLAR })){
                _DialogoDispositivo.Estado("Se establecio el monto");
            }else{
                _DialogoDispositivo.Estado("Error al establecer el monto"); // Intentar de nuevo
            }
        }

        @Override
        public void onRequestPinEntry(BBDeviceController.PinEntrySource pinEntrySource) {
            _DialogoDispositivo.Estado("onRequestPinEntry");
            /*
            dismissDialog();
            if (pinEntrySource == BBDeviceController.PinEntrySource.KEYPAD) {
                statusEditText.setText(getString(R.string.enter_pin_on_keypad));
            } else {
                dismissDialog();

                dialog = new Dialog(currentActivity);
                dialog.setContentView(R.layout.pin_dialog);
                dialog.setTitle(getString(R.string.enter_pin));

                dialog.findViewById(R.id.confirmButton).setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                String pin = ((EditText) dialog.findViewById(R.id.pinEditText)).getText().toString();
                                bbDeviceController.sendPinEntryResult(pin);
                                dismissDialog();
                            }
                        });

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
            }*/
        }

        @Override
        public void onRequestOnlineProcess(String tlv) {
            _DialogoDispositivo.Estado("onRequestOnlineProcess");
            /*String content = getString(R.string.request_data_to_server) + "\n";
            Hashtable<String, String> decodeData = BBDeviceController.decodeTlv(tlv);
            Object[] keys = decodeData.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                String value = decodeData.get(key);
                content += key + ": " + value + "\n";
            }
            statusEditText.setText(content);

            dismissDialog();
            dialog = new Dialog(currentActivity);
            dialog.setContentView(R.layout.alert_dialog);
            dialog.setTitle(R.string.request_data_to_server);

            if (isPinCanceled) {
                ((TextView) dialog.findViewById(R.id.messageTextView)).setText(R.string.replied_failed);
            } else {
                ((TextView) dialog.findViewById(R.id.messageTextView)).setText(R.string.replied_success);
            }*/

            //dialog.findViewById(R.id.confirmButton).setOnClickListener(
            //   new View.OnClickListener() {

            // @Override
            // public void onClick(View v) {
            //if (isPinCanceled) {
            //bbDeviceController.sendOnlineProcessResult(null);
            //} else {
            // bbDeviceController.sendOnlineProcessResult("8A023030");
            //}
            //dismissDialog();
            //    }
            //});

            //dialog.show();
        }

        @Override
        public void onRequestTerminalTime() {
            //dismissDialog();
            String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
            _Controlador.sendTerminalTime(terminalTime);
            //statusEditText.setText(getString(R.string.request_terminal_time) + " " + terminalTime);
        }

        @Override
        public void onRequestDisplayText(BBDeviceController.DisplayText displayText) {
            //dismissDialog();
            //_DialogoDispositivo.Estado("onRequestDisplayText");
            _DialogoDispositivo.Estado(displayText.toString());
        }

        @Override
        public void onRequestClearDisplay() {
            //dismissDialog();
            //statusEditText.setText("");
            _DialogoDispositivo.Estado("onRequestClearDisplay");
        }

        @Override
        public void onRequestFinalConfirm() {
            //dismissDialog();
            //if (!isPinCanceled) {
            //dialog = new Dialog(currentActivity);
            //dialog.setContentView(R.layout.confirm_dialog);
            //dialog.setTitle(getString(R.string.confirm_amount));

            //String message = getString(R.string.amount) + ": $" + amount;
            //if (!cashbackAmount.equals("")) {
            //    message += "\n" + getString(R.string.cashback_amount) + ": $" + cashbackAmount;
            //}

            //((TextView) dialog.findViewById(R.id.messageTextView)).setText(message);

            //dialog.findViewById(R.id.confirmButton).setOnClickListener(
            // new View.OnClickListener() {
            // @Override
            // public void onClick(View v) {
            //   bbDeviceController.sendFinalConfirmResult(true);
            //    dialog.dismiss();
            //  }
            //});
            _Controlador.sendFinalConfirmResult(true);
            //dialog.findViewById(R.id.cancelButton).setOnClickListener(
            //new View.OnClickListener() {
            //   @Override
            //   public void onClick(View v) {
            //bbDeviceController.sendFinalConfirmResult(false);
            //dialog.dismiss();
            //}
            //});

            //dialog.show();
            //} else {
            //bbDeviceController.sendFinalConfirmResult(false);
            //}
        }

        @Override
        public void onRequestPrintData(int index, boolean isReprint) {
            _DialogoDispositivo.Estado("onRequestPrintData");
            //bbDeviceController.sendPrintData(receipts.get(index));
            if (isReprint) {
                //statusEditText.setText(getString(R.string.request_reprint_data) + index);
            } else {
                //statusEditText.setText(getString(R.string.request_printer_data) + index);
            }
        }

        @Override
        public void onPrintDataCancelled() {
            _DialogoDispositivo.Estado("onPrintDataCancelled");
            //statusEditText.setText(getString(R.string.printer_operation_cancelled));
        }

        @Override
        public void onPrintDataEnd() {
            _DialogoDispositivo.Estado("onPrintDataEnd");
            //statusEditText.setText(getString(R.string.printer_operation_end));
        }

        @Override
        public void onBatteryLow(BBDeviceController.BatteryStatus batteryStatus) {
            if (batteryStatus == BBDeviceController.BatteryStatus.LOW) {
                //statusEditText.setText(getString(R.string.battery_low));
                _DialogoDispositivo.Estado("Bateria baja");
            } else if (batteryStatus == BBDeviceController.BatteryStatus.CRITICALLY_LOW) {
                _DialogoDispositivo.Estado("Bateria criticamente baja");
                //statusEditText.setText(getString(R.string.battery_critically_low));
            }
        }

        @Override
        public void onAudioDevicePlugged() {
            //statusEditText.setText(getString(R.string.device_plugged));
        }

        @Override
        public void onAudioDeviceUnplugged() {
            //statusEditText.setText(getString(R.string.device_unplugged));
        }

        @Override
        public void onError(BBDeviceController.Error errorState, String errorMessage) {
            //dismissDialog();
            //if(progressDialog != null) {
            //progressDialog.dismiss();
            //progressDialog = null;
            //}

            //amountEditText.setText("");
            String mensaje = "";
            if (errorState == BBDeviceController.Error.CMD_NOT_AVAILABLE) {
                mensaje = "Comando no disponible";
            } else if (errorState == BBDeviceController.Error.TIMEOUT) {
                mensaje = "El dispositivo no respondió";
            } else if (errorState == BBDeviceController.Error.UNKNOWN) {
                mensaje = "Error desconocido";
            } else if (errorState == BBDeviceController.Error.DEVICE_BUSY) {
                mensaje = "Dispositivo ocupado";
            } else if (errorState == BBDeviceController.Error.INPUT_OUT_OF_RANGE) {
                mensaje = "Entrada fuera de rango";
            } else if (errorState == BBDeviceController.Error.INPUT_INVALID_FORMAT) {
                mensaje = "Formato no valido";
                //Toast.makeText(currentActivity, getString(R.string.invalid_format), Toast.LENGTH_LONG).show();
            } else if (errorState == BBDeviceController.Error.INPUT_INVALID) {
                mensaje = "Entrada no valida";
                //Toast.makeText(currentActivity, getString(R.string.input_invalid), Toast.LENGTH_LONG).show();
            } else if (errorState == BBDeviceController.Error.CASHBACK_NOT_SUPPORTED) {
                mensaje = "No soporta cashback";
                //Toast.makeText(currentActivity, getString(R.string.cashback_not_supported), Toast.LENGTH_LONG).show();
            } else if (errorState == BBDeviceController.Error.CRC_ERROR) {
                mensaje = "Error CRC";
            } else if (errorState == BBDeviceController.Error.COMM_ERROR) {
                mensaje = "Error COMM"; //content = getString(R.string.comm_error);
            } else if (errorState == BBDeviceController.Error.FAIL_TO_START_BT) {
                mensaje = "Error al inicializar Bluetooth";
            } else if (errorState == BBDeviceController.Error.FAIL_TO_START_AUDIO) {
                mensaje = "Fallo al iniciar audio";
            } else if (errorState == BBDeviceController.Error.INVALID_FUNCTION_IN_CURRENT_CONNECTION_MODE) {
                mensaje = "Funcion no valida";
            } else if (errorState == BBDeviceController.Error.COMM_LINK_UNINITIALIZED) {
                mensaje = "La comunicacion no ha sido iniciada";
            } else if (errorState == BBDeviceController.Error.BTV4_NOT_SUPPORTED) {
                mensaje = "Bluetooth v4 no soportado";
                //Toast.makeText(currentActivity, getString(R.string.bluetooth_4_not_supported), Toast.LENGTH_LONG).show();
            } else if (errorState == BBDeviceController.Error.CHANNEL_BUFFER_FULL) {
                mensaje = "Buffer de canal lleno";
            } else if (errorState == BBDeviceController.Error.BLUETOOTH_PERMISSION_DENIED) {
                //content = getString(R.string.bluetooth_permission_denied);
                mensaje = "Permiso de bluetooth denegado";
            } else if (errorState == BBDeviceController.Error.VOLUME_WARNING_NOT_ACCEPTED) {
                mensaje = "Advertencia de volumen no soportada";
                //content = getString(R.string.volume_warning_not_accepted);
            } else if (errorState == BBDeviceController.Error.FAIL_TO_START_SERIAL) {
                mensaje = "Error al iniciar comunicacion serial";
            } else if (errorState == BBDeviceController.Error.USB_DEVICE_NOT_FOUND) {
                mensaje = "Dispositivo USB no encontrado";
                //content = getString(R.string.usb_device_not_found);
            } else if (errorState == BBDeviceController.Error.USB_DEVICE_PERMISSION_DENIED) {
                mensaje = "Permiso USB denegado";
            } else if (errorState == BBDeviceController.Error.USB_NOT_SUPPORTED) {
                mensaje = "USB No soportado";//content = getString(R.string.usb_not_supported);
            }

            if (errorMessage != null && !errorMessage.equals("")) {
                mensaje += "\n" + "ocurrio un error: " + errorMessage;
            }
            _DialogoDispositivo.Estado("onError: " + mensaje + ".");
        }

        @Override
        public void onReturnCAPKList(List<CAPK> capkList) {
            String content = "capk";
            for (int i = 0; i < capkList.size(); ++i) {
                CAPK capk = capkList.get(i);
                content += "\n" + i + ": ";
                content += "\n" + "location" + capk.location;
                content += "\n" + "rid" + capk.rid;
                content += "\n" + "index" + capk.index;
                content += "\n";
            }
            _DialogoDispositivo.Estado(content);
            //setStatus(content);
        }

        @Override
        public void onReturnCAPKDetail(CAPK capk) {
            String content = "capk";
            if (capk != null) {
                content += "\n" + "location" + capk.location;
                content += "\n" + "rid" + capk.rid;
                content += "\n" + "index" + capk.index;
                content += "\n" + "exponent" + capk.exponent;
                content += "\n" + "modulus" + capk.modulus;
                content += "\n" + "checksum" + capk.checksum;
                content += "\n" + "size" + capk.size;
                content += "\n";
            } else {
                content += "\nnull \n";
            }
            _DialogoDispositivo.Estado(content);
            //setStatus(content);
        }

        @Override
        public void onReturnCAPKLocation(String location) {
            _DialogoDispositivo.Estado(location);
            //setStatus(getString(R.string.location) + location);
        }

        @Override
        public void onReturnUpdateCAPKResult(boolean isSuccess) {
            if (isSuccess) {
                _DialogoDispositivo.Estado("update_capk_success");
                //setStatus(getString(R.string.update_capk_success));
            } else {
                _DialogoDispositivo.Estado("update_capk_fail");
                //setStatus(getString(R.string.update_capk_fail));
            }
        }

        @Override
        public void onReturnEmvReport(String tlv) {
            _DialogoDispositivo.Estado("onReturnEmvReport");
            /*String content = getString(R.string.emv_report) + "\n";

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
            */
            //setStatus(content);
        }

        @Override
        public void onReturnEmvReportList(Hashtable<String, String> data) {
            //String content = getString(R.string.emv_report_list) + "\n";
            Object[] keys = data.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                String value = data.get(key);
                //content += key + ": " + value + "\n";
            }
            _DialogoDispositivo.Estado("onReturnEmvReportList");
            //setStatus(content);
        }

        @Override
        public void onSessionInitialized() {
            _DialogoDispositivo.Estado("onSessionEmvReportList");
            //setStatus(getString(R.string.session_initialized));
        }

        @Override
        public void onSessionError(BBDeviceController.SessionError sessionError, String errorMessage) {
            if (sessionError == BBDeviceController.SessionError.FIRMWARE_NOT_SUPPORTED) {
                _DialogoDispositivo.Estado("Firmware no soportado");
                //setStatus(getString(R.string.session_error_firmware_not_supported));
            } else if (sessionError == BBDeviceController.SessionError.INVALID_SESSION) {
                _DialogoDispositivo.Estado("Sesion no valida");
                //setStatus(getString(R.string.session_error_invalid_session));
            } else if (sessionError == BBDeviceController.SessionError.INVALID_VENDOR_TOKEN) {
                _DialogoDispositivo.Estado("Tocken de fabricante no valido");
                //setStatus(getString(R.string.session_error_invalid_vendor_token));
            } else if (sessionError == BBDeviceController.SessionError.SESSION_NOT_INITIALIZED) {
                _DialogoDispositivo.Estado("Sesion no valida");
                //setStatus(getString(R.string.session_error_session_not_initialized));
            }

            //setStatus(getString(R.string.error_message) + errorMessage);
        }

        @Override
        public void onReturnReadGprsSettingsResult(boolean isSuccess, Hashtable<String, Object> data) {
            if (isSuccess) {

                String text = "Lectura de ajustes GPRS correcta: ";
                text += "\n Operador: " + data.get("operator");
                text += "\n APN :" + data.get("apn");
                text += "\n Usuario: " + data.get("username");
                text += "\n Clave: " + data.get("password");
                _DialogoDispositivo.Estado(text);

            } else {
                String text = "read_gprs_setting_fail";
                BBDeviceController.TerminalSettingStatus terminalSettingStatus = (BBDeviceController.TerminalSettingStatus)data.get("gprs");
                switch (terminalSettingStatus) {
                    case SUCCESS:
                        //setStatus(getString(R.string.read_terminal_setting_success));
                        break;
                    case LENGTH_INCORRECT:
                        //setStatus(getString(R.string.length_incorrect));
                        break;
                    case TLV_INCORRECT:
                        //setStatus(getString(R.string.tlv_incorrect));
                        break;
                    case TAG_NOT_FOUND:
                        //setStatus(getString(R.string.tag_not_found));
                        break;
                    case BOOTLOADER_NOT_SUPPORT:
                        //setStatus(getString(R.string.bootloader_not_support));
                        break;
                    case TAG_NOT_ALLOWED_TO_ACCESS:
                        //setStatus(getString(R.string.tag_not_allowed_to_access));
                        break;
                    case USER_DEFINED_DATA_NOT_ENALBLED:
                        //setStatus(getString(R.string.user_defined_data_not_allowed_to_change));
                        break;
                    case TAG_NOT_WRITTEN_CORRECTLY:
                        //setStatus(getString(R.string.tag_not_written_correctly));

                        break;
                    default:
                        break;
                }
                _DialogoDispositivo.Estado(text);
                //setStatus(text);
            }
        }

        @Override
        public void onReturnReadWiFiSettingsResult(boolean isSuccess, Hashtable<String, Object> data) {
            if (isSuccess) {
                String text = "read_wifi_setting_success";
                text += "\n" + "ssid" + data.get("ssid");
                text += "\n" + "password" + data.get("password");
                text += "\n" + "url" + data.get("url");
                text += "\n" + "portNumber" + data.get("portNumber");
                _DialogoDispositivo.Estado(text);
            } else {
                String text = "read_wifi_setting_fail";
                Object[] keys = data.keySet().toArray();
                Arrays.sort(keys);
                for (Object key : keys) {
                    text += "\n" + key + " : ";
                    BBDeviceController.TerminalSettingStatus terminalSettingStatus = (BBDeviceController.TerminalSettingStatus)data.get(key);
                    switch (terminalSettingStatus) {
                        case SUCCESS:
                            text += "read_terminal_setting_success";
                            break;
                        case LENGTH_INCORRECT:
                            text += "length_incorrect";
                            break;
                        case TLV_INCORRECT:
                            text += "tlv_incorrect";
                            break;
                        case TAG_NOT_FOUND:
                            text += "tag_not_found";
                            break;
                        case BOOTLOADER_NOT_SUPPORT:
                            text += "bootloader_not_support";
                            break;
                        case TAG_NOT_ALLOWED_TO_ACCESS:
                            text += "tag_not_allowed_to_access";
                            break;
                        case USER_DEFINED_DATA_NOT_ENALBLED:
                            text += "user_defined_data_not_allowed_to_change";
                            break;
                        case TAG_NOT_WRITTEN_CORRECTLY:
                            text += "tag_not_written_correctly";
                            break;
                        default:
                            break;
                    }
                }
                _DialogoDispositivo.Estado(text);
            }
        }

        @Override
        public void onReturnUpdateGprsSettingsResult(boolean isSuccess, Hashtable<String, BBDeviceController.TerminalSettingStatus> data) {
            if (isSuccess) {
                String text = "update_gprs_setting_success";
                _DialogoDispositivo.Estado(text);
            } else {
                String text = "update_gprs_setting_fail";
                text += "\n terminal_setting_status";

                //String text = getString(R.string.update_gprs_setting_fail);
                //text += "\n" + getString(R.string.terminal_setting_status) + data.get("gprs");
                //setStatus(text);


                _DialogoDispositivo.Estado(text);
            }
        }

        @Override
        public void onReturnUpdateWiFiSettingsResult(boolean isSuccess, Hashtable<String, BBDeviceController.TerminalSettingStatus> data) {
            if (isSuccess) {
                //String text = getString(R.string.update_wifi_setting_success);
                //setStatus(text);


                _DialogoDispositivo.Estado("update_wifi_setting_success");
            } else {
                String text = "update_wifi_setting_fail";
                Object[] keys = data.keySet().toArray();
                Arrays.sort(keys);
                for (Object key : keys) {
                    text += "\n" + key + " : ";
                    BBDeviceController.TerminalSettingStatus terminalSettingStatus = data.get(key);
                    switch (terminalSettingStatus) {
                        case SUCCESS:
                            text += "read_terminal_setting_success";
                            break;
                        case LENGTH_INCORRECT:
                            text += "length_incorrect";
                            break;
                        case TLV_INCORRECT:
                            text += "tlv_incorrect";
                            break;
                        case TAG_NOT_FOUND:
                            text += "tag_not_found";
                            break;
                        case BOOTLOADER_NOT_SUPPORT:
                            text += "bootloader_not_support";
                            break;
                        case TAG_NOT_ALLOWED_TO_ACCESS:
                            text += "tag_not_allowed_to_access";
                            break;
                        case USER_DEFINED_DATA_NOT_ENALBLED:
                            text += "user_defined_data_not_allowed_to_change";
                            break;
                        case TAG_NOT_WRITTEN_CORRECTLY:
                            text += "tag_not_written_correctly";
                            break;
                        default:
                            break;
                    }
                }
                _DialogoDispositivo.Estado(text);
                //setStatus(text);
            }
        }

        @Override
        public void onAudioAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings) {
            //if(progressDialog != null) {
            //progressDialog.dismiss();
            //progressDialog = null;
            //}

            String outputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bbpos.bbdevice/";
            String filename = "settings.txt";
            String content = "auto_config_completed";
            if(isDefaultSettings) {
                content += "\n" + "default_settings";
                new File(outputDirectory + filename).delete();
            } else {
                content += "\nsettings " + autoConfigSettings;

                try {
                    File directory = new File(outputDirectory);
                    if(!directory.isDirectory()) {
                        directory.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(outputDirectory + filename, false);
                    fos.write(autoConfigSettings.getBytes());
                    fos.flush();
                    fos.close();

                    content += "\n" + "settings_written_to_external_storage";
                } catch(Exception e) {
                }
            }
            _DialogoDispositivo.Estado(content);
        }

        @Override
        public void onAudioAutoConfigError(BBDeviceController.AudioAutoConfigError autoConfigError) {
            //if(progressDialog != null) {
            //   progressDialog.dismiss();
            //   progressDialog = null;
            //}

            if(autoConfigError == BBDeviceController.AudioAutoConfigError.PHONE_NOT_SUPPORTED) {
                //    statusEditText.setText(getString(R.string.auto_config_error_phone_not_supported));
            } else if(autoConfigError == BBDeviceController.AudioAutoConfigError.INTERRUPTED) {
                //    statusEditText.setText(getString(R.string.auto_config_error_interrupted));
            }
        }

        @Override
        public void onAudioAutoConfigProgressUpdate(double percentage) {
            // if(progressDialog != null) {
            //    progressDialog.setProgress((int)percentage);
            // }
        }

        @Override
        public void onDeviceHere(boolean arg0) {

        }

        @Override
        public void onNoAudioDeviceDetected() {
            //dismissDialog();
            //if(progressDialog != null) {
            //progressDialog.dismiss();
            //progressDialog = null;
            //}
            //statusEditText.setText(getString(R.string.no_device_detected));
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    //Toast.makeText(currentActivity, getString(R.string.no_device_detected), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onReturnNfcDataExchangeResult(boolean isSuccess, Hashtable<String, String> data) {
            if (isSuccess) {
                //String text = getString(R.string.nfc_data_exchange_success);
                //text += "\n" + getString(R.string.ndef_record) + data.get("ndefRecord");
                //setStatus(text);
            } else {
                //String text = getString(R.string.nfc_data_exchange_fail);
                //text += "\n" + getString(R.string.error_message) + data.get("errorMessage");
                //setStatus(text);
            }
        }

        @Override
        public void onReturnNfcDetectCardResult(BBDeviceController.NfcDetectCardResult nfcDetectCardResult, Hashtable<String, Object> data) {
            String text = "";
            text += "Resultado NFC: " + nfcDetectCardResult;
            text += "\n" + "nfc_tag_information: " + data.get("nfcTagInfo");
            text += "\n" + "nfc_card_uid: " + data.get("nfcCardUID");
            if (data.containsKey("errorMessage")) {
                text += "\n Mensaje de Error: " + data.get("errorMessage");
            }
            _DialogoDispositivo.Estado(text);
        }

        @Override
        public void onUsbConnected() {
            //setStatus(getString(R.string.usb_connected));
            _DialogoDispositivo.Estado("USB Conectado");
        }

        @Override
        public void onUsbDisconnected() {
            //ded.Estado("USB Desconectado");
            _DialogoDispositivo.Estado("USB Desconectado");
        }

        @Override
        public void onRequestDisplayAsterisk(int arg0) {
        }

        @Override
        public void onSerialConnected() {
            /*
            final ProgressDialog progressDialog = ProgressDialog.show(BrioBaseActivity.this, getString(R.string.please_wait), getString(R.string.initializing));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                    }
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            //statusEditText.setText(getString(R.string.serial_connected));
                        }
                    });
                }
            }).start();
            */
        }

        @Override
        public void onSerialDisconnected() {
            //setStatus(getString(R.string.serial_disconnected));
        }

        @Override
        public void onBarcodeReaderConnected() {
        }

        @Override
        public void onBarcodeReaderDisconnected() {
        }

        @Override
        public void onReturnBarcode(String arg0) {
        }

        @Override
        public void onRequestDisplayLEDIndicator(BBDeviceController.ContactlessStatus arg0) {
        }

        @Override
        public void onRequestProduceAudioTone(BBDeviceController.ContactlessStatusTone arg0) {
        }

        @Override
        public void onReturnReadAIDResult(Hashtable<String, Object> data) {
            String text = "read_aid_success" ;//getString(R.string.read_aid_success);

            Object[] keys = data.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                text += "\n" + key + " : ";
                Object obj = data.get(key);
                if (obj instanceof String) {
                    text += (String)obj;
                } else if (obj instanceof Boolean) {
                    text += obj + "";
                } else if (obj instanceof BBDeviceController.TerminalSettingStatus) {
                    text += obj + "";
                }
            }
            _DialogoDispositivo.Estado(text);

            //setStatus(text);
        }

        @Override
        public void onReturnUpdateAIDResult(Hashtable<String, BBDeviceController.TerminalSettingStatus> data) {
            String text = "update_aid_success";//= getString(R.string.update_aid_success);

            Object[] keys = data.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                text += "\n" + key + " : ";
                Object obj = data.get(key);
                if (obj instanceof String) {
                    text += (String)obj;
                } else if (obj instanceof Boolean) {
                    text += obj + "";
                } else if (obj instanceof BBDeviceController.TerminalSettingStatus) {
                    text += obj + "";
                }
            }
            _DialogoDispositivo.Estado(text);
            // ded.Estado(text);
        }

        @Override
        public void onDeviceReset() {
            //setStatus(getString(R.string.device_reset));
            //ded.Estado("Dispositivo iniciado");
            _DialogoDispositivo.Estado("Se reinicio el dispositivo");
        }

        @Override
        public void onPowerButtonPressed() {
            //setStatus(getString(R.string.power_button_pressed));
            _DialogoDispositivo.Estado("Boton de apagado presionado");
        }

        @Override
        public void onPowerDown() {
            //ded.Estado("Dispositivo apagado");
            //setStatus(getString(R.string.power_down));
            _DialogoDispositivo.Estado("Boton apagar abajo");
        }
    }
}
