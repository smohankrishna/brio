package net.hova_it.barared.brio.apis.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Articulo;
import net.hova_it.barared.brio.apis.models.views.ViewArticulo;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.views.BrioEditText;

/**
 * Esta clase agrega el precio de los productos ya existentes desde el POS
 * Created by Edith Maldonado on 13/04/2016.
 */
public class ArticleEditPrecioDialog extends Fragment {

    private final String TAG = "ARTICLE_EDIT_DIALOG";

    private View root, btnDContinuar, btnDCancelar;
    private TextView dialogCodigoProEdit, dialogNomProEdit;
    private BrioEditText  dialogPrecioPro;
    private double ap_precio;
    private int id_articulo;
    public DialogListener dialogListener;
    private ScrollView scrollViewDialog;
    private Context context;
    private ModelManager modelManager;
    private SessionManager sessionManager;
    private Articulo articulo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.article_express_edit_dialog, container, false);//getActivity().getLayoutInflater().inflate(R.layout.article_express_dialog, null);

        context = getActivity();
        scrollViewDialog = new ScrollView(context);
        modelManager = new ModelManager(context);
        sessionManager = SessionManager.getInstance(context);

        articulo = modelManager.articulos.getByIdArticulo(id_articulo);//new Articulo();

        dialogCodigoProEdit = (TextView) root.findViewById(R.id.tvdialogaddexpcodigo_pre);
        dialogCodigoProEdit.setText(articulo.getCodigoBarras());
        dialogNomProEdit = (TextView)root.findViewById(R.id.tvdialogaddexpnombre_pre);
        dialogNomProEdit.setText(articulo.getNombre());
        dialogPrecioPro = (BrioEditText)root.findViewById(R.id.tvdialogaddexpprecio_ed);
        btnDCancelar = root.findViewById(R.id.tv_btndialogcancel);
        btnDContinuar = root.findViewById(R.id.tv_btnimagen);

        ViewArticulo varticulo = modelManager.viewArticulos.getByIdArticulo(id_articulo);
        String unidad = articulo.getGranel()? varticulo.getUnidad() : Utils.getString(R.string.brio_unidad, context);

        ((TextView) root.findViewById(R.id.tvdialogaddexpsubtitle)).setText(Utils.getString(R.string.ap_dialogaddexpress_edit_subtitle, context).replace("?", unidad));
        ((EditText)root.findViewById(R.id.tvdialogaddexpprecio_ed)).setHint(Utils.getString(R.string.ap_dialogaddexpress_hintprecio, context).replace("?", unidad));


        btnDCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), Utils.getString(R.string.ap_dialogaddexpress_cancel_edit, context), Toast.LENGTH_LONG).show();
                remove1();

                BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity)getActivity(), null, Utils.getString(R.string.ap_dialogaddexpress_cancel_edit, context));
                bad.show();

                dialogListener.onCancel();
            }
        });

        btnDContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validacion()) {

                    saveArticle();

                    Toast.makeText(getContext(), R.string.ap_dialogaddexpress_exit_edit, Toast.LENGTH_LONG).show();

                    remove1();
                    dialogListener.onAccept();
                }
            }
        });

        return root;
    }


    /**
     * remueve el teclado
     */
    public void remove1() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        ((BrioActivityMain)getActivity()).managerTeclado.closeKeyboard();
    }

    /**
     *Metodo donde se mostrara el fragmento
     * @param fragmentManager Variable que trae el id de la vista donde se va a mostrar el fragmento
     */
    public void show1(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().add(R.id.fragmentHolder, this).commit();
    }

    /**
     * Se realiza la validacion de que el precio sea mayor a cero
     * @return retorna el boolean de la validación
     */

    public boolean validacion(){
        getEditPrecioExpress();

        if (ap_precio<=0) {
            dialogPrecioPro.setError(Utils.getString(R.string.ap_valpregral, context));
            Toast.makeText(context, Utils.getString(R.string.ap_valpregral, context), Toast.LENGTH_SHORT).show();
            scrollViewDialog.scrollTo(0, dialogPrecioPro.getTop());
            return false;
        }
        else if (dialogCodigoProEdit.length()>30){
            dialogCodigoProEdit.setError("Codigo de Barras excede el max de digitos");
            Toast.makeText(context, Utils.getString(R.string.ap_cod_product_invalid, context),Toast.LENGTH_SHORT).show();
            scrollViewDialog.scrollTo(0,dialogCodigoProEdit.getTop());
            return false;
        }

        return  true;
    }

    /**Se limpian el campo de precio     */
    public void cleanCampos(){

        dialogPrecioPro.getText().clear();
    }

    /**
     * Obtiene la cadena de texto del precio
     */
    public void getEditPrecioExpress(){

        String ap_precios = dialogPrecioPro.getText().toString().trim();
        ap_precio = Double.valueOf(ap_precios.isEmpty() ? "0" : ap_precios);

    }

    /**
     * Guarda en base de datos solo el precio del articulo con respecto
     * al id del producto seleccionado
     */

    public void saveArticle(){


        articulo.setPrecioVenta(ap_precio);

        Long rowId = modelManager.articulos.save(articulo);
        Log.d(TAG, "Articulo creado con ID: " + rowId);
        Articulo saved = modelManager.articulos.getByIdArticulo(rowId.intValue());
        Log.d(TAG, "Nuevo Articulo express \n" + Utils.pojoToString(saved));

        Articulo lectura = modelManager.articulos.getByIdArticulo(rowId.intValue());
        Log.d(TAG, "Articulo Guardado \n " + Utils.pojoToString(lectura));
        cleanCampos();
    }

    /**Se setea el codigo de barras, se usa para la pistola
     * @param id_articulo codigo de barras
     */
    public void setId_articulo(int id_articulo) {
        this.id_articulo = id_articulo;

    }

}


