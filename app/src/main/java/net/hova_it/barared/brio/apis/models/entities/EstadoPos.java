package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo EstadoPos
 * Created by Mauricio Cerón on 21/04/2016.
 */
public class EstadoPos{
    private int cantidadV;
    private int cantidadE;
    private int cantidadS;

    private double totalV;
    private double totalE;
    private double totalS;

    private double gananciaV;
    private double gananciaE;
    private double gananciaS;

    public int getCantidadE() {
        return cantidadE;
    }

    public void setCantidadE(int cantidadE) {
        this.cantidadE = cantidadE;
    }

    public int getCantidadS() {
        return cantidadS;
    }

    public void setCantidadS(int cantidadS) {
        this.cantidadS = cantidadS;
    }

    public int getCantidadV() {
        return cantidadV;
    }

    public void setCantidadV(int cantidadV) {
        this.cantidadV = cantidadV;
    }

    public double getGananciaE() {
        return gananciaE;
    }

    public void setGananciaE(double gananciaE) {
        this.gananciaE = gananciaE;
    }

    public double getGananciaS() {
        return gananciaS;
    }

    public void setGananciaS(double gananciaS) {
        this.gananciaS = gananciaS;
    }

    public double getGananciaV() {
        return gananciaV;
    }

    public void setGananciaV(double gananciaV) {
        this.gananciaV = gananciaV;
    }

    public double getTotalE() {
        return totalE;
    }

    public void setTotalE(double totalE) {
        this.totalE = totalE;
    }

    public double getTotalS() {
        return totalS;
    }

    public void setTotalS(double totalS) {
        this.totalS = totalS;
    }

    public double getTotalV() {
        return totalV;
    }

    public void setTotalV(double totalV) {
        this.totalV = totalV;
    }
}
