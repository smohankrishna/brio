package net.hova_it.barared.brio.apis.pos;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioManagerInterface;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.admin.ArticleEditPrecioDialog;
import net.hova_it.barared.brio.apis.admin.ArticleExpressDialog;
import net.hova_it.barared.brio.apis.hw.barcodescanner.ScannerManager;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Cliente;
import net.hova_it.barared.brio.apis.models.views.ViewArticulo;
import net.hova_it.barared.brio.apis.pos.api.POSMainFragment;
import net.hova_it.barared.brio.apis.pos.api.POSVenta;
import net.hova_it.barared.brio.apis.pos.granel.GranelAdapter;
import net.hova_it.barared.brio.apis.pos.granel.GranelFragment;
import net.hova_it.barared.brio.apis.pos.granel.GranelItem;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoManager2;
import net.hova_it.barared.brio.apis.utils.teclado.TecladoOnClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manejador del POS.
 * <br/>
 * Esta clase singleton contiene toda la lógica correspondiente al Punto de Venta,
 * incluyendo la lógica de 3 ventas en paralelo, lista de artículos a granel,
 * llamadas a fragments de alta y edición expres de articulos, ventana de búsqueda
 * de productos y conexión al lector de código de barras para la búsqueda de productos.
 * <br/>
 * El POS esta conformado de un fragment base que contiene el fragment de artículos granel
 * y un viewpager con 3 fragments (las 3 ventas en paralelo), contiene también un fragment
 * adicional para la búsqueda de productos.
 * <br/>
 * array de 3 @see POSVenta, cada uno contiene un fragment ticket
 *
 *
 * Created by Herman Peralta on 13/01/2016.
 */
public class POSManager implements BrioManagerInterface, ScannerManager.ScannerListener {
    public final static int NUM_TICKETS = 3;
    private static POSManager daManager;

    private Context context;
    private BrioActivityMain mainActivity;
    private POSMainFragment posMainFragment;
    private FragmentManager fragmentManager;
    public GranelFragment granelFragment;

    // INI: Agregado por: Manuel Delgadillo - 21/FEB/2017
    // FIN: Agregado por: Manuel Delgadillo - 21/FEB/2017

    private List<POSVenta> pagesVentas;

    private ArticulosIndexer mHistorialArticulos;

    private ModelManager managerModel;

    private boolean isShowingFragment = false;

    /**
     * Listener de click en la lista de articulos de granel para agregarlo a la venta actual.
     */
    private FragmentListButtonListener granelListener = new FragmentListButtonListener() {
        @Override
        public void onListButtonClicked(View btn, Object value) {

            DebugLog.log(POSManager.this.getClass(), "Granel", "Click en item");
            granelFragment.setEnabled(false);

            GranelItem selected = (GranelItem) value;
            DebugLog.log(POSManager.this.getClass(), "Granel", "Envio articulo a POSManager: " + Utils.pojoToString(selected.getViewArticulo()));

            onInputLineMatch(selected.getViewArticulo().getCodigoBarras());
        }
    };

    /**
     * Obtener instancia del singleton.
     *
     * @param context
     * @return
     */
    public static POSManager getInstance(Context context){
        if(daManager ==null) {
            daManager = new POSManager(context);
        }
        return daManager;
    }

    private POSManager(Context context) {
        this.context = context;
        this.mainActivity = (BrioActivityMain) context;

        mHistorialArticulos = new ArticulosIndexer(context);
        managerModel = new ModelManager(context);
    }

