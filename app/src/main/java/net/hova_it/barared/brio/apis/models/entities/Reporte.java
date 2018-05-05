package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Reporte
 * Created by Mauricio Cerón on 18/04/2016.
 */
public class Reporte {
    private int idArticulo; // PK
    private int idCentral;
    private String codigoBarras;
    private String nombreArticulo;
    private String nombreMarca;
    private String presentacion;
    private int contenido;
    private String unidad;
    private boolean granel;
    private double precioBase;
    private double precioCompra;
    private double precioBarared;
    private double venta;
    private double vendidos;
    private double ganancia;
    private String concepto;
    private String cantidad;
    private String hora;
    private String fecha;
    private int idUsuario;
    private String nombreUsuario;
    private int orden;






    public Reporte(){}

    public Reporte(int idCentral, String codigoBarras, String nombreArticulo, String nombreMarca,
                   String presentacion, int contenido, String unidad, boolean granel) {


        this.idCentral = idCentral;
        this.codigoBarras = codigoBarras;
        this.nombreArticulo = nombreArticulo;
        this.nombreMarca = nombreMarca;
        this.presentacion = presentacion;
        this.contenido = contenido;
        this.unidad = unidad;
        this.granel = granel;

    }

    public void init( int idCentral,String codigoBarras,String nombreArticulo,String nombreMarca,
                      String presentacion, int contenido,String unidad,boolean granel) {


        this.idCentral = idCentral;
        this.codigoBarras = codigoBarras;
        this.nombreArticulo = nombreArticulo;
        this.nombreMarca = nombreMarca;
        this.presentacion = presentacion;
        this.contenido = contenido;
        this.unidad = unidad;
        this.granel = granel;

    }

    public int getIdArticulo() {
        return idArticulo;
    }
    public double getPrecioBase() {
        return precioBase;
    }


    public void setPrecioBase(double precio) {
        precioBase=precio;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }


    public void setPrecioCompra(double precioCompra) {
        this.precioCompra=precioCompra;
    }

    public double getPrecioBarared() {
        return precioBarared;
    }

    public void setPrecioBarared(double precio) {
        precioBarared=precio;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public int getIdCentral() {
        return idCentral;
    }

    public void setIdCentral(int idCentral) {
        this.idCentral = idCentral;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public String getNombreMarca() {
        return nombreMarca;
    }

    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public int getContenido() {
        return contenido;
    }

    public void setContenido(int contenido) {
        this.contenido = contenido;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public boolean getGranel() {
        return granel;
    }

    public void setGranel(boolean granel) {
        this.granel = granel;
    }

    public double getVenta() {
        return venta;
    }

    public void setVenta(double venta) {
        this.venta=venta;
    }

    public double getVendidos() {
        return vendidos;
    }

    public void setVendidos(double vendidos) {
        this.vendidos=vendidos;
    }

    public double getGanancia() {
        return ganancia;
    }

    public void setGanancia(double ganancia) {
        this.ganancia=ganancia;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDescripcionItem() {
        String desc = nombreArticulo + " " + nombreMarca + " " + presentacion;

        if(granel) {
            desc = unidad + " " + desc;
        } else {
            desc = desc + " " + unidad;
        }

        return desc;
    }


    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
