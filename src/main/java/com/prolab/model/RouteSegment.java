package com.prolab.model;

public class RouteSegment {
    private final Stop fromStop;
    private final Stop toStop;
    private final String vehicleType; // "bus", "tram", "walk", "taxi"
    private final double distance;
    private final double time;
    private final double cost;
    private final boolean isTransfer;

    public RouteSegment(Stop fromStop, Stop toStop, String vehicleType,
                       double distance, double time, double cost, boolean isTransfer) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.vehicleType = vehicleType;
        this.distance = distance;
        this.time = time;
        this.cost = cost;
        this.isTransfer = isTransfer;
    }

    public Stop getFromStop() {
        return fromStop;
    }

    public Stop getToStop() {
        return toStop;
    }

    public String getType() {
        return vehicleType;
    }

    public double getDistance() {
        return distance;
    }

    public double getTime() {
        return time;
    }

    public double getCost() {
        return cost;
    }

    public boolean isTransfer() {
        return isTransfer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (fromStop == null) {
            sb.append("🚶 Yürüme: ");
        } else if (toStop == null) {
            sb.append("🚶 Yürüme: ").append(fromStop.getName()).append(" -> ");
        } else {
            sb.append(fromStop.getName()).append(" -> ");
        }

        if (toStop != null) {
            sb.append(toStop.getName());
        }

        if (vehicleType != null) {
            switch (vehicleType) {
                case "bus":
                    sb.append(" 🚌");
                    break;
                case "tram":
                    sb.append(" 🚋");
                    break;
                case "walk":
                    sb.append(" 🚶");
                    break;
                case "taxi":
                    sb.append(" 🚕");
                    break;
            }
        }

        if (isTransfer) {
            sb.append(" (Transfer)");
        }

        sb.append(String.format(" (%.1f km, %.0f dk, %.2f TL)", distance, time, cost));

        return sb.toString();
    }
}