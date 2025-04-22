package tn.fst.spring.projet_spring.exception;

/**
 * Exception thrown when geocoding fails (e.g., address not found, API error).
 */
public class GeocodingException extends RuntimeException {

    public GeocodingException(String message) {
        super(message);
    }

    public GeocodingException(String message, Throwable cause) {
        super(message, cause);
    }
} 