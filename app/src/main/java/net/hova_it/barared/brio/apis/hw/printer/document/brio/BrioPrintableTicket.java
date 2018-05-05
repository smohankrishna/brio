package net.hova_it.barared.brio.apis.hw.printer.document.brio;

import android.content.Context;
import android.util.Log;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.hw.printer.document.api.PrintableDocument;
import net.hova_it.barared.brio.apis.hw.printer.document.api.Section;
import net.hova_it.barared.brio.apis.hw.printer.document.api.TextBlock;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.CardPaymentRequest;
import net.hova_it.barared.brio.apis.models.entities.Comercio;
import net.hova_it.barared.brio.apis.models.entities.SuperTicket;
import net.hova_it.barared.brio.apis.models.entities.TicketTipoPago;
import net.hova_it.barared.brio.apis.payment.PaymentService;
import net.hova_it.barared.brio.apis.pos.api.ItemsTicketController;
import net.hova_it.barared.brio.apis.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by Herman Peralta on 17/02/2016.
 */
public class BrioPrintableTicket extends PrintableDocument {

    private final static String LINE = "================================================";//48
    private final static String LINEM = "_______________________________________________";
    private final static String LINE_BRIO = StringUtils.center("Brío", 38);

    private Context context;
    private SuperTicket superTicket;

    public BrioPrintableTicket(Context context, SuperTicket superTicket) {
        this.context = context;
        this.superTicket = superTicket;

        build();
    }

    public BrioPrintableTicket(Context context, int idTicket) {
        this.context = context;
        this.superTicket = Utils.getSuperTicketFromDB(context, idTicket);

        build();
    }

    private final static String FORMAT_ITEM = "%6s %-26s %9s";
    private final static String FORMAT_ITEM_EXT = "       %-26s";

    private final static String FORMAT_ITEM_WESTERN = "%-45s";
    private final static String FORMAT_ITEM_EXT_WESTERN = "     %-40s";

    private final static String FORMAT_PAYMENT  = "       %-15s %10s";
    private final static String pattern = "[^0-9a-zA-ZñÑáéíóúáéíóúÁÉÍÓÚüÜ()+-_ ,.:%$'\\/*]";

    private void build() {
        ModelManager modelManager = new ModelManager(context);
        int idComercio = Integer.valueOf(modelManager.settings.getByNombre("ID_COMERCIO").getValor());
        Comercio comercio = modelManager.comercios.getByIdComercio(idComercio);

        switch(this.superTicket.getTicket().getIdTipoTicket()) {
            //this.superTicket.getTicket().getIdTipoTicket()   /////////////
            case PaymentService.TIPO_TICKET_WESTERN_PAGO:
                buildWesternTicket(comercio);
                break;

            case PaymentService.TIPO_PAGO_SEGURO:
                buildPagoSegurosTicket(comercio);
                break;

            default:
                buildDefaultTicket(comercio);
                break;
        }

    }



