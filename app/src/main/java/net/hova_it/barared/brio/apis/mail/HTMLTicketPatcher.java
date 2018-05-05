package net.hova_it.barared.brio.apis.mail;

import android.content.Context;
import android.util.Log;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.CardPaymentRequest;
import net.hova_it.barared.brio.apis.models.entities.Comercio;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.payment.PaymentService;
import net.hova_it.barared.brio.apis.pos.api.ItemsTicketController;
import net.hova_it.barared.brio.apis.utils.Utils;

/**
 * Esta clase permite renderear un ticket en HTML.
 *
 * Created by Herman Peralta on 13/04/2016.
 */
public class HTMLTicketPatcher {

    /**
     * Regresa un String con el ticket cuyo id es ticketId en HTML.
     *
     * @param ticketId
     * @param context
     * @return
     */
    public static String superTicketToHTML(int ticketId, Context context) {
        SuperTicket superTicket = Utils.getSuperTicketFromDB(context, ticketId);
        if(superTicket == null){
            return null;
        }
        return superTicketToHTML(superTicket, context);
    }


    /**
     * Regresa un String con el superticket en HTML.
     * @param superTicket
     * @param context
     * @return
     */
    public static String superTicketToHTML(SuperTicket superTicket, Context context) {
        ModelManager modelManager = new ModelManager(context);

        int idComercio = Integer.valueOf(modelManager.settings.getByNombre("ID_COMERCIO").getValor());
        Comercio comercio = modelManager.comercios.getByIdComercio(idComercio);

        String tablaItemsTicket = null;//llenaTicketPOS(superTicket, context);
        Log.d("DEBUG", "llega con id: " + superTicket.getTicket().getIdTipoTicket());
        if(superTicket.getTicket().getIdTipoTicket() != PaymentService.TIPO_TICKET_WESTERN_PAGO) {
            tablaItemsTicket = llenaTicketPOS(superTicket, context);
        }

        int tituloTicket = R.string.payment_tipo_ticket_pos;

        //todo: hacer un switch para obtener el ticket de acuerdo al tipo de venta
        switch (superTicket.getTicket().getIdTipoTicket()) {
                //superTicket.getTicket().getIdTipoTicket()   //////////
            case PaymentService.TIPO_TICKET_POS:
                tituloTicket = R.string.payment_tipo_ticket_pos;
                //tablaItemsTicket = llenaTicketPOS(superTicket, context);
                break;

            case PaymentService.TIPO_TICKET_SERVICIO:
                tituloTicket = R.string.payment_tipo_ticket_servicio;
                break;

            case PaymentService.TIPO_TICKET_TAE:
                tituloTicket = R.string.payment_tipo_ticket_tae;
                break;

            case PaymentService.TIPO_TICKET_INTERNET:
                tituloTicket = R.string.payment_tipo_ticket_internet;
                break;

            case PaymentService.TIPO_TICKET_BANCO_ENTRADA:
                tituloTicket = R.string.payment_tipo_ticket_banco_entrada;
                break;

            case PaymentService.TIPO_TICKET_BANCO_SALIDA:
                tituloTicket = R.string.payment_tipo_ticket_banco_salida;
                break;

            case PaymentService.TIPO_PAGO_SEGURO:
                tituloTicket = R.string.payment_tipo_ticket_seguro;
                break;

            case PaymentService.TIPO_TICKET_WESTERN_PAGO:
                tituloTicket = R.string.payment_tipo_ticket_western_pago;
                tablaItemsTicket = llenaTicketWestern(superTicket, context);
                break;
        }

        String tablaTotalesTicketItems = "";

        boolean totalEfectivoProcesado = false;

        double totalPagosEfectivo = 0.0;

        for(TicketTipoPago tipoPago : superTicket.ticketTipoPago) {
            if (tipoPago.getIdTipoPago() == PaymentService.TIPO_PAGO_EFECTIVO)
                totalPagosEfectivo += tipoPago.getMonto(); // aqui se esta calculando el total de los pagos que se hicieron unicamente en efectivo
        }

        for(TicketTipoPago tipoPago : superTicket.ticketTipoPago) {
            String pagoNombre = "";

            if(tipoPago.getMonto()>0) {
                switch (tipoPago.getIdTipoPago()) {
                    case PaymentService.TIPO_PAGO_EFECTIVO:
                        pagoNombre = "Efectivo";
                        break;

                    case PaymentService.TIPO_PAGO_TARJETA:
                        CardPaymentRequest cardPaymentRequest = (CardPaymentRequest)Utils.fromJSON(tipoPago.getDescripcion(), CardPaymentRequest.class);
                        pagoNombre = "Tarjeta";
                        if(cardPaymentRequest != null) {
                            Log.d("TARJ.", Utils.pojoToString(cardPaymentRequest));
                            pagoNombre += " " + cardPaymentRequest.getCardtype();
                            String cardn = cardPaymentRequest.getCreditcard();
                            if(cardn!=null && cardn.length()>=4) {
                                cardn = cardn.substring(cardn.length() - 4, cardn.length());
                                pagoNombre += " " + cardn;
                            }
                            pagoNombre += "<br/>AUT. " + cardPaymentRequest.getAuthorization();
                        }
                        break;

                    case PaymentService.TIPO_PAGO_FIADO:
                        pagoNombre = "Fiado";
                        break;

                    case PaymentService.TIPO_PAGO_VALES:
                        pagoNombre = "Vales";
                        break;
                }
            }
            if ((totalEfectivoProcesado == false && tipoPago.getIdTipoPago()==PaymentService.TIPO_PAGO_EFECTIVO  ) || tipoPago.getIdTipoPago()==PaymentService.TIPO_PAGO_TARJETA) {

                if(tipoPago.getIdTipoPago()==PaymentService.TIPO_PAGO_EFECTIVO){
                    totalEfectivoProcesado = true;
                }

                double monto = tipoPago.getIdTipoPago() == PaymentService.TIPO_PAGO_EFECTIVO ? totalPagosEfectivo : tipoPago.getMonto();

                tablaTotalesTicketItems += tablaTotalesTicketItem
                        .replace("?pagoNombre", pagoNombre)
                        .replace("?pagoMonto", String.format("$%.2f", monto));
            }

        }

        String ticket = "";


        switch (superTicket.getTicket().getIdTipoTicket()){
                //superTicket.getTicket().getIdTipoTicket()
            case PaymentService.TIPO_PAGO_SEGURO:

                ticket=  ticketTemplateSeguroPago
                        .replace("?tablaItemsTicket", tablaItemsTicket)
                        .replace("?tablaTotalesTicket", tablaTotalesTicket.replace("?tablaTotalesTicketItem", tablaTotalesTicketItems))
                        .replace("?tituloTicket", Utils.getString(tituloTicket, context))
                        .replace("?fechaTicket", Utils.getBrioDate(superTicket.getTicket().getTimestamp()))
                        .replace("?nombreComercio", comercio.getDescComercio())
                        .replace("?direccionComercio", comercio.getDireccionCompleta())
                        .replace("?idComercio", "" + idComercio)
                        .replace("?cambioTicket", String.format("$%.2f", superTicket.getTicket().getCambio()))
                        .replace("?totalTicket", String.format("$%.2f", superTicket.getTicket().getImporteNeto()))
                        .replace("?aviso", Utils.getString(R.string.payment_tipo_ticket_seguro_aviso, context))
                        .replace("?logo", "<img width=\"256\" height=\"100\" title=\"logo tienda\" alt=\"\" src=\"" + "file://" + Utils.getBrioInternalPath(context) + "ticket_logo.png" + "\">");

                break;

            case PaymentService.TIPO_TICKET_WESTERN_PAGO:

                ticket = ticketTemplateWesternPago
                        .replace("?tablaItemsTicket", tablaItemsTicket)
                        //.replace("?tablaTotalesTicket", tablaTotalesTicket.replace("?tablaTotalesTicketItem", tablaTotalesTicketItems))
                        //.replace("?tituloTicket", Utils.getString(tituloTicket, context))
                        //.replace("?fechaTicket", Utils.getBrioDate(superTicket.getTicket().getTimestamp()))
                        .replace("?nombreComercio", comercio.getDescComercio())
                        .replace("?direccionComercio", (context.getResources().getString(R.string.direccion_western_html)))//comercio.getDireccionCompleta())
                        .replace("?idComercio", "" + idComercio)
                        .replace("?cambioTicket", String.format("$%.2f", superTicket.getTicket().getCambio()))
                        .replace("?totalTicket", String.format("$%.2f", superTicket.getTicket().getImporteNeto()))
                        .replace("?aviso", Utils.getString(R.string.payment_tipo_ticket_western_aviso, context))
                        .replace("?declara", Utils.getString(R.string.payment_tipo_ticket_western_declara, context))
                        .replace("?logo", "<img width=\"256\" height=\"100\" title=\"logo tienda\" alt=\"\" src=\"" + "file://" + Utils.getBrioInternalPath(context) + "ticket_logo.png" + "\">");

                break;
            default:ticket= ticketTemplate
                    .replace("?tablaItemsTicket", tablaItemsTicket)
                    .replace("?tablaTotalesTicket", tablaTotalesTicket.replace("?tablaTotalesTicketItem", tablaTotalesTicketItems))
                    .replace("?tituloTicket", Utils.getString(tituloTicket, context))
                    .replace("?fechaTicket", Utils.getBrioDate(superTicket.getTicket().getTimestamp()))
                    .replace("?nombreComercio", comercio.getDescComercio())
                    .replace("?direccionComercio", comercio.getDireccionCompleta())
                    .replace("?idComercio", "" + idComercio)
                    .replace("?cambioTicket", String.format("$%.2f", superTicket.getTicket().getCambio()))
                    .replace("?totalTicket", String.format("$%.2f", superTicket.getTicket().getImporteNeto()))
                    .replace("?logo", "<img width=\"256\" height=\"100\" title=\"logo tienda\" alt=\"\" src=\"" + "file://" + Utils.getBrioInternalPath(context) + "ticket_logo.png" + "\">");
        }
        System.out.println(ticket);

        return ticket;
    }

