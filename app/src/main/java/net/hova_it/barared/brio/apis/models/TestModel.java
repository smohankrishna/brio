package net.hova_it.barared.brio.apis.models;

/**
 * Created by Alejandro Gomez on 04/12/2015.
 */
public class TestModel {
    private String _id;
    private String prodSKU;
    private float prodPrice;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getProdSKU() {
        return prodSKU;
    }

    public void setProdSKU(String prodSKU) {
        this.prodSKU = prodSKU;
    }

    public float getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(float prodPrice) {
        this.prodPrice = prodPrice;
    }

}
