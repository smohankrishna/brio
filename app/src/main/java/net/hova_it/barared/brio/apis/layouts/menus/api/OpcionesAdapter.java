package net.hova_it.barared.brio.apis.layouts.menus.api;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.holder.MenuBrioHolder;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;

import org.apache.commons.lang3.reflect.Typed;

/**
 * Adapter para llenar las opciones que muestra el MenuFragment.
 * Tambien gestiona los permisos por tipo de usuario en cada menú.
 *
 * @see MenuFragment
 *
 * Este adapter carga automaticamente los arrays
 * resid_array_string_menuitems y resid_array_drawable_menuiconsoff
 * (Strings y drawables) para generar los items a mostrar en el
 * recyclerview del menú.
 *
 * Created by Herman Peralta on 09/03/2016.
 */
public class OpcionesAdapter extends RecyclerView.Adapter<MenuBrioHolder> {

    private final static int NO_RESOURCE = -1;

    private Context context;

    private String permisos;
    private String[] opcionesStrings;

    private int[] opcionesResIds;
    private String[] opcionesResNames;

    private int[] iconosOffResIds;

    /**
     * Listener para informar al usuario que no tiene permiso para acceder a algúna opción de menú.
     */
    private View.OnClickListener nopermiso = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) context,
                    "Opcion no disponible",
                    "Tu perfil no permite utilizar esta opción");
            bad.show();
        }
    };

    private FragmentListButtonListener fragmentListButtonListener;

    /**
     * Carga los strings del array resid_array_string_menuitems y los drawables del array resid_array_drawable_menuiconsoff
     * y automáticamente genera los items necesarios en el adapter, asociando por índice en el array cada texto con cada imagen.
     *
     * @param context
     * @param resid_array_string_menuitems
     * @param resid_array_drawable_menuiconsoff
     */
    public OpcionesAdapter(Context context, int resid_array_string_menuitems, int resid_array_drawable_menuiconsoff) {
        this.context = context;

        SessionManager sessionManager = SessionManager.getInstance(context);

        int idperfil = sessionManager.readInt("idPerfil");
        permisos = context.getResources().getStringArray(R.array.brio_permisos)[idperfil];


        TypedArray menuTitlesArray = context.getResources().obtainTypedArray(resid_array_string_menuitems);
        TypedArray menuIconsOffArray = resid_array_drawable_menuiconsoff != NO_RESOURCE ? context.getResources().obtainTypedArray(resid_array_drawable_menuiconsoff) : null;

        opcionesStrings = new String[menuTitlesArray.length()];
        opcionesResIds = new int[opcionesStrings.length];
        opcionesResNames = new String[opcionesStrings.length];
        iconosOffResIds = new int[opcionesStrings.length];

        for(int position=0; position<menuTitlesArray.length() ; position++) {
            opcionesStrings[position] = menuTitlesArray.getString(position);
            opcionesResIds[position] = menuTitlesArray.getResourceId(position, NO_RESOURCE);
            iconosOffResIds[position] = menuIconsOffArray != null? menuIconsOffArray.getResourceId(position, NO_RESOURCE) : NO_RESOURCE;

            String opcionResIdName = context.getResources().getResourceName(opcionesResIds[position]);
            opcionesResNames[position] = opcionResIdName.substring(opcionResIdName.lastIndexOf("/") + 1, opcionResIdName.length());

        }

        menuTitlesArray.recycle();
        if(menuIconsOffArray != null) { menuIconsOffArray.recycle(); }
    }

    public void setFragmentListButtonListener(FragmentListButtonListener fragmentListButtonListener) {
        this.fragmentListButtonListener = fragmentListButtonListener;
    }

    @Override
    public MenuBrioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.menu_item, null);
        return new MenuBrioHolder (item);
    }

    @Override
    public void onBindViewHolder(MenuBrioHolder menuBrioHolder, int position) {
        menuBrioHolder.itemView.setTag(position);
        menuBrioHolder.itemView.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick (View v) {
                if(Utils.shouldPerformClick()) {
                    //regresa el boton y la posicion
                    fragmentListButtonListener.onListButtonClicked(v, v.getTag());
                }
            }
        });
        

        menuBrioHolder.menu_titulo.setText(opcionesStrings[position]);
        //FIXME:solo comentar para test
         if(!permisos.contains( opcionesResNames[position] )) {
            //no tienes permiso para el menu
            menuBrioHolder.itemView.setOnClickListener(nopermiso);
        }

        // Aplicando iconos
        if(iconosOffResIds[position] == NO_RESOURCE) {
            menuBrioHolder.menu_icono.setVisibility(View.GONE);
        } else {
            menuBrioHolder.menu_icono.setVisibility(View.VISIBLE);
            menuBrioHolder.menu_icono.setImageResource(iconosOffResIds[position]);
        }

    }

    @Override
    public int getItemCount() {
        return opcionesStrings.length;
    }

}
