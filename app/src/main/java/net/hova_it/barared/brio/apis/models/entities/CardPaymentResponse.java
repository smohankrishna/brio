package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo CardPaymentResponse
 * Created by Herman Peralta on 24/05/2016.
 */
public class CardPaymentResponse {

    private CardPaymentRequest request;
    private long transactionId;
    private String message;

    public CardPaymentRequest getRequest() {
        return request;
    }

    public void setRequest(CardPaymentRequest request) {
        this.request = request;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
