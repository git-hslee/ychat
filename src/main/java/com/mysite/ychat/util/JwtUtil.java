package com.mysite.ychat.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "yourSecretKeyyourSecretKeyyourSecretKeyyourSecretKey"; // 🔹 최소 32바이트 필요
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 6; // 🔹 6시간 유효

    // 🔹 HMAC SHA-256 키 생성
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 🔹 JWT 생성 (사용자 ID 기반)
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId) // 🔹 사용자 ID 설정
                .setIssuedAt(new Date()) // 🔹 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 🔹 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 🔹 서명 알고리즘 적용
                .compact();
    }

    // 🔹 JWT에서 클레임(사용자 정보) 추출
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // 🔹 서명 검증 키 설정
                .build()
                .parseClaimsJws(token)
                .getBody(); // 🔹 클레임 정보 반환
    }

    // 🔹 JWT에서 사용자 ID 추출
    public String extractUserId(String token) {
        return extractClaims(token).getSubject(); // 🔹 subject(사용자 ID) 반환
    }

 // 🔹 토큰유효성 검사 userId(Subject) 반환
    public String validateToken(String token) {
        try {
            Claims claims = extractClaims(token); // 🔹 토큰에서 Claims 추출
            return claims.getSubject(); // 🔹 Subject 값(userId) 반환
        } catch (Exception e) {
            return null; // 🔹 유효하지 않은 토큰일 경우 null 반환
        }
    }
}
