package net.hova_it.barared.brio.apis.network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;


/**
 * Created by Alejandro on 21/03/2016.
 */
public class NetworkStatus {
    private final static String TAG = "BRIO_LOG";

    private static ConnectivityManager getConnectivityManager(Context context){
        return (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private static WifiManager getWifiManager(Context context){
        return (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }

    public static NetworkInfo getNetworkInfo(Context context){
        return getConnectivityManager(context).getActiveNetworkInfo();
    }

    public static WifiInfo getWiFiInfo(Context context){
        return getWifiManager(context).getConnectionInfo();
    }

    public static DhcpInfo getDhcpInfo(Context context){
        return getWifiManager(context).getDhcpInfo();
    }

    public static String testNetwork(Context context){
        String out = "Network Test...\n";
        NetworkInfo networkInfo = getNetworkInfo(context);

        if (networkInfo != null) {
            out += "NetworkInfo(getExtraInfo): " + networkInfo.getExtraInfo() + "\n";
            out += "NetworkInfo(getType): " + networkInfo.getType() + "\n";
            out += "NetworkInfo(getTypeName): " + networkInfo.getTypeName() + "\n";
            out += "NetworkInfo(getSubtype): " + networkInfo.getSubtype() + "\n";
            out += "NetworkInfo(getSubtypeName): " + networkInfo.getSubtypeName() + "\n";
            out += "NetworkInfo(getReason): " + networkInfo.getReason() + "\n";

            out += "NetworkInfo(isAvailable): " + networkInfo.isAvailable() + "\n";
            out += "NetworkInfo(isConnected): " + networkInfo.isConnected() + "\n";

            NetworkInfo.State state = networkInfo.getState();
            out += "NetworkInfo 'State' (name): " + state.name() + "\n";

            if(networkInfo.getType() == 1){
                WifiInfo wifiInfo = getWiFiInfo(context);
                out += "NetworkInfo 'WifiInfo' (getSSID): " + wifiInfo.getSSID() + "\n";
                out += "NetworkInfo 'WifiInfo' (getMacAddress): " + wifiInfo.getMacAddress() + "\n";
                out += "NetworkInfo 'WifiInfo' (getIpAddress): " + intToIP(wifiInfo.getIpAddress()) + "\n";
                DhcpInfo dhcpInfo = getDhcpInfo(context);
                out += "NetworkInfo 'DhcpInfo' (ipAddress): " + intToIP(dhcpInfo.ipAddress) + "\n";
                out += "NetworkInfo 'DhcpInfo' (gateway): " + intToIP(dhcpInfo.gateway) + "\n";
            }

        }else{
            out += "Network Not Available!" + "\n";
        }

        return out;
    }

    /**
     * Diagnosticar si el wifi esta encendido.
     * @param context
     * @return
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        if(networkInfo == null) { return false; }
        return networkInfo.isAvailable();
    }

    /**
     * Diagnosticar si el dispositivo esta conectado a alguna red.
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        if(networkInfo == null) { return false; }
        return networkInfo.isConnected();
    }

    /**
     * Diagnosticar si la red en la que esta conectado el dispositivo cuenta con acceso a internet.
     * @param context
     * @return
     */
    public static boolean hasInternetAccess(Context context) {
        boolean hasAccess;
        try {
            hasAccess = (isAvailable(context) && isConnected(context));

        } catch (Exception e) {
            hasAccess = false;
        }

        return hasAccess;
    }

    /**
     * Convierte un entero a octetos.
     * @param ip
     * @return
     */
    public static String intToIP(int ip){
        return String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
    }

    public static void setNeverSleepWifi(final AppCompatActivity activity) {
        boolean wifiNeverSleep = false;
        try {

            int setting = android.provider.Settings.Global.getInt(activity.getContentResolver(), android.provider.Settings.Global.WIFI_SLEEP_POLICY);
            if(setting != android.provider.Settings.Global.WIFI_SLEEP_POLICY_NEVER) {
                //android.provider.Settings.Global.putInt(activity.getContentResolver(), android.provider.Settings.Global.WIFI_SLEEP_POLICY, android.provider.Settings.Global.WIFI_SLEEP_POLICY_NEVER);
            } else {
                wifiNeverSleep = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            wifiNeverSleep = false;
        }

        //Toast.makeText(activity, "Wifi never sleep " + wifiNeverSleep, Toast.LENGTH_SHORT).show();

        if(!wifiNeverSleep) {
            BrioConfirmDialog bcd = new BrioConfirmDialog(activity,
                    "Configuración de Brío",
                    "La opción \"Mantener Wi-Fi durante suspensión\" de la configuración avanzada de Wi-Fi debe establecerse en \"Siempre\".'",
                    new String[]{"Cancelar", "Configurar"},
                    new DialogListener() {
                @Override
                public void onAccept() {
                    activity.startActivity(new Intent(Settings.ACTION_WIFI_IP_SETTINGS));
                }

                @Override
                public void onCancel() {

                }
            });
            bcd.show();
        }
    }
}
