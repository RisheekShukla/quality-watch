package com.qualitywatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Profile("prod")
public class ProdSecurityConfig {

    private final TelemetryApiKeyAuthFilter telemetryApiKeyAuthFilter;
    private final QualityWatchProperties properties;

    public ProdSecurityConfig(TelemetryApiKeyAuthFilter telemetryApiKeyAuthFilter, QualityWatchProperties properties) {
        this.telemetryApiKeyAuthFilter = telemetryApiKeyAuthFilter;
        this.properties = properties;
    }

    @Bean
    SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/telemetry/upload").permitAll()
                        .requestMatchers("/api/v1/telemetry/health").permitAll()
                        .requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().denyAll())
                .addFilterBefore(telemetryApiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    UserDetailsService dashboardUserDetailsService(PasswordEncoder passwordEncoder) {
        var user = User.builder()
                .username(properties.getDashboard().getUsername())
                .password(passwordEncoder.encode(properties.getDashboard().getPassword()))
                .roles("DASHBOARD")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
