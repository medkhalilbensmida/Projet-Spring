package tn.fst.spring.projet_spring.services.logistics;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tn.fst.spring.projet_spring.model.logistics.Livreur;

import java.time.Duration;

@Service
@Slf4j
public class DistanceCalculationService {

    @Value("${routing.service.provider:haversine}")
    private String routingServiceProvider;

    @Autowired
    private WebClient webClient;

    private static final String OSRM_API_URL = "https://router.project-osrm.org";

    /**
     * Calculate the distance between a livreur and a destination.
     * Uses OSRM if configured, falls back to Haversine formula.
     *
     * @param livreur The livreur with coordinates
     * @param destinationLat Destination latitude
     * @param destinationLon Destination longitude
     * @return Distance in meters
     */
    public double calculateDistance(Livreur livreur, double destinationLat, double destinationLon) {
        if (livreur.getLatitude() == null || livreur.getLongitude() == null) {
            log.warn("Livreur {} coordinates not set, cannot calculate distance.", livreur.getId());
            throw new IllegalArgumentException("Livreur coordinates not set for ID: " + livreur.getId());
        }

        double originLat = livreur.getLatitude();
        double originLon = livreur.getLongitude();

        if ("osrm".equalsIgnoreCase(routingServiceProvider)) {
            log.info("Attempting distance calculation using OSRM for Livreur: {} ({}, {}) to Dest: ({}, {})",
                    livreur.getId(), originLat, originLon, destinationLat, destinationLon);
            try {
                return calculateOsrmDistance(originLat, originLon, destinationLat, destinationLon);
            } catch (Exception e) {
                log.warn("OSRM distance calculation failed, falling back to Haversine: {}", e.getMessage());
                return calculateHaversineDistance(originLat, originLon, destinationLat, destinationLon);
            }
        } else {
            log.info("Calculating distance using Haversine formula (OSRM disabled or set to '{}') for Livreur: {} ({}, {}) to Dest: ({}, {})",
                    routingServiceProvider, livreur.getId(), originLat, originLon, destinationLat, destinationLon);
            return calculateHaversineDistance(originLat, originLon, destinationLat, destinationLon);
        }
    }

    /**
     * Calculate road distance using OSRM Route API.
     * IMPORTANT: The public OSRM API has usage limits and is not recommended for high-volume production use.
     * Consider self-hosting an OSRM instance or using a commercial provider.
     *
     * @param originLat Origin latitude
     * @param originLon Origin longitude
     * @param destLat Destination latitude
     * @param destLon Destination longitude
     * @return Distance in meters, or throws RuntimeException on failure.
     */
    private double calculateOsrmDistance(double originLat, double originLon, double destLat, double destLon) {
        String coordinates = String.format("%s,%s;%s,%s", originLon, originLat, destLon, destLat);
        String uri = String.format("/route/v1/driving/%s?overview=false&alternatives=false&steps=false&annotations=false", coordinates);

        log.debug("Calling OSRM API: {}{}", OSRM_API_URL, uri);

        try {
            Mono<JsonNode> responseMono = webClient.get()
                    .uri(OSRM_API_URL + uri)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .timeout(Duration.ofSeconds(10));

            JsonNode response = responseMono.block();

            if (response != null && response.has("routes") && response.get("routes").isArray() && response.get("routes").size() > 0) {
                JsonNode route = response.get("routes").get(0);
                if (route.has("distance")) {
                    double distanceInMeters = route.get("distance").asDouble();
                    log.info("OSRM API returned distance: {} meters for coords {}", distanceInMeters, coordinates);
                    return distanceInMeters;
                } else {
                    log.warn("OSRM response for coords {} missing 'distance' field in route: {}", coordinates, route);
                    throw new RuntimeException("OSRM response missing 'distance' field.");
                }
            } else {
                 log.warn("Invalid or empty OSRM response for coords {}: {}", coordinates, response != null ? response.toString().substring(0, Math.min(response.toString().length(), 200)) + "..." : "null");
                throw new RuntimeException("Invalid or empty OSRM response.");
            }
        } catch (Exception e) {
            log.error("Error calculating OSRM distance for coords {}: {}", coordinates, e.getMessage(), e);
            throw new RuntimeException("Failed to calculate distance using OSRM: " + e.getMessage(), e);
        }
    }

    /**
     * Calculate straight-line distance using the Haversine formula.
     *
     * @param lat1 Origin latitude
     * @param lon1 Origin longitude
     * @param lat2 Destination latitude
     * @param lon2 Destination longitude
     * @return Distance in meters
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000; // Earth radius in meters

        // Convert latitude and longitude from degrees to radians
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // Haversine formula
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceInMeters = EARTH_RADIUS * c;
        log.debug("Haversine formula calculated distance: {} meters", distanceInMeters);
        return distanceInMeters; // Distance in meters
    }
} 