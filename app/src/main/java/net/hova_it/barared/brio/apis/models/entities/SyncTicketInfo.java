package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo SyncTicketInfo
 * Created by Herman Peralta on 09/05/2016.
 */
public class SyncTicketInfo {
    private long comm;
    private long trans;
    private String prov;
    private String serv;
    private int ttype;
    private int status;
    private String reference;
    private String authorization;
    private double amount;
    private double commi;
    private long generated;
    private String request;
    private String response;

    public long getComm() {
        return comm;
    }

    public void setComm(long comm) {
        this.comm = comm;
    }

    public long getTrans() {
        return trans;
    }

    public void setTrans(long trans) {
        this.trans = trans;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getServ() {
        return serv;
    }

    public void setServ(String serv) {
        this.serv = serv;
    }

    public int getTtype() {
        return ttype;
    }

    public void setTtype(int ttype) {
        this.ttype = ttype;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCommi() {
        return commi;
    }

    public void setCommi(double commi) {
        this.commi = commi;
    }

    public long getGenerated() {
        return generated;
    }

    public void setGenerated(long generated) {
        this.generated = generated;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
