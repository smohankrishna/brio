package net.hova_it.barared.brio.apis.pos.granel;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.views.ViewArticulo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import lat.brio.core.bussines.BrInventario;

/**
 * Loader que permite cargar de forma asíncrona la información de todos los productos
 * a granel de la base de datos.
 *
 * http://developer.android.com/intl/es/reference/android/content/AsyncTaskLoader.html
 *
 * Created by Herman Peralta on 21/12/2015.
 */
public class GranelListLoader extends AsyncTaskLoader<List<GranelItem>> {
    
    private List<GranelItem> mList = null;
    
    private BrInventario brInventario;
    
    public GranelListLoader (BrInventario brInventario, Context context) {
        super (context);
        this.brInventario = brInventario;
    }
    
    /**
     * En esta función se realiza la carga de los articulos.
     * Se ejecuta en un hilo asíncrono al fondo.
     * @return
     */
    @Override
    public List<GranelItem> loadInBackground () {
        
        //return pruebaLlenado();
        
        List<GranelItem> granel = new ArrayList<GranelItem> ();
        List<ViewArticulo> list = brInventario.getListArticulos (true);
        
        
        for (ViewArticulo va : list) {
            granel.add (new GranelItem (va));
        }
        
        return granel;
    }
    
    /**
     * Este callback se invoca cuando loadInBackground() termina de cargar
     * los datos.
     * @param articulos - Los datos cargados por loadInBackground().
     */
    @Override
    public void deliverResult (List<GranelItem> articulos) {
        if (isReset ()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (articulos != null) {
                onReleaseResources (articulos);
            }
        }
        
        List<GranelItem> oldArts = mList;
        mList = articulos;
        
        if (isStarted ()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult (articulos);
        }
        
        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldArts != null) {
            onReleaseResources (oldArts);
        }
    }
    
    /**
     * Callback que se invoca cuando se solicita iniciar el Loader
     */
    @Override
    protected void onStartLoading () {
        if (mList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult (mList);
        }
        
        
        // todo, verificar si cambian los datos aqui para forzar a volver a cargar
        if (takeContentChanged () || mList == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad ();
        }
    }
    
    /**
     * Callback que se invoca cuando se solicita detener el Loader
     */
    @Override
    protected void onStopLoading () {
        // Attempt to cancel the current load task if possible.
        cancelLoad ();
    }
    
    /**
     * Callback que se invoca cuando se solicita cancelar el Loader
     * @param apps - los datos del loader a cancelar
     */
    @Override
    public void onCanceled (List<GranelItem> apps) {
        super.onCanceled (apps);
        
        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources (apps);
    }
    
    /**
     * Callback que se invoca cuando se solicita resetear el Loader
     */
    @Override
    protected void onReset () {
        super.onReset ();
        
        // Ensure the loader is stopped
        onStopLoading ();
        
        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mList != null) {
            onReleaseResources (mList);
            mList = null;
        }
    }
    
    /**
     * Función usada para liberar memoria usada por los datos del loader
     * @param items
     */
    protected void onReleaseResources (List<GranelItem> items) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
    
    
}
