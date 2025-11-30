package com.prolab.model;

public class GeneralPassenger extends Passenger {

    public GeneralPassenger() {
        super("General");
    }

    @Override
    public double calculateDiscountedCost(double originalCost) {
        return originalCost; // İndirim yok
    }
}