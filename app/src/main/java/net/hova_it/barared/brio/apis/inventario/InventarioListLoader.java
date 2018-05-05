package net.hova_it.barared.brio.apis.inventario;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import net.hova_it.barared.brio.apis.models.daos.ViewInventarioDAO;
import net.hova_it.barared.brio.apis.models.views.ViewInventario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lat.brio.core.bussines.BrInventario;

/**Ordenamientos de listas que se obtienen de la base de datos
 * Created by Mauricio Cerón on 08/02/2016.
 */
public class InventarioListLoader extends AsyncTaskLoader<List<ViewInventario>> {

    private List<ViewInventario> mList = null;

    private ViewInventarioDAO VI;
    private BrInventario brInventario;
    //Tipo de ordenamientos
    private int ORDER_TYPE = 0;
    public static final int ORDER_BY_ARTICULO = 0;
    public static final int ORDER_BY_MARCA = 1;
    public static final int ORDER_BY_PRESENTACION = 2;
    public static final int ORDER_BY_CODIGO_BARRAS = 3;
    public static final int ORDER_BY_PRECIO_COMPRA = 4;
    public static final int ORDER_BY_PRECIO_VENTA = 5;
    public static final int ORDER_BY_EXISTENCIA = 6;
    public static final int NO_ORDER = 7;


    public InventarioListLoader( Context context, ViewInventarioDAO vi, int orden) {
        super(context);
        ORDER_TYPE = orden;
        VI = vi;
    }
    public InventarioListLoader( Context context, BrInventario brInventario, int orden) {
        super(context);
        this.brInventario = brInventario;
        ORDER_TYPE = orden;
        
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<ViewInventario> loadInBackground() {
       //todo: pruebas de estres
        int lim = 300;

        List<ViewInventario> mEntries = new ArrayList<>(lim);
        mEntries = brInventario.getListInventario (null, null);

        if(mEntries != null) {


            /*for (int i = 0; i < mEntries.size(); i++) {
                Log.w("Resultado del getAll", mEntries.get(i).getIdArticulo() + " " + mEntries.get(i).getNombreArticulo());
            }*/



       /* for(int b=0; b<=300; b++){
            mEntries.add(mEntries.get(0));
            mEntries.add(mEntries.get(1));
            mEntries.add(mEntries.get(2));
        }*/

     /*       switch (ORDER_TYPE) {


                case ORDER_BY_ARTICULO:
                    Collections.emptyList();
                    Collections.sort(mEntries, ViewInventario.ComparatorNombreArticulo);

                    break;

                case ORDER_BY_MARCA:

                    Collections.emptyList();
                    Collections.sort(mEntries, ViewInventario.ComparatorNombreMarca);
                    break;

                case ORDER_BY_PRESENTACION:
                    Collections.sort(mEntries, ViewInventario.ComparatorPresentacion);
                    break;

                case ORDER_BY_CODIGO_BARRAS:
                    Collections.sort(mEntries, ViewInventario.ComparatorCodigoBarras);
                    break;

                case ORDER_BY_PRECIO_COMPRA:
                    Collections.sort(mEntries,ViewInventario.ComparatorPrecioCompra);
                    break;

                case ORDER_BY_PRECIO_VENTA:
                    Collections.sort(mEntries,ViewInventario.ComparatorPrecioVenta);
                    break;

                case ORDER_BY_EXISTENCIA:
                    Collections.sort(mEntries,ViewInventario.ComparatorExistencias);
                    break;
            }*/
        }
        return mEntries;



    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<ViewInventario> articulos) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (articulos != null) {
                onReleaseResources(articulos);
            }
        }

        List<ViewInventario> oldArts = mList;
        mList = articulos;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(articulos);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldArts != null) {
            onReleaseResources(oldArts);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mList);
        }


        // todo, verificar si cambian los datos aqui para forzar a volver a cargar
        if (takeContentChanged() || mList == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<ViewInventario> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mList != null) {
            onReleaseResources(mList);
            mList = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<ViewInventario> items) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }

 



}
