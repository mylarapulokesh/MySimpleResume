package com.resume.resume_gateway.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                // ACCOUNT-SERVICE ROUTES
                .route("account-login",
                        r -> r.path("/login")
                                .uri("lb://account-service"))

                .route("account-create",
                        r -> r.path("/create/account")
                                .uri("lb://account-service"))

                .route("account-get-info",
                        r -> r.path("/get/information/**")
                                .uri("lb://account-service"))

                .route("account-showcase-save",
                        r -> r.path("/save/showcase/templates")
                                .uri("lb://account-service"))

                .route("account-showcase-get",
                        r -> r.path("/get/showcase/templates")
                                .uri("lb://account-service"))
                .route("account-reset-password",
                        r -> r.path("/reset/password")
                                .uri("lb://account-service"))

                // RESUME-SERVICE ROUTES
                .route("resume-save-info",
                        r -> r.path("/save/information")
                                .uri("lb://resume-service"))

                .route("resume-generate",
                        r -> r.path("/get/resume")
                                .uri("lb://resume-service"))

                .route("resume-save-template",
                        r -> r.path("/save/template")
                                .uri("lb://resume-service"))

                .route("resume-get-template",
                        r -> r.path("/get/template/**")
                                .uri("lb://resume-service"))

                .route("resume-get-all-templates",
                        r -> r.path("/get/all/templates")
                                .uri("lb://resume-service"))

                // EMAIL-SERVICE ROUTES
                .route("email-send-welcome",
                        r -> r.path("/send/welcome/email")
                                .uri("lb://email-service"))
                .route("email-send-password-reset",
                        r -> r.path("/send/password/reset")
                                .uri("lb://account-service"))

                .build();
    }
}