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
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 🔹 1시간 유효

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

    // 🔹 토큰 유효성 체크
    public boolean validateToken(String token) {
        try {
            extractClaims(token); // 🔹 유효한지 확인 (파싱 가능한지 체크)
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
