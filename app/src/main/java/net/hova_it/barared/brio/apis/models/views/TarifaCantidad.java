package net.hova_it.barared.brio.apis.models.views;

/**
 * Estructura del modelo TarifaCantidad
 * Created by Mauricio Cerón on 15/12/2015.
 */
public class TarifaCantidad {

    private int cantidadMayoreo;
    private double precio;

    public TarifaCantidad(){}

    public TarifaCantidad(int cantidadMayoreo,double precio ){
        this.cantidadMayoreo = cantidadMayoreo;
        this.precio=precio;

    }

    public void init(int cantidadInferior,double precio ){
        this.cantidadMayoreo =cantidadInferior;
        this.precio=precio;

    }

    public int getCantidadMayoreo() {
        return cantidadMayoreo;
    }

    public void setCantidadMayoreo(int cantidadMayoreo) {
        this.cantidadMayoreo = cantidadMayoreo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

}
