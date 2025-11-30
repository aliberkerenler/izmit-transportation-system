package com.prolab.factory;

import com.prolab.model.Taxi;

public class DefaultTaxiFactory implements TaxiFactory {
    private final double openingFee;
    private final double costPerKm;

    public DefaultTaxiFactory(double openingFee, double costPerKm) {
        this.openingFee = openingFee;
        this.costPerKm = costPerKm;
    }

    @Override
    public Taxi createTaxi() {
        return new Taxi(openingFee, costPerKm);
    }
}