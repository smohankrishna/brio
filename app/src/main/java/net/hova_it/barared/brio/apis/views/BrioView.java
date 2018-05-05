package net.hova_it.barared.brio.apis.views;

import android.widget.TextView;

import net.hova_it.barared.brio.apis.utils.teclado.TecladoOnClickListener;

/**
 * Interfaz que deben implementar todos los
 * Views personalizados que utilizan el teclado personalizado
 * de la aplicación
 */
public interface BrioView {
    /**
     * Obtener el TextView del View
     * @return
     */
    TextView getTextView ();

    /**
     * Obtener el listener del teclado, de tal forma que
     * el teclado personalizado se pueda comunicar con este view.
     *
     * @return
     */
    TecladoOnClickListener getTecladoOnClickListener ();

    /**
     * Obtener el id del laayout ocultable al mostrar el teclado personalizado de la aplicación.
     * @return
     */
    int getIdLayoutOcultable ();
}
