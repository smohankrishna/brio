package lat.brio.api.dongle;

import android.bluetooth.BluetoothDevice;

import com.bbpos.bbdevice.BBDeviceController;
import com.bbpos.bbdevice.CAPK;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Joe.Tan on 10/7/2016.
 */
public class SDKListener implements BBDeviceController.BBDeviceControllerListener {

    public void onConnected(Object device) {

    }

    public void onConnectFail() {

    }

    public void onDisconnected() {

    }

    public void onInitialCompleted() {

    }

    public void onReturnDeviceList(List<Object> list) {

    }

    @Override
    public void onWaitingForCard(BBDeviceController.CheckCardMode checkCardMode) {

    }

    @Override
    public void onWaitingReprintOrPrintNext() {

    }

    @Override
    public void onBTReturnScanResults(List<BluetoothDevice> list) {

    }

    @Override
    public void onBTScanTimeout() {

    }

    @Override
    public void onBTScanStopped() {

    }

    @Override
    public void onBTConnected(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void onBTDisconnected() {

    }

    @Override
    public void onUsbConnected() {

    }

    @Override
    public void onUsbDisconnected() {

    }

    @Override
    public void onSerialConnected() {

    }

    @Override
    public void onSerialDisconnected() {

    }

    @Override
    public void onReturnCheckCardResult(BBDeviceController.CheckCardResult checkCardResult, Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnCancelCheckCardResult(boolean b) {

    }

    @Override
    public void onReturnDeviceInfo(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnTransactionResult(BBDeviceController.TransactionResult transactionResult) {

    }

    @Override
    public void onReturnBatchData(String s) {

    }

    @Override
    public void onReturnReversalData(String s) {

    }

    @Override
    public void onReturnAmountConfirmResult(boolean b) {

    }

    @Override
    public void onReturnPinEntryResult(BBDeviceController.PinEntryResult pinEntryResult, Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnPrintResult(BBDeviceController.PrintResult printResult) {

    }

    @Override
    public void onReturnAmount(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnUpdateAIDResult(Hashtable<String, BBDeviceController.TerminalSettingStatus> hashtable) {

    }

    @Override
    public void onReturnUpdateGprsSettingsResult(boolean b, Hashtable<String, BBDeviceController.TerminalSettingStatus> hashtable) {

    }

    @Override
    public void onReturnUpdateTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus) {

    }

    @Override
    public void onReturnUpdateWiFiSettingsResult(boolean b, Hashtable<String, BBDeviceController.TerminalSettingStatus> hashtable) {

    }

    @Override
    public void onReturnReadAIDResult(Hashtable<String, Object> hashtable) {

    }

    @Override
    public void onReturnReadGprsSettingsResult(boolean b, Hashtable<String, Object> hashtable) {

    }

    @Override
    public void onReturnReadTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus, String s) {

    }

    @Override
    public void onReturnReadWiFiSettingsResult(boolean b, Hashtable<String, Object> hashtable) {

    }

    @Override
    public void onReturnEnableInputAmountResult(boolean b) {

    }

    @Override
    public void onReturnCAPKList(List<CAPK> list) {

    }

    @Override
    public void onReturnCAPKDetail(CAPK capk) {

    }

    @Override
    public void onReturnCAPKLocation(String s) {

    }

    @Override
    public void onReturnUpdateCAPKResult(boolean b) {

    }

    @Override
    public void onReturnEmvReportList(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnEmvReport(String s) {

    }

    @Override
    public void onReturnDisableInputAmountResult(boolean b) {

    }

    @Override
    public void onReturnPhoneNumber(BBDeviceController.PhoneEntryResult phoneEntryResult, String s) {

    }

    @Override
    public void onReturnEmvCardDataResult(boolean b, String s) {

    }

    @Override
    public void onReturnEmvCardNumber(boolean b, String s) {

    }

    @Override
    public void onReturnEncryptPinResult(boolean b, Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnEncryptDataResult(boolean b, Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnInjectSessionKeyResult(boolean b, Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnPowerOnIccResult(boolean b, String s, String s1, int i) {

    }

    @Override
    public void onReturnPowerOffIccResult(boolean b) {

    }

    @Override
    public void onReturnApduResult(boolean b, Hashtable<String, Object> hashtable) {

    }

    @Override
    public void onRequestSelectApplication(ArrayList<String> arrayList) {

    }

    @Override
    public void onRequestSetAmount() {

    }

    @Override
    public void onRequestPinEntry(BBDeviceController.PinEntrySource pinEntrySource) {

    }

    @Override
    public void onRequestOnlineProcess(String s) {

    }

    @Override
    public void onRequestTerminalTime() {

    }

    @Override
    public void onRequestDisplayText(BBDeviceController.DisplayText displayText) {

    }

    @Override
    public void onRequestDisplayAsterisk(int i) {

    }

    @Override
    public void onRequestDisplayLEDIndicator(BBDeviceController.ContactlessStatus contactlessStatus) {

    }

    @Override
    public void onRequestProduceAudioTone(BBDeviceController.ContactlessStatusTone contactlessStatusTone) {

    }

    @Override
    public void onRequestClearDisplay() {

    }

    @Override
    public void onRequestFinalConfirm() {

    }

    @Override
    public void onRequestPrintData(int i, boolean b) {

    }

    @Override
    public void onPrintDataCancelled() {

    }

    @Override
    public void onPrintDataEnd() {

    }

    @Override
    public void onBatteryLow(BBDeviceController.BatteryStatus batteryStatus) {

    }

    @Override
    public void onAudioDevicePlugged() {

    }

    @Override
    public void onAudioDeviceUnplugged() {

    }

    @Override
    public void onError(BBDeviceController.Error error, String s) {

    }

    @Override
    public void onSessionInitialized() {

    }

    @Override
    public void onSessionError(BBDeviceController.SessionError sessionError, String s) {

    }

    @Override
    public void onAudioAutoConfigProgressUpdate(double v) {

    }

    @Override
    public void onAudioAutoConfigCompleted(boolean b, String s) {

    }

    @Override
    public void onAudioAutoConfigError(BBDeviceController.AudioAutoConfigError audioAutoConfigError) {

    }

    @Override
    public void onNoAudioDeviceDetected() {

    }

    @Override
    public void onDeviceHere(boolean b) {

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

    }

    @Override
    public void onReturnNfcDetectCardResult(BBDeviceController.NfcDetectCardResult nfcDetectCardResult, Hashtable<String, Object> hashtable) {

    }

    @Override
    public void onBarcodeReaderConnected() {

    }

    @Override
    public void onBarcodeReaderDisconnected() {

    }

    @Override
    public void onReturnBarcode(String s) {

    }
}
