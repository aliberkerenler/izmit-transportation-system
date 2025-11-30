package com.prolab.model;

public abstract class Vehicle {
    protected String type;

    public Vehicle(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


}