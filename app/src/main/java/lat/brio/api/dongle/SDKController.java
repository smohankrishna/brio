package lat.brio.api.dongle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.bbpos.bbdevice.BBDeviceController;
import com.bbpos.bbdevice.CAPK;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Joe.Tan on 10/7/2016.
 */
public class SDKController implements BBDeviceController.BBDeviceControllerListener {

    private static final String CONNECTION_MODE = "ConnectionMode";
    private static final String ADDRESS = "connectBT";

    private List<SDKListener> eventListeners = new ArrayList<>();
    private BBDeviceController bbDeviceController;
    private Hashtable<String, String> deviceInfo;

    private boolean mIsInitializing = false;
    private SharedPreferences mConfig;
    private final Context mContext;

    private static SDKController instance = null;
    public static SDKController getInstance(Context ctx) {
        if (instance != null)
            return instance;

        instance = new SDKController(ctx);
        return instance;
    }

    public static SDKController getInstance() {
        return instance;
    }

    public SDKController(Context ctx) {
        mContext = ctx;
        bbDeviceController = BBDeviceController.getInstance(ctx, this);
        BBDeviceController.setDebugLogEnabled(true);
        mConfig = ctx.getSharedPreferences("DeviceInfo", Context.MODE_PRIVATE);
    }

    public boolean isConnected() {
        BBDeviceController.ConnectionMode mode = bbDeviceController.getConnectionMode();
        if (mode != BBDeviceController.ConnectionMode.AUDIO) {
            return (bbDeviceController.getConnectionMode() != BBDeviceController.ConnectionMode.NONE);
        } else {
            return deviceInfo != null;
        }
    }

    public boolean isInitializing() {
        return mIsInitializing;
    }

    public boolean isWisePosMode() {
        return Build.MODEL.startsWith("BBPOS_WSC");
    }

    public boolean isHeadsetOn() {
        AudioManager localAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return localAudioManager.isWiredHeadsetOn();
    }

    public boolean start() {
        if (isWisePosMode()) {
            bbDeviceController.setDetectAudioDevicePlugged(false);
            bbDeviceController.startSerial();
            return true;
        }

        String str = mConfig.getString(CONNECTION_MODE, BBDeviceController.ConnectionMode.NONE.toString());
        BBDeviceController.ConnectionMode mode = BBDeviceController.ConnectionMode.valueOf(str);
        switch (mode) {
            case AUDIO: {
                bbDeviceController.startAudio();
                bbDeviceController.setDetectAudioDevicePlugged(true);
                if (isHeadsetOn()) {
                    bbDeviceController.getDeviceInfo();
                }
                break;
            }
            case BLUETOOTH: {
                String address = mConfig.getString(ADDRESS, null);
                if (address == null)
                    return false;

                BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                bbDeviceController.setDetectAudioDevicePlugged(false);
                bbDeviceController.connectBT(device);
                break;
            }
            case USB: {
                bbDeviceController.setDetectAudioDevicePlugged(false);
                bbDeviceController.startUsb();
                break;
            }
            default: {

            }
                return false;
        }

        Log.i("BB_SDK", "start mode:" + mode.toString());

        return true;
    }

    public void stop() {
        BBDeviceController.ConnectionMode mode = bbDeviceController.getConnectionMode();
        switch (mode) {
            case SERIAL:
                bbDeviceController.stopSerial();
                break;
            case BLUETOOTH:
                bbDeviceController.disconnectBT();
                break;
            case AUDIO:
                bbDeviceController.stopAudio();
                break;
            case USB:
                bbDeviceController.stopUsb();
                break;
        }

        bbDeviceController.setDetectAudioDevicePlugged(false);
        deviceInfo = null;
    }

    public void startBTScan(String[] deviceName, int scanTimeout) {
        bbDeviceController.startBTScan(deviceName, scanTimeout);
    }

