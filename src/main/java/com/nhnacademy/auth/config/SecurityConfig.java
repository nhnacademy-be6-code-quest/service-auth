package com.nhnacademy.auth.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final DiscoveryClient discoveryClient;

    private String gatewayIp;
    private int gatewayPort;

    @PostConstruct
    public void init() {
        List<ServiceInstance> instances = discoveryClient.getInstances("gateway");
        if (instances != null && !instances.isEmpty()) {
            ServiceInstance instance = instances.getFirst();
            gatewayIp = instance.getHost();
            gatewayPort = instance.getPort();
        }
        log.info("Gateway IP: " + gatewayIp);
        log.info("Gateway Port: " + gatewayPort);
    }

    @Bean
    @SuppressWarnings("java:S4502") // Be sure to disable csrf
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req.anyRequest().access((authentication, context) -> {
                            String remoteAddr = context.getRequest().getRemoteAddr();
                            int remotePort = context.getRequest().getRemotePort();
                            boolean granted = remoteAddr.equals(gatewayIp) && gatewayPort == remotePort;

                            if (!granted) {
                                log.warn("Remote address {} not granted", remoteAddr);
                            }
                            return new AuthorizationDecision(granted);
                        })
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
