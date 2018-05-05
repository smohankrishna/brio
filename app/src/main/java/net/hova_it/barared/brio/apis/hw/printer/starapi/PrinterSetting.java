package net.hova_it.barared.brio.apis.hw.printer.starapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;


/**
 * Printer information.
 * <p>
 * Parte del SDK de la impresora STAR TSP100 FuturePRNT.
 * La impresora requiere las librerías StarIOPort3.1.jar y StarIO_Extension.jar en app/libs.
 */
public class PrinterSetting {
    /**
     * The constant typeBluetoothIF.
     */
    public static final String typeBluetoothIF = "BT:";
    /**
     * The constant typeUsbIF.
     */
    public static final String typeUsbIF       = "USB:";
    /**
     * The constant typeEthernetIF.
     */
    public static final String typeEthernetIF  = "TCP:";

    /**
     * The constant PREF_KEY_DEVICE_NAME.
     */
    public static final String PREF_KEY_DEVICE_NAME  = "pref_key_device_name";
    /**
     * The constant PREF_KEY_PRINTER_TYPE.
     */
    public static final String PREF_KEY_PRINTER_TYPE = "pref_key_printertype";
    /**
     * The constant PREF_KEY_MAC_ADDRESS.
     */
    public static final String PREF_KEY_MAC_ADDRESS  = "pref_key_mac_address";

    /**
     * The constant PREF_KEY_EMULATION.
     */
    public static final String PREF_KEY_EMULATION  = "pref_key_emulation";

    private Context mContext;


 

    /**
     * Instantiates a new Printer setting.
     *
     * @param context the context
     */
    public PrinterSetting(Context context) {
        mContext = context;
    }

    /**
     * Escribir a un puerto
     *
     * @param portName   the port name
     * @param macAddress the mac address
     */
    public void write(String portName, String macAddress) {
        write(portName, macAddress, "");
    }

    /**
     * Escribir a un puerto
     *
     * @param portName    the port name
     * @param macAddress  the mac address
     * @param printerType the printer type
     */
    public void write(String portName, String macAddress, String printerType) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        prefs.edit()
                .putString(PREF_KEY_DEVICE_NAME, portName)
                .putString(PREF_KEY_MAC_ADDRESS, macAddress)
                .putString(PREF_KEY_PRINTER_TYPE, printerType)
                .apply();
    }

    /**
     * Obtener el nombre del puerto.
     *
     * @return port name
     */
    public String getPortName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        String macAddress = prefs.getString(PREF_KEY_MAC_ADDRESS, "");

        // --- Bluetooth ---
        // It can communication used device name(Ex.BT:Star Micronics) at bluetooth.
        // If android device has paired two same name device, can't choose destination target.
        // If used Mac Address(Ex. BT:00:12:3f:XX:XX:XX) at Bluetooth, can choose destination target.
        if (macAddress.startsWith("BT:")) {
            return macAddress;
        }

        return prefs.getString(PREF_KEY_DEVICE_NAME, "");
    }

    /**
     * Obtener el nombre de la impresora.
     *
     * @return device name
     */
    public String getDeviceName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getString(PREF_KEY_DEVICE_NAME, "");
    }

    /**
     * Obtener la dirección MAC de la impresora.
     *
     * @return mac address
     */
    public String getMacAddress() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getString(PREF_KEY_MAC_ADDRESS, "");
    }

    /**
     * Obtener el tipo de la impresora conectada
     *
     * @return printer type
     */
    public String getPrinterType() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getString(PREF_KEY_PRINTER_TYPE, "");
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public Context getContext() {
        return mContext;
    }

   

}
