package cl.oursour.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("auth", r -> r.path("/api/auth/**").uri("lb://ms-auth"))
            .route("catalogo", r -> r.path("/api/catalogo/**").uri("lb://ms-catalogo"))
            .route("pedidos", r -> r.path("/api/pedidos/**").uri("lb://ms-pedidos"))
            .build();
    }
}