    /**
     * Llena filas de una tabla HTML para ingresar los items vendidos.
     *
     * @param superTicket
     * @param context
     * @return
     */
    private static String llenaTicketPOS(SuperTicket superTicket, Context context) {
        String ticketItems = "";

        for(ItemsTicketController itemsTicketController : superTicket.itemsTicket) {
            ticketItems += ticketPOSItem
                    .replace("?cantidad", String.format("%.2f", itemsTicketController.getCantidad()))
                    .replace("?desc", itemsTicketController.getDescripcion())
                    .replace("?punit", String.format("$%.2f", itemsTicketController.getImporteUnitario()))
                    .replace("?total", String.format("$%.2f", itemsTicketController.getImporteTotal()));
        }

        return tablaItemsTicket.replace("?ticketItems", ticketItems);
    }

    /**
     * Llenar los datos del ticket western union
     * @param superTicket
     * @param context
     * @return
     */
    private static String llenaTicketWestern(SuperTicket superTicket, Context context) {
        String ticketItems = "";

        for(ItemsTicketController itemsTicketController : superTicket.itemsTicket) {
            String[] data = null;

            if(itemsTicketController.getDescripcion().toLowerCase().startsWith("nombre del agente")) {
                continue;
            }

            if(itemsTicketController.getDescripcion().contains(":")) {
                data = new String[2];

                data[0] = itemsTicketController.getDescripcion().substring(0, itemsTicketController.getDescripcion().indexOf(":"));
                data[1] = itemsTicketController.getDescripcion().substring(itemsTicketController.getDescripcion().indexOf(":") + 1, itemsTicketController.getDescripcion().length());

                ticketItems += ticketWesternItem
                        .replace("?name", data[0])
                        .replace("?desc", data[1]);
            }
        }

        return tablaWesternItemsTicket.replace("?ticketItems", ticketItems);
    }

