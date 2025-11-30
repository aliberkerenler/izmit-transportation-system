package com.prolab.service;

import com.prolab.model.Stop;
import com.prolab.model.Connection;
import com.prolab.model.Transfer;
import com.prolab.model.Vehicle;
import com.prolab.model.Bus;
import com.prolab.model.Tram;
import com.prolab.graph.Graph;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;

public class DataLoader {
    private final ObjectMapper objectMapper;

    public DataLoader() {
        this.objectMapper = new ObjectMapper();
    }

    public Graph loadData(String filePath) throws IOException {
        Graph graph = new Graph();
        JsonNode root = objectMapper.readTree(new File(filePath));

        // İlk geçiş: Tüm durakları yükle
        for (JsonNode stopNode : root.get("duraklar")) {
            Stop stop = new Stop(
                    stopNode.get("id").asText(),
                    stopNode.get("name").asText(),
                    stopNode.get("type").asText(),
                    stopNode.get("lat").asDouble(),
                    stopNode.get("lon").asDouble()
            );
            graph.addStop(stop);
        }

        // İkinci geçiş: Bağlantıları ve transfer bilgilerini ekle
        for (JsonNode stopNode : root.get("duraklar")) {
            String stopId = stopNode.get("id").asText();
            Stop currentStop = graph.getStops().get(stopId);

            // Bağlantıları yükle
            for (JsonNode nextStopNode : stopNode.get("nextStops")) {
                Stop toStop = graph.getStops().get(nextStopNode.get("stopId").asText());
                if (toStop != null) {

                    String stopType = currentStop.getType();
                    Vehicle vehicle;
                    if (stopType.equals("bus")) {
                        vehicle = new Bus();
                    } else if (stopType.equals("tram")) {
                        vehicle = new Tram();
                    } else {
                        vehicle = null; // Diğer durumlarda varsayılan olarak null tutulabilir.
                    }
                    String vehicleType = vehicle != null ? vehicle.getType() : stopType;

                    Connection connection = new Connection(
                            currentStop,
                            toStop,
                            vehicleType,
                            nextStopNode.get("mesafe").asDouble(),
                            nextStopNode.get("sure").asDouble(),
                            nextStopNode.get("ucret").asDouble()
                    );
                    graph.addConnection(connection);
                }
            }

            // Transfer bilgilerini yükle
            JsonNode transferNode = stopNode.get("transfer");
            if (transferNode != null && !transferNode.isNull()) {
                Stop transferStop = graph.getStops().get(transferNode.get("transferStopId").asText());
                if (transferStop != null) {
                    Transfer transfer = new Transfer(
                            currentStop,
                            transferStop,
                            transferNode.get("transferSure").asDouble(),
                            transferNode.get("transferUcret").asDouble()
                    );
                    graph.addTransfer(transfer);
                }
            }
        }
        return graph;
    }

    public java.util.Map<String, Double> loadTaxiInfo(String filePath) throws IOException {
        JsonNode root = objectMapper.readTree(new File(filePath));
        JsonNode taxiNode = root.get("taxi");

        return java.util.Map.of(
                "openingFee", taxiNode.get("openingFee").asDouble(),
                "costPerKm", taxiNode.get("costPerKm").asDouble()
        );
    }
}