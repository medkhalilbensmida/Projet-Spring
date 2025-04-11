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
        try {
            return GoogleAdsClient.newBuilder().fromPropertiesFile().build();
        } catch (Exception e) {
            throw new RuntimeException("Google Ads Client initialization failed.", e);
        }
    }
}