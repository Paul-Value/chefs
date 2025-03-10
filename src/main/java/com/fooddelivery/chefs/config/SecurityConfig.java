package com.fooddelivery.chefs.config;

import com.fooddelivery.chefs.security.ChefAccessCodeFilter;
import com.fooddelivery.chefs.security.DeviceIdFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private DeviceIdFilter deviceIdFilter;

    @Autowired
    private ChefAccessCodeFilter chefAccessCodeFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Эндпоинты для поваров
                        .requestMatchers("/chefs/**").hasRole("CHEF")
                        // Эндпоинты для заказчиков
                        .requestMatchers("/addresses/**", "/orders/**", "/users/**").hasRole("CUSTOMER")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(chefAccessCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(deviceIdFilter, ChefAccessCodeFilter.class);

        return http.build();
    }
}
