package com.bnvlab.concienciadeabundancia.clases;

import java.util.HashMap;

/**
 * Created by Marcos on 13/11/2017.
 */

public class AppValues {
    int counter=1;
    HashMap<String,String> paymentLinks;

    public AppValues() {
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public HashMap<String, String> getPaymentLinks() {
        return paymentLinks;
    }

    public void setPaymentLinks(HashMap<String, String> paymentLinks) {
        this.paymentLinks = paymentLinks;
    }
}