    @Override
    public Fragment createFragments(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;

        posMainFragment = (POSMainFragment) fragmentManager.findFragmentByTag("posMainFragment");
        granelFragment = (GranelFragment) fragmentManager.findFragmentByTag("granelFragment");

        DebugLog.log(getClass(), "POSManager", "posMainFragment null? " + (posMainFragment == null));
        DebugLog.log(getClass(), "POSManager", "granelFragment null? " + (granelFragment == null));

        // Recuperando fragment principal
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(posMainFragment == null) {
            posMainFragment = new POSMainFragment();
            fragmentTransaction.replace(containerId, posMainFragment, "posMainFragment");

            pagesVentas = new ArrayList<>();
            for(int position=0 ; position<POSManager.NUM_TICKETS ; position++) {
                pagesVentas.add(position, new POSVenta(mainActivity, posMainFragment.pageSwapListener, position, fragmentManager));
            }
            posMainFragment.pagesVentas = pagesVentas;
        } else {
            fragmentTransaction.attach(posMainFragment);
        }
        fragmentTransaction.commit();

        // Recuperando fragment interno de granel
        fragmentTransaction = fragmentManager.beginTransaction();
        if(granelFragment == null) {
            granelFragment = GranelFragment.newInstance();
            fragmentTransaction.replace(R.id.pos_panel_left, granelFragment, "granelFragment");
        } else {
            fragmentTransaction.attach(granelFragment);
        }
        fragmentTransaction.commit();

        granelFragment.setFragmentListButtonListener(granelListener);

        if(mHistorialArticulos.refresh()) {
            //todo, mandar que el superticketcontroller refresque
        }

        mainActivity.managerScanner.startScannerListening(this, ScannerManager.KEYCODE_DELIM_ENTER);

        return null;
    }

    @Override
    public void removeFragments() {
        mainActivity = ((BrioActivityMain)context);
        mainActivity.managerScanner.stopScannerListening(this);
        mainActivity.getSupportFragmentManager()
            .beginTransaction()
                .detach(granelFragment)
                .detach(posMainFragment)
            .commit();
    }

    /**
     * Listener que se invoca cuando se escanea un código de barras con el lector USB
     *
     * @param codBarras - El código de barras enviado por el lector USB
     */
    @Override
    public void onInputLineMatch(String codBarras) {
        POSVenta venta = posMainFragment.getCurrentVenta();

        switch (venta.getStatus()) {
            case POSVenta.STATUS_TICKET:
                addArticuloByCodigoBarras(codBarras);
                break;

            case POSVenta.STATUS_CLIENTE:
                findClienteByCodigoBarras(codBarras);
                break;
        }
    }

