package org.udesa.tp1.model;


public class Merchant implements Identifiable {
    private final String merchantId;
    private final String merchantName;

    public Merchant(String merchantId, String merchantName) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
    }


    @Override
    public String id(){
        return merchantId;
    }

    public String merchantName() {
        return merchantName;
    }
}