    private final static String ticketTemplateSeguroPago =
    "       <html>\n" +
    "           <head>\n" +
    "               <meta charset=\"utf-8\" lang=\"es-mx\"/>\n" +
    "           </head>\n" +
    "           <body>\n" +
    "           <center>\n" +
    "            <!--logoini-->?logo<!--logofin-->\n" +
    "               <table style=\"width:80%\" cellpadding=\"10\" cellspacing=\"0\">\n" +
    "               <thead>\n" +
    "                   <tr><th><h2><center>?nombreComercio</center></h2></th></tr>\n" +
    "               </thead>\n" +
    "                   <tbody align=\"center\">\n" +
    "                   <tr><td><hr></td></tr>\n" +
    "                   <tr><td><p align=\"right\">?fechaTicket</p></td></tr>\n" +
    "                   <tr><td><center><h4>?tituloTicket</h4></center></td></tr>\n" +
    "                   <tr><td>?tablaItemsTicket</td></tr>\n" +
    "                   <tr><td>?tablaTotalesTicket</td></tr>\n" +
    "                   <tr><td><hr></td></tr>\n" +
    "                   <tr><td><p align=\"center\" >?aviso</p></td></tr>\n" +
    "                   <tr><td><hr></td></tr>\n" +
    "                   </tbody>\n" +
    "                   <tfoot>\n" +
    "                   <tr><td><p align=\"center\">Comercio no. ?idComercio, ?direccionComercio.</p></td></tr>\n" +
    "                   <tr><td></td></tr>\n" +
    "                   </tfoot>\n" +
    "                   </table>\n" +
    "                   </center>\n" +
    "                   </body>\n" +
    "                   </html>\n" ;

