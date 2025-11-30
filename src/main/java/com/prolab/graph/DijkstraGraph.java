package com.prolab.graph;

import com.prolab.model.Stop;
import com.prolab.model.Route;
import com.prolab.model.RouteSegment;
import com.prolab.model.Passenger;
import com.prolab.model.Connection;
import com.prolab.model.Transfer;
import java.util.*;

public class DijkstraGraph extends Graph {
    private final Map<String, Double> distances;
    private final Map<String, Stop> previousStops;
    private final PriorityQueue<Stop> queue;
    private final Set<String> visited;

    public DijkstraGraph(Graph graph) {
        super();
        this.stops = graph.stops;
        this.connections = graph.connections;
        this.transfers = graph.transfers;
        this.distances = new HashMap<>();
        this.previousStops = new HashMap<>();
        this.visited = new HashSet<>();
        this.queue = new PriorityQueue<>((a, b) ->
                Double.compare(distances.getOrDefault(a.getId(), Double.MAX_VALUE),
                        distances.getOrDefault(b.getId(), Double.MAX_VALUE)));
    }

    @Override
    public List<Route> findRoutes(Stop startStop, Stop endStop, Passenger passenger) {
        List<Route> routes = new ArrayList<>();
        visited.clear();

        // İlk rota için Dijkstra algoritmasıyla en kısa yolu bul
        findShortestPath(startStop, endStop);
        Route shortestRoute = buildRoute(startStop, endStop, passenger);
        if (shortestRoute != null && !shortestRoute.getSegments().isEmpty()) {
            routes.add(shortestRoute);
        }

        // Alternatif rotaları bul
        findAlternativeRoutes(startStop, endStop, passenger, routes);

        return routes;
    }

    private void findShortestPath(Stop startStop, Stop endStop) {
        distances.clear();
        previousStops.clear();
        queue.clear();

        for (Stop stop : stops.values()) {
            distances.put(stop.getId(), Double.MAX_VALUE);
            previousStops.put(stop.getId(), null);
        }

        distances.put(startStop.getId(), 0.0);
        queue.offer(startStop);

        while (!queue.isEmpty()) {
            Stop currentStop = queue.poll();

            if (currentStop.equals(endStop)) {
                break;
            }

            for (Connection connection : connections.get(currentStop.getId())) {
                Stop nextStop = connection.getToStop();
                double newDistance = distances.get(currentStop.getId()) + connection.getDistance();

                if (newDistance < distances.get(nextStop.getId())) {
                    distances.put(nextStop.getId(), newDistance);
                    previousStops.put(nextStop.getId(), currentStop);
                    queue.offer(nextStop);
                }
            }

            for (Transfer transfer : transfers.get(currentStop.getId())) {
                Stop nextStop = transfer.getToStop();
                double newDistance = distances.get(currentStop.getId()) + transfer.getDuration();

                if (newDistance < distances.get(nextStop.getId())) {
                    distances.put(nextStop.getId(), newDistance);
                    previousStops.put(nextStop.getId(), currentStop);
                    queue.offer(nextStop);
                }
            }
        }
    }

    private void findAlternativeRoutes(Stop startStop, Stop endStop, Passenger passenger, List<Route> routes) {
        double shortestCost = distances.get(endStop.getId());
        if (shortestCost == Double.MAX_VALUE) {
            return;
        }

        for (Stop stop : stops.values()) {
            if (stop.equals(startStop) || stop.equals(endStop)) {
                continue;
            }

            visited.add(stop.getId());
            findShortestPath(startStop, stop);
            Route firstPart = buildRoute(startStop, stop, passenger);

            findShortestPath(stop, endStop);
            Route secondPart = buildRoute(stop, endStop, passenger);

            if (firstPart != null && secondPart != null &&
                    !firstPart.getSegments().isEmpty() && !secondPart.getSegments().isEmpty()) {
                double totalCost = firstPart.getTotalCost() + secondPart.getTotalCost();
                if (totalCost <= shortestCost * 1.2) {
                    Route alternativeRoute = new Route();
                    alternativeRoute.addAllSegments(firstPart.getSegments());
                    alternativeRoute.addAllSegments(secondPart.getSegments());
                    routes.add(alternativeRoute);
                }
            }
            visited.remove(stop.getId());
        }
    }

    private Route buildRoute(Stop startStop, Stop endStop, Passenger passenger) {
        List<Stop> path = new ArrayList<>();
        Stop currentStop = endStop;
        while (currentStop != null) {
            path.add(0, currentStop);
            currentStop = previousStops.get(currentStop.getId());
        }

        if (path.isEmpty() || !path.get(0).equals(startStop)) {
            return null;
        }

        Route route = new Route();
        for (int i = 0; i < path.size() - 1; i++) {
            Stop fromStop = path.get(i);
            Stop toStop = path.get(i + 1);

            Connection connection = findConnection(fromStop, toStop);
            if (connection != null) {
                RouteSegment segment = new RouteSegment(
                        fromStop, toStop, connection.getVehicleType(),
                        connection.getDistance(),
                        connection.getDuration(),
                        passenger.calculateDiscountedCost(connection.getFare()),
                        false
                );
                route.addSegment(segment);
            } else {
                Transfer transfer = findTransfer(fromStop, toStop);
                if (transfer != null) {
                    RouteSegment segment = new RouteSegment(
                            fromStop, toStop, "transfer",
                            0.0,
                            transfer.getDuration(),
                            passenger.calculateDiscountedCost(transfer.getCost()),
                            true
                    );
                    route.addSegment(segment);
                }
            }
        }
        return route;
    }

    private Connection findConnection(Stop fromStop, Stop toStop) {
        for (Connection connection : connections.get(fromStop.getId())) {
            if (connection.getToStop().equals(toStop)) {
                return connection;
            }
        }
        return null;
    }

    private Transfer findTransfer(Stop fromStop, Stop toStop) {
        for (Transfer transfer : transfers.get(fromStop.getId())) {
            if (transfer.getToStop().equals(toStop)) {
                return transfer;
            }
        }
        return null;
    }
}