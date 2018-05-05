package net.hova_it.barared.brio.apis.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Articulo;
import net.hova_it.barared.brio.apis.models.entities.Inventario;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.views.BrioEditText;

/**
 * Created by Edith Maldonado on 22/03/2016.
 * Clase que manda a llamar el POS cuando el articulo no existe para dar de alta express solo con los campos necesarios
 */
public class ArticleExpressDialog extends Fragment {

    private final String TAG = "ARTICLE_EXPRESS_DIALOG";

    private View root, btnDContinuar, btnDCancelar;
    private TextView dialogCodigoPro;
    private BrioEditText dialogNomPro, dialogPrecioPro;
    public String codigo;
    private String ap_codigo, ap_nombre;
    private double ap_precio;
    public DialogListener dialogListener;
    private ScrollView scrollViewDialog;
    private Context context;
    ModelManager modelManager;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.article_express_dialog, container, false);//getActivity().getLayoutInflater().inflate(R.layout.article_express_dialog, null);

        context = getActivity();

        scrollViewDialog = new ScrollView(context);
        modelManager = new ModelManager(context);
        sessionManager = SessionManager.getInstance(context);

        dialogCodigoPro = (TextView) root.findViewById(R.id.tvdialogaddexpcodigo);
        dialogCodigoPro.setText(codigo);
        dialogNomPro = (BrioEditText)root.findViewById(R.id.tvdialogaddexpnombre);
        dialogPrecioPro = (BrioEditText)root.findViewById(R.id.tvdialogaddexpprecio);
        dialogPrecioPro.setHint(Utils.getString(R.string.ap_dialogaddexpress_hintprecio, getActivity()).replace("?",Utils.getString(R.string.brio_unidad, context)));
        btnDCancelar = root.findViewById(R.id.tv_btndialogcancel);
        btnDContinuar = root.findViewById(R.id.tv_btnimagen);


        btnDCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), Utils.getString(R.string.ap_dialogaddexpress_cancel, context), Toast.LENGTH_LONG).show();
                remove1();
            }
        });

        btnDContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validacion()) {

                    saveArticle();

                    Toast.makeText(getContext(), R.string.ap_dialogaddexpress_exit, Toast.LENGTH_LONG).show();
                    remove1();
                    dialogListener.onAccept();
                }
            }
        });

        return root;
    }

    /**Remueve el fragmento al terminar la transaccion y cierra el teclado
     */
    public void remove1() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        ((BrioActivityMain)getActivity()).managerTeclado.closeKeyboard();
        dialogListener.onCancel();
    }

    /**
     *Metodo donde se mostrara el fragmento
     * @param fragmentManager Variable que trae el id de la vista donde se va a mostrar el fragmento
     */
    public void show1(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().add(R.id.fragmentHolder, this).commit();
    }

    /**
     * Se realizan las validaciones si existe el nombre del producto y permitir un precio valido
     * @return Retorna el booleano de la validacion
     */

    public boolean validacion(){

        getProductoExpress();

        if (dialogCodigoPro.length()>30){
            dialogCodigoPro.setError(Utils.getString(R.string.ap_cod_product_invalid,context));
            Toast.makeText(context, Utils.getString(R.string.ap_cod_product_invalid, context),Toast.LENGTH_SHORT).show();
            scrollViewDialog.scrollTo(0, dialogCodigoPro.getTop());
            return false;
        }
        else if (ap_nombre.trim().equalsIgnoreCase("")) {
            dialogNomPro.setError(Utils.getString(R.string.ap_valnombre, context));
            Toast.makeText(context, Utils.getString(R.string.ap_valnombre,context), Toast.LENGTH_SHORT).show();
            scrollViewDialog.scrollTo(0,dialogNomPro.getTop());
            return false;
        }else if (modelManager.articulos.getByDescArticulo(ap_nombre)!= null){
            dialogNomPro.setError(Utils.getString(R.string.ap_valnombrequals,context));
            Toast.makeText(context, Utils.getString(R.string.ap_valnombrequals,context), Toast.LENGTH_SHORT).show();
            scrollViewDialog.scrollTo(0, dialogNomPro.getTop());
            return false;
        }
        if(dialogPrecioPro.length()==0){
            dialogPrecioPro.setError(Utils.getString(R.string.ap_val_precnull, context));
            Toast.makeText(context, Utils.getString(R.string.ap_val_precnull,context), Toast.LENGTH_SHORT).show();
            scrollViewDialog.scrollTo(0,dialogPrecioPro.getTop());
            return false;
        }else if (ap_precio<=0) {
            dialogPrecioPro.setError(Utils.getString(R.string.ap_valpregral, context));
            Toast.makeText(context, Utils.getString(R.string.ap_valpregral, context), Toast.LENGTH_SHORT).show();
            scrollViewDialog.scrollTo(0, dialogPrecioPro.getTop());
            return false;
        }
        return  true;
    }

    /** Resetea los campos de texto cuando se cierra y se cambia de fragmento
     */

    public void cleanCampos(){
        dialogNomPro.getText().clear();
        dialogPrecioPro.getText().clear();
    }

    /**
     * Obtiene las cadenas de texto
     */
    public void getProductoExpress(){
        ap_codigo =  dialogCodigoPro.getText().toString().trim();
        ap_nombre = dialogNomPro.getText().toString().trim();

        String ap_precios = dialogPrecioPro.getText().toString().trim();
        ap_precio = Double.valueOf(ap_precios.length()==0 ? "0" : ap_precios);

    }

    /**
     * Metodo que guarda el articulo en base datos, como solo se ingresa el nombre y el precio
     * los demas datos se guardan como los de default
     */
    public void saveArticle(){

        Articulo articulo = new Articulo();
        Inventario inventario = new Inventario();

        articulo.setCodigoBarras(ap_codigo);
        articulo.setNombre(ap_nombre);
        articulo.setIdMarca(868);
        articulo.setIdPresentacion(33);
        articulo.setIdUnidad(28);
        articulo.setContenido(0);
        articulo.setIdFamilia(98);
        articulo.setGranel(false);
        articulo.setImagen("sin imagen");
        articulo.setIdUsuario(sessionManager.readInt("idUsuario"));
        articulo.setPrecioVenta(ap_precio);

        Long rowId = modelManager.articulos.save(articulo);
        Log.d(TAG, "Articulo creado con ID: " + rowId);
        Articulo saved = modelManager.articulos.getByIdArticulo(rowId.intValue());
        Log.d(TAG, "Nuevo Articulo express \n" + Utils.pojoToString(saved));

        inventario.setIdArticulo(rowId.intValue());
        inventario.setExistencias(0);
        inventario.setIdSesion(sessionManager.readInt("idSesion"));
        Long inv = modelManager.inventario.save(inventario);
        Log.d(TAG, "Exisencias en test_inventario creadas" + inventario);
        Inventario saveinv = modelManager.inventario.getByIdInventario(inv.intValue());
        Log.d(TAG, "Existencias \n" + Utils.pojoToString(saveinv));

        Articulo lectura = modelManager.articulos.getByIdArticulo(rowId.intValue());
        Log.d(TAG, "Articulo Guardado \n " + Utils.pojoToString(lectura));
        cleanCampos();


    }

    /**
     * Trae el codigo de barras de la pistola y lo setea en el campo de codigo
     * @param codigo contiene el codigo de barras
     */

    public void setDialogCodigoPro(String codigo) {
        this.codigo = codigo;
    }

}