    /**
     * Template html para el ticket de Western union
     */
    private final static String ticketTemplateWesternPago =
    "           <html>\n" +
    "           <head>\n" +
    "               <meta charset=\"utf-8\" lang=\"es-mx\"/>\n" +
    "           </head>\n" +
    "           <body>\n" +
    "               <center>\n" +
    "               <div>\n" + //inicio Recibo del beneficiaroo
    "                   <!--logoini-->?logo<!--logofin-->\n" +
    "                   <table style=\"width:80%\" cellpadding=\"5\" cellspacing=\"0\">\n" +
    "                       <thead>\n" +
    "                           <tr><th><h2><center>?nombreComercio</center></h2></th></tr>\n" +
    "                           <tr><td><p align=\"center\">Comercio no. ?idComercio, ?direccionComercio.</p></td></tr>\n" +
    "                       </thead>\n" +
    "                       <tbody align=\"center\">\n" +
    "                           <tr><td><hr></td></tr>\n" +
    "                           <tr><td><center><h2>RECIBO DEL CLIENTE</h2></center></td></tr>\n" +
    "                           <tr><td>Confirmación De Recibo</td></tr>\n" +
    "                           <tr><td>?tablaItemsTicket</td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td><p>?aviso<p></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td><p>?declara<p></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td><hr/></td></tr>\n" +
    "                           <tr><th><h6><center>Firma del Beneficiario</center></h6></th></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td><hr/></td></tr>\n" +
    "                           <tr><th><h6><center>Firma del Agente</center></h6></th></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td>Visita www.wu.com/mywu para obtener más información acerca del Programa My WU&reg;</td></tr>\n" +
    "                       </tbody>\n" +
//    "                       <tfoot>\n" +
//    "                           <tr><td><p align=\"center\">Comercio no. ?idComercio, ?direccionComercio.</p></td></tr>\n" +
//    "                       </tfoot>\n" +
    "                   </table>\n" +
    "               </div>\n" + //fin Recibo del beneficiaro
/* //aqui va el recibo del agente, pero no seria necesario dado que ese no lo debe ver el cliente

            "                           <tr><td></td></tr>\n" +
            "                           <tr><td></td></tr>\n" +
            "                           <tr><td></td></tr>\n" +
            "                           <tr><td></td></tr>\n" +
            "                           <tr><td><hr/></td></tr>\n" +
            "                           <tr><td><hr/></td></tr>\n" +
            "                           <tr><td></td></tr>\n" +
            "                           <tr><td></td></tr>\n" +
            "                           <tr><td></td></tr>\n" +
            "                           <tr><td></td></tr>\n" +


            //**********************************************************************************************

    "               <div>\n" + //inicio Recibo del agente
    "                   <!--logoini-->?logo<!--logofin-->\n" +
    "                   <table style=\"width:80%\" cellpadding=\"5\" cellspacing=\"0\">\n" +
    "                       <thead>\n" +
    "                           <tr><th><h2><center>?nombreComercio</center></h2></th></tr>\n" +
    "                           <tr><td><p align=\"center\">Comercio no. ?idComercio, ?direccionComercio.</p></td></tr>\n" +
    "                       </thead>\n" +
    "                       <tbody align=\"center\">\n" +
    "                           <tr><td><hr></td></tr>\n" +
    "                           <tr><td><center><h2>RECIBO DEL AGENTE</h2></center></td></tr>\n" +
    "                           <tr><td>?agente</td></tr>\n" +
    "                           <tr><td>?tablaItemsTicket</td></tr>\n" +
    "                           <tr><td><table>" +
    "                                <tr><td align=\"left\">[ ] Registrate en el programa My WU&reg;</td></tr>\n" +
    "                                <tr><td align=\"left\">Mi número My WU&reg;:</td></tr>\n" +
    "                                <tr><td align=\"left\">Mis puntos acumulados de My WU&reg;:</td></tr>\n" +
    "                                <tr><td align=\"left\">Mis puntos generados de My WU&reg;:</td></tr>\n" +
    "                           </table></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td><p>?aviso<p></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td><p>?declara<p></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td><hr/></td></tr>\n" +
    "                           <tr><th><h6><center>Firma del Beneficiario</center></h6></th></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td><hr/></td></tr>\n" +
    "                           <tr><th><h6><center>Firma del Agente</center></h6></th></tr>\n" +
    "                           <tr><td></td></tr>\n" +
    "                           <tr><td>Visita www.wu.com/mywu para obtener más información acerca del Programa My WU&reg;</td></tr>\n" +
    "                       </tbody>\n" +
    "                   </table>\n" +
    "               </div>\n" + //fin Recibo del agente
Hasta aqui va el recibo del agente*/
    "               </center>\n" +
    "           </body>\n" +
    "           <html>\n";

