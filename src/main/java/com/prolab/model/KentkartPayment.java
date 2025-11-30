package com.prolab.model;

public class KentkartPayment extends Payment {
    public KentkartPayment(double amount) {
        super(amount);
    }

    @Override
    public boolean canPay(double cost) {
        return amount >= cost;
    }
}