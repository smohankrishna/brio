package net.hova_it.barared.brio.apis.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.hova_it.barared.brio.apis.utils.Utils;

public class IniciaAppReceiver extends BroadcastReceiver {


    /**
     * Clase encargada de detectar cuando el dispositivo ha sido iniciado y iniciará la app
     *
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
    
        Utils.restartApp();
    }
}
