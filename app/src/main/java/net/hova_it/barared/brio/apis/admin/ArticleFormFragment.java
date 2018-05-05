package net.hova_it.barared.brio.apis.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Articulo;
import net.hova_it.barared.brio.apis.models.entities.Inventario;
import net.hova_it.barared.brio.apis.hw.barcodescanner.ScannerManager;
import net.hova_it.barared.brio.apis.session.SessionManager;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAcceptDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.views.BrioEditText;


/**
 * Created by Edith Maldonado on 08/01/2016.
 * Esta clase es un fragmento que da de alta un nuevo producto hereda OptionMenuFragment
 * e implementa el listener de la lectora de codigo de barras
 */
public class ArticleFormFragment extends OptionMenuFragment implements ScannerManager.ScannerListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private final String TAG = "BARA_LOG";
    private Context context;
    private BrioEditText etCodigo,  etNombre, etPreciog, etPrecioCom,etExist;
    private Button btnSave,btnCancel;
    private String nombre,ap_nombre, ap_codigo;
    private CheckBox cbox_si, cbox_no;
    private boolean ap_granel;

    private double ap_preciogral, ap_preciocomp, ap_existencia ;



    ModelManager manager;
    ScannerManager managerScanner;
    private SessionManager sessionManager;
    private View root;
    //private Button btnPistola;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true); // Evita que se muera el fragment al rotar cel


        // final ArticleFormFragmenListener articleFormFragmentListener = (articleForManager);
        manager = ((BrioBaseActivity)getActivity()).modelManager;    //esta heredando de brio y se ve como lo manda a llamar
        context = getActivity();
        sessionManager = SessionManager.getInstance(context);

        root = inflater.inflate(R.layout.article_form_fragment, container, false);



        // maxLength on barcode EditText set to 30 digits RO
        EditText etproduc_codigo = (EditText) root.findViewById(R.id.etproduc_codigo);
        etproduc_codigo.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(30)});

        etCodigo = (BrioEditText) root.findViewById(R.id.etproduc_codigo);
        etNombre = (BrioEditText) root.findViewById(R.id.etproduc_nombre);
        etPreciog = (BrioEditText) root.findViewById(R.id.etproduc_pregral);
        etPrecioCom = (BrioEditText)root.findViewById(R.id.etproduc_precompra);
        etExist = (BrioEditText)root.findViewById(R.id.etproduc_existencias);
        cbox_si = (CheckBox) root.findViewById(R.id.cbox_si);
        cbox_no = (CheckBox) root.findViewById(R.id.cbox_no);
        cbox_no.setOnCheckedChangeListener(this);
        cbox_si.setOnCheckedChangeListener(this);
        cbox_no.setChecked(true);


        btnSave = (Button) root.findViewById(R.id.btn_botonalta);
        btnSave.setOnClickListener(this);
        btnCancel = (Button) root.findViewById(R.id.btn_botoncancel);
        btnCancel.setOnClickListener(this);

        managerScanner = ScannerManager.getInstance(getActivity());

        etCodigo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ap_codigo= etCodigo.getText().toString().trim();

                if (!hasFocus) {
                    validaCod(ap_codigo);

                }
            }
        });

        nombre = etNombre.getText().toString();

        etNombre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                ap_nombre = etNombre.getText().toString();
                if (!hasFocus) {
                    validaNom(ap_nombre);

                }
            }
        });


        return root;
    }

    /**
     * Aqui es donde se valida la existencia del producto por el codigo  de barras, si no se encuentra en la base
     * te setea el codigo en el campo correspondiente en caso contrario te manda un mensaje de que ya existe
     * @param codigo Cadena de codigo de barras que se obtiene con la pistola
     *
     * @return retorna el boolean de la validacion
     */
    private boolean validaCod(String codigo) {
        if (codigo != null){
            if(codigo.length()>30){
                etCodigo.setError("codigo invalido");
                Toast.makeText(context, Utils.getString(R.string.ap_cod_product_invalid, context),Toast.LENGTH_SHORT).show();
                getScroll().scrollTo(0, etCodigo.getTop());
                return false;
            }
            else if (manager.articulos.getByCodigoBarras(codigo) != null) {
                etCodigo.setError(Utils.getString(R.string.ap_valcodigoequals, context));
                Toast.makeText(context, Utils.getString(R.string.ap_valcodigoequals, context), Toast.LENGTH_SHORT).show();
                getScroll().scrollTo(0, etCodigo.getTop());
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Validacion de la  existencia del nombre del producto en base de datos
     * @param nombre cadena nombre del producto
     * @return retorna el boolean de la validacion
     */
    private boolean validaNom(String nombre){

        if (nombre!=null && nombre.length()>0 && manager.articulos.getByDescArticulo(nombre) != null) {
             etNombre.setError(Utils.getString(R.string.ap_valnombrequals, context));
             Toast.makeText(context, Utils.getString(R.string.ap_valnombrequals, context), Toast.LENGTH_SHORT).show();
             getScroll().scrollTo(0, etNombre.getTop());
             return false;
        }else if(nombre == null || nombre.isEmpty()) {
            etNombre.setError(Utils.getString(R.string.ap_valnombre, context));
            Toast.makeText(context, Utils.getString(R.string.ap_valnombre, context), Toast.LENGTH_SHORT).show();
            getScroll().scrollTo(0, etNombre.getTop());
            return false;
        }

        return  true;
    }

    /**
     * Obtiene las cadenas de texto de los campos
     */
    public void getProducto(){

        String ap_pgral = etPreciog.getText().toString().trim();
        ap_preciogral = Double.valueOf(ap_pgral.isEmpty() ? "0" : ap_pgral);
        String ap_pcompra = etPrecioCom.getText().toString().trim();
        ap_preciocomp = Double.valueOf(ap_pcompra.isEmpty() ? "0" : ap_pcompra);
        String textExist = etExist.getText().toString();
        ap_existencia = Double.valueOf(textExist.equals("") ? "0" : textExist);

        ap_granel = cbox_si.isChecked();


    }

    /**
     * Validaciones para comprobar tipos de datos correctos
     * @return el boolean de la validacion
     */

    public boolean  validacion(){
        getProducto();

        if (ap_preciogral <= 0 ) {
            etPreciog.setError(Utils.getString(R.string.ap_valpregral,context));
            Toast.makeText(context, Utils.getString(R.string.ap_valpregral,context), Toast.LENGTH_SHORT).show();
            getScroll().scrollTo(0, etPreciog.getTop());
            return false;
        }

        if (ap_existencia < 0){
            etExist.setError(Utils.getString(R.string.ap_valexist,context));
            Toast.makeText(context, Utils.getString(R.string.ap_valexist,context), Toast.LENGTH_LONG).show();
            getScroll().scrollTo(0, etExist.getTop());
            return false;
        }

        if(ap_codigo.isEmpty() && cbox_no.isChecked()){
            etCodigo.setError(Utils.getString(R.string.ap_valcodigo, context));
            Toast.makeText(context, Utils.getString(R.string.ap_valcodigo, context), Toast.LENGTH_SHORT).show();
            getScroll().scrollTo(0,etCodigo.getTop());
            return false;
        }

        if (ap_codigo.length()>30){
            etCodigo.setError(Utils.getString(R.string.ap_cod_product_invalid, context));
            Toast.makeText(context, Utils.getString(R.string.ap_cod_product_invalid, context), Toast.LENGTH_SHORT).show();
            getScroll().scrollTo(0,etCodigo.getTop());
            return false;
        }
        return true;
    }

    /**Boton de guardar, hace las validaciones y guarda la base de datos
     * @param v Se llama cuando se hace click en guardar
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_botonalta:
                ap_codigo = etCodigo.getText().toString().trim();
                if( validaCod(ap_codigo) && validaNom(ap_nombre) && validacion()) {
                    ap_codigo = ap_codigo.isEmpty() && cbox_si.isChecked()? "T-"+Utils.getCurrentTimestamp() : ap_codigo;
                    saveArticle();
                }
                break;
            case R.id.btn_botoncancel:
                cleanFields();
                remove();
                break;
        }
    }

    /**Funcion para limpiar los campos de texto     */

    public void cleanFields(){

        etCodigo.getText().clear();
        etNombre.getText().clear();
        etPreciog.getText().clear();
        etPrecioCom.getText().clear();
        etExist.getText().clear();

        cbox_si.setChecked(false);
    }


    /** Este metodo de guarda en base de datos la información del nuevo articulo  */
    public void saveArticle() {

        Articulo articulo = new Articulo();
        Inventario inventario = new Inventario();


        articulo.setCodigoBarras(ap_codigo);
        articulo.setNombre(ap_nombre);
        articulo.setPrecioCompra(ap_preciocomp);
        articulo.setIdMarca(868);
        articulo.setIdPresentacion(33);
        articulo.setIdUnidad(28);
        articulo.setContenido(0);
        articulo.setIdFamilia(98);
        articulo.setGranel(ap_granel);
        articulo.setImagen("sin imagen");
        articulo.setIdUsuario(sessionManager.readInt("idUsuario"));
        articulo.setPrecioVenta(ap_preciogral);


        Long rowId = manager.articulos.save(articulo);
        Log.d(TAG, "Articulo creado con ID: " + rowId);
        Articulo saved = manager.articulos.getByIdArticulo(rowId.intValue());
        Log.d(TAG, "Nuevo Articulo \n" + Utils.pojoToString(saved));


        inventario.setIdArticulo(rowId.intValue());
        inventario.setExistencias(ap_existencia);
        inventario.setIdSesion(sessionManager.readInt("idSesion"));
        Long inv = manager.inventario.save(inventario);
        Log.d(TAG, "Exisencias en test_inventario creadas" + inventario);
        Inventario saveinv = manager.inventario.getByIdInventario(inv.intValue());
        Log.d(TAG, "Existencias \n" + Utils.pojoToString(saveinv));


        Articulo lectura = manager.articulos.getByIdArticulo(rowId.intValue());
        Log.d(TAG, "Articulo Guardado \n " + Utils.pojoToString(lectura));



        BrioAcceptDialog bad = new BrioAcceptDialog((AppCompatActivity) context,
                Utils.getString(R.string.ap_articlecreate_title, context),
                Utils.getString(R.string.ap_article_create, context),
                Utils.getString(R.string.brio_aceptar, context), new DialogListener() {
            @Override
            public void onAccept() {

                remove();
                cleanFields();
                ((BrioActivityMain)context).managerTeclado.closeKeyboard();
            }

            @Override
            public void onCancel() {


            }
        });
        bad.show();
    }


    @Override
    public void onResume() {
        super.onResume();

        managerScanner.startScannerListening(this, ScannerManager.KEYCODE_DELIM_ENTER);
    }

    @Override
    public void onPause() {
        super.onPause();

        managerScanner.stopScannerListening(this);
    }

    /** Se ocupa para los seterror que te vuelva a posicionar en campo donde se encuentra el error
     * @return el Scroll en la posicion del campo
     */
    public ScrollView getScroll(){
        return (ScrollView)root.findViewById(R.id.scroll_article);
    }


    @Override
    protected View getRootView() {
        return root;
    }

    /**
     * Limpia los campos del fragment al momento de darle un back
     */

    @Override
    protected void beforeRemove() {
        cleanFields();
    }

    /**
     * Valida el codigo de barras con la pistola y lo setea en el campo de codigo de barras
     * @param codBarras cadena del codigo de barras
     */
    @Override
    public void onInputLineMatch(String codBarras) {
        if(validaCod(codBarras)) {
            etCodigo.setText(codBarras);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cbox_si:
                cbox_no.setChecked(!isChecked);
                break;
            case R.id.cbox_no:
                cbox_si.setChecked(!isChecked);
                break;
        }
    }
}