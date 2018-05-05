package net.hova_it.barared.brio.apis.models.views;

import android.database.Cursor;

import net.hova_it.barared.brio.apis.models.daos.CorreosClientesDB;
import net.hova_it.barared.brio.apis.models.daos.ViewInventarioDB;

import java.util.Comparator;

import lat.brio.core.BrioGlobales;

/**
 * Estructura del modelo ViewInventario
 * Created by Mauricio Cerón on 04/02/2016.
 */
public class ViewInventario implements Comparable<ViewInventario> {
    private int idArticulo; // PK
    private int idInventario;
    private String nombreArticulo;
    private String nombreMarca;
    private double contenido;
    private String unidad;
    private String presentacion;
    private String codigoBarras;
    private double precioCompra;
    private double precioVenta;
    private double existencias;
    
    
    public ViewInventario () {}
    
    public ViewInventario (String nombreArticulo, String nombreMarca,
            double contenido, String unidad, String presentacion, String codigoBarras, double precioCompra, double precioVenta, double existencias) {
        
        this.nombreArticulo = nombreArticulo;
        this.nombreMarca = nombreMarca;
        this.contenido = contenido;
        this.unidad = unidad;
        this.presentacion = presentacion;
        this.codigoBarras = codigoBarras;
        this.precioVenta = precioVenta;
        this.precioCompra = precioCompra;
        this.existencias = existencias;
        
        
    }
    
    public void init (String nombreArticulo, String nombreMarca,
            double contenido, String unidad, String presentacion, String codigoBarras, double precioCompra, double precioVenta, double existencias) {
        
        this.nombreArticulo = nombreArticulo;
        this.nombreMarca = nombreMarca;
        this.contenido = contenido;
        this.unidad = unidad;
        this.presentacion = presentacion;
        this.codigoBarras = codigoBarras;
        this.precioVenta = precioVenta;
        this.precioCompra = precioCompra;
        this.existencias = existencias;
        
    }
    
    
    public ViewInventario (Cursor cursor) {
        super ();
        try {
    
            this. idArticulo = cursor.getInt (cursor.getColumnIndex (ViewInventarioDB.KEY_ID_ARTICULO));
            this. idInventario = cursor.getInt (cursor.getColumnIndex (ViewInventarioDB.KEY_ID_INVENTARIO));
            this. nombreArticulo = cursor.getString (cursor.getColumnIndex (ViewInventarioDB.KEY_NOMBRE));
            this. nombreMarca = cursor.getString (cursor.getColumnIndex (ViewInventarioDB.KEY_MARCA));
            this. contenido= cursor.getDouble (cursor.getColumnIndex (ViewInventarioDB.KEY_CONTENIDO));
            this. unidad = cursor.getString (cursor.getColumnIndex (ViewInventarioDB.KEY_UNIDAD));
            this. presentacion = cursor.getString (cursor.getColumnIndex (ViewInventarioDB.KEY_PRESENTACION));
            this. codigoBarras = cursor.getString (cursor.getColumnIndex (ViewInventarioDB.KEY_CODE_BAR));
            this. precioCompra = cursor.getDouble (cursor.getColumnIndex (ViewInventarioDB.KEY_PRECIO_COMPRA));
            this. precioVenta= cursor.getDouble (cursor.getColumnIndex (ViewInventarioDB.KEY_PRECIO_VENTA));
            this. existencias= cursor.getDouble (cursor.getColumnIndex (ViewInventarioDB.KEY_EXISTENCIAS));
            
        } catch (Exception e) {
            e.printStackTrace ();
            BrioGlobales.writeLog ("ViewInventario", "ViewInventario()", e.getMessage ());
        }
    }
    
    public int getIdArticulo () {
        return idArticulo;
    }
    
    public void setIdArticulo (int idArticulo) {
        this.idArticulo = idArticulo;
    }
    
    public int getIdInventario () {
        return idInventario;
    }
    
    public void setIdInventario (int idInventario) {
        this.idInventario = idInventario;
    }
    
    
    public String getNombreArticulo () {
        return nombreArticulo;
    }
    
    public void setNombreArticulo (String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }
    
    public String getNombreMarca () {
        return nombreMarca;
    }
    
    public void setNombreMarca (String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }
    
    public double getContenido () {
        return contenido;
    }
    
    public void setContenido (double contenido) {
        this.contenido = contenido;
    }
    
    public String getUnidad () {
        return unidad;
    }
    
    public void setUnidad (String unidad) {
        this.unidad = unidad;
    }
    
    public String getPresentacion () {
        return presentacion;
    }
    
    public void setPresentacion (String presentacion) {
        this.presentacion = presentacion;
    }
    
    public String getCodigoBarras () {
        return codigoBarras;
    }
    
    public void setCodigoBarras (String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }
    
    public double getPrecioCompra () {
        return precioCompra;
    }
    
    public void setPrecioCompra (double precioCompra) {
        this.precioCompra = precioCompra;
    }
    
