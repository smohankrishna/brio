package lat.brio.core;

import android.app.Application;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.apis.models.ModelManager;

import java.util.ArrayList;

import lat.brio.api.alerta.OAlerta;

/**
 * Created by guillermo.ortiz on 12/02/18.
 */

public class AppController extends Application {
    
    private static BrioActivityMain currentActivity;
    private static AppController mInstance = null;
    private ModelManager modelManager = null;
    private static ArrayList<OAlerta> _Alertas = null;
    private int currentAlert = 0;
    private long lastInteraction;
    
    /**
     * Se utiliza el AppControler, para tener las constantes omnipresentes en la aplicación
     * es decir, se inicializan y se pueden utilizar en cualquiier momento
     */
    @Override
    public void onCreate () {
        super.onCreate ();
        mInstance = this;
        ManagerInitializer.i.init (getApplicationContext ());
        //modelManager = new ModelManager (getApplicationContext ());
    }
    
    @Override
    public void onTerminate () {
        super.onTerminate ();
    }
    
    public static AppController getInstance () {
        return AppController.mInstance;
    }
    
    public static void setCurrentActivity (BrioActivityMain activity) {
        currentActivity = activity;
    }
    
    public static BrioActivityMain getCurrentActivity () {
        return currentActivity;
    }
    
    public ModelManager getModelManager () {
        
        if (modelManager == null)
            modelManager = new ModelManager (getApplicationContext ());
        return modelManager;
    }
    
    public ArrayList<OAlerta> getAlertas () {
        return _Alertas;
    }
    
    public static void setAlertas (ArrayList<OAlerta> alertas) {
        _Alertas = alertas;
    }
    
    public OAlerta getAlert () {
        OAlerta alerta = null;
        
        if (getAlertas () != null && getAlertas ().size () > 0) {
            if (currentAlert > getAlertas ().size () - 1)
                currentAlert = 0;//Reiniciamos el index
            alerta = getAlertas ().get (currentAlert);
            currentAlert++;
        }
        return alerta;
    }
    
    public long getLastInteraction () {
        return lastInteraction;
    }
    
    public void setLastInteraction (long lastInteraction) {
        this.lastInteraction = lastInteraction;
    }
}
