package com.foodiestrip.config;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 오리진 설정
        config.addAllowedOrigin("http://localhost:5173"); // React 앱의 주소
        config.addAllowedOrigin("https://foodiestrip.com"); // 프로덕션 도메인

        // 허용할 헤더 설정
        config.addAllowedHeader("*");

        // 허용할 HTTP 메서드 설정
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");

        // 인증 정보 허용 설정
        config.setAllowCredentials(true);

        // 모든 경로에 이 설정 적용
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
