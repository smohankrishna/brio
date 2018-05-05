package net.hova_it.barared.brio.apis.pos.api;

import net.hova_it.barared.brio.apis.models.entities.ItemsTicket;
import net.hova_it.barared.brio.apis.models.views.ViewArticulo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Lógica de cada item del ticket (e.g. cada artículo o grupo de
 * artículos en una venta o ticket). Lleva la lógica de multiplicar
 * el precio unitario por el número de artículos (o unidades de granel)
 * del artículo y obtener un subtotal.
 *
 * Created by Herman Peralta on 27/11/2015.
 */
public class ItemsTicketController extends ItemsTicket {
    private static final String KEY_LOG = ItemsTicketController.class.getSimpleName();
    private boolean granel;
    private boolean mPrecioManual = false;
    private double mImporteUnitarioManual;
    //private double impuestos;

    //private List<ImpuestosDeArticulo> mListaImpuestos;
    //private List<TarifaCantidad> mListaPrecios;

    private double existenciasInventario;
    private double existenciasActual;

    private String unidad;

    public ItemsTicketController(int idTicket, ViewArticulo art, double cantidad, double existenciasInventario) {
        super();

        unidad = art.getUnidad();
        granel = art.getGranel();

        setIdTicket(idTicket);
        setIdArticulo(art.getIdArticulo());
        setCodigoBarras(art.getCodigoBarras());
        setDescripcion(art.getDescripcionItem());

        setImporteUnitario(art.getPrecioBase());
        setPrecioCompra(art.getPrecioCompra());


        //this.mListaImpuestos = art.getListImpuestos();
        //this.mListaPrecios = art.getListPrecios();
        this.existenciasInventario = existenciasInventario;
        //this.impuestos = 0d;

        setCantidad(cantidad); // llamar al final del constructor siempre
    }

    public ItemsTicketController(ItemsTicket itemsTicket) {
        setIdItemTicket(itemsTicket.getIdItemTicket());
        setIdTicket(itemsTicket.getIdTicket());
        setIdArticulo(itemsTicket.getIdArticulo());
        setCodigoBarras(itemsTicket.getCodigoBarras());
        setDescripcion(itemsTicket.getDescripcion());
        setCantidad(itemsTicket.getCantidad());
        setImporteUnitario(itemsTicket.getImporteUnitario());
        setImporteTotal(itemsTicket.getImporteTotal());
        setTimestamp(itemsTicket.getTimestamp());
    }

    @Override
    public void setCantidad(double cant) {
        super.setCantidad(cant);

        existenciasActual = existenciasInventario - cant;

        updateImporteTotal();
    }

    /**
     * Regresa el importe bruto (sin impuestos)
     * PAra el importe con impuestos (neto) usar getImporteTotal()
     * @return
     *
    public double getImpuestos() {
        return impuestos;
    }*/

    public void setPrecioManual(double precio) {
        mPrecioManual = true;
        mImporteUnitarioManual = precio;

        updateImporteTotal();
    }

    public void removePrecioManual() {
        mPrecioManual = false;

        updateImporteTotal();
    }

    public double getExistenciasActual() {
        return existenciasActual;
    }

    public boolean hasPrecioManual() {
        return mPrecioManual;
    }

    private void updateImporteTotal() {
        /*
        if(mPrecioManual) {
            //todo: aqui verificar si el precio fue manual, pero si el de mayoreo es menor, se toma el manual o de mayoreo?
            setImporteUnitario(mImporteUnitarioManual);
        } else {
            setImporteUnitario();
        }
        */
        //todo: checar si es cliente barared y setear ImporteUnitario
        double importetotal = getCantidad() * getImporteUnitario();
        BigDecimal bd = new BigDecimal(importetotal);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        setImporteTotal(bd.doubleValue());

        //todo calcular el acumulado de todos los impuestos
        //impuestos = getImporteTotal();
        /*
        if(mListaImpuestos != null) {
            for (ImpuestosDeArticulo ida : mListaImpuestos) {
                impuestos -= (getImporteTotal() / (1.0d + (ida.getImpuesto() / 100.d)));
            }
        }
        */
    }

    public boolean getGranel() {
        return granel;
    }

    public String getUnidad() {
        return unidad;
    }

    @Override
    public String toString() {
        return "{mIdArticulo:" + getIdArticulo() + ", mDescArticulo:" + getDescripcion() + ", mCantidad:" + getCantidad() +"}";
    }

    public double getExistenciasInventario() {
        return existenciasInventario;
    }
}

