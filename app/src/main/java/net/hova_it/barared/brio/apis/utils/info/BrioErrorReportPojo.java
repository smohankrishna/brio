package net.hova_it.barared.brio.apis.utils.info;

/**
 * Created by Herman Peralta on 18/05/2016.
 * Extra la informacion para  generar el reporte de errores
 */
public class BrioErrorReportPojo {
    private BrioAppInfoPojo brioAppInfo;
    private SystemInfoPojo systemInfo;
    private String error;
    private String stackTrace;
    private String cause;
    private String causeTrace;

    public BrioAppInfoPojo getBrioAppInfo() {
        return brioAppInfo;
    }

    public void setBrioAppInfo(BrioAppInfoPojo brioAppInfo) {
        this.brioAppInfo = brioAppInfo;
    }

    public SystemInfoPojo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(SystemInfoPojo systemInfo) {
        this.systemInfo = systemInfo;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getCauseTrace() {
        return causeTrace;
    }

    public void setCauseTrace(String causeTrace) {
        this.causeTrace = causeTrace;
    }

}
