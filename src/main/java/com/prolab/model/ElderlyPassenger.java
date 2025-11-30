package com.prolab.model;

public class ElderlyPassenger extends Passenger {

    public ElderlyPassenger() {
        super("Elderly");
    }

    @Override
    public double calculateDiscountedCost(double originalCost) {
        return originalCost * 0.5; // %50 indirim
    }
}