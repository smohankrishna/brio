package net.hova_it.barared.brio.apis.layouts.menus.mibrio;

/**
 * Created by guillermo.ortiz on 20/03/18.
 */

public enum TagMiBrioEnum {
    
    UNIVERSIDAD ("MI_BRIO_UNIVERSIDAD"),
    NOTICIAS ("MI_BRIO_NOTICIAS"),
    MAIL ("MI_BRIO_MAIL"),
    CONTRATO ("MI_BRIO_CONTRATO"),
    GARANTIA ("MI_BRIO_GARANTIA"),
    HORARIOS ("MI_BRIO_HORARIOS"),
    POLITICAS ("MI_BRIO_POLITICAS"),
    ASOCIADOS ("MI_BRIO_ASOCIADOS"),
    ATENCION ("MI_BRIO_ATENCION"),
    RECOMPENSAS ("MI_BRIO_RECOMPENSAS");
    
    String titulo;
    
    
    TagMiBrioEnum (String titulo) {
        this.titulo = titulo;
    }
    
    public String getTitulo () {
        return titulo;
    }
}
