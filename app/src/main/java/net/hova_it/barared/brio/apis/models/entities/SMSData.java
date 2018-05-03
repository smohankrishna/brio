package net.hova_it.barared.brio.apis.models.entities;

/**
 * Created by guillermo.ortiz on 02/03/18.
 */

public class SMSData {
    
    private String telefono;
    private String mensaje;
    private int comercio;
    private long id_ticket;
    private String usuario;
    private String uuid;
    private int tipo_ticket;
    private String ticket_html;
    private long fecha;
    
    public SMSData () {}
    
    
    public String getTelefono () {
        return telefono;
    }
    
    public void setTelefono (String telefono) {
        this.telefono = telefono;
    }
    
    public String getMensaje () {
        return mensaje;
    }
    
    public void setMensaje (String mensaje) {
        this.mensaje = mensaje;
    }
    
    public int getComercio () {
        return comercio;
    }
    
    public void setComercio (int comercio) {
        this.comercio = comercio;
    }
    
    public long getId_ticket () {
        return id_ticket;
    }
    
    public void setId_ticket (long id_ticket) {
        this.id_ticket = id_ticket;
    }
    
    public String getUsuario () {
        return usuario;
    }
    
    public void setUsuario (String usuario) {
        this.usuario = usuario;
    }
    
    public String getUuid () {
        return uuid;
    }
    
    public void setUuid (String uuid) {
        this.uuid = uuid;
    }
    
    public int getTipo_ticket () {
        return tipo_ticket;
    }
    
    public void setTipo_ticket (int tipo_ticket) {
        this.tipo_ticket = tipo_ticket;
    }
    
    public String getTicket_html () {
        return ticket_html;
    }
    
    public void setTicket_html (String ticket_html) {
        this.ticket_html = ticket_html;
    }
    
    public long getFecha () {
        return fecha;
    }
    
    public void setFecha (long fecha) {
        this.fecha = fecha;
    }
}
