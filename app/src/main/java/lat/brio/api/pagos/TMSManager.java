package lat.brio.api.pagos;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

import static android.R.attr.onClick;

/**
 * Created by Joe.Tan on 11/16/2016.
 */

public class TMSManager {

    private static TMSManager instance;

    public static TMSManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new TMSManager(ctx);
        }

        return instance;
    }

    public static TMSManager getInstance() {
        return instance;
    }



    private HashMap<String, String> defaultServerSettings = new HashMap<String, String>() {{
            put("Gateway IP Address", "https://52.220.0.234:443/gateway-service");
            put("Enable Sale With Tip", "0");
            put("Tip Amount", "0,5,10,20");
            put("Void", "1");
            put("Refund", "1");
            put("Auth/Auth Comp", "1");
            put("Payment type", "CREDIT_CARD,1,0;GIFT_CARD,1,1;KEYED,1,2;WECHAT_PAY,1,3;DEBIT_CARD,1,4;BBPOS_GIFT,1,5;CASH,1,6");
            put("Card Accepted", "Visa,1;MasterCard,1;American Express,1;JCB,1;UnionPay,1");
            put("AmountDetailFlag", "1");
            put("HwEMV", "1");
            put("HwMagstripe", "1");
            put("HwNFC", "1");
            put("HwPinpad", "1");
            put("hostResponseTimeOut", "60");
            put("screenTimeOut", "240");
            put("connectRetry", "2");
            put("hbInterval", "900");
            put("Developer Mode", "0");
            put("DemoFlag", "0");
            put("LogEnableFlag", "1");
        }
    };


    private SharedPreferences preferences;

    public TMSManager(Context ctx) {
        preferences = ctx.getSharedPreferences("ServerSettings", Context.MODE_PRIVATE);
    }

    public String getSettingValue(String key)
    {
        return preferences.getString(key, defaultServerSettings.get(key)); //return default value if not exist
    }

    public boolean setSettingValue(String key, String value)
    {
        return preferences.edit().putString(key, value).commit();
    }

    public String[] getKeyset() {
        String[] keys = new String[]{};
        keys = defaultServerSettings.keySet().toArray(keys);
        return keys;
    }
}
