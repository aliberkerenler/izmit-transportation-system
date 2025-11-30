package com.prolab.graph;

import com.prolab.model.Stop;
import com.prolab.model.Route;
import com.prolab.model.RouteSegment;
import com.prolab.model.Passenger;
import com.prolab.model.Transfer;
import com.prolab.model.Connection;
import com.prolab.strategy.DistanceCalculator;
import com.prolab.strategy.HaversineCalculator;
import java.util.*;

public class Graph {
    protected Map<String, Stop> stops;
    protected Map<String, List<Connection>> connections;
    protected Map<String, List<Transfer>> transfers;
    private DistanceCalculator distanceCalculator;

    public Graph() {
        this.stops = new HashMap<>();
        this.connections = new HashMap<>();
        this.transfers = new HashMap<>();
        // Varsayılan mesafe hesaplayıcısını kullanıyoruz.
        this.distanceCalculator = new HaversineCalculator();
    }

    // Alternatif: dışarıdan da ekleyebilirsiniz.
    public Graph(DistanceCalculator calculator) {
        this();
        this.distanceCalculator = calculator;
    }

    public void addStop(Stop stop) {
        stops.put(stop.getId(), stop);
        connections.put(stop.getId(), new ArrayList<>());
        transfers.put(stop.getId(), new ArrayList<>());
    }

    public void addConnection(Connection connection) {
        connections.get(connection.getFromStop().getId()).add(connection);
    }

    public void addTransfer(Transfer transfer) {
        transfers.get(transfer.getFromStop().getId()).add(transfer);
        transfer.getFromStop().setTransfer(transfer);
    }

    public Map<String, Stop> getStops() {
        return stops;
    }

    public Stop findNearestStop(double latitude, double longitude) {
        Stop nearestStop = null;
        double minDistance = Double.MAX_VALUE;
        for (Stop stop : stops.values()) {
            double distance = calculateDistance(latitude, longitude, stop.getLatitude(), stop.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearestStop = stop;
            }
        }
        return nearestStop;
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        return distanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
    }

    // Template Method pattern özelliğine benzer şekilde, rota bulmanın algoritma iskeletini tanımlıyoruz.
    public List<Route> findRoutes(Stop startStop, Stop endStop, Passenger passenger) {
        List<Route> allRoutes = new ArrayList<>();

        List<Route> busRoutes = findBusOnlyRoutes(startStop, endStop, passenger);
        allRoutes.addAll(busRoutes);

        List<Route> tramRoutes = findTramOnlyRoutes(startStop, endStop, passenger);
        allRoutes.addAll(tramRoutes);

        findTransferRoutes(startStop, endStop, passenger, allRoutes);

        return allRoutes;
    }

    private List<Route> findBusOnlyRoutes(Stop startStop, Stop endStop, Passenger passenger) {
        List<Route> routes = new ArrayList<>();
        Set<String> uniqueRoutes = new HashSet<>();
        Set<String> visited = new HashSet<>();
        List<Stop> currentPath = new ArrayList<>();
        currentPath.add(startStop);
        visited.add(startStop.getId());

        findBusRoutesDFS(startStop, endStop, passenger, visited, currentPath, routes, uniqueRoutes);
        return routes;
    }

    private void findBusRoutesDFS(Stop currentStop, Stop endStop, Passenger passenger,
                                  Set<String> visited, List<Stop> currentPath, List<Route> routes,
                                  Set<String> uniqueRoutes) {
        if (currentStop.equals(endStop)) {
            Route route = createRoute(currentPath, passenger);
            if (route != null && !route.getSegments().isEmpty()) {
                String routeKey = route.toString();
                if (!uniqueRoutes.contains(routeKey)) {
                    routes.add(route);
                    uniqueRoutes.add(routeKey);
                }
            }
            return;
        }
        List<Connection> connections = this.connections.get(currentStop.getId());
        if (connections != null) {
            for (Connection connection : connections) {
                Stop nextStop = connection.getToStop();
                if (!visited.contains(nextStop.getId())) {
                    visited.add(nextStop.getId());
                    currentPath.add(nextStop);
                    findBusRoutesDFS(nextStop, endStop, passenger, visited, currentPath, routes, uniqueRoutes);
                    currentPath.remove(currentPath.size() - 1);
                    visited.remove(nextStop.getId());
                }
            }
        }
    }

    private List<Route> findTramOnlyRoutes(Stop startStop, Stop endStop, Passenger passenger) {
        List<Route> routes = new ArrayList<>();
        Set<String> uniqueRoutes = new HashSet<>();
        Set<String> visited = new HashSet<>();
        List<Stop> currentPath = new ArrayList<>();
        currentPath.add(startStop);
        visited.add(startStop.getId());

        findTramRoutesDFS(startStop, endStop, passenger, visited, currentPath, routes, uniqueRoutes);
        return routes;
    }

