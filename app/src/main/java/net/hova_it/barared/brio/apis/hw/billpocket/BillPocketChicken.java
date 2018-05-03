package net.hova_it.barared.brio.apis.hw.billpocket;

import net.hova_it.barared.brio.apis.payment.PaymentService;

/**
 * POJO para los parametros a enviar a BillPocket
 *
 * Created by Herman Peralta on 15/03/2016.
 */
public class BillPocketChicken {

    public final static String RESULT_APPROVED = "aprobada";
    public final static String RESULT_DECLINED = "rechazada";
    public final static String RESULT_ERROR = "error";
    public final static String RESULT_APPROVED_INVALIDO = "aprobadainvalida";

    private String
        /*--------- Request fields ---------*/
            pin,           /*The valid PIN to open the local Billpocket's app*/
            amount,        /*The amount without tip of the charge*/
            transaction,   /*The transaction type: venta or devolucion*/
            identifier,    /*Identifier so your app can relate the payments with its own database*/
            reference,     /*Text reference to identify the payment for the customer*/
            xpLandscape;   /*Para la version experimental, poner horizontal*/

        /*--------- Response fields ---------*/
    private String
            result,        /*The result of the charge. It can be: aprobada (aproved), rechazada (declined) or error (error).*/

        /*Reponse aproved*/
            transactionid, /*El id de transaccion de Billpocket*/
            authorization, /*El # de autorización del banco*/
            creditcard,    /*Últimos 4 dígitos de la tarjeta*/
            cardtype,      /*Tipo tarjeta*/
            url,           /*Url del voucher de billpocket*/ /*https://test.bpckt.com/api/v1/ticket/<url>*/

        /*Response Error/Declined*/
            statusinfo;    /*Mensaje de error del banco*/

    private PaymentService.PagoAttachListener pagoAttachListener;

    public BillPocketChicken() {
        transaction = "venta";
        xpLandscape = "true";
    }
    ////////////////////////////////////////////////////////// REQUEST /////////////////////////////

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setAmount(Double amount) {
        this.amount = amount.toString();
    }

    public String getAmount() {
        return amount;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getXpLandscape() {
        return xpLandscape;
    }

    public void setXpLandscape(String xpLandscape) {
        this.xpLandscape = xpLandscape;
    }

    ////////////////////////////////////////////////////////// RESPONSE ////////////////////////////

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getCreditcard() {
        return creditcard;
    }

    public void setCreditcard(String creditcard) {
        this.creditcard = creditcard;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatusinfo() {
        return statusinfo;
    }

    public void setStatusinfo(String statusinfo) {
        this.statusinfo = statusinfo;
    }

    public void setPagoAttachListener(PaymentService.PagoAttachListener pagoAttachListener) {
        this.pagoAttachListener = pagoAttachListener;
    }

    public PaymentService.PagoAttachListener getPagoAttachListener() {
        return pagoAttachListener;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }
}
