package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo ItemsTicket
 * Created by Mauricio Cerón on 09/12/2015.
 */
/*
PK  id_item_ticket INTEGER PRIMARY KEY AUTOINCREMENT
    id_ticket INTEGER  NOT NULL
    id_articulo TEXT  NULL
    codigo_barras TEXT  NOT NULL
    descripcion TEXT  NOT NULL
    cantidad REAL  NOT NULL
    importe_unitario INTEGER  NOT NULL
    importe_total INTEGER  NOT NULL
    timestamp TEXT  NOT NULL
    FOREIGN KEY (id_articulo)REFERENCES Articulos (id_articulo)
    FOREIGN KEY (id_ticket)REFERENCES Tickets (id_ticket)
 */
public class ItemsTicket {
    private int idItemTicket;
    private int idTicket;
    private int idArticulo;
    private String codigoBarras;
    private String descripcion;
    private double cantidad;
    private double importeUnitario; //con impuesto (neto)
    private double importeTotal; // con impuesto (neto)
    private long timestamp;
    private double precioCompra;


    public ItemsTicket(){}

    public ItemsTicket(int idTicket,int idArticulo,String codigoBarras,String descripcion,double cantidad,double importeUnitario,double importeTotal, long timestamp) {
        //this.idItemTicket=idItemTicket; //prueba
        this.idTicket = idTicket;
        this.idArticulo = idArticulo;
        this.codigoBarras = codigoBarras;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.importeUnitario = importeUnitario;
        this.importeTotal = importeTotal;
        this.timestamp = timestamp;
    }

    public int getIdItemTicket() {
        return idItemTicket;
    }

    public void setIdItemTicket(int idItemTicket) {
        this.idItemTicket = idItemTicket;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getImporteUnitario() {
        return importeUnitario;
    }

    public void setImporteUnitario(double importeUnitario) {
        this.importeUnitario = importeUnitario;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }
}
