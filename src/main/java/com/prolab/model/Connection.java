package com.prolab.model;

public class Connection {
    private final Stop fromStop;
    private final Stop toStop;
    private final String vehicleType;
    private final double distance;
    private final double duration;
    private final double fare;

    public Connection(Stop fromStop, Stop toStop, String vehicleType,
                     double distance, double duration, double fare) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.vehicleType = vehicleType;
        this.distance = distance;
        this.duration = duration;
        this.fare = fare;
    }

    public Stop getFromStop() {
        return fromStop;
    }

    public Stop getToStop() {
        return toStop;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public double getDistance() {
        return distance;
    }

    public double getDuration() {
        return duration;
    }

    public double getFare() {
        return fare;
    }
} 