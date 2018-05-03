package net.hova_it.barared.brio.apis.admin;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.MenuManager;
import net.hova_it.barared.brio.apis.models.daos.UsuarioDAO;
import net.hova_it.barared.brio.apis.models.entities.Usuario;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;

import java.util.ArrayList;
import java.util.List;

/**Adaptador del llenado de usuario
 * Created by Mauricio Ceron on 03/03/2016.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UsuarioViewHolder> implements UserFormManager.UserFromManagerEditListener {
    private List<Usuario> usuarios;
    private Context context;
    //private UserFormFragment edicionUsuario;
    private UserFormManager userFormManager;
    private FragmentManager fragmentManager;
    private CatalogUsuarioFragment catalogUsuarioFragment;
    private BrioConfirmDialog dialogConfirmacion;
    private int layout;
    private String mensaje;
    private final Object me;
    private UsuarioDAO usuarioDAO;
    private MenuManager menuManager;

    /**Constructor del adaptador
     * @param context contexto de la aplicacion
     * @param menuManager manager de los menus
     * @param usuarios lista de usuarios
     */

public UserAdapter(Context context, MenuManager menuManager, List<Usuario> usuarios){
        this.context = context;
        this.usuarios = usuarios;//new ArrayList<>();
        this.userFormManager =UserFormManager.getInstance(context);
        this.me = this;
        this.menuManager = menuManager;
        usuarioDAO = ((BrioActivityMain)context).modelManager.usuarios;

        }
    public void setFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }
@Override
public UsuarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vItem = LayoutInflater.from(context).inflate(R.layout.admin_usuario_row, parent, false);
        return new UsuarioViewHolder(vItem);
        }

@Override
public void onBindViewHolder(UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.usuario2 = usuario;
        holder.posicion=position;
        holder.usuario.setText(usuario.getUsuario());
        holder.nombre.setText(usuario.getNombre());
        holder.apellido.setText(usuario.getApellidos());
        holder.usuarioActivo.setChecked(usuario.getActivo());

        if(usuario.getIdUsuario() == 1){
            holder.usuarioActivo.setEnabled(false);
        }


        }

@Override
public int getItemCount() {
        return usuarios.size();
        }

    /**
     * Te trae el arreglo de usuarios
     * @param data usuarios
     */
public void setData(List<Usuario> data) {
        if (data != null) {
        usuarios = new ArrayList<>();
        usuarios.addAll(data);
        }
        this.notifyDataSetChanged();
        }

    @Override
    public void onEdit() {
        catalogUsuarioFragment.onEdit();
    }

    @Override
    public void onCancel() {
        Log.w("onCancel", "adios!!!!!!!!");

    }


    /**Declaracion de las vistas que tiene el item del recyclerview     */
    public class UsuarioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView usuario;
    public TextView nombre;
    public TextView apellido;
    public CheckBox usuarioActivo;
    public LinearLayout rowUsuario;
    public int posicion;
    public Usuario usuario2;

        /**Caste de las variables con respecto a sus vistas
         *  @param vitem view item
         */
    public UsuarioViewHolder(View vitem) {
        super(vitem);
        usuario=(TextView)vitem.findViewById(R.id.usuario);
        nombre=(TextView)vitem.findViewById(R.id.nombre);
        apellido=(TextView)vitem.findViewById(R.id.apellido);
        usuarioActivo=(CheckBox)vitem.findViewById(R.id.userActivo);
        rowUsuario=(LinearLayout)vitem.findViewById(R.id.rowUsuario);
        rowUsuario.setOnClickListener(this);
        usuarioActivo.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rowUsuario:
                userFormManager.setAdapter((UserAdapter) me) ;
                menuManager.onShowOptionFragment(userFormManager.replaceFragment(fragmentManager, R.id.fragmentHolder, usuario2));
            break;

            case R.id.userActivo:
                final Usuario usuario = usuarios.get(posicion);
/*
                DialogActionListener dialogActionListener = new DialogActionListener() {
                    @Override
                    public void onAcceptResult(Object results) {
                        usuarioDAO.save(usuario);

                    }

                    @Override
                    public void onCancelResult(Object results) {
                        Log.w("onCancel",usuario.getActivo()+"");
                        usuarioActivo.setChecked(!usuario.getActivo());
                    }
                };

                if(usuarioActivo.isChecked()){
                    usuario.setActivo(true);
                    mensaje=(Utils.getString(R.string.cat_usuarios_activar,context))+ usuario.getUsuario() +"?";
                }else {
                    usuario.setActivo(false);
                    mensaje = (Utils.getString(R.string.cat_usuarios_desactivar,context)) + usuario.getUsuario() + "?";
                }

                dialogConfirmacion = (DialogConfirmacion.newInstance(mensaje, dialogActionListener));
                dialogConfirmacion.show(((AppCompatActivity) context).getSupportFragmentManager(), "Confirmacion");
*/

                DialogListener dialogListener = new DialogListener() {
                    @Override
                    public void onAccept() {
                        usuarioDAO.save(usuario);

                    }

                    @Override
                    public void onCancel() {
                        Log.w("onCancel",usuario.getActivo()+"");
                        usuarioActivo.setChecked(!usuario.getActivo());
                    }
                };

                if(usuarioActivo.isChecked()){
                    usuario.setActivo(true);
                    mensaje=(Utils.getString(R.string.cat_usuarios_activar,context))+ usuario.getUsuario() +"?";
                }else {
                    usuario.setActivo(false);
                    mensaje = (Utils.getString(R.string.cat_usuarios_desactivar,context)) + usuario.getUsuario() + "?";
                }

                dialogConfirmacion = new BrioConfirmDialog((AppCompatActivity) context, "", mensaje, null, dialogListener);
                dialogConfirmacion.show();

            break;
        }
    }
}

    /**Set del catalogo de usuario quien manda a llamar al adapatador
     * @param catalogUsuarioFragment fragment de usuarios
     */
    public void setCatalogUsuarioFragment(CatalogUsuarioFragment catalogUsuarioFragment) {
        this.catalogUsuarioFragment = catalogUsuarioFragment;
    }

    public interface AdapterUsuarioEditListener {
        void onEdit();
    }
}

