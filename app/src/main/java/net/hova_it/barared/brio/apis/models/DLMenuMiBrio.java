package net.hova_it.barared.brio.apis.models;

/**
 * Created by guillermo.ortiz on 21/03/18.
 */

public class DLMenuMiBrio {
    
    String strname;
    int idImagen;
    String strPermiso;
    private String tagEnum;
    
    public DLMenuMiBrio (String strname, int idImagen, String strPermiso,String tagEnum) {
        this.strname = strname;
        this.idImagen = idImagen;
        this.strPermiso = strPermiso;
        this.tagEnum = tagEnum;
    }
    
    public String getStrname () {
        return strname;
    }
    
    public void setStrname (String strname) {
        this.strname = strname;
    }
    
    public int getIdImagen () {
        return idImagen;
    }
    
    public void setIdImagen (int idImagen) {
        this.idImagen = idImagen;
    }
    
    public String getStrPermiso () {
        return strPermiso;
    }
    
    public void setStrPermiso (String strPermiso) {
        this.strPermiso = strPermiso;
    }
    
    public String getTagEnum () {
        return tagEnum;
    }
    
    public void setTagEnum (String tagEnum) {
        this.tagEnum = tagEnum;
    }
}
