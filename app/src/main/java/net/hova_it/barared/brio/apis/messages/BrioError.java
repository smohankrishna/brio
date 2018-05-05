package net.hova_it.barared.brio.apis.messages;

import net.hova_it.barared.brio.apis.utils.DebugLog;

import java.sql.Timestamp;


/**
 * Created by Herman Peralta on 18/02/2016.
 */
public class BrioError {
    public int KEY_MODULE;
    public int ID_MODULE_ERROR;

    public String errorMsg;
    public String errorDesc;

    private Timestamp timestamp;


    public BrioError(int KEY_MODULE, int ID_MODULE_ERROR, String errorMsg, String errorDesc){
        this.KEY_MODULE = KEY_MODULE;
        this.ID_MODULE_ERROR = ID_MODULE_ERROR;
        this.errorMsg = errorMsg;
        this.errorDesc = errorDesc;

        //todo cambiar a Utils.getTimestamp()
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        String logline = String.format("%s,\tKEY_MODULE:%d,\tID_MODULE_ERROR:%d,\tMESSAGE:%s,\tERROR_DESC:%s", timestamp.toString(), KEY_MODULE, ID_MODULE_ERROR, errorMsg, errorDesc);
        DebugLog.log(this.getClass(), "ERROR", logline);
        return logline;
    }
}