    private void findTramRoutesDFS(Stop currentStop, Stop endStop, Passenger passenger,
                                   Set<String> visited, List<Stop> currentPath, List<Route> routes,
                                   Set<String> uniqueRoutes) {
        if (currentStop.equals(endStop)) {
            Route route = createRoute(currentPath, passenger);
            if (route != null) {
                String routeKey = route.toString();
                if (!uniqueRoutes.contains(routeKey)) {
                    routes.add(route);
                    uniqueRoutes.add(routeKey);
                }
            }
            return;
        }
        List<Connection> connections = this.connections.get(currentStop.getId());
        if (connections != null) {
            for (Connection connection : connections) {
                Stop nextStop = connection.getToStop();
                if (!visited.contains(nextStop.getId()) && nextStop.getType().equals("tram")) {
                    visited.add(nextStop.getId());
                    currentPath.add(nextStop);
                    findTramRoutesDFS(nextStop, endStop, passenger, visited, currentPath, routes, uniqueRoutes);
                    currentPath.remove(currentPath.size() - 1);
                    visited.remove(nextStop.getId());
                }
            }
        }
    }

    private void findTransferRoutes(Stop startStop, Stop endStop, Passenger passenger, List<Route> routes) {
        if (startStop.getTransfer() != null) {
            Transfer transfer = startStop.getTransfer();
            Stop transferStop = stops.get(transfer.getTransferStopId());
            if (transferStop != null) {
                List<Route> firstSegment = new ArrayList<>();
                Set<String> visited = new HashSet<>();
                List<Stop> currentPath = new ArrayList<>();
                currentPath.add(startStop);
                visited.add(startStop.getId());
                findRoutesDFS(startStop, transferStop, passenger, visited, currentPath, firstSegment);

                List<Route> secondSegment = new ArrayList<>();
                visited.clear();
                currentPath.clear();
                currentPath.add(transferStop);
                visited.add(transferStop.getId());
                findRoutesDFS(transferStop, endStop, passenger, visited, currentPath, secondSegment);

                if (!firstSegment.isEmpty() && !secondSegment.isEmpty()) {
                    for (Route first : firstSegment) {
                        for (Route second : secondSegment) {
                            Route combined = new Route();
                            combined.addAllSegments(first.getSegments());
                            combined.addAllSegments(second.getSegments());
                            routes.add(combined);
                        }
                    }
                }
            }
        }
    }

    private void findRoutesDFS(Stop currentStop, Stop endStop, Passenger passenger,
                               Set<String> visited, List<Stop> currentPath, List<Route> routes) {
        if (currentStop.equals(endStop)) {
            Route route = createRoute(currentPath, passenger);
            if (route != null) {
                routes.add(route);
            }
            return;
        }
        List<Connection> connections = this.connections.get(currentStop.getId());
        if (connections != null) {
            for (Connection connection : connections) {
                Stop nextStop = connection.getToStop();
                if (!visited.contains(nextStop.getId())) {
                    visited.add(nextStop.getId());
                    currentPath.add(nextStop);
                    findRoutesDFS(nextStop, endStop, passenger, visited, currentPath, routes);
                    currentPath.remove(currentPath.size() - 1);
                    visited.remove(nextStop.getId());
                }
            }
        }
        if (currentStop.getTransfer() != null) {
            Stop transferStop = stops.get(currentStop.getTransfer().getTransferStopId());
            if (transferStop != null && !visited.contains(transferStop.getId())) {
                visited.add(transferStop.getId());
                currentPath.add(transferStop);
                findRoutesDFS(transferStop, endStop, passenger, visited, currentPath, routes);
                currentPath.remove(currentPath.size() - 1);
                visited.remove(transferStop.getId());
            }
        }
    }

    private Route createRoute(List<Stop> path, Passenger passenger) {
        if (path.size() < 2) {
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
                Transfer transfer = fromStop.getTransfer();
                if (transfer != null && transfer.getTransferStopId().equals(toStop.getId())) {
                    RouteSegment segment = new RouteSegment(
                            fromStop, toStop, "transfer",
                            0.0,
                            transfer.getTransferDuration(),
                            passenger.calculateDiscountedCost(transfer.getTransferFare()),
                            true
                    );
                    route.addSegment(segment);
                } else {
                    return null;
                }
            }
        }
        return route;
    }

    private Connection findConnection(Stop fromStop, Stop toStop) {
        List<Connection> conns = this.connections.get(fromStop.getId());
        if (conns != null) {
            for (Connection connection : conns) {
                if (connection.getToStop().equals(toStop)) {
                    return connection;
                }
            }
        }
        return null;
    }
}