    private void buildDefaultTicket(Comercio comercio) {
        Section header = new Section();
        header.addBlock(new TextBlock(LINE_BRIO, TextBlock.ATTR_TEXT_SIZE_BIG, true, false));
        header.addBlock(new TextBlock(""));
        header.addBlock(new TextBlock(StringUtils.center(comercio.getDescComercio(), 45), TextBlock.ATTR_TEXT_SIZE_NORMAL, true, false));
        header.addBlock(new TextBlock(""));
        header.addBlock(new TextBlock(StringUtils.center(Utils.getNombreTicket(context, superTicket.getTicket().getIdTipoTicket()), 45), TextBlock.ATTR_TEXT_SIZE_NORMAL, true, false));
        header.addBlock(new TextBlock(""));
        header.addBlock(new TextBlock(String.format("%48s", Utils.getBrioDate(superTicket.getTicket().getTimestamp())), TextBlock.ATTR_TEXT_SIZE_SMALL));
        header.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL));

        Section articles = new Section();
        for(ItemsTicketController itemsTicket : superTicket.itemsTicket) {
            ArrayList<TextBlock> items = getItemsTicketBlock(itemsTicket);
            for(TextBlock item : items) {
                articles.addBlock(item);
            }
        }
        articles.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL));

        Section payments = new Section();
        payments.addBlock(new TextBlock(String.format(FORMAT_PAYMENT, "Total", String.format("%7.2f", superTicket.getTicket().getImporteNeto())), TextBlock.ATTR_TEXT_SIZE_NORMAL));

        TicketTipoPago totalEfectivo = null;

        for(TicketTipoPago ticketTipoPago : superTicket.ticketTipoPago) {
            if(ticketTipoPago.getIdTipoPago() == PaymentService.TIPO_PAGO_EFECTIVO) {
                if(totalEfectivo == null){
                    totalEfectivo = new TicketTipoPago();
                    totalEfectivo.setIdTipoPago(PaymentService.TIPO_PAGO_EFECTIVO);
                }
                totalEfectivo.setMonto(totalEfectivo.getMonto() + ticketTipoPago.getMonto());
            }
        }
        if(totalEfectivo != null){
            payments.addBlock(getTicketTipoPagoBlock(totalEfectivo));
        }


        for(TicketTipoPago ticketTipoPago : superTicket.ticketTipoPago) {
            // Se inyectan los items de numero de tarjeta y autorización para cada pago de tarjeta.
            if(ticketTipoPago.getIdTipoPago() == PaymentService.TIPO_PAGO_TARJETA) {
                payments.addBlock(getTicketTipoPagoBlock(ticketTipoPago));
                CardPaymentRequest cardPaymentRequest = (CardPaymentRequest)Utils.fromJSON(ticketTipoPago.getDescripcion(), CardPaymentRequest.class);
                if(cardPaymentRequest != null) {
                    String pagoNombre = cardPaymentRequest.getCardtype();
                    String cardn = cardPaymentRequest.getCreditcard();
                    if(cardn!=null && cardn.length()>=4) {
                        cardn = cardn.substring(cardn.length() - 4, cardn.length());
                        pagoNombre += " " + cardn;

                    }
                    payments.addBlock(new TextBlock("          " + pagoNombre, TextBlock.ATTR_TEXT_SIZE_NORMAL));

                    payments.addBlock(new TextBlock("          " + "Aut. " + cardPaymentRequest.getAuthorization(), TextBlock.ATTR_TEXT_SIZE_NORMAL));
                }
            }
        }


        payments.addBlock(new TextBlock(String.format(FORMAT_PAYMENT, "Cambio", String.format("%7.2f", superTicket.getTicket().getCambio())), TextBlock.ATTR_TEXT_SIZE_NORMAL));
        payments.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL));
        payments.addBlock(new TextBlock(""));

        Section footer = new Section();
        final String sfooter = String.format("Comercio no. %d, %s", comercio.getIdComercio(), comercio.getDireccionCompleta()).trim().replaceAll(pattern, "");
        final ArrayList<String> fragments = Utils.fragmentString(sfooter, 48);
        for(final String fragment : fragments) {
            footer.addBlock(new TextBlock(StringUtils.center(fragment, 48), TextBlock.ATTR_TEXT_SIZE_SMALL));
        }
        footer.addBlock(new TextBlock("", TextBlock.ATTR_TEXT_SIZE_SMALL));

        sections.add(header);
        sections.add(articles);
        sections.add(payments);
        sections.add(footer);
    }

    private void buildPagoSegurosTicket(Comercio comercio) {
        Section header = new Section();
        header.addBlock(new TextBlock(LINE_BRIO, TextBlock.ATTR_TEXT_SIZE_BIG, true, false));
        header.addBlock(new TextBlock(""));
        header.addBlock(new TextBlock(StringUtils.center(comercio.getDescComercio(), 45), TextBlock.ATTR_TEXT_SIZE_NORMAL, true, false));
        header.addBlock(new TextBlock(""));
        header.addBlock(new TextBlock(StringUtils.center(Utils.getNombreTicket(context, superTicket.getTicket().getIdTipoTicket()), 45), TextBlock.ATTR_TEXT_SIZE_NORMAL, true, false));
        header.addBlock(new TextBlock(""));
        header.addBlock(new TextBlock(String.format("%48s", Utils.getBrioDate(superTicket.getTicket().getTimestamp())), TextBlock.ATTR_TEXT_SIZE_SMALL));
        header.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL));

        Section articles = new Section();
        for(ItemsTicketController itemsTicket : superTicket.itemsTicket) {
            ArrayList<TextBlock> items = getItemsTicketBlock(itemsTicket);
            for(TextBlock item : items) {
                articles.addBlock(item);
            }
        }
        articles.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL));

        Section payments = new Section();
        payments.addBlock(new TextBlock(String.format(FORMAT_PAYMENT, "Total", String.format("%7.2f", superTicket.getTicket().getImporteNeto())), TextBlock.ATTR_TEXT_SIZE_NORMAL));
        for(TicketTipoPago ticketTipoPago : superTicket.ticketTipoPago) {

            payments.addBlock(getTicketTipoPagoBlock(ticketTipoPago));

            // Se inyectan los items de numero de tarjeta y autorización para cada pago de tarjeta.
            if(ticketTipoPago.getIdTipoPago() == PaymentService.TIPO_PAGO_TARJETA) {
                CardPaymentRequest cardPaymentRequest = (CardPaymentRequest)Utils.fromJSON(ticketTipoPago.getDescripcion(), CardPaymentRequest.class);
                if(cardPaymentRequest != null) {
                    String pagoNombre = cardPaymentRequest.getCardtype();
                    String cardn = cardPaymentRequest.getCreditcard();
                    if(cardn!=null && cardn.length()>=4) {
                        cardn = cardn.substring(cardn.length() - 4, cardn.length());
                        pagoNombre += " " + cardn;

                    }
                    payments.addBlock(new TextBlock("          " + pagoNombre, TextBlock.ATTR_TEXT_SIZE_NORMAL));

                    payments.addBlock(new TextBlock("          " + "Aut. " + cardPaymentRequest.getAuthorization(), TextBlock.ATTR_TEXT_SIZE_NORMAL));
                }
            }
        }
        payments.addBlock(new TextBlock(String.format(FORMAT_PAYMENT, "Cambio", String.format("%7.2f", superTicket.getTicket().getCambio())), TextBlock.ATTR_TEXT_SIZE_NORMAL));
        payments.addBlock(new TextBlock(""));
        payments.addBlock(new TextBlock(""));
        payments.addBlock(new TextBlock((context.getResources().getString(R.string.payment_tipo_ticket_seguro_aviso)), TextBlock.ATTR_TEXT_SIZE_MSMALL));
        payments.addBlock(new TextBlock(""));
        payments.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL));
        payments.addBlock(new TextBlock(""));

        Section footer = new Section();
        final String sfooter = String.format("Comercio no. %d, %s", comercio.getIdComercio(), comercio.getDireccionCompleta()).trim().replaceAll(pattern, "");
        final ArrayList<String> fragments = Utils.fragmentString(sfooter, 48);
        for(final String fragment : fragments) {
            footer.addBlock(new TextBlock(StringUtils.center(fragment, 48), TextBlock.ATTR_TEXT_SIZE_SMALL));
        }
        footer.addBlock(new TextBlock("", TextBlock.ATTR_TEXT_SIZE_SMALL));

        sections.add(header);
        sections.add(articles);
        sections.add(payments);
        sections.add(footer);
    }

    private void buildWesternTicket(Comercio comercio) {
        nombreAgente = "";
        //Se usaban para el for de la direccion pero se cambio a bloques
        final String direccion = String.format("Comercio no. ", comercio.getIdComercio(), (context.getResources().getString(R.string.direccion_western)));//comercio.getDireccionCompleta()).trim().replaceAll(pattern, "");
        final ArrayList<String> dirfragments = Utils.fragmentString(direccion, 48);
        //Section footer = new Section();
        //final String sfooter = String.format("Comercio no. %d, %s", comercio.getIdComercio(), comercio.getDireccionCompleta()).trim().replaceAll(pattern, "");
        //final ArrayList<String> fragments = Utils.fragmentString(sfooter, 48);
        //for(final String fragment : fragments) {
        //    footer.addBlock(new TextBlock(StringUtils.center(fragment, 48), TextBlock.ATTR_TEXT_SIZE_SMALL));
        //}
        //footer.addBlock(new TextBlock("", TextBlock.ATTR_TEXT_SIZE_SMALL));
        //footer.addBlock(new TextBlock((context.getResources().getString(R.string.payment_tipo_ticket_western_aviso)), TextBlock.ATTR_TEXT_SIZE_MSMALL));
        //footer.addBlock(new TextBlock("", TextBlock.ATTR_TEXT_SIZE_SMALL));


        Section headerBeneficiario = new Section();
        headerBeneficiario.addBlock(new TextBlock(""));
        headerBeneficiario.addBlock(new TextBlock(LINE_BRIO, TextBlock.ATTR_TEXT_SIZE_BIG, true, false));
        headerBeneficiario.addBlock(new TextBlock(""));
        headerBeneficiario.addBlock(new TextBlock(StringUtils.center(comercio.getDescComercio(), 45), TextBlock.ATTR_TEXT_SIZE_NORMAL, true, false));
        /*Direccion*/
        /*for(final String fragment : dirfragments) {
            headerBeneficiario.addBlock(new TextBlock(StringUtils.center(fragment, 48), TextBlock.ATTR_TEXT_SIZE_SMALL));
        }*/
        headerBeneficiario.addBlock(new TextBlock(StringUtils.center("Comercio no. "+ comercio.getIdComercio(), 45), TextBlock.ATTR_TEXT_SIZE_SMALL));
        headerBeneficiario.addBlock(new TextBlock(StringUtils.center("Av. Jose Eleuterio #250 int 201", 45), TextBlock.ATTR_TEXT_SIZE_SMALL));
        headerBeneficiario.addBlock(new TextBlock(StringUtils.center("Col Santa Maria, Monterrey, Nuevo Leon", 45), TextBlock.ATTR_TEXT_SIZE_SMALL));
        headerBeneficiario.addBlock(new TextBlock(StringUtils.center("CP 64650, Tel 50475555", 45), TextBlock.ATTR_TEXT_SIZE_SMALL));
        headerBeneficiario.addBlock(new TextBlock(""));
        /*Direccion*/
        headerBeneficiario.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL));
        headerBeneficiario.addBlock(new TextBlock(StringUtils.center("RECIBO DEL CLIENTE", 35), TextBlock.ATTR_TEXT_SIZE_BIG, true, false));
        headerBeneficiario.addBlock(new TextBlock(""));
        headerBeneficiario.addBlock(new TextBlock(StringUtils.center("Confirmación De Recibido", 45), TextBlock.ATTR_TEXT_SIZE_NORMAL, true, false));
        headerBeneficiario.addBlock(new TextBlock(""));

        Section articles = new Section();
        for(ItemsTicketController itemsTicket : superTicket.itemsTicket) {
            ArrayList<TextBlock> items = getItemsTicketBlock(itemsTicket);
            for(TextBlock item : items) {
                articles.addBlock(item);
            }
        }
