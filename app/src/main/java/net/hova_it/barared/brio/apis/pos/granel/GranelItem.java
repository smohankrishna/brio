package net.hova_it.barared.brio.apis.pos.granel;

import net.hova_it.barared.brio.apis.models.views.ViewArticulo;

/**
 * Cada uno de los items de articulos granel mostrados en el grid de granel.
 *
 * Created by Herman Peralta on 17/12/2015.
 */
public class GranelItem {

    private String name;
    private ViewArticulo viewArticulo;

    public GranelItem(ViewArticulo va) {
        this.name = va.getNombreArticulo();
        this.viewArticulo = va;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ViewArticulo getViewArticulo() {
        return viewArticulo;
    }

    public void setViewArticulo(ViewArticulo viewArticulo) {
        this.viewArticulo = viewArticulo;
    }
}
