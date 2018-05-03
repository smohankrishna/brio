package net.hova_it.barared.brio.apis.layouts.menus.mibrio;

import android.content.Context;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuFragment;
import net.hova_it.barared.brio.apis.models.DLMenuMiBrio;
import net.hova_it.barared.brio.apis.models.DLSortMenu;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lat.brio.core.AppController;
import lat.brio.core.BrioGlobales;

import static net.hova_it.barared.brio.apis.layouts.menus.mibrio.TagMiBrioEnum.UNIVERSIDAD;

/**
 * Created by Herman Peralta on 09/03/2016.
 */
public class MenuMiBrioFragment extends MenuFragment {
    
    
    private static Context context;
    private static MenuMiBrioFragment fgmt = null;
    
    public static MenuMiBrioFragment newInstance (Context contexto) {
        context = contexto;
        if (fgmt == null)
            fgmt = new MenuMiBrioFragment ();
        fgmt.mibrio = true;
        fgmt.itemsMenuDinamico = getMenu ();
        fgmt.resid_layout = R.layout.menu_mibrio;
        fgmt.num_columnas = 2;
        
        
        return fgmt;
    }
    
    public static void updateItems () {
        fgmt.itemsMenuDinamico = getMenu ();
        fgmt.miBrioAdapter.updateItems (fgmt.itemsMenuDinamico);
    }
    
    private static ArrayList<DLMenuMiBrio> getMenu () {
        ArrayList<DLMenuMiBrio> items = new ArrayList<> ();
        ArrayList<DLSortMenu> list = Utils.getSortMiBrio ();
        
        for (DLSortMenu dl : list) {
            items.add (getItemMenu (dl.getKey ()));
        }
        return items;
    }
    
    
    private static DLMenuMiBrio getItemMenu (String strenum) {
        
        TagMiBrioEnum miBrioEnum = TagMiBrioEnum.valueOf (strenum);
        DLMenuMiBrio item = null;
        String strname = "";
        int idImagen = - 1;
        String strPermiso = "";
        int idString = - 1;
        
        
        switch (miBrioEnum) {
            case UNIVERSIDAD://Universidad Brío
                idImagen = R.drawable.menu_mibrio_briouniversidad_off;
                idString = R.string.menu_mibrio_briouniversidad;
                break;
            case NOTICIAS://Brío Noticias
                idString = R.string.menu_mibrio_brionoticias;
                idImagen = R.drawable.menu_mibrio_brionoticias_off;
                break;
            case MAIL://Mi mail brio
                idString = R.string.menu_mibrio_briomail;
                idImagen = R.drawable.menu_mibrio_briomail_off;
                break;
            case CONTRATO://Mi Contrato Brío
                idString = R.string.menu_mibrio_briocontrato;
                idImagen = R.drawable.menu_mibrio_briocontrato_off;
                break;
            case GARANTIA://Garantía Extendida
                idString = R.string.menu_mibrio_briogarantia;
                idImagen = R.drawable.menu_mibrio_briogarantia_off;
                break;
            case HORARIOS://Horarios Brío
                idString = R.string.menu_mibrio_briohorarios;
                idImagen = R.drawable.menu_mibrio_briohorarios_on;
                break;
            case POLITICAS://Políticas de Servicio
                idString = R.string.menu_mibrio_briopoliticas;
                idImagen = R.drawable.menu_mibrio_briopoliticas_on;
                break;
            case ASOCIADOS://Servicios para el Asociado
                idString = R.string.menu_mibrio_brioservicios;
                idImagen = R.drawable.menu_mibrio_brioservicios_on;
                break;
            case ATENCION://Compromiso de Atención
                idString = R.string.menu_mibrio_briotiempos;
                idImagen = R.drawable.menu_mibrio_briotiempos_on;
                break;
            case RECOMPENSAS://Recompensa Brío
                idString = R.string.menu_mibrio_briorecompensa;
                idImagen = R.drawable.menu_mibrio_briorecompensa_on;
                break;
            
        }
        strname = context.getString (idString);
        String opcionResIdName = context.getResources ().getResourceName (idString);
        strPermiso = opcionResIdName.substring (opcionResIdName.lastIndexOf ("/") + 1, opcionResIdName.length ());
        
        item = new DLMenuMiBrio (strname, idImagen, strPermiso, strenum);
        
        return item;
    }
    
    
    @Override
    public void configureUI () {
    
    }
    
    @Override
    protected void beforeRemove () {
    
    }
}
