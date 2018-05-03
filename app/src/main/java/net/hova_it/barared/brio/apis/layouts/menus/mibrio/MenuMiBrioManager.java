package net.hova_it.barared.brio.apis.layouts.menus.mibrio;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuFragment;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuManager;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.DLMenuMiBrio;
import net.hova_it.barared.brio.apis.models.DLSortMenu;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Settings;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.videollamada.VideoLlamadaFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import lat.brio.core.AppController;
import lat.brio.core.BrioGlobales;

import static net.hova_it.barared.brio.apis.layouts.menus.mibrio.TagMiBrioEnum.*;

/**
 * Created by Herman Peralta on 09/03/2016.
 * Muestra el menu de mi brio
 */
public class MenuMiBrioManager extends MenuManager {
    
    protected static MenuMiBrioManager daManager;
    static Context context;
    
    public static MenuMiBrioManager getInstance (Context contexto) {
        if (daManager == null) {
            daManager = new MenuMiBrioManager (contexto);
        }
        
        context = contexto;
        return daManager;
    }
    
    private MenuMiBrioManager (Context context) {
        super (context, "MIBRIO", R.array.menu_mibrio, false);
    }
    
    @Override
    protected FragmentListButtonListener getMenulistener () {
        return menulistener;
    }
    
    @Override
    protected MenuFragment getMenuFragmentInstance () {
        return MenuMiBrioFragment.newInstance (context);
    }
    
    private FragmentListButtonListener menulistener = new FragmentListButtonListener () {
        @Override
        public void onListButtonClicked (View btn, Object value) {
            int position = (Integer) value;
            DLMenuMiBrio tag = (DLMenuMiBrio) btn.getTag ();

          /* Estas opciones vienen de res/xml/arrays
               y se configuraron en el MenuMiBrioFragment */
            OptionMenuFragment opcion = null;
            
            TagMiBrioEnum miBrioEnum = valueOf (tag.getTagEnum ());
            switch (miBrioEnum) {
                case UNIVERSIDAD://Universidad Brío
                    opcion = new BrioUniversidad ();
                    break;
                case NOTICIAS://Brío Noticias
                    opcion = new BrioPdfView ();
                    break;
                case MAIL://Mi mail brio
                    opcion = new MailBrioView ();
                    break;
                case CONTRATO://Mi Contrato Brío
                    opcion = new BrioContrato ();
                    break;
                case GARANTIA://Garantía Extendida
                    opcion = new BrioGarantiaExtendida ();
                    break;
                case HORARIOS://Horarios Brío
                    opcion = new BrioHorarios ();
                    break;
                case POLITICAS://Políticas de Servicio
                    opcion = new BrioPoliticas ();
                    break;
                case ASOCIADOS://Servicios para el Asociado
                    opcion = new BrioServicios ();
                    break;
                case ATENCION://Compromiso de Atención
                    opcion = new BrioCompromiso ();
                    break;
                case RECOMPENSAS://Recompensa Brío
                    opcion = new BrioRecompensa ();
                    break;
                
            }
            
            updateSortMiBrio (tag.getTagEnum ());
            
            if (opcion != null) {
                onShowOptionFragment (opcion); // esto indica que ya mostré un fragment y que se habilite el back
            }
            DebugLog.log (getClass (), "MENU", "Listener opcion " + position);
        }
    };
    
    private void updateSortMiBrio (String strEnum) {
        ArrayList<DLSortMenu> list = Utils.getSortMiBrio ();
        
        StringBuilder strSort = new StringBuilder ("");
        int i = 0;
        for (DLSortMenu dl : list) {
            if (dl.getKey ().equals (strEnum)) {
                int val = dl.getValue ();
                val++;
                dl.setValue (val);
            }
            String sort;
            if (i == 0) {
                sort = String.format (new Locale ("ES", "MX"), "%s;%d", dl.getKey (), dl.getValue ());
            } else {
                sort = String.format (new Locale ("ES", "MX"), "|%s;%d", dl.getKey (), dl.getValue ());
            }
            strSort.append (sort);
            i++;
        }
        ModelManager modelManager = AppController.getInstance ().getModelManager ();
        Settings settSort = modelManager.settings.getByNombre (BrioGlobales.KEY_MI_BRIO_ORDERNAMIENTO);
        
        if (settSort == null) {
            settSort = new Settings ();
            settSort.setNombre (BrioGlobales.KEY_MI_BRIO_ORDERNAMIENTO);
            settSort.setValor (strSort.toString ());
            modelManager.settings.save (settSort);
        } else {
            modelManager.settings.update (BrioGlobales.KEY_MI_BRIO_ORDERNAMIENTO, strSort.toString ());
        }
    }
    
    public void iniciarVideoLlamada () {
        OptionMenuFragment opcion = new VideoLlamadaFragment ();
        onShowOptionFragment (opcion);
    }
}
