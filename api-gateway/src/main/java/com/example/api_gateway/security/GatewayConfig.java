package com.example.api_gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

public class GatewayConfig {

    // This file is intentionally left as a no-op.
// CORS is handled entirely by spring.cloud.gateway.globalcors in application.yml.
// Having a CorsWebFilter bean here AND globalcors in YAML causes a conflict
// where both try to write CORS headers and the response ends up with
// duplicated or missing headers — resulting in the browser CORS error.
//
// Do NOT add @Configuration or any @Bean here.
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of("http://localhost:4200"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setExposedHeaders(List.of("Authorization"));
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsWebFilter(source);
//    }

}
