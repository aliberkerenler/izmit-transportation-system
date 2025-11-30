package com.prolab.model;

public abstract class Passenger {
    protected String name;

    public Passenger(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract double calculateDiscountedCost(double originalCost);
}