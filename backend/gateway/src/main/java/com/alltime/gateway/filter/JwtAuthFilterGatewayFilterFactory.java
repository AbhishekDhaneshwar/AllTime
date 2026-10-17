package com.alltime.gateway.filter;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component("jwtAuthFilter") // 🚨 Force the exact bean name your YAML expects
public class JwtAuthFilterGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtAuthFilterGatewayFilterFactory.Config> {

    @Value("${jwt.secret}")
    private String secretKeyString;

    // Explicitly reference the exact inner static class type
    public JwtAuthFilterGatewayFilterFactory() {
        super(JwtAuthFilterGatewayFilterFactory.Config.class);
    }

    // Ensure a completely empty public static class with an open default
    // constructor
    public static class Config {
        public Config() {
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            org.springframework.http.server.reactive.ServerHttpRequest request = exchange.getRequest();

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.regionMatches(true, 0, "bearer ", 0, 7)) {
                return handleUnauthorized(exchange, "Missing or Malformed Authorization Header");
            }

            String token = authHeader.substring(7);

            try {
                SecretKey key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                org.springframework.http.server.reactive.ServerHttpRequest mutatedRequest = exchange.getRequest()
                        .mutate()
                        .header("X-Authenticated-User", claims.getSubject())
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                return handleUnauthorized(exchange, "Invalid or expired token execution");
            }
        };
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String logMessage) {
        org.springframework.http.server.reactive.ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("X-Gateway-Auth-Error", logMessage);
        return response.setComplete();
    }
}
