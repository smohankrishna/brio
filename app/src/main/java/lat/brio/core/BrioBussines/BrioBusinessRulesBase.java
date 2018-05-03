package lat.brio.core.BrioBussines;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.sqlite.SQLiteDatabase;

import net.hova_it.barared.brio.apis.models.daos.TMAEVersionDB;

import lat.brio.core.BrioGlobales;
import lat.brio.core.DataBussines.DataBaseBusiness;

/**
 * Created by guillermo.ortiz on 08/03/18.
 */

public abstract class BrioBusinessRulesBase {
    
    private Context contexto;
    private ApplicationInfo app;
    private static TMAEVersionDB tmaeVersion;
    private boolean inicializado = false;
    
    public BrioBusinessRulesBase (Context contexto, SQLiteDatabase mAccess) {
    
        this.contexto = contexto;
        this.app = contexto.getApplicationInfo();
        if (mAccess == null)
        {
            inicializar();
            mAccess = BrioGlobales.getAccess();
            setInicializado(true);
        }
        tmaeVersion = new TMAEVersionDB(contexto, mAccess);
    }
    
    
    /**
     * M�todo encargado de incializar la base de datos las tablas de la base de datos en la que se almacenan los paquetes de importaci�n y
     * exportaci�n. Tambi�n si inicializan todas la variables de configuraci�n que se van a utilizar durante el ciclo de vida de la aplicacion.
     */
    public abstract void inicializar();
    
    /**
     * M�todo encargado de llamar a los distintos m�todos que realicen actualizaciones en el la aplicaci�n.
     */
    public abstract void actualizaciones();
    
    /**
     * M�todo get parra recuperar el valor de ApplicationInfo
     */
    public ApplicationInfo getApp()
    {
        return app;
    }
    
    /**
     * M�todo get parra recuperar el valor del contexto.
     */
    public Context getContexto()
    {
        return contexto;
    }
    
    public boolean getInicializado() {
        return inicializado;
    }
    
    public void setInicializado(boolean inicializado) {
        this.inicializado = inicializado;
    }
    
}
