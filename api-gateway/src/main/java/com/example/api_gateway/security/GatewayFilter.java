package com.example.api_gateway.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import java.util.List;

@Component
@RequiredArgsConstructor
public class GatewayFilter implements GlobalFilter {

    private final GatewayJwtUtil jwtUtil;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/signup",
            "/api/auth/forgot-password",
            "/api/auth/reset-password"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (PUBLIC_PATHS.stream().anyMatch(path::contains)){
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return unauthorized(exchange , "Missing token");
        }

        try{
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.validateToken(token);

            String role = jwtUtil.extractRole(claims);

//            Attach role to header (We can add what I was think to the request at this point , need to understand what all I can add to the jwt after mutating)
            ServerHttpRequest mutateRequest = exchange.getRequest().mutate()
                    .header("X-User-Role" , role)
                    .build();

            return chain.filter(exchange.mutate().request(mutateRequest).build());
        }catch (Exception ex){
            return unauthorized(exchange , "Invalid token");
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange , String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(message.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}

