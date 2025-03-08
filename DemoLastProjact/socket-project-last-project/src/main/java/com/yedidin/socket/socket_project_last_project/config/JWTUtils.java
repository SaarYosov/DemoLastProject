package com.yedidin.socket.socket_project_last_project.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

public class JWTUtils {
    private final SecretKey secretKey;
    private final long expirationTime = 864_000_000; // 10 days

    public JWTUtils() {
        // Use a fixed secret key instead of a random one
        String base64Key = "b8f5bd5e87d345fca22a3b1c1e9df73c9e8d5a7b3f1e0c4a6d8b2e0f1a3c5d7";
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    }


    // יצירת JWT
    public String generateToken(String email, String role) {
        // Ensure role has correct format without ROLE_ prefix (will be added by filter)
        String cleanRole = role;
        if (role.startsWith("ROLE_")) {
            cleanRole = role.substring(5);
        }

        return Jwts.builder()
                .setSubject(email)
                .claim("role", cleanRole)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String extractByEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // אימות ופריסת JWT
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    // לקבלת שם המשתמש מתוך ה-JWT
    public String getUsernameFromToken(String token) {
        return validateToken(token).getSubject();
    }

    // לקבלת התפקיד מתוך ה-JWT
    public String getRoleFromToken(String token) {
        return (String) validateToken(token).get("role");
    }

    // בדיקה אם ה-JWT עדיין תקף
    public boolean isTokenExpired(String token) {
        try {
            return validateToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}
