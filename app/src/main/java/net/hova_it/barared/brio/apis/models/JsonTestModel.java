package net.hova_it.barared.brio.apis.models;

import java.util.List;

/**
 * Created by Alejandro Gomez on 07/12/2015.
 */
public class JsonTestModel {

    private int totalProducts;
    private List<TestModel> products;

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public List<TestModel> getProducts() {
        return products;
    }

    public void setProducts(List<TestModel> products) {
        this.products = products;
    }

}
