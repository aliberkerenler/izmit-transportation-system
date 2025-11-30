package com.prolab.model;

public class Taxi extends Vehicle {
    private double openingFee;
    private double costPerKm;

    public Taxi(double openingFee, double costPerKm) {
        super("taxi");
        this.openingFee = openingFee;
        this.costPerKm = costPerKm;
    }

    public double calculateFare(double distance) {
        return openingFee + (distance * costPerKm);
    }


}