package com.prolab.model;

public class Transfer {
    private final Stop fromStop;
    private final Stop toStop;
    private final double duration;
    private final double cost;

    public Transfer(Stop fromStop, Stop toStop, double duration, double cost) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.duration = duration;
        this.cost = cost;
    }

    public Stop getFromStop() {
        return fromStop;
    }

    public Stop getToStop() {
        return toStop;
    }

    public double getDuration() {
        return duration;
    }

    public double getCost() {
        return cost;
    }

    public String getTransferStopId() {
        return toStop.getId();
    }

    public double getTransferDuration() {
        return duration;
    }

    public double getTransferFare() {
        return cost;
    }
} 