    public void stopBTScan() {
        bbDeviceController.stopBTScan();
    }

    private Runnable mInitialCompleted = new Runnable() {
        @Override
        public void run() {
            mIsInitializing = false;
            for (SDKListener eventListener: eventListeners) {
                eventListener.onInitialCompleted();
            }
        }
    };

    private void startCompleted(Object device) {
        mIsInitializing = true;
        for (SDKListener eventListener: eventListeners) {
            eventListener.onConnected(device);
        }

        int time = 500;
        if (bbDeviceController.getConnectionMode() == BBDeviceController.ConnectionMode.SERIAL) {
            time = 10000;
        }

        new Handler().postDelayed(mInitialCompleted, time);
    }

    public void setConnectionMode(BBDeviceController.ConnectionMode mode, String address) {
        SharedPreferences.Editor editor = mConfig.edit();
        editor.putString(CONNECTION_MODE, mode.toString());
        if (address != null) {
            editor.putString(ADDRESS, address);
        } else {
            editor.remove(ADDRESS);
        }
        editor.commit();
    }

    public void startEmv(BBDeviceController.CheckCardMode checkCardMode) {
        Hashtable<String, Object> data = new Hashtable<String, Object>();
        data.put("emvOption", BBDeviceController.EmvOption.START);
        data.put("checkCardMode", checkCardMode);
        String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
        data.put("terminalTime", terminalTime);
        bbDeviceController.startEmv(data);
    }

    public void checkCard(BBDeviceController.CheckCardMode checkCardMode) {
        Hashtable<String, Object> data = new Hashtable<String, Object>();
        data.put("emvOption", BBDeviceController.EmvOption.START);
        data.put("checkCardMode", checkCardMode);
        String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
        data.put("terminalTime", terminalTime);
        bbDeviceController.checkCard(data);
    }

    public void cancel() {
        bbDeviceController.cancelCheckCard();
        bbDeviceController.cancelPinEntry();
        bbDeviceController.cancelSetAmount();
    }

    public void enterPin() {
        Hashtable<String, Object> data = new Hashtable<String, Object>();
        data.put("pinEntryTimeout", 120);
        bbDeviceController.startPinEntry(data);
    }

    public Hashtable<String, String> decodeTLV(String tlv) {
        return BBDeviceController.decodeTlv(tlv);
    }

    public BBDeviceController.ConnectionMode getConnectionMode() {
        return bbDeviceController.getConnectionMode();
    }

    public Hashtable<String, String> getDeviceInfo() {
        if (deviceInfo == null && isConnected()) {
            bbDeviceController.getDeviceInfo();
        }

        return deviceInfo;
    }

    public void setAmount(String amount) {
        BBDeviceController.TransactionType transactionType = BBDeviceController.TransactionType.GOODS;
        BBDeviceController.CurrencyCharacter[] currencyCharacter = new BBDeviceController.CurrencyCharacter[] {
                BBDeviceController.CurrencyCharacter.M, BBDeviceController.CurrencyCharacter.X, BBDeviceController.CurrencyCharacter.N
        };
        bbDeviceController.setAmount(amount, "0", "840", transactionType, currencyCharacter);
    }

    public void sendOnlineProcessResult(String tlv) {
        bbDeviceController.sendOnlineProcessResult(tlv);
    }

    public void sendFinalConfirmResult(boolean isConfirmed) {
        bbDeviceController.sendFinalConfirmResult(isConfirmed);
    }

