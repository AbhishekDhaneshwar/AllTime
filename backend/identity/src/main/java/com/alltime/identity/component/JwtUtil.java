package com.alltime.identity.component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationTime;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expirationTime) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    public String generateToken(String phoneNumber) {
        Date now = new Date();
        Date expDate = new Date(now.getTime() + expirationTime);
        return Jwts.builder()
                .subject(phoneNumber)
                .issuedAt(now)
                .expiration(expDate)
                .signWith(secretKey)
                .compact();

    }

}
