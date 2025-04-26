package com.example.technestbe.util.usermanagement;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret:default-secret-key-for-testing-purposes-only}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key())
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT token is expired: " + e.getMessage());
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid JWT token format: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("JWT validation failed: " + e.getMessage());
        }
    }

    private javax.crypto.SecretKey key() {
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes());
    }
}