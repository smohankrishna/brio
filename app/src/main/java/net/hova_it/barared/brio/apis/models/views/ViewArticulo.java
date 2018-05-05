package net.hova_it.barared.brio.apis.models.views;

import android.database.Cursor;

import net.hova_it.barared.brio.apis.models.daos.ViewArticuloDB;
import net.hova_it.barared.brio.apis.models.daos.ViewInventarioDB;

import lat.brio.core.BrioGlobales;

/**
 * Estructura del modelo ViewArticulo
 * Created by Mauricio Cerón on 15/12/2015.
 */
/*

CREATE VIEW ViewArticulo AS
A.id_articulo
A.id_central
A.precio_venta
A.precio_compra
A.codigo_barras
A.nombre
M.nombre AS marca
P.nombre AS presentacion
A.contenido
U.desc_unidad AS Unidad
A.Granel

 */

public class ViewArticulo {

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
    private int vendidos;



    public ViewArticulo(){}

    public ViewArticulo( int idCentral,String codigoBarras,String nombreArticulo,String nombreMarca,
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

    public ViewArticulo (Cursor cursor, ViewArticuloDB viewArticuloDB) {
        super ();
        try {




            this. idArticulo = cursor.getInt (0);
            this. idCentral = cursor.getInt (1);
            this. precioBase= cursor.getDouble (2);
            this. precioCompra = cursor.getDouble (3);
            this. codigoBarras = cursor.getString (4);
            this. nombreArticulo = cursor.getString (5);
            this. nombreMarca = cursor.getString (6);
            this. presentacion = cursor.getString (7);
            this. contenido= cursor.getInt (8);
            this. unidad = cursor.getString (9);
            this. granel= Boolean.parseBoolean(cursor.getString ( 10));
            this. vendidos = cursor.getInt (11);

        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.writeLog ("ViewInventario", "ViewInventario()", e.getMessage ());
        }
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

    public String getDescripcionItem() {
        String desc = nombreArticulo + (nombreMarca.toUpperCase().contains("SIN")? "" : (" " + nombreMarca)) + (presentacion.toUpperCase().contains("SIN")? "" : (" " + presentacion));

        String uni = (unidad.toUpperCase().contains("SIN")? "" : unidad);

        if(granel) {
            desc = (uni.length()>0? (uni + " ") : "") + desc;
        } else {
            desc = desc + (uni.length()>0? (" " + uni) : "");
        }

        return desc;
    }
}