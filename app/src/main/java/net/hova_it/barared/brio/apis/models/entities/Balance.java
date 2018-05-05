package net.hova_it.barared.brio.apis.models.entities;

/**
 *Estructura del modelo Balance
 * Created by Mauricio Cerón on 01/03/2016.
 */
public class Balance {
    private double saldoInicial;
    private double entradas;
    private double salidas;
    private double ventasFiado;
    private double ventasEfectivo;
    private double ventasTarjeta;
    private double ventasVales;
    private double total;

    public Balance(){}

    public double getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public double getEntradas() {
        return entradas;
    }

    public void setEntradas(double entradas) {
        this.entradas = entradas;
    }

    public double getSalidas() {
        return salidas;
    }

    public void setSalidas(double salidas) {
        this.salidas = salidas;
    }

    public double getVentasFiado() {
        return ventasFiado;
    }

    public void setVentasFiado(double ventasFiado) {
        this.ventasFiado = ventasFiado;
    }

    public double getVentasEfectivo() {
        return ventasEfectivo;
    }

    public void setVentasEfectivo(double ventasEfectivo) {
        this.ventasEfectivo = ventasEfectivo;
    }

    public double getVentasTarjeta() {
        return ventasTarjeta;
    }

    public void setVentasTarjeta(double ventasTarjeta) {
        this.ventasTarjeta = ventasTarjeta;
    }

    public double getVentasVales() {
        return ventasVales;
    }

    public void setVentasVales(double ventasVales) {
        this.ventasVales = ventasVales;
    }

    public void calcularTotal(){
        this.total = (saldoInicial + entradas + ventasEfectivo + ventasTarjeta + ventasFiado + ventasVales) - salidas;
    }

    public double calcularCierreCaja(){
        double totalCierre;
        totalCierre=(saldoInicial+ entradas + ventasEfectivo)- salidas;
        return totalCierre ;

    }

    public double getTotal() { return total;}
}