    //--------------------------------------------------------------------------------------------------------------------------

    /**
     * Template html para el ticket de POS
     */
    private final static String ticketTemplate =
            "<html>\n" +
            "  <head>\n" +
            "    <meta charset=\"utf-8\" lang=\"es-mx\"/>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <center>\n" +
            "      <!--logoini-->?logo<!--logofin-->\n" +
            "      <table style=\"width:80%\" cellpadding=\"10\" cellspacing=\"0\">\n" +
            "        <thead>\n" +
            "            <tr><th><h2><center>?nombreComercio</center></h2></th></tr>\n" +
            "        </thead>\n" +
            "        <tbody align=\"center\">\n" +
            "          <tr><td><hr></td></tr>\n" +
            "          <tr><td><p align=\"right\">?fechaTicket</p></td></tr>\n" +
            "          <tr><td><center><h4>?tituloTicket</h4></center></td></tr>\n" +
            "          <tr><td>?tablaItemsTicket</td></tr>\n" +
            "          <tr><td>?tablaTotalesTicket</td></tr>\n" +
            "          <tr><td><hr></td></tr>\n" +
            "        </tbody>\n" +
            "        <tfoot>\n" +
            "          <tr><td><p align=\"center\">Comercio no. ?idComercio, ?direccionComercio.</p></td></tr>\n" +
            "        </tfoot>\n" +
            "      </table>\n" +
            "    </center>\n" +
            "  </body>\n" +
            "</html>\n";

    //------------------------------------------------------------------------------------------------------------
    //                                     Ticket de POS
    //------------------------------------------------------------------------------------------------------------

    private final static String ticketPOSHeader = "\n<thead>" +
            "<th align=\"center\">Cantidad</th>" +
            "<th align=\"left\">Descripción</th>" +
            "<th align=\"right\">Precio unitario</th>" +
            "<th align=\"right\">Subtotal</th>" +
            "</thead>\n";

    private final static String tablaTotalesTicket =
            "\n<table cellpadding=\"10\">" +
            "\n<tr><td align=\"right\" colspan=\"3\"><b>Total a cobrar</b></td>\n<td align=\"right\">?totalTicket</td></tr>" +
            "?tablaTotalesTicketItem" +
            "\n<tr><td align=\"right\" colspan=\"3\"><b>Cambio</b></td>\n<td align=\"right\">?cambioTicket</td></tr>" +
            "\n</table>\n";

    private final static String tablaTotalesTicketItem =
            "\n<tr><td align=\"right\" colspan=\"3\"><b>?pagoNombre</b></td>\n<td align=\"right\">?pagoMonto</td></tr>";

    private final static String tablaMetadatos =
            "\n<tr><td align=\"right\" colspan=\"3\"><b>?key/b></td>\n<td align=\"right\">?value</td></tr>";

    private final static String tablaItemsTicket =
            "\n<table cellpadding=\"10\">\n" + ticketPOSHeader + "?ticketItems</table>\n";

    private final static String ticketPOSItem =
            "<tr>" +
            "<td align=\"center\">?cantidad</td>" +
            "<td align=\"left\">?desc</td>" +
            "<td align=\"right\">?punit</td>" +
            "<td align=\"right\">?total</td>" +
            "</tr>\n";

    private final static String tablaWesternItemsTicket =
            "\n<table>\n?ticketItems</table>\n";

    private final static String ticketWesternItem =
            "<tr>" +
            "<td align=\"left\"><b>?name</b></td>" +
            "<td align=\"right\">?desc</td>" +
            "</tr>\n";
}