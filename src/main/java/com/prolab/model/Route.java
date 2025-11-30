package com.prolab.model;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private final List<RouteSegment> segments;
    private double totalCost;
    private double totalDistance;
    private int totalTime;
    private double totalDiscount;

    public Route() {
        this.segments = new ArrayList<>();
        this.totalDistance = 0.0;
        this.totalTime = 0;
        this.totalCost = 0.0;
        this.totalDiscount = 0.0;
    }

    private void updateTotalValues() {
        totalDistance = 0.0;
        totalTime = 0;
        totalCost = 0.0;

        for (RouteSegment segment : segments) {
            totalDistance += segment.getDistance();
            totalTime += segment.getTime();
            totalCost += segment.getCost();
        }
    }

    public void addSegment(RouteSegment segment) {
        segments.add(segment);
        updateTotalValues();
    }

    public void addSegment(int index, RouteSegment segment) {
        segments.add(index, segment);
        updateTotalValues();
    }

    public void addAllSegments(List<RouteSegment> newSegments) {
        segments.addAll(newSegments);
        updateTotalValues();
    }

    public List<RouteSegment> getSegments() {
        return segments;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public double getTotalCost() {
        return totalCost - totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Rota segmentleri
        for (int i = 0; i < segments.size(); i++) {
            RouteSegment segment = segments.get(i);
            String vehicleType = segment.getType();
            String vehicleEmoji;
            
            switch (vehicleType) {
                case "bus":
                    vehicleEmoji = "🚌";
                    break;
                case "tram":
                    vehicleEmoji = "🚋";
                    break;
                case "taxi":
                    vehicleEmoji = "🚕";
                    break;
                case "transfer":
                    vehicleEmoji = "🔄";
                    break;
                default:
                    vehicleEmoji = "🚶";
            }
            
            // Taksi rotası için özel format
            if (vehicleType.equals("taxi")) {
                if (segment.getFromStop() == null) {
                    sb.append(String.format("%d️⃣ Başlangıç Noktası %s %s", 
                        i + 1,
                        vehicleEmoji,
                        segment.getToStop().getName()));
                } else if (segment.getToStop() == null) {
                    sb.append(String.format("%d️⃣ %s %s Varış Noktası", 
                        i + 1,
                        segment.getFromStop().getName(),
                        vehicleEmoji));
                }
            } else {
                // Normal rotalar için format
                Stop fromStop = segment.getFromStop();
                Stop toStop = segment.getToStop();
                
                if (fromStop == null) {
                    sb.append(String.format("%d️⃣ Başlangıç Noktası %s %s", 
                        i + 1,
                        vehicleEmoji,
                        toStop.getName()));
                } else if (toStop == null) {
                    sb.append(String.format("%d️⃣ %s %s Varış Noktası", 
                        i + 1,
                        fromStop.getName(),
                        vehicleEmoji));
                } else {
                    sb.append(String.format("%d️⃣ %s %s %s", 
                        i + 1,
                        fromStop.getName(),
                        vehicleEmoji,
                        toStop.getName()));
                }
            }
            
            if (segment.isTransfer()) {
                sb.append(" (Transfer)");
            }
            
            sb.append(String.format(" (%.1f km, %.0f dk, %.2f TL)\n",
                segment.getDistance(),
                segment.getTime(),
                segment.getCost()));
        }
        
        // Toplam bilgiler
        sb.append("\n📊 Toplam:\n");
        sb.append("● 💰 Ücret: ").append(String.format("%.2f TL\n", getTotalCost()));
        sb.append("● ⏳ Süre: ").append(getTotalTime()).append(" dk\n");
        sb.append("● 📏 Mesafe: ").append(String.format("%.2f km", getTotalDistance()));
        
        return sb.toString();
    }
} 