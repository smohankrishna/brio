package net.hova_it.barared.brio.apis.models.entities;

/**
 * Created by guillermo.ortiz on 02/03/18.
 */

public class MailUsers {
    
    private int idMailUser;
    private String mailUser;
    
    public MailUsers(){}
    
    public int getIdMailUser() {
        return idMailUser;
    }
    
    public void setIdMailUser(int idMailUser) {
        this.idMailUser = idMailUser;
    }
    
    public String getMailUser() {
        return mailUser;
    }
    
    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }
    
    @Override
    public String toString() {
        return this.getMailUser();
    }
    
}
