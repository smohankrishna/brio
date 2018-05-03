package net.hova_it.barared.brio.apis.utils.info;

/**
 * Created by Delgadillo on 01/03/17.
 * Pojo de informacion del sistema
 */
public class InfoPojo {
    
    private String packageName;
    private String versionName;
    private int versionCode;
    private long lastUpdateTime;
    private String applicationInfo;
    private int idComercio;
    private String nombreComercio;
    private String usuario;
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
    private String GPS;
    private String clave;
    private String direccionIP;
    private String acceso;
    private String brioVerRollback;
    
    public void setBrioVerRollback(String brioVerRollback) {
        this.brioVerRollback = brioVerRollback;
    }
    
    public String getBrioDate() {
        return brioDate;
    }
    
    public void setBrioDate(String brioDate) {
        this.brioDate = brioDate;
    }
    
    
    public void setAndroidSDK(int androidSDK) {
        this.androidSDK = androidSDK;
    }
    
    
    public void setAndroidCodename(String androidCodename) {
        this.androidCodename = androidCodename;
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
    
    public void setAndroidProduct(String androidProduct) {
        this.androidProduct = androidProduct;
    }
    
    public void setAndroidUUID(String androidUUID) {
        this.androidUUID = androidUUID;
    }
    
    public void setAndroidLanguaje(String androidLanguaje) {
        this.androidLanguaje = androidLanguaje;
    }
    
    public void setAndroidCodPais(String androidCodPais) {
        this.androidCodPais = androidCodPais;
    }
    
    public void setAndroidCores(int androidCores) {
        this.androidCores = androidCores;
    }
    
    
    public void setAndroidMemoryTotal(long androidMemoryTotal) {
        this.androidMemoryTotal = androidMemoryTotal;
    }
    
    public void setAndroidMemoryFree(long androidMemoryFree) {
        this.androidMemoryFree = androidMemoryFree;
    }
    
    public void setAndroidMemoryUsed(long androidMemoryUsed) {
        this.androidMemoryUsed = androidMemoryUsed;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    
    
    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
    
    
    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    
    public void setApplicationInfo(String applicationInfo) {
        this.applicationInfo = applicationInfo;
    }
    
    public int getIdComercio() {
        return idComercio;
    }
    
    public void setIdComercio(int idComercio) {
        this.idComercio = idComercio;
    }
    
    
    public void setNombreComercio(String nombreComercio) {
        this.nombreComercio = nombreComercio;
    }
    
    public String getUsuario() {
        return usuario;
    }
    
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    public void setGPS(String GPS) {
        this.GPS = GPS;
    }
    
    public void setClave(String clave) {
        this.clave = clave;
    }
    
    public void setDireccionIP(String direccionIP) {
        this.direccionIP = direccionIP;
    }
    
    public void setAcceso(String acceso) {
        this.acceso = acceso;
    }
}
