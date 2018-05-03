package net.hova_it.barared.brio.apis.pos.search;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.views.ViewArticulo;

import java.util.ArrayList;
import java.util.List;

import lat.brio.core.bussines.BrInventario;

/**
 * Loader asíncrono de todos los artículos de la base de datos a memoria, utilizado
 * en el fragment de búsqueda de artículos.
 *
 * Created by Herman Peralta on 09/02/2016.
 */
public class SearchListLoader extends AsyncTaskLoader<List<String[]>> {
    private final static String KEY_LOG = SearchListLoader.class.getSimpleName();

    //Tipos de loaders
    public final static int LOADER_TYPE_ALL = 0;

    private List<String[]> mList;

    private int LOADER_TYPE;

    private ModelManager managerModel;
    private List<ViewArticulo> articulos;

    private BrInventario brInventario;
    public SearchListLoader(BrInventario brInventario, Context context) {
        super(context);
        this.brInventario = brInventario;

        this.LOADER_TYPE = LOADER_TYPE;
        managerModel = new ModelManager(context);
    }

    @Override
    public List<String[]> loadInBackground() {
        List<String[]> resultado = new ArrayList<>();
        articulos = brInventario.getListArticulos ( false);

        for(ViewArticulo art : articulos) {
            String[] row = new String[3];

            row[0] = art.getDescripcionItem();
            row[1] = String.format("%.2f", art.getPrecioBase());
            row[2] = art.getCodigoBarras();

            resultado.add(row);
        }

        return resultado;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<String[]> rows) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (rows != null) {
                onReleaseResources(rows);
            }
        }

        List<String[]> oldArts = mList;
        mList = rows;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(rows);
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
    public void onCanceled(List<String[]> rows) {
        super.onCanceled(rows);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(rows);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
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
    protected void onReleaseResources(List<String[]> rows) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }

}
