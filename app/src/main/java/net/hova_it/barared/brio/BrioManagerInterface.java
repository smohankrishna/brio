package net.hova_it.barared.brio;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Interfaz que implementan los managers
 * Created by Herman Peralta on 12/01/2016.
 */
public interface BrioManagerInterface {
    /**
     * Implementar aqui la muestra de los fragments de cada manager (transaction sobre containerId)
     *
     * @param fragmentManager
     * @param containerId Id de la vista
     */
    Fragment createFragments (FragmentManager fragmentManager, int containerId);

    /**
     * Implementar aquí la forma de remover los fragments de cada manager
     */
    void removeFragments ();
}
