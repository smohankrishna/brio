package net.hova_it.barared.brio.apis.models.views;

/**
 * Estructura del modelo ImpuestoDeArticulo
 * Created by Mauricio Cerón on 06/01/2016.
 */
public class ImpuestosDeArticulo {
    private String nombre;
    private double impuesto;
    private int prioridad;

    public ImpuestosDeArticulo(){}

    public ImpuestosDeArticulo(String nombre, double impuesto, int prioridad ){
        this.nombre=nombre;
        this.impuesto=impuesto;
        this.prioridad=prioridad;

    }

    public void init(String nombre, double impuesto, int prioridad ){
        this.nombre=nombre;
        this.impuesto=impuesto;
        this.prioridad=prioridad;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }
}
