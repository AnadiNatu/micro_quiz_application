package com.example.api_gateway.security;


import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//import java.awt.image.DataBuffer;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtGatewayFilter implements GlobalFilter {

    private final GatewayJwtUtil jwtUtil;

//    private static final List<String> PUBLIC_PATHS = List.of(
//            "/api/auth/login",
//            "/api/auth/signup",
//            "/api/auth/forgot-password",
//            "/api/auth/reset-password"
//    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // ✅ 1. Allow preflight FIRST
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // ✅ 2. Allow ALL auth endpoints
        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        // 🔒 3. Secure everything else
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing token");
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.validateToken(token);

            String role = jwtUtil.extractRole(claims);

            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header("X-User-Role", role)
                    .build();

            System.out.println("PATH: " + path);
            System.out.println("METHOD: " + exchange.getRequest().getMethod());

            return chain.filter(exchange.mutate().request(request).build());

        } catch (Exception ex) {
            return unauthorized(exchange, "Invalid token");
        }
    }
    private Mono<Void> unauthorized(ServerWebExchange exchange , String message){
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(message.getBytes());

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
