package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo SyncData
 * Created by Mauricio Cerón on 09/03/2016.
 */
/*
CREATE TABLE Sync_Data
    id_sequence INTEGER PRIMARY KEY AUTOINCREMENT
    id_comercio INTEGER  NOT NULL
    uuid TEXT  NOT NULL
    table TEXT  NOT NULL
    operation TEXT  NOT NULL
    data TEXT  NOT NULL
    timestamp INTEGER NOT NULL DEFAULT (strftime('%s','now'))
 */

public class SyncData {
    private long idSequence;
    private int idComercio;
    private String uuid;
    private String table;
    private String operation;
    private String data;
    private long timestamp;


    public SyncData(){}


    public SyncData( int idComercio,String uuid, String table,String operation, String data) {
        this.idComercio = idComercio;
        this.uuid = uuid;
        this.table = table;
        this.operation = operation;
        this.data = data;
    }

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
