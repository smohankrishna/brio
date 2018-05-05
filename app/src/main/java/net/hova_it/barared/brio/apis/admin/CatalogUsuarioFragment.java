package net.hova_it.barared.brio.apis.admin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Usuario;

import java.util.List;

/**
 * Created by Mauricio Ceron on 03/03/2016.
 * Clase que te muestra el catalogo de usuarios dados de alta
 */
public class CatalogUsuarioFragment extends OptionMenuFragment implements UserAdapter.AdapterUsuarioEditListener {

    private RecyclerView recycler;
    private UserAdapter adapter;
    private View root;
    private TextView catTitle;
    private FragmentManager fragmentManager;
    private int layout;
    private List<Usuario> usuarios;

    ModelManager manager;

    public void setFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }

    public CatalogUsuarioFragment(){

    }

    @Override
    public void onResume() {

        super.onResume();
        Log.w("searchAlarmStart", "entreeeeee");
        adapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        root = inflater.inflate(R.layout.admin_catalogo_usuarios, container, false);
        catTitle = (TextView)root.findViewById(R.id.tv_catalog_titles);
        catTitle.setText(getString(R.string.cat_usuarios_titulo));
        recycler = (RecyclerView)root.findViewById(R.id.reciclerViewCatSen);

        manager = new ModelManager(getActivity());
        usuarios = update();//manager.usuarios.getAll();

        recycler.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(llm);

        adapter = new UserAdapter(getActivity(),menuManager, usuarios);
        adapter.notifyDataSetChanged();
        adapter.setFragmentManager(fragmentManager);
        adapter.setCatalogUsuarioFragment(this);
        adapter.setLayout(layout);

        recycler.setAdapter(adapter);

        return root;
    }

    /**
     * Funcion que te actualiza la lista de usuarios
     * @return retorna la lista de usuarios
     */
    public List<Usuario> update(){
        List<Usuario> usuarios=manager.usuarios.getAll();
        //usuarios.delete(0);
        return usuarios;
    }

    /**
     * Es mandada a llamar cuando se edita un usuario y se actualiza
     */
    @Override
    public void onEdit() {
        Log.w("onEdit de catalogo", "entreeeeee");
        adapter.setData(update());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected View getRootView() {
        return root;
    }

    @Override
    protected void beforeRemove() {

    }
}

