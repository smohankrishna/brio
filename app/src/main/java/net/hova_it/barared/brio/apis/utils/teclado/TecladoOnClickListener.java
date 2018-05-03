package net.hova_it.barared.brio.apis.utils.teclado;

/**
 * Utilizada para la comunicacion entre el teclado y el fragment.
 * Created by Mauricio Cerón on 15/02/2016.
 */
public abstract class TecladoOnClickListener {
    /**
     * Al presionar la tecla "OK" se confirma el ingreso de informacion por medio de cualquier teclado.
     * Se regresa al fragment el texto ingresado para su uso.
     * @param results
     */
    public abstract void onAcceptResult(Object... results);

    /**
     * Al cancelar el ingreso de informacion con cualquier teclado se le indica al fragment que puede
     * continuar.
     * @param results
     */
    public abstract void onCancelResult(Object... results);
}