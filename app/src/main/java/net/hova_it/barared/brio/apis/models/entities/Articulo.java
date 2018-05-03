package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Articulo
 * Created by Alejandro Gomez on 27/11/2015.
 */

public class Articulo {

    private int idArticulo; // PK
    private int idCentral;
    private String codigoBarras;
    private String nombre;
    private int idMarca;
    private int idPresentacion;
    private int idUnidad;
    private double contenido;
    private double precioVenta;
    private double precioCompra;
    private int idFamilia;
    private boolean granel;
    private String imagen;
    private long timestamp;
    private int idUsuario;
    private long fechaBaja;

    public Articulo(){}

    public Articulo(int idCentral, String codigoBarras, String nombre, int idMarca, int idPresentacion, int idUnidad, double contenido, double precioVenta, double precioCompra, int idFamilia, boolean granel, String imagen, long timestamp, int idUsuario) {
        this.idCentral = idCentral;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.idMarca = idMarca;
        this.idPresentacion = idPresentacion;
        this.idUnidad = idUnidad;
        this.contenido = contenido;
        this.precioVenta = precioVenta;
        this.precioCompra = precioCompra;
        this.idFamilia = idFamilia;
        this.granel = granel;
        this.imagen = imagen;
        this.timestamp = timestamp;
        this.idUsuario = idUsuario;
    }

    public int getIdArticulo() {
        return idArticulo;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public int getIdPresentacion() {
        return idPresentacion;
    }

    public void setIdPresentacion(int idPresentacion) {
        this.idPresentacion = idPresentacion;
    }

    public int getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(int idUnidad) {
        this.idUnidad = idUnidad;
    }

    public double getContenido() {
        return contenido;
    }

    public void setContenido(double contenido) {
        this.contenido = contenido;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public int getIdFamilia() {
        return idFamilia;
    }

    public void setIdFamilia(int idFamilia) {
        this.idFamilia = idFamilia;
    }

    public boolean getGranel() {
        return granel;
    }

    public void setGranel(boolean granel) {
        this.granel = granel;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public long getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(long fechaBaja) {
        this.fechaBaja = fechaBaja;
    }
}