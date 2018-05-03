package net.hova_it.barared.brio.apis.models.entities;

/**
 * Created by guillermo.ortiz on 02/03/18.
 */

public class TelefonoUsers {
    
    private int idNumTel;
    private String numTelefonico;
    
    public TelefonoUsers(){}
    
    public int getIdNumTel() {
        return idNumTel;
    }
    
    public void setIdNumTel(int idNumTel) {
        this.idNumTel = idNumTel;
    }
    
    public String getNumTelefonico() {
        return numTelefonico;
    }
    
    public void setNumTelefonico(String numTelefonico) {
        this.numTelefonico = numTelefonico;
    }
    
    @Override
    public String toString() {
        return this.getNumTelefonico();
    }
}