    public double getPrecioVenta () {
        return precioVenta;
    }
    
    public void setPrecioVenta (double precioVenta) {
        this.precioVenta = precioVenta;
    }
    
    public double getExistencias () {
        return existencias;
    }
    
    public void setExistencias (double existencias) {
        this.existencias = existencias;
    }
    
    ////////////////////////////////COMPARADORES/////////////////////////////////////////////////////
    
    @Override
    public int compareTo (ViewInventario compareViewInventario) {
        //int compareExistencias = ((ViewInventario) compareViewInventario).getExistencias();
        
        //ascending order
        //return this.existencias - compareExistencias;
        return 1;
        //descending order
        //return compareQuantity - this.quantity;
    }
    
    public static Comparator<ViewInventario> ComparatorNombreArticulo
            = new Comparator<ViewInventario> () {
        
        public int compare (ViewInventario viewInventario1, ViewInventario viewInventario2) {
            
            String viewInventarioNombre1 = viewInventario1.getNombreArticulo ().toUpperCase ();
            String viewInventarioNombre2 = viewInventario2.getNombreArticulo ().toUpperCase ();
            
            //ascending order
            return viewInventarioNombre1.compareTo (viewInventarioNombre2);
            
            //descending order
            //return fruitName2.compareTo(fruitName1);
        }
        
    };
    
    public static Comparator<ViewInventario> ComparatorNombreMarca
            = new Comparator<ViewInventario> () {
        
        public int compare (ViewInventario viewInventario1, ViewInventario viewInventario2) {
            
            String viewInventarioNombre1 = viewInventario1.getNombreMarca ().toUpperCase ();
            String viewInventarioNombre2 = viewInventario2.getNombreMarca ().toUpperCase ();
            
            //ascending order
            return viewInventarioNombre1.compareTo (viewInventarioNombre2);
            
            //descending order
            //return fruitName2.compareTo(fruitName1);
        }
        
    };
    
    public static Comparator<ViewInventario> ComparatorPresentacion
            = new Comparator<ViewInventario> () {
        
        public int compare (ViewInventario viewInventario1, ViewInventario viewInventario2) {
            
            String viewInventarioPresentacion1 = viewInventario1.getPresentacion ().toUpperCase ();
            String viewInventarioPresentacion2 = viewInventario2.getPresentacion ().toUpperCase ();
            
            //ascending order
            return viewInventarioPresentacion1.compareTo (viewInventarioPresentacion2);
            
            //descending order
            //return fruitName2.compareTo(fruitName1);
        }
        
    };
    
    public static Comparator<ViewInventario> ComparatorCodigoBarras
            = new Comparator<ViewInventario> () {
        
        public int compare (ViewInventario viewInventario1, ViewInventario viewInventario2) {
            
            String viewInventarioCategoria1 = viewInventario1.getCodigoBarras ().toUpperCase ();
            String viewInventarioCategoria2 = viewInventario2.getCodigoBarras ().toUpperCase ();
            
            //ascending order
            return viewInventarioCategoria1.compareTo (viewInventarioCategoria2);
            
            //descending order
            //return fruitName2.compareTo(fruitName1);
        }
        
    };
    
    public static Comparator<ViewInventario> ComparatorPrecioCompra
            = new Comparator<ViewInventario> () {
        
        public int compare (ViewInventario viewInventario1, ViewInventario viewInventario2) {
            
            double viewInventarioPrecio1 = viewInventario1.getPrecioCompra ();
            double viewInventarioPrecio2 = viewInventario2.getPrecioCompra ();
            
            //ascending order
            return (int) (viewInventarioPrecio1 - viewInventarioPrecio2);
            
            //descending order
            //return fruitName2.compareTo(fruitName1);
        }
        
    };
    
    public static Comparator<ViewInventario> ComparatorPrecioVenta
            = new Comparator<ViewInventario> () {
        
        public int compare (ViewInventario viewInventario1, ViewInventario viewInventario2) {
            
            double viewInventarioPrecio1 = viewInventario1.getPrecioVenta ();
            double viewInventarioPrecio2 = viewInventario2.getPrecioVenta ();
            
            //ascending order
            return (int) (viewInventarioPrecio1 - viewInventarioPrecio2);
            
            //descending order
            //return fruitName2.compareTo(fruitName1);
        }
        
    };
    
    public static Comparator<ViewInventario> ComparatorExistencias
            = new Comparator<ViewInventario> () {
        
        public int compare (ViewInventario viewInventario1, ViewInventario viewInventario2) {
            
            double viewInventarioExistencias1 = viewInventario1.getExistencias ();
            double viewInventarioExistencias2 = viewInventario2.getExistencias ();
            
            //ascending order
            return (int) (viewInventarioExistencias1 - viewInventarioExistencias2);
            
            //descending order
            //return fruitName2.compareTo(fruitName1);
        }
        
    };
    
    
}
