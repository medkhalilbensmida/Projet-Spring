package tn.fst.spring.projet_spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "shipping")
@Data
public class ShippingProperties {
    /** Coût fixe de base pour toute livraison */
    private double fixedCost;
    /** Tarif au kilogramme */
    private double pricePerKg;
    /** Tarif au kilomètre */
    private double pricePerKm;
    /** Default origin latitude (e.g., store location) */
    private double originLat;
    /** Default origin longitude (e.g., store location) */
    private double originLon;
}
