package net.hova_it.barared.brio.apis.drawer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import net.hova_it.barared.brio.BrioManagerInterface;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.RegistroApertura;
import net.hova_it.barared.brio.apis.models.entities.RegistroCierre;
import net.hova_it.barared.brio.apis.session.SessionManager;

/**Se implementa la validacion de apertura de caja
 * Created by HOVAIT on 26/01/2016.
 */
public class CajaAperturaManager implements BrioManagerInterface {

    private final static String KEY_LOG = CajaAperturaManager.class.getSimpleName();
    private static CajaAperturaManager daManager;
    private SessionManager sessionManager;
    private ModelManager modelManager;

    private RegistroApertura lastApertura;
    private RegistroCierre lastCierre;

    private CajaAperturaFragment cajaAperturaFragment;

    FragmentManager fragmentManager;

    public static CajaAperturaManager getInstance(Context context) {
        if(daManager == null) {
            daManager = new CajaAperturaManager(context);
        }
        return daManager;
    }

    private CajaAperturaManager(Context context) {
        modelManager =  new ModelManager(context);
        sessionManager = SessionManager.getInstance(context);
    }

    /**Verifica si la caja esta abierta, si es mayor a la fecha de cierre regresa true y guarda el id de la caja
     * @return un boolean
     */
    public boolean isOpen(){

        boolean isOpen = false;

        Log.w("isOpen","entre"+isOpen);
        lastCierre = modelManager.registrosCierre.getLast();
        lastApertura = modelManager.registrosApertura.getLast();

        if (lastApertura != null){

            if (lastApertura.getFechaApertura() > lastCierre.getFechaCierre()){
                // la caja está abierta
                sessionManager.saveInt("idCaja",Integer.valueOf(modelManager.settings.getByNombre("ID_CAJA").getValor()));
                isOpen =true;
                Log.w("isOpen", "caja abierta" + isOpen);
            } else {
                isOpen = false;
                Log.w("isOpen","caja cerrada"+isOpen);
            }
        }else{
            isOpen = false;
            Log.w("isOpen","no existe apertura de caja"+isOpen);
        }
        return isOpen;
    }

    /**Metodo que crea el fragmento y lo muestra
     */
    @Override
    public Fragment createFragments(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;

        cajaAperturaFragment = (CajaAperturaFragment) this.fragmentManager.findFragmentByTag("cajaAperturaFragment");
        if(cajaAperturaFragment == null) {
            cajaAperturaFragment = new CajaAperturaFragment();
        }

        this.fragmentManager
            .beginTransaction()
                .add(containerId, cajaAperturaFragment, "cajaAperturaFragment")
            .commit();

        return null; //todo: cambiar esto para regresar el fragment a mostrar
    }

    @Override
    public void removeFragments() {
        //ya se hace en el fragment
    }
}