    /**
     * Agregar un articulo a la venta actual mediante su codigo de barras.
     *
     * @param codigoBarras - El código de barras del artículo a agregar a la venta.
     */
    public void addArticuloByCodigoBarras(final String codigoBarras) {
        final POSVenta venta = posMainFragment.getCurrentVenta();

        if(codigoBarras.equals(GranelAdapter.ITEM_CODBARRAS_VARIOS)) {
            agregaVarios(venta);
            return;
        }

        final ViewArticulo art = mHistorialArticulos.getByCodigoBarras(codigoBarras);
        if(art == null) {
            // si el articulo es nulo es por que no esta en la base de datos
            altaExpress(codigoBarras);
        } else {
            if(art.getPrecioBase() == 0) {
                //Si el articulo tiene precio 0, entonces para venderlo necesito que capturen el precio
                editarExpress(codigoBarras, art.getIdArticulo());
            } else {
                if (art.getGranel()) {
                    //si el articulo se vende a granel, pide la cantidad
                    mainActivity.managerTeclado.openKeyboard(true, TecladoManager2.TIPO_TECLADO.NUMERICO,
                            Utils.getString(R.string.pos_granel_add, context).replace("?", art.getUnidad()),
                            "0.00",
                            new TecladoOnClickListener() {
                                @Override
                                public void onAcceptResult(Object... results) {

                                    granelFragment.setEnabled(true);

                                    MediaUtils.hideSystemUI((AppCompatActivity) context);

                                    Double cantidad = (Double) results[0];

                                    if (cantidad <= 0) {
                                        BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) context,
                                                Utils.getString(R.string.pos_error_cantidad_title, context),
                                                Utils.getString(R.string.pos_error_cantidad_msg, context));
                                        bad.show();
                                    } else {
                                        venta.addArticulo(art, cantidad, getExistenciasInventario(art));
                                    }
                                }

                                @Override
                                public void onCancelResult(Object... results) {
                                    MediaUtils.hideSystemUI((AppCompatActivity) context);

                                    granelFragment.setEnabled(true);
                                }
                            });
                } else {
                    //si el articulo no es a granel, manda 1
                    venta.addArticulo(art, 1, getExistenciasInventario(art));
                }
            }
        }
    }

    private int varios_codbar = 0;
    private void agregaVarios(final POSVenta venta) {
        mainActivity.managerTeclado.openKeyboard(true, TecladoManager2.TIPO_TECLADO.NUMERICO,
                Utils.getString(R.string.pos_varios_precio_msg, context), "0.00", new TecladoOnClickListener() {
                    @Override
                    public void onAcceptResult(Object... results) {
                        Double precio = (Double) results[0];

                        if (precio <= 0) {
                            precio = 0.d;
                            BrioAlertDialog bad = new BrioAlertDialog((AppCompatActivity) context,
                                    "Venta de VARIOS",
                                    "El precio de venta se estableció en $0.00");
                           bad.show();
                        }

                        varios_codbar--;

                        ViewArticulo varios = new ViewArticulo(0, String.valueOf(varios_codbar), "VARIOS", "", "", 1, "", false);
                        varios.setIdArticulo(varios_codbar);
                        varios.setPrecioBase(precio);
                        
                        venta.addArticulo(varios, 1, 0);

                        granelFragment.setEnabled(true);
                    }

                    @Override
                    public void onCancelResult(Object... results) {
                        granelFragment.setEnabled(true);
                    }
                });
    }

    /**
     * Buscar un cliente por su código de cliente (no utilizado actualmente).
     * @param codigoBarras
     */
    public void findClienteByCodigoBarras(String codigoBarras) {
        final POSVenta venta = posMainFragment.getCurrentVenta();

        Cliente cliente = new Cliente(1, "653726873", "Herman P. B.", 1);//managerModel.clientes.getByCodigoClienteBarared(codigoBarras);

        venta.setCliente(cliente);
    }

    /**
     * showAllBanners el fragment de alta expres cuando se escanea un articulo cuyo
     * código de barras no se encuentra en la base de datos.
     * @param codigoBarras
     */
    private void altaExpress(final String codigoBarras) {

        if(isShowingFragment) { return; }

        isShowingFragment = true;

        BrioConfirmDialog bcd = new BrioConfirmDialog(mainActivity,
                Utils.getString(R.string.pos_article_notfound_title, mainActivity),
                Utils.getString(R.string.pos_article_notfound, mainActivity), null, new DialogListener() {
            @Override
            public void onAccept() {
                mainActivity.disableMenus();

                ArticleExpressDialog articleExpressDialog = new ArticleExpressDialog();
                articleExpressDialog.dialogListener = new DialogListener() {
                    @Override
                    public void onAccept() {
                        mainActivity.enableMenus();
                        isShowingFragment = false;
                        //reinicio la búsqueda
                        addArticuloByCodigoBarras(codigoBarras);
                    }

                    @Override
                    public void onCancel() {
                        mainActivity.enableMenus();
                        isShowingFragment = false;
                        granelFragment.setEnabled(true);
                    }
                };
                articleExpressDialog.setDialogCodigoPro(codigoBarras);
                articleExpressDialog.show1(mainActivity.getSupportFragmentManager());
            }

            @Override
            public void onCancel() {
                isShowingFragment = false;
            }
        });
        bcd.show();
    }

    /**
     * Muestra el fragment de edición de artículo cuando el precio de un artículo no ha sido establecido (precio=0).
     * @param codigoBarras
     * @param articleid
     */
    private void editarExpress(final String codigoBarras, final int articleid) {

        if(isShowingFragment) { return; }

        isShowingFragment = true;

        mainActivity.disableMenus();

        ArticleEditPrecioDialog articleEditPrecioDialog = new ArticleEditPrecioDialog();
        articleEditPrecioDialog.dialogListener = new DialogListener() {
            @Override
            public void onAccept() {
                mainActivity.enableMenus();
                isShowingFragment = false;

                //reinicio la búsqueda
                mHistorialArticulos.refresh();
                addArticuloByCodigoBarras(codigoBarras);
            }

            @Override
            public void onCancel() {
                mainActivity.enableMenus();

                isShowingFragment = false;
                granelFragment.setEnabled(true);
            }
        };

        articleEditPrecioDialog.setId_articulo(articleid);
        articleEditPrecioDialog.show1(mainActivity.getSupportFragmentManager());
    }

    /**
     * Obtener las existencias del artículo solicitado.
     * @param art - artículo solicitado.
     * @return
     */
    private double getExistenciasInventario(ViewArticulo art) {
        if(art != null) {
            return managerModel.inventario.getByIdArticulo(art.getIdArticulo()).getExistencias();
        } else {
            return 0d;
        }
    }

    /**
     * Clase que sirve como caché de artículos.
     * Almacena los detalles de los últimos artículos vendidos y permite disminuir el acceso a la base de datos.
     * La lógica es, al buscar un articlo se busca en esta cache, si no se encuentra, esta clase se encarga de
     * traer la información del artículo de la base de datos y almacenarlo en RAM, de tal forma que la próxima
     * vez que se solicite el mismo artículo, ya no sea necesario traerlo de la base de datos.
     */
    private class ArticulosIndexer {
        private final static int TYPE_CODBARRAS = 0;
        private final static int TYPE_SKU = 1;
        private final static int TYPE_COUNT = 2; // El número de formas de buscar

        private List<ViewArticulo> listArticulos;
        private List<HashMap<Object, Integer>> maps;

        private ModelManager managerModel;

        //todo: poner un límite de artículos indexados
        //todo: poner funcion purge
        public ArticulosIndexer(Context context) {

            managerModel = new ModelManager(context);

            listArticulos = new ArrayList<>();

            maps = new ArrayList<>();
            for(int i=0 ; i<TYPE_COUNT ; i++) {
                maps.add(new HashMap<Object, Integer>());
            }
        }

        /**
         * Obtener un artículo de la cache por su código de barras.
         * Si el artículo no está en la cache, se consulta su información a
         * la base de datos, se almacena en la cache y se regresa el artículo.
         * @param codigoBarras
         * @return
         */
        public ViewArticulo getByCodigoBarras(String codigoBarras) {
            return getArticulo(TYPE_CODBARRAS, codigoBarras);
        }

        /**
         * Obtener un artículo de la cache por su sku.
         * Si el artículo no está en la cache, se consulta su información a
         * la base de datos, se almacena en la cache y se regresa el artículo.
         * (no usado)
         * @param sku
         * @return
         */
        public ViewArticulo getBySku(int sku) {
            return getArticulo(TYPE_SKU, sku);
        }

        public int getCount() {
            return listArticulos.size();
        }

        /**
         * Obtener el artículo solicitado de la cache o de la base de datos.
         *
         * @param type
         * @param key
         * @return
         */
        private ViewArticulo getArticulo(int type, Object key) {
            ViewArticulo va = null;

            if (maps.get(type).containsKey(key)) {
                //Busco si ya tengo el artículo.
                va = listArticulos.get(maps.get(type).get(key));
                DebugLog.log(getClass(), "POS INDEXER", "Encontrado ' + key + '");
            } else {
                //Si no lo tengo, lo obtengo de la BD y lo guardo.
                String debugmsg = "getArticulo(): ";
                switch (type) {
                    case TYPE_CODBARRAS:
                        debugmsg += "by CodBarras '" + key + "'; ";
                        va = managerModel.viewArticulos.getByCodigoBarras((String)key);

                        DebugLog.log(getClass(), "ALTA", "entro al switch '" + key + "' es null? " + (va == null));
                        break;

                    case TYPE_SKU:
                        debugmsg += " by Sku '" + key + "'; ";
                        //va = managerModel.viewArticulos.getBySku((int)key);
                        break;
                }
                if(va != null) {
                    listArticulos.add(va);

                    HashMap<Object, Integer> map;
                    for(int t=0 ; t<TYPE_COUNT ; t++) {
                        map = maps.get(t);
                        map.put(key, listArticulos.size()-1);
                    }
                    debugmsg += "articulo indexado, total indexados: " + getCount();
                } else {
                    debugmsg  += "No se encontró articulo buscado";
                }

                DebugLog.log(getClass(), "POS INDEXER", debugmsg);
            }

            return va;
        }


        /**
         * Metodo a utilizar cuando se requiere refrescar la información de la
         * cache de artículos (e.g. cuando se cambia el precio de un artículo en
         * el inventario). Esto permite que el cambio del artículo tambien se refleje
         * en las ventas actuales.
         * @return
         */
        public boolean refresh() {

            DebugLog.log(getClass(), "POSManager", "refrescando el indexer");

            boolean needrefresh = false;

            for(int i=0 ; i<listArticulos.size() ; i++) {
                ViewArticulo art = listArticulos.get(i);
                ViewArticulo tmp = managerModel.viewArticulos.getByIdArticulo(art.getIdArticulo());
                //todo revisar esta comparacion
                if(!art.equals(tmp)) {
                    listArticulos.set(i, tmp);

                    needrefresh = true;
                }
            }

            return needrefresh;
        }
    }
}
