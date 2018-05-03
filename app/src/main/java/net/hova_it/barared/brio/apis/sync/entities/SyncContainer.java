package net.hova_it.barared.brio.apis.sync.entities;

/**
 * Created by Alejandro Gomez on 09/03/2016.
 */
public class SyncContainer {
    private long idSequence;
    private int idComercio;
    private String uuid;
    private String table;
    private String operation;
    private String data;
    private long stamp;

    public long getIdSequence() {
        return idSequence;
    }

    public void setIdSequence(long idSequence) {
        this.idSequence = idSequence;
    }

    public int getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(int idComercio) {
        this.idComercio = idComercio;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getStamp() {
        return stamp;
    }

    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

    @Override
    public String toString() {
        return "{" +
                "\\\"idSequence\\\":" + idSequence + "," +
                "\\\"table\\\":\\\"" + table + "\\\"" + "," +
                "\\\"operation\\\":\\\"" + operation + "\\\"" + "," +
                "\\\"data\\\":" + data + "," +
                "\\\"stamp\\\":" + stamp +
                "}";
    }

}
