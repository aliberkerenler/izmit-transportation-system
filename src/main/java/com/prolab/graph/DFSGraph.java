package com.prolab.graph;

import com.prolab.model.Stop;
import com.prolab.model.Route;
import com.prolab.model.RouteSegment;
import com.prolab.model.Passenger;
import com.prolab.model.Connection;
import java.util.*;

public class DFSGraph extends Graph {
    private Set<String> visited;

    public DFSGraph(Graph graph) {
        super();
        this.stops = graph.stops;
        this.connections = graph.connections;
        this.transfers = graph.transfers;
        this.visited = new HashSet<>();
    }

    @Override
    public List<Route> findRoutes(Stop startStop, Stop endStop, Passenger passenger) {
        visited.clear();
        List<Route> routes = new ArrayList<>();
        List<Stop> currentPath = new ArrayList<>();
        currentPath.add(startStop);
        visited.add(startStop.getId());

        dfs(startStop, endStop, passenger, currentPath, routes);

        return routes;
    }

    private void dfs(Stop currentStop, Stop endStop, Passenger passenger,
                     List<Stop> currentPath, List<Route> routes) {
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
                    dfs(nextStop, endStop, passenger, currentPath, routes);
                    currentPath.remove(currentPath.size() - 1);
                    visited.remove(nextStop.getId());
                }
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