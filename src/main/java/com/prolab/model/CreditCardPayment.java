package com.prolab.model;

public class CreditCardPayment extends Payment {
    public CreditCardPayment(double amount) {
        super(amount);
    }

    @Override
    public boolean canPay(double cost) {
        return amount >= cost;
    }
}