package com.prolab.model;

public class CashPayment extends Payment {
    public CashPayment(double amount) {
        super(amount);
    }

    @Override
    public boolean canPay(double cost) {
        return amount >= cost;
    }
}