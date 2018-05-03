package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Transacciones
 * Created by Mauricio Cerón on 22/04/2016.
 */
public class Transacciones {

    private double servicioC;
    private double taeC;
    private double internetC;
    private double bancoC;
    private double servicioT;
    private double taeT;
    private double internetT;
    private double bancoT;
    private double servicioComision;
    private double bancoComision;
    private double servicioGanancia;
    private double bancoGanancia;
    private double totalVentasTarjeta;
    private int cantidadVentasTarjeta;
    private double comisionesTarjeta;


    public double getTotalVentasTarjeta() {
        return totalVentasTarjeta;
    }

    public void setTotalVentasTarjeta(double totalVentasTarjeta) {
        this.totalVentasTarjeta = totalVentasTarjeta;
    }

    public int getCantidadVentasTarjeta() {
        return cantidadVentasTarjeta;
    }

    public void setCantidadVentasTarjeta(int cantidadVentasTarjeta) {
        this.cantidadVentasTarjeta = cantidadVentasTarjeta;
    }


    public double getComisionesTarjeta() {
        return comisionesTarjeta;
    }

    public void setComisionesTarjeta(double comisionesTarjeta) {
        this.comisionesTarjeta = comisionesTarjeta;
    }


    public double getServicioGanancia() {
        return servicioGanancia;
    }

    public void setServicioGanancia(double servicioGanancia) {
        this.servicioGanancia = servicioGanancia;
    }


    public double getServicioC() {
        return servicioC;
    }

    public void setServicioC(double servicioC) {
        this.servicioC = servicioC;
    }

    public double getTaeC() {
        return taeC;
    }

    public void setTaeC(double taeC) {
        this.taeC = taeC;
    }

    public double getInternetC() {
        return internetC;
    }

    public void setInternetC(double internetC) {
        this.internetC = internetC;
    }

    public double getServicioT() {
        return servicioT;
    }

    public void setServicioT(double servicioT) {
        this.servicioT = servicioT;
    }

    public double getTaeT() {
        return taeT;
    }

    public void setTaeT(double taeT) {
        this.taeT = taeT;
    }

    public double getInternetT() {
        return internetT;
    }

    public void setInternetT(double internetT) {
        this.internetT = internetT;
    }

    public double getServicioComision() {
        return servicioComision;
    }

    public void setServicioComision(double servicioComision) {
        this.servicioComision = servicioComision;
    }

    public double getBancoC() {
        return bancoC;
    }

    public void setBancoC(double bancoC) {
        this.bancoC = bancoC;
    }

    public double getBancoT() {
        return bancoT;
    }

    public void setBancoT(double bancoT) {
        this.bancoT = bancoT;
    }

    public double getBancoComision() {
        return bancoComision;
    }

    public void setBancoComision(double bancoComision) {
        this.bancoComision = bancoComision;
    }

    public double getBancoGanancia() {
        return bancoGanancia;
    }

    public void setBancoGanancia(double bancoGanancia) {
        this.bancoGanancia = bancoGanancia;
    }



}
