package net.hova_it.barared.brio.apis.utils.info;

/**
 * Created by Herman Peralta on 18/05/2016.
 * Extrae la información del sistema para la generacion del log de errores que se manda
 */
public class SystemInfoPojo {

    private String brioDate;
    private int androidSDK;
    private String androidCodename;
    private String androidDevice;
    private String androidModel;
    private String androidProduct;
    private String androidUUID;
    private String androidLanguaje;
    private String androidCodPais;
    private int androidCores;
    private long androidMemoryTotal;
    private long androidMemoryFree;
    private long androidMemoryUsed;

    public String getBrioDate() {
        return brioDate;
    }

    public void setBrioDate(String brioDate) {
        this.brioDate = brioDate;
    }

    public int getAndroidSDK() {
        return androidSDK;
    }

    public void setAndroidSDK(int androidSDK) {
        this.androidSDK = androidSDK;
    }

    public String getAndroidCodename() {
        return androidCodename;
    }

    public void setAndroidCodename(String androidCodename) {
        this.androidCodename = androidCodename;
    }

    public String getAndroidDevice() {
        return androidDevice;
    }

    public void setAndroidDevice(String androidDevice) {
        this.androidDevice = androidDevice;
    }

    public String getAndroidModel() {
        return androidModel;
    }

    public void setAndroidModel(String androidModel) {
        this.androidModel = androidModel;
    }

    public String getAndroidProduct() {
        return androidProduct;
    }

    public void setAndroidProduct(String androidProduct) {
        this.androidProduct = androidProduct;
    }

    public String getAndroidUUID() {
        return androidUUID;
    }

    public void setAndroidUUID(String androidUUID) {
        this.androidUUID = androidUUID;
    }

    public String getAndroidLanguaje() {
        return androidLanguaje;
    }

    public void setAndroidLanguaje(String androidLanguaje) {
        this.androidLanguaje = androidLanguaje;
    }

    public String getAndroidCodPais() {
        return androidCodPais;
    }

    public void setAndroidCodPais(String androidCodPais) {
        this.androidCodPais = androidCodPais;
    }

    public int getAndroidCores() {
        return androidCores;
    }

    public void setAndroidCores(int androidCores) {
        this.androidCores = androidCores;
    }

    public long getAndroidMemoryTotal() {
        return androidMemoryTotal;
    }

    public void setAndroidMemoryTotal(long androidMemoryTotal) {
        this.androidMemoryTotal = androidMemoryTotal;
    }

    public long getAndroidMemoryFree() {
        return androidMemoryFree;
    }

    public void setAndroidMemoryFree(long androidMemoryFree) {
        this.androidMemoryFree = androidMemoryFree;
    }

    public long getAndroidMemoryUsed() {
        return androidMemoryUsed;
    }

    public void setAndroidMemoryUsed(long androidMemoryUsed) {
        this.androidMemoryUsed = androidMemoryUsed;
    }
}
