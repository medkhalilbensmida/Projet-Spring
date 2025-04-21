package tn.fst.spring.projet_spring.config;

import com.google.ads.googleads.lib.GoogleAdsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class GoogleAdsConfig {

    @Bean
    public GoogleAdsClient googleAdsClient() {
        try (InputStream propertiesStream = getClass().getClassLoader().getResourceAsStream("ads.properties")) {
            if (propertiesStream == null) {
                throw new IOException("ads.properties not found in classpath.");
            }
            Properties adsProperties = new Properties();
            adsProperties.load(propertiesStream);

            GoogleAdsClient client = GoogleAdsClient.newBuilder()
                    .fromProperties(adsProperties)
                    .build();

            System.out.println("Google Ads Client initialized successfully.");
            return client;
                    
        } catch (IOException e) {
            throw new RuntimeException("Google Ads Client initialization failed.", e);
        }
    }
}
