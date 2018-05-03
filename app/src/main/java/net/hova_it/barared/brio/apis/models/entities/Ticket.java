package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Ticket
 * Created by Alejandro Gomez on 30/11/2015.
 */

/*
PK  id_ticket INTEGER PRIMARY KEY AUTOINCREMENT
    importe_bruto REAL  NOT NULL
    importe_neto REAL  NULL
    impuestos REAL  NULL
    id_moneda INTEGER  NULL
    id_cliente INTEGER  NULL
    timestamp TEXT  NOT NULL
    id_usuario INTEGER  NULL
    id_caja INTEGER  NULL
     cambio REAL  NULL
    id_tipo_ticket INTEGER NOT NULL
    descripcion TEXT  NOT NULL
    FOREIGN KEY (id_forma_pago)REFERENCES Formas_pago (id_forma_pago)
    FOREIGN KEY (id_cliente)REFERENCES Cliente (id_cliente)
    FOREIGN KEY (id_caja)REFERENCES Cajas (id_caja)
    FOREIGN KEY (id_usuario)REFERENCES Usuarios (id_usuario)
    FOREIGN KEY (id_moneda)REFERENCES Monedas (id_moneda)
 */

public class Ticket {
    private int idTicket;
    private double importeBruto;
    private double importeNeto;
    private double impuestos;
    private double descuento;
    private int idMoneda;
    private int idCliente;
    private long timestamp;//
    private int idComercio;
    private int idUsuario;
    private int idCaja;
    private double cambio;
    private int idTipoTicket;
    private String descripcion;

    public Ticket() {

    }

    public Ticket( double importeBruto, double importeNeto, double impuestos, double descuento, int idMoneda, int idCliente, long timestamp, int idComercio,int idUsuario, int idCaja,
                  double cambio, int idTipoTicket, String descripcion) {

        this.importeBruto = importeBruto;
        this.importeNeto = importeNeto;
        this.impuestos = impuestos;
        this.descuento = descuento;
        this.idMoneda = idMoneda;
        this.idCliente = idCliente;
        this.timestamp = timestamp;
        this.idComercio = idComercio;
        this.idUsuario = idUsuario;
        this.idCaja = idCaja;
        this.cambio = cambio;
        this.idTipoTicket = idTipoTicket;
        this.descripcion = descripcion;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public double getImporteBruto() {
        return importeBruto;
    }

    public void setImporteBruto(double importeBruto) {
        this.importeBruto = importeBruto;
    }

    public double getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(double importeNeto) {
        this.importeNeto = importeNeto;
    }

    public double getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(double impuestos) {
        this.impuestos = impuestos;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public int getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(int idMoneda) {
        this.idMoneda = idMoneda;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(int idComercio) {
        this.idComercio = idComercio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(int idCaja) {
        this.idCaja = idCaja;
    }

    public double getCambio() {
        return cambio;
    }

    public void setCambio(double cambio) {
        this.cambio = cambio;
    }

    public int getIdTipoTicket(){
        return idTipoTicket;
    }

    public void setIdTipoTicket(int idTipoTicket){
        this.idTipoTicket = idTipoTicket;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

}
