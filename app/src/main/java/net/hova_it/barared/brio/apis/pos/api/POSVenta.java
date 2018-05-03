package net.hova_it.barared.brio.apis.pos.api;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.apis.models.entities.Cliente;
import net.hova_it.barared.brio.apis.models.views.ViewArticulo;
import net.hova_it.barared.brio.apis.pos.POSManager;
import net.hova_it.barared.brio.apis.utils.DebugLog;

/**
 * Controlador de una Venta.
 *
 * Created by Herman Peralta on 04/04/2016.
 */
public class POSVenta {

    public final static int STATUS_TICKET = 0;
    public final static int STATUS_CLIENTE = 1;

    public POSTicketFragment fgmtTicket;
    public POSClienteFragment fgmtCliente;

    private Context context;

    private int position;
    private int status = STATUS_TICKET;

    private Cliente cliente;

    private POSManager managerPOS;
    private POSSuperTicketController posSuperTicketController;

    public POSVenta(BrioActivityMain context, POSMainFragment.PageSwapListener pageSwapListener, int position, final FragmentManager fragmentManager) {
        DebugLog.log(getClass(), "POS", "inicia POSVenta " + position);
        this.context = context;
        this.position = position;
        this.managerPOS = POSManager.getInstance(context);

        posSuperTicketController = new POSSuperTicketController(context);

        Fragment recovered = fragmentManager.findFragmentByTag(POSMainFragment.getPageTag(position));

        if(recovered == null) {
            fgmtTicket = POSTicketFragment.newInstance(position);
            fgmtCliente = POSClienteFragment.newInstance(position);
        } else {
            if(recovered instanceof POSTicketFragment) {
                //recuperé el ticket, hay que instanciar el cliente
                fgmtTicket = (POSTicketFragment) recovered;
                fgmtCliente = POSClienteFragment.newInstance(position);
            } else
            if(recovered instanceof POSClienteFragment) {
                //recuperé el cliente, hay que instanciar el ticket
                fgmtCliente = (POSClienteFragment) recovered;
                fgmtTicket = POSTicketFragment.newInstance(position);
            }
        }

        fgmtTicket.setPageSwapListener(pageSwapListener);
        fgmtCliente.setPageSwapListener(pageSwapListener);
    }

    /**
     * Regresa el fragment de acuerdo a si se debe mostrar el ticket o realizar la busqueda de
     * algun cliente.
     * @return
     */
    public POSFragment getFragment() {
        DebugLog.log(getClass(), "POS", "page " + position + ", STATUS: " + status);

        switch (status) {
            case STATUS_TICKET:
                fgmtTicket.setPOSSuperTicketController(posSuperTicketController);
                return fgmtTicket;

            case STATUS_CLIENTE:
                return fgmtCliente;

            default:
                return null;
        }
    }

    /**
     * Agregar articulo al ticket.
     * @param articulo
     * @param cantidad
     * @param existencias
     */
    public void addArticulo(ViewArticulo articulo, double cantidad, double existencias) {
        if(status == STATUS_TICKET) {
            posSuperTicketController.addItem(articulo, cantidad, existencias);
        }
    }

    /**
     * Agregar cliente (opcional para el usuario).
     * @param cliente
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Obtener status de la venta.
     * @param STATUS
     */
    public void setStatus(int STATUS) {
        DebugLog.log(getClass(), "POS", "page " + position + ", STATUS: " + STATUS);

        this.status = STATUS;
    }

    /**
     * Modificar el status de la venta.
     * @return
     */
    public int getStatus() {
        return status;
    }

    public void recoverFragments(FragmentManager fragmentManager) {
        //POSFragment fgmt = (POSFragment) fragmentManager.findFragmentByTag(POSMainFragment.getPageTag(position));

        //DebugLog.log(getClass(), "POS", "[" + position + "] status: " + status +  ",  fragment null? " + (fgmt == null));

        /*
        switch (status) {
            case STATUS_TICKET:
                if(fgmt != null) {
                    fgmtTicket = (POSTicketFragment) fgmt;
                } else {
                    fgmtTicket = POSTicketFragment.newInstance(position);
                }
                fgmtCliente = POSClienteFragment.newInstance(position);
                break;

            case STATUS_CLIENTE:
                if(fgmt != null) {
                    fgmtCliente = (POSClienteFragment) fgmt;
                } else {
                    fgmtCliente = POSClienteFragment.newInstance(position);
                }
                fgmtTicket = POSTicketFragment.newInstance(position);
                break;
        }
        */

        if(fgmtTicket == null) {
            fgmtTicket = POSTicketFragment.newInstance(position);
        }

        if(fgmtCliente == null) {
            fgmtCliente = POSClienteFragment.newInstance(position);
        }

        //Todo: set cliente en el fragment cliente
        fgmtTicket.setPOSSuperTicketController(posSuperTicketController);
    }

    public void setPageSwapListener (POSMainFragment.PageSwapListener pageSwapListener) {
        fgmtCliente.setPageSwapListener(pageSwapListener);
        fgmtTicket.setPageSwapListener(pageSwapListener);
    }
}
