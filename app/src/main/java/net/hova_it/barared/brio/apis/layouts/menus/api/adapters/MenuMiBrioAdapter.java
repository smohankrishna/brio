package net.hova_it.barared.brio.apis.layouts.menus.api.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.holder.MenuBrioHolder;
import net.hova_it.barared.brio.apis.models.DLMenuMiBrio;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;

import java.util.ArrayList;

/**
 * Created by guillermo.ortiz on 21/03/18.
 */

public class MenuMiBrioAdapter extends RecyclerView.Adapter<MenuBrioHolder> {
    
    
    private FragmentListButtonListener fragmentListButtonListener;
    private Context context;
    ArrayList<DLMenuMiBrio> items;
    private String permisos;
    
    public MenuMiBrioAdapter (Context context, ArrayList<DLMenuMiBrio> items) {
        this.context = context;
        this.items = items;
        
        SessionManager sessionManager = SessionManager.getInstance (context);
        int idperfil = sessionManager.readInt ("idPerfil");
        permisos = context.getResources ().getStringArray (R.array.brio_permisos)[idperfil];
        
    }
    
    public void setFragmentListButtonListener (FragmentListButtonListener fragmentListButtonListener) {
        this.fragmentListButtonListener = fragmentListButtonListener;
    }
    
    @Override
    public MenuBrioHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View item = LayoutInflater.from (context).inflate (R.layout.menu_item, null);
        return new MenuBrioHolder (item);
    }
    
    public void updateItems(ArrayList<DLMenuMiBrio> items){
        this.items = items;
        notifyDataSetChanged ();
    }
    
    @Override
    public void onBindViewHolder (MenuBrioHolder holder, final int position) {
        
        final DLMenuMiBrio item = items.get (position);
        
        holder.itemView.setTag (item);
        holder.itemView.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick (View v) {
                if (Utils.shouldPerformClick ()) {
                    //regresa el boton y la posicion
                    fragmentListButtonListener.onListButtonClicked (v, position);
                }
            }
        });
        
        holder.menu_titulo.setText (items.get (position).getStrname ());
        if (! permisos.contains (item.getStrPermiso ())) {
            //no tienes permiso para el menu
            holder.itemView.setOnClickListener (nopermiso);
        }
        
        // Aplicando iconos
        if (item.getIdImagen () == - 1) {
            holder.menu_icono.setVisibility (View.GONE);
        } else {
            holder.menu_icono.setVisibility (View.VISIBLE);
            holder.menu_icono.setImageResource (item.getIdImagen ());
        }
        
    }
    
    @Override
    public int getItemCount () {
        return items.size ();
    }
    
    /**
     * Listener para informar al usuario que no tiene permiso para acceder a algúna opción de menú.
     */
    private View.OnClickListener nopermiso = new View.OnClickListener () {
        @Override
        public void onClick (View v) {
            BrioAlertDialog bad = new BrioAlertDialog ((AppCompatActivity) context,
                    "Opcion no disponible",
                    "Tu perfil no permite utilizar esta opción");
            bad.show ();
        }
    };
    
}