    public static void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e("BB_SDK", e.getMessage());
        }
    }

    public void unpairDevice() {
        String address = mConfig.getString(ADDRESS, null);
        if (address == null)
            return;

        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        unpairDevice(device);
    }

    /*
    Note this function will replace any existing eventlistener with the new one
    If chain event want to support, use arrayList
    */
    public void RegisterSDKEventListener(SDKListener eventListener)
    {
        if (!eventListeners.contains(eventListener))
            eventListeners.add(eventListener);
    }

    public void UnregisterSDKEventListener(SDKListener eventListener)
    {
        if (eventListeners.contains(eventListener))
            eventListeners.remove(eventListener);
    }

    @Override
    public void onWaitingForCard(BBDeviceController.CheckCardMode checkCardMode) {
        for (SDKListener eventListener: eventListeners) {
            eventListener.onWaitingForCard(checkCardMode);
        }
    }

    @Override
    public void onWaitingReprintOrPrintNext() {
        for (SDKListener eventListener: eventListeners) {
            eventListener.onWaitingReprintOrPrintNext();
        }
    }

    @Override
    public void onBTReturnScanResults(List<BluetoothDevice> list) {
        List<Object> devices = new ArrayList<>();
        for (BluetoothDevice device: list) {
            devices.add(device);
        }

        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnDeviceList(devices);
        }
    }

    @Override
    public void onBTScanTimeout() {
        for (SDKListener eventListener: eventListeners) {
            eventListener.onBTScanTimeout();
        }
    }

    @Override
    public void onBTScanStopped() {
        for (SDKListener eventListener: eventListeners) {
            eventListener.onBTScanStopped();
        }
    }

    @Override
    public void onBTConnected(BluetoothDevice bluetoothDevice) {
        setConnectionMode(BBDeviceController.ConnectionMode.BLUETOOTH, bluetoothDevice.getAddress());
        startCompleted(bluetoothDevice);
    }

    @Override
    public void onBTDisconnected() {
        deviceInfo = null;
        for (SDKListener eventListener: eventListeners) {
            eventListener.onDisconnected();
        }
    }

    @Override
    public void onUsbConnected() {
        setConnectionMode(BBDeviceController.ConnectionMode.USB, null);
        startCompleted(null);
    }

    @Override
    public void onUsbDisconnected() {
        deviceInfo = null;
        for (SDKListener eventListener: eventListeners) {
            eventListener.onDisconnected();
        }
    }

    @Override
    public void onSerialConnected() {
        setConnectionMode(BBDeviceController.ConnectionMode.SERIAL, null);
        startCompleted(null);
    }

    @Override
    public void onSerialDisconnected() {
        deviceInfo = null;
        for (SDKListener eventListener: eventListeners) {
            eventListener.onDisconnected();
        }
    }

    @Override
    public void onReturnCheckCardResult(BBDeviceController.CheckCardResult checkCardResult, Hashtable<String, String> hashtable) {
        Log.i("BB_SDK", "1, onReturnCheckCardResult:" + checkCardResult.toString());
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnCheckCardResult(checkCardResult, hashtable);
        }
    }

    @Override
    public void onReturnCancelCheckCardResult(boolean b) {
        Log.i("BB_SDK", "2");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnCancelCheckCardResult(b);
        }
    }

    @Override
    public void onReturnDeviceInfo(Hashtable<String, String> hashtable) {
        Log.i("BB_SDK", "onReturnDeviceInfo");
        deviceInfo = (Hashtable<String, String>) hashtable.clone();

        BBDeviceController.ConnectionMode mode = bbDeviceController.getConnectionMode();
        if ((mode == BBDeviceController.ConnectionMode.AUDIO) && (deviceInfo == null)) {
            setConnectionMode(BBDeviceController.ConnectionMode.AUDIO, null);
            startCompleted(null);
        }

        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnDeviceInfo(hashtable);
        }
    }

    @Override
    public void onReturnTransactionResult(BBDeviceController.TransactionResult transactionResult) {
        Log.i("BB_SDK", "3");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnTransactionResult(transactionResult);
        }
    }

    @Override
    public void onReturnBatchData(String s) {
        Log.i("BB_SDK", "4");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnBatchData(s);
        }
    }

    @Override
    public void onReturnReversalData(String s) {
        Log.i("BB_SDK", "5");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnReversalData(s);
        }
    }

    @Override
    public void onReturnAmountConfirmResult(boolean b) {
        Log.i("BB_SDK", "6");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnAmountConfirmResult(b);
        }
    }

    @Override
    public void onReturnPinEntryResult(BBDeviceController.PinEntryResult pinEntryResult, Hashtable<String, String> hashtable) {
        Log.i("BB_SDK", "7");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnPinEntryResult(pinEntryResult, hashtable);
        }
    }

    @Override
    public void onReturnPrintResult(BBDeviceController.PrintResult printResult) {
        Log.i("BB_SDK", "8");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnPrintResult(printResult);
        }
    }

    @Override
    public void onReturnAmount(Hashtable<String, String> hashtable) {
        Log.i("BB_SDK", "9");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnAmount(hashtable);
        }
    }

    @Override
    public void onReturnUpdateAIDResult(Hashtable<String, BBDeviceController.TerminalSettingStatus> hashtable) {

    }

    @Override
    public void onReturnUpdateGprsSettingsResult(boolean b, Hashtable<String, BBDeviceController.TerminalSettingStatus> hashtable) {
        Log.i("BB_SDK", "10");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnUpdateGprsSettingsResult(b, hashtable);
        }
    }

    @Override
    public void onReturnUpdateTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus) {
        Log.i("BB_SDK", "11");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnUpdateTerminalSettingResult(terminalSettingStatus);
        }
    }

    @Override
    public void onReturnUpdateWiFiSettingsResult(boolean b, Hashtable<String, BBDeviceController.TerminalSettingStatus> hashtable) {
        Log.i("BB_SDK", "12");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnUpdateWiFiSettingsResult(b, hashtable);
        }
    }

    @Override
    public void onReturnReadAIDResult(Hashtable<String, Object> hashtable) {

    }

    @Override
    public void onReturnReadGprsSettingsResult(boolean b, Hashtable<String, Object> hashtable) {
        Log.i("BB_SDK", "13");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnReadGprsSettingsResult(b, hashtable);
        }
    }

    @Override
    public void onReturnReadTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus, String s) {
        Log.i("BB_SDK", "14");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnReadTerminalSettingResult(terminalSettingStatus, s);
        }
    }

    @Override
    public void onReturnReadWiFiSettingsResult(boolean b, Hashtable<String, Object> hashtable) {
        Log.i("BB_SDK", "15");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnReadWiFiSettingsResult(b, hashtable);
        }
    }

    @Override
    public void onReturnEnableInputAmountResult(boolean b) {
        Log.i("BB_SDK", "16");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnEnableInputAmountResult(b);
        }
    }

    @Override
    public void onReturnCAPKList(List<CAPK> list) {
        Log.i("BB_SDK", "17");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnCAPKList(list);
        }
    }

    @Override
    public void onReturnCAPKDetail(CAPK capk) {
        Log.i("BB_SDK", "18");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnCAPKDetail(capk);
        }
    }

    @Override
    public void onReturnCAPKLocation(String s) {
        Log.i("BB_SDK", "19");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnCAPKLocation(s);
        }
    }

    @Override
    public void onReturnUpdateCAPKResult(boolean b) {
        Log.i("BB_SDK", "20");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnUpdateCAPKResult(b);
        }
    }

    @Override
    public void onReturnEmvReportList(Hashtable<String, String> hashtable) {
        Log.i("BB_SDK", "21");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnEmvReportList(hashtable);
        }
    }

    @Override
    public void onReturnEmvReport(String s) {
        Log.i("BB_SDK", "22");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnEmvReport(s);
        }
    }

    @Override
    public void onReturnDisableInputAmountResult(boolean b) {
        Log.i("BB_SDK", "23");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnDisableInputAmountResult(b);
        }
    }

    @Override
    public void onReturnPhoneNumber(BBDeviceController.PhoneEntryResult phoneEntryResult, String s) {
        Log.i("BB_SDK", "24");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnPhoneNumber(phoneEntryResult, s);
        }
    }

    @Override
    public void onReturnEmvCardDataResult(boolean b, String s) {
        Log.i("BB_SDK", "25");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnEmvCardDataResult(b, s);
        }
    }

    @Override
    public void onReturnEmvCardNumber(boolean b, String s) {
        Log.i("BB_SDK", "26");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnEmvCardNumber(b, s);
        }
    }

    @Override
    public void onReturnEncryptPinResult(boolean b, Hashtable<String, String> hashtable) {
        Log.i("BB_SDK", "27");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnEncryptPinResult(b, hashtable);
        }
    }

    @Override
    public void onReturnEncryptDataResult(boolean b, Hashtable<String, String> hashtable) {
        Log.i("BB_SDK", "28");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnEncryptDataResult(b, hashtable);
        }
    }

    @Override
    public void onReturnInjectSessionKeyResult(boolean b, Hashtable<String, String> hashtable) {
        Log.i("BB_SDK", "29");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnInjectSessionKeyResult(b, hashtable);
        }
    }

    @Override
    public void onReturnPowerOnIccResult(boolean b, String s, String s1, int i) {
        Log.i("BB_SDK", "30");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnPowerOnIccResult(b, s, s1, i);
        }
    }

    @Override
    public void onReturnPowerOffIccResult(boolean b) {
        Log.i("BB_SDK", "31");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnPowerOffIccResult(b);
        }
    }

    @Override
    public void onReturnApduResult(boolean b, Hashtable<String, Object> hashtable) {
        Log.i("BB_SDK", "32");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnApduResult(b, hashtable);
        }
    }

    @Override
    public void onRequestSelectApplication(ArrayList<String> arrayList) {
        Log.i("BB_SDK", "33");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onRequestSelectApplication(arrayList);
        }
    }

    @Override
    public void onRequestSetAmount() {
        Log.i("BB_SDK", "34");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onRequestSetAmount();
        }
    }

    @Override
    public void onRequestPinEntry(BBDeviceController.PinEntrySource pinEntrySource) {
        Log.i("BB_SDK", "35");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onRequestPinEntry(pinEntrySource);
        }
    }

    @Override
    public void onRequestOnlineProcess(String s) {
        Log.i("BB_SDK", "onRequestOnlineProcess");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onRequestOnlineProcess(s);
        }
    }

    @Override
    public void onRequestTerminalTime() {
        Log.i("BB_SDK", "onRequestTerminalTime");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onRequestTerminalTime();
        }
    }

    @Override
    public void onRequestDisplayText(BBDeviceController.DisplayText displayText) {
        Log.i("BB_SDK", "36, displayText:" + displayText.toString());
        for (SDKListener eventListener: eventListeners) {
            eventListener.onRequestDisplayText(displayText);
        }
    }

    @Override
    public void onRequestDisplayAsterisk(int i) {
        Log.i("BB_SDK", "37");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onRequestDisplayAsterisk(i);
        }
    }

    @Override
    public void onRequestDisplayLEDIndicator(BBDeviceController.ContactlessStatus contactlessStatus) {

    }

    @Override
    public void onRequestProduceAudioTone(BBDeviceController.ContactlessStatusTone contactlessStatusTone) {

    }

    @Override
    public void onRequestClearDisplay() {
        Log.i("BB_SDK", "38");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onRequestTerminalTime();
        }
    }

    @Override
    public void onRequestFinalConfirm() {
        Log.i("BB_SDK", "onRequestFinalConfirm");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onRequestFinalConfirm();
        }
    }

    @Override
    public void onRequestPrintData(int i, boolean b) {
        Log.i("BB_SDK", "39");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onRequestPrintData(i, b);
        }
    }

    @Override
    public void onPrintDataCancelled() {
        Log.i("BB_SDK", "40");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onPrintDataCancelled();
        }
    }

    @Override
    public void onPrintDataEnd() {
        Log.i("BB_SDK", "41");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onPrintDataEnd();
        }
    }

    @Override
    public void onBatteryLow(BBDeviceController.BatteryStatus batteryStatus) {
        Log.i("BB_SDK", "42");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onBatteryLow(batteryStatus);
        }
    }

    @Override
    public void onAudioDevicePlugged() {
        Log.i("BB_SDK", "43");
        if (getConnectionMode() != BBDeviceController.ConnectionMode.AUDIO) {
            return;
        }

        bbDeviceController.getDeviceInfo();
    }

    @Override
    public void onAudioDeviceUnplugged() {
        Log.i("BB_SDK", "44");
        if (getConnectionMode() != BBDeviceController.ConnectionMode.AUDIO) {
            return;
        }

        deviceInfo = null;
        for (SDKListener eventListener: eventListeners) {
            eventListener.onDisconnected();
        }
    }

    @Override
    public void onError(BBDeviceController.Error error, String s) {
        Log.i("BB_SDK", "45, error:" + error.toString());
        if (error.toString().startsWith("FAIL_TO_START")) {
            stop();
            for (SDKListener eventListener: eventListeners) {
                eventListener.onConnectFail();
            }
        } else {
            for (SDKListener eventListener: eventListeners) {
                eventListener.onError(error, s);
            }
        }


    }

    @Override
    public void onSessionInitialized() {
        Log.i("BB_SDK", "46");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onSessionInitialized();
        }
    }

    @Override
    public void onSessionError(BBDeviceController.SessionError sessionError, String s) {
        Log.i("BB_SDK", "47");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onSessionError(sessionError, s);
        }
    }

    @Override
    public void onAudioAutoConfigProgressUpdate(double v) {
        Log.i("BB_SDK", "48");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onAudioAutoConfigProgressUpdate(v);
        }
    }

    @Override
    public void onAudioAutoConfigCompleted(boolean b, String s) {
        Log.i("BB_SDK", "49");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onAudioAutoConfigCompleted(b, s);
        }
    }

    @Override
    public void onAudioAutoConfigError(BBDeviceController.AudioAutoConfigError audioAutoConfigError) {
        Log.i("BB_SDK", "50");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onAudioAutoConfigError(audioAutoConfigError);
        }
    }

    @Override
    public void onNoAudioDeviceDetected() {
        for (SDKListener eventListener: eventListeners) {
            eventListener.onNoAudioDeviceDetected();
        }
    }

    @Override
    public void onDeviceHere(boolean b) {
        Log.i("BB_SDK", "onDeviceHere");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onDeviceHere(b);
        }
    }

    @Override
    public void onPowerDown() {

    }

    @Override
    public void onPowerButtonPressed() {

    }

    @Override
    public void onDeviceReset() {

    }

    @Override
    public void onReturnNfcDataExchangeResult(boolean b, Hashtable<String, String> hashtable) {
        Log.i("BB_SDK", "51");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnNfcDataExchangeResult(b, hashtable);
        }
    }

    @Override
    public void onReturnNfcDetectCardResult(BBDeviceController.NfcDetectCardResult nfcDetectCardResult, Hashtable<String, Object> hashtable) {
        Log.i("BB_SDK", "52");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnNfcDetectCardResult(nfcDetectCardResult, hashtable);
        }
    }

    @Override
    public void onBarcodeReaderConnected() {
        Log.i("BB_SDK", "53");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onBarcodeReaderConnected();
        }
    }

    @Override
    public void onBarcodeReaderDisconnected() {
        Log.i("BB_SDK", "54");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onBarcodeReaderDisconnected();
        }
    }

    @Override
    public void onReturnBarcode(String s) {
        Log.i("BB_SDK", "55");
        for (SDKListener eventListener: eventListeners) {
            eventListener.onReturnBarcode(s);
        }
    }
}
