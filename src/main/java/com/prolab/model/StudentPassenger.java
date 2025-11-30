package com.prolab.model;

public class StudentPassenger extends Passenger {

    public StudentPassenger() {
        super("Student");
    }

    @Override
    public double calculateDiscountedCost(double originalCost) {
        return originalCost * 0.5; // %50 indirim
    }
}