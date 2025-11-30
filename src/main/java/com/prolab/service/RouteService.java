package com.prolab.service;

import com.prolab.graph.Graph;
import com.prolab.model.Passenger;
import com.prolab.model.Route;
import com.prolab.model.RouteSegment;
import com.prolab.model.Stop;
import com.prolab.factory.TaxiFactory;
import com.prolab.factory.DefaultTaxiFactory;
import com.prolab.model.Taxi;
import java.util.*;
import java.util.stream.Collectors;

public class RouteService {
    private final Graph graph;
    private final TaxiFactory taxiFactory;
    private static final double MAX_WALKING_DISTANCE = 3.0; // km

    public RouteService(Graph graph, double taxiOpeningFee, double costPerKm) {
        this.graph = graph;
        // taxiFactory kullanılıyor
        this.taxiFactory = new DefaultTaxiFactory(taxiOpeningFee, costPerKm);
    }

    public Map<String, Route> findOptimalRoutes(double startLat, double startLon, double endLat, double endLon, Passenger passenger) {
        List<Route> candidateRoutes = new ArrayList<>();
        Stop endStop = graph.findNearestStop(endLat, endLon);
        if (endStop == null) {
            System.out.println("❌ Hedef durağı bulunamadı!");
            return Collections.emptyMap();
        }
        System.out.println("📍 Bitiş için en yakın durak: " + endStop.getName());

        candidateRoutes.addAll(findRoutesWithWalking(startLat, startLon, endStop, passenger));
        candidateRoutes.addAll(findRoutesWithTaxi(startLat, startLon, endStop, passenger));

        double endWalkingDistance = graph.calculateDistance(endLat, endLon, endStop.getLatitude(), endStop.getLongitude());
        for (Route route : candidateRoutes) {
            if (endWalkingDistance <= MAX_WALKING_DISTANCE) {
                route.addSegment(new RouteSegment(endStop, null, "walk",
                        endWalkingDistance, endWalkingDistance * 20,
                        0.0, false));
            } else {
                Taxi taxi = taxiFactory.createTaxi();
                double taxiCostEnd = taxi.calculateFare(endWalkingDistance);
                double taxiTimeEnd = (endWalkingDistance / 50.0) * 60;
                route.addSegment(new RouteSegment(endStop, null, "taxi",
                        endWalkingDistance, taxiTimeEnd,
                        passenger.calculateDiscountedCost(taxiCostEnd), false));
            }
            applyTransferDiscount(route);
        }

        candidateRoutes.sort(Comparator.comparingDouble(Route::getTotalCost));
        return getOptimalRoutesByCategory(candidateRoutes);
    }

    private List<Route> findRoutesWithWalking(double startLat, double startLon, Stop endStop, Passenger passenger) {
        List<Route> candidateRoutes = new ArrayList<>();
        Stop walkingStartStop = graph.findNearestStop(startLat, startLon);
        double startWalkingDistance = graph.calculateDistance(startLat, startLon, walkingStartStop.getLatitude(), walkingStartStop.getLongitude());
        System.out.println("📏 Başlangıç noktasına en yakın durak: " + walkingStartStop.getName());

        if (startWalkingDistance <= MAX_WALKING_DISTANCE) {
            List<Route> publicRoutes = graph.findRoutes(walkingStartStop, endStop, passenger);
            for (Route route : publicRoutes) {
                route.addSegment(0, new RouteSegment(null, walkingStartStop, "walk",
                        startWalkingDistance, startWalkingDistance * 20,
                        0.0, false));
                candidateRoutes.add(route);
            }
        }
        return candidateRoutes;
    }

