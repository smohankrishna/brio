package lat.brio.core;

import android.content.Context;

import net.hova_it.barared.brio.apis.interfaces.Initializer;
import net.hova_it.barared.brio.apis.utils.preferencias.Preferencias;

/**
 * Created by guillermo.ortiz on 12/02/18.
 */

public enum ManagerInitializer implements Initializer {
    
    i;
    
    /**
     * INICIALIZA las Preferencias DE LA APLICACION
     *
     * @param context
     */
    @Override
    public void init(Context context) {
        
        Preferencias.i.init(context);
        
    }
    
    /**
     * Limpia las preferencias de la aplicación
     */
    @Override
    public void clear() {
        
        Preferencias.i.clear();
    }
}
