package net.hova_it.barared.brio.apis.layouts.menus.api.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.hova_it.barared.brio.R;

/**
 * Created by guillermo.ortiz on 21/03/18.
 */

public class MenuBrioHolder extends RecyclerView.ViewHolder  {
    
    public TextView menu_titulo;
    public ImageView menu_icono;
    
    public MenuBrioHolder (View itemView) {
        super(itemView);
        
        menu_titulo = (TextView) itemView.findViewById(R.id.menu_titulo);
        menu_icono = (ImageView) itemView.findViewById(R.id.menu_icono);
        
        //itemView.setOnClickListener(this);
    }
    
    public TextView getMenu_titulo () {
        return menu_titulo;
    }
    
    public ImageView getMenu_icono () {
        return menu_icono;
    }
}
