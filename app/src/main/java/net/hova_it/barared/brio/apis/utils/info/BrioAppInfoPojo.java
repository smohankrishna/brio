package net.hova_it.barared.brio.apis.utils.info;

/**
 * Created by Herman Peralta on 18/05/2016.
 * Extrae la información de la aplicación
 */
public class BrioAppInfoPojo {
    private String packageName;
    private String versionName;
    private int versionCode;
    private long lastUpdateTime;
    private String applicationInfo;
    private int idComercio;
    private String nombreComercio;
    private String usuario;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getApplicationInfo() {
        return applicationInfo;
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

    public String getNombreComercio() {
        return nombreComercio;
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
}
