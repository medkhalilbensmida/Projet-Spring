package tn.fst.spring.projet_spring.services.logistics;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.LatLng;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.model.logistics.Livreur;

@Service
@Slf4j
public class DistanceCalculationService {

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    @Value("${assignment.use-google-maps:false}")
    private boolean useGoogleMaps;

    /**
     * Calculate the distance between a livreur and a destination.
     * Uses Google Maps Distance Matrix API if configured, falls back to Haversine formula.
     * 
     * @param livreur The livreur with coordinates
     * @param destinationLat Destination latitude
     * @param destinationLon Destination longitude
     * @return Distance in meters
     */
    public double calculateDistance(Livreur livreur, double destinationLat, double destinationLon) {
        if (livreur.getLatitude() == null || livreur.getLongitude() == null) {
            throw new IllegalArgumentException("Livreur coordinates not set");
        }

        if (useGoogleMaps) {
            log.info("Attempting distance calculation using Google Maps API for Livreur: {} to Dest: ({}, {})", livreur.getId(), destinationLat, destinationLon);
            try {
                return calculateGoogleMapsDistance(
                    livreur.getLatitude(), livreur.getLongitude(),
                    destinationLat, destinationLon
                );
            } catch (Exception e) {
                log.warn("Google Maps distance calculation failed, falling back to Haversine: {}", e.getMessage());
                // Fall back to Haversine if Google Maps fails
                return calculateHaversineDistance(
                    livreur.getLatitude(), livreur.getLongitude(),
                    destinationLat, destinationLon
                );
            }
        } else {
            log.info("Calculating distance using Haversine formula (Google Maps disabled) for Livreur: {} to Dest: ({}, {})", livreur.getId(), destinationLat, destinationLon);
            return calculateHaversineDistance(
                livreur.getLatitude(), livreur.getLongitude(),
                destinationLat, destinationLon
            );
        }
    }

    /**
     * Calculate road distance using Google Maps API.
     * 
     * @param originLat Origin latitude
     * @param originLon Origin longitude
     * @param destLat Destination latitude
     * @param destLon Destination longitude
     * @return Distance in meters
     */
    private double calculateGoogleMapsDistance(double originLat, double originLon, double destLat, double destLon) {
        try (GeoApiContext context = new GeoApiContext.Builder().apiKey(googleMapsApiKey).build()) {
            LatLng origin = new LatLng(originLat, originLon);
            LatLng destination = new LatLng(destLat, destLon);
            
            // Request the distance matrix
            DistanceMatrix result = DistanceMatrixApi.newRequest(context)
                .origins(origin)
                .destinations(destination)
                .await();

            if (result.rows.length > 0 && result.rows[0].elements.length > 0) {
                DistanceMatrixElement element = result.rows[0].elements[0];
                if (element.distance != null) {
                    double distanceInMeters = element.distance.inMeters;
                    log.info("Google Maps API returned distance: {} meters", distanceInMeters);
                    return distanceInMeters;
                }
            }
            
            log.warn("No valid distance result obtained from Google Maps API for origin ({}, {}) to dest ({}, {})", originLat, originLon, destLat, destLon);
            throw new RuntimeException("No valid distance result from Google Maps API");
        } catch (Exception e) {
            log.error("Error calculating Google Maps distance: {}", e.getMessage());
            throw new RuntimeException("Failed to calculate distance using Google Maps", e);
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
        log.info("Haversine formula calculated distance: {} meters", distanceInMeters);
        return distanceInMeters; // Distance in meters
    }
} 