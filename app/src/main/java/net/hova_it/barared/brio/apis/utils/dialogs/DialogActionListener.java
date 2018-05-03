package net.hova_it.barared.brio.apis.utils.dialogs;

/**
 * Establece comunicacion entre el fragment dialog y la clase que lo desplego.
 * Created by Mauricio Cerón on 15/02/2016.
 */

public abstract class DialogActionListener {
    /**
     * Al pulsar aceptar en el fragment dialog se le comunica el estatus a la clase que lo desplego.
     * @param results
     */
    public abstract void onAcceptResult(Object results);

    /**
     * Al pulsar cancelar en el fragment dialog se le comunica el estatus a la clase que lo desplego.
     * @param results
     */
    public abstract void onCancelResult(Object results);
}