/*
        Section payments = new Section();
        for(TicketTipoPago ticketTipoPago : superTicket.ticketTipoPago) {

            payments.addBlock(getTicketTipoPagoBlock(ticketTipoPago));

            // Se inyectan los items de numero de tarjeta y autorización para cada pago de tarjeta.
            if(ticketTipoPago.getIdTipoPago() == PaymentService.TIPO_PAGO_TARJETA) {
                CardPaymentRequest cardPaymentRequest = (CardPaymentRequest)Utils.fromJSON(ticketTipoPago.getDescripcion(), CardPaymentRequest.class);
                if(cardPaymentRequest != null) {
                    String pagoNombre = cardPaymentRequest.getCardtype();
                    String cardn = cardPaymentRequest.getCreditcard();
                    if(cardn!=null && cardn.length()>=4) {
                        cardn = cardn.substring(cardn.length() - 4, cardn.length());
                        pagoNombre += " " + cardn;

                    }
                    payments.addBlock(new TextBlock("          " + pagoNombre, TextBlock.ATTR_TEXT_SIZE_NORMAL));

                    payments.addBlock(new TextBlock("          " + "Aut. " + cardPaymentRequest.getAuthorization(), TextBlock.ATTR_TEXT_SIZE_NORMAL));
                }
            }
        }
*/

        Section footer = new Section();
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock((context.getResources().getString(R.string.payment_tipo_ticket_western_aviso)), TextBlock.ATTR_TEXT_SIZE_MSMALL));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock((context.getResources().getString(R.string.payment_tipo_ticket_western_declara)), TextBlock.ATTR_TEXT_SIZE_MSMALL));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL));
        footer.addBlock(new TextBlock(StringUtils.center("Firma del Beneficiario", 45), TextBlock.ATTR_TEXT_SIZE_NORMAL,true, false));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL));
        footer.addBlock(new TextBlock(StringUtils.center("Firma del Agente", 45), TextBlock.ATTR_TEXT_SIZE_NORMAL,true, false));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock(""));
        footer.addBlock(new TextBlock("Visita www.wu.com/mywu para obtener más información acerca del Programa My WU®"));

        Section div = new Section();
        div.addBlock(new TextBlock(""));
        div.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL, true, false));
        div.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL, true, false));
        div.addBlock(new TextBlock(""));

        Section headerAgente = new Section();
        headerAgente.addBlock(new TextBlock(""));
        headerAgente.addBlock(new TextBlock(LINE_BRIO, TextBlock.ATTR_TEXT_SIZE_BIG, true, false));
        headerAgente.addBlock(new TextBlock(""));
        /*Direccion*/
        headerAgente.addBlock(new TextBlock(StringUtils.center(comercio.getDescComercio(), 45), TextBlock.ATTR_TEXT_SIZE_NORMAL, true, false));
        /*for(final String fragment : dirfragments) {
            headerAgente.addBlock(new TextBlock(StringUtils.center(fragment, 48), TextBlock.ATTR_TEXT_SIZE_SMALL));
        }*/
        headerAgente.addBlock(new TextBlock(StringUtils.center("Comercio no. "+ comercio.getIdComercio(), 45), TextBlock.ATTR_TEXT_SIZE_SMALL));
        headerAgente.addBlock(new TextBlock(StringUtils.center("Av. Jose Eleuterio #250 int 201", 45), TextBlock.ATTR_TEXT_SIZE_SMALL));
        headerAgente.addBlock(new TextBlock(StringUtils.center("Col Santa Maria, Monterrey, Nuevo Leon", 45), TextBlock.ATTR_TEXT_SIZE_SMALL));
        headerAgente.addBlock(new TextBlock(StringUtils.center("CP 64650, Tel 50475555", 45), TextBlock.ATTR_TEXT_SIZE_SMALL));
        headerAgente.addBlock(new TextBlock(""));
        /*Direccion*/
        headerAgente.addBlock(new TextBlock(LINE, TextBlock.ATTR_TEXT_SIZE_SMALL));
        headerAgente.addBlock(new TextBlock(StringUtils.center("RECIBO DEL AGENTE", 35), TextBlock.ATTR_TEXT_SIZE_BIG, true, false));
        headerAgente.addBlock(new TextBlock(""));
        headerAgente.addBlock(new TextBlock(nombreAgente, TextBlock.ATTR_TEXT_SIZE_SMALL, false, false));
        headerAgente.addBlock(new TextBlock(""));


        //Creando copia del beneficiario
        sections.add(headerBeneficiario);
        sections.add(articles);
        //sections.add(payments);
        sections.add(footer);

        sections.add(div);

        //creando copia del agente
        sections.add(headerAgente);
        sections.add(articles);
        //sections.add(payments);
        sections.add(footer);
    }

    private String nombreAgente = null;

    private ArrayList<TextBlock> getItemsTicketBlock(ItemsTicketController itemsTicket) {
        ArrayList<String> fragments = null;
        ArrayList<TextBlock> blocks = null;

        if(superTicket.getTicket().getIdTipoTicket() != PaymentService.TIPO_TICKET_WESTERN_PAGO) {
            String artDesc = itemsTicket.getDescripcion().trim()
                    .replaceAll(pattern, "")
                    .replaceAll("<br />", "")
                    .replaceAll("<b>", "")
                    .replaceAll("</b>", "");

            fragments = Utils.fragmentString(artDesc, 26);
            blocks = new ArrayList<>();
            blocks.add(new TextBlock(String.format(FORMAT_ITEM, String.format("%3.2f", itemsTicket.getCantidad()), fragments.get(0), String.format("%6.2f", itemsTicket.getImporteTotal())), TextBlock.ATTR_TEXT_SIZE_NORMAL));

            for(int i=1 ; i<fragments.size() ; i++) {
                blocks.add(new TextBlock(String.format(FORMAT_ITEM_EXT, fragments.get(i).trim()), TextBlock.ATTR_TEXT_SIZE_NORMAL));
            }
        } else {
            String artDesc = itemsTicket.getDescripcion().trim()
                    //.replaceAll(pattern, "")
                    .replaceAll("<br />", "")
                    .replaceAll("<b>", "")
                    .replaceAll("</b>", "");

            blocks = new ArrayList<>();

            if(artDesc.toLowerCase().startsWith("nombre del agente")) {
                try {
                    nombreAgente = artDesc;//artDesc.substring(artDesc.indexOf(":") + 2, artDesc.length());
                } catch (Exception e) {
                    e.printStackTrace();
                    nombreAgente = "";
                }

                return blocks;
            }

            fragments = Utils.fragmentString(artDesc, 40);

            if(fragments.size() > 0) {
                Log.i("detail", "'" + String.format(FORMAT_ITEM_WESTERN, fragments.get(0).trim()) + "'");
                blocks.add(new TextBlock(String.format(FORMAT_ITEM_WESTERN, fragments.get(0).trim()), TextBlock.ATTR_TEXT_SIZE_NORMAL));

                for (int i = 1; i < fragments.size(); i++) {
                    blocks.add(new TextBlock(String.format(FORMAT_ITEM_EXT_WESTERN, fragments.get(i).trim()), TextBlock.ATTR_TEXT_SIZE_NORMAL));
                    Log.i("detail", "'" + String.format(FORMAT_ITEM_WESTERN, fragments.get(i).trim()) + "'");
                }
            } else {
                blocks.add(new TextBlock(""));
            }
        }

        return blocks;
    }

    private TextBlock getTicketTipoPagoBlock(TicketTipoPago ticketTipoPago) {
        String text = "";

        double monto = ticketTipoPago.getMonto();

        //if(PaymentService.TIPO_PAGO_EFECTIVO == ticketTipoPago.getIdTipoPago()) {   //este metodo esta sumando el monto pàgaddo mas el cambio
        //    monto += superTicket.getTicket().getCambio();
        //}

        text = String.format(FORMAT_PAYMENT, Utils.getNombrePago(context, ticketTipoPago.getIdTipoPago()), String.format("%7.2f", monto));

        return new TextBlock(text, TextBlock.ATTR_TEXT_SIZE_NORMAL, true, false);
    }

}