    private List<Route> findRoutesWithTaxi(double startLat, double startLon, Stop endStop, Passenger passenger) {
        List<Route> candidateRoutes = new ArrayList<>();
        Collection<Stop> allStops = graph.getStops().values();
        List<Stop> sortedStops = allStops.stream()
                .sorted(Comparator.comparingDouble(stop -> graph.calculateDistance(startLat, startLon, stop.getLatitude(), stop.getLongitude())))
                .collect(Collectors.toList());

        boolean candidateFound = false;
        for (Stop candidateStop : sortedStops) {
            double distanceToCandidate = graph.calculateDistance(startLat, startLon, candidateStop.getLatitude(), candidateStop.getLongitude());
            if (distanceToCandidate <= MAX_WALKING_DISTANCE) {
                continue;
            }
            List<Route> publicRoutesFromCandidate = graph.findRoutes(candidateStop, endStop, passenger);
            if (publicRoutesFromCandidate.isEmpty()) {
                continue;
            }
            for (Route route : publicRoutesFromCandidate) {
                Taxi taxi = taxiFactory.createTaxi();
                double taxiCost = taxi.calculateFare(distanceToCandidate);
                double taxiTime = (distanceToCandidate / 50.0) * 60;
                route.addSegment(0, new RouteSegment(null, candidateStop, "taxi",
                        distanceToCandidate, taxiTime,
                        passenger.calculateDiscountedCost(taxiCost), false));
                candidateRoutes.add(route);
            }
            candidateFound = true;
            break;
        }
        if (!candidateFound) {
            System.out.println("⚠ Hiçbir toplu taşıma rotası bulunamadı, sadece taksi rotası oluşturuluyor.");
            double taxiDistance = graph.calculateDistance(startLat, startLon, endStop.getLatitude(), endStop.getLongitude());
            Taxi taxi = taxiFactory.createTaxi();
            double taxiCost = taxi.calculateFare(taxiDistance);
            double taxiTime = (taxiDistance / 50.0) * 60;
            Route taxiRoute = new Route();
            taxiRoute.addSegment(new RouteSegment(null, endStop, "taxi",
                    taxiDistance, taxiTime,
                    passenger.calculateDiscountedCost(taxiCost), false));
            candidateRoutes.add(taxiRoute);
        }
        return candidateRoutes;
    }

    public Map<String, Route> getOptimalRoutesByCategory(List<Route> candidateRoutes) {
        Map<String, Route> optimalRoutes = new HashMap<>();
        for (Route route : candidateRoutes) {
            String category = classifyRoute(route);
            if (!optimalRoutes.containsKey(category) ||
                    route.getTotalTime() < optimalRoutes.get(category).getTotalTime()) {
                optimalRoutes.put(category, route);
            }
        }
        return optimalRoutes;
    }

    private String classifyRoute(Route route) {
        boolean hasBus = false;
        boolean hasTram = false;
        boolean hasTaxi = false;
        for (RouteSegment segment : route.getSegments()) {
            String type = segment.getType();
            if ("bus".equals(type)) {
                hasBus = true;
            } else if ("tram".equals(type)) {
                hasTram = true;
            } else if ("taxi".equals(type)) {
                hasTaxi = true;
            }
        }
        if (hasTaxi && (hasBus || hasTram))
            return "taksi_toplu";
        else if (hasTaxi)
            return "sadece_taksi";
        else if (hasBus && hasTram)
            return "otobus_tramvay";
        else if (hasBus)
            return "sadece_otobus";
        else if (hasTram)
            return "sadece_tramvay";
        else
            return "diğer";
    }

    private void applyTransferDiscount(Route route) {
        double discount = 0.0;
        for (int i = 0; i < route.getSegments().size() - 1; i++) {
            RouteSegment seg1 = route.getSegments().get(i);
            RouteSegment seg2 = route.getSegments().get(i + 1);
            if ("bus".equals(seg1.getType()) && "tram".equals(seg2.getType())) {
                discount += 0.10 * (seg1.getCost() + seg2.getCost());
            }
        }
        route.setTotalDiscount(discount);
    }

    public List<Route> findRoutes(double startLat, double startLon, double endLat, double endLon, Passenger passenger) {
        return new ArrayList<>(findOptimalRoutes(startLat, startLon, endLat, endLon, passenger).values());
    }
}