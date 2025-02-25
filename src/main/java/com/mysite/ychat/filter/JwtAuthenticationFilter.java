package com.mysite.ychat.filter;

import com.mysite.ychat.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // public 경로 전체는 JWT 검증을 건너뜁니다.
        if (requestURI.startsWith("/api/public/")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // Authorization 헤더가 없거나 Bearer 형식이 아닌 경우, 그냥 다음 필터로 넘어갑니다.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("Authorization header is missing or invalid for URI: {}", requestURI);
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // "Bearer " 이후의 토큰 추출

        // 토큰 유효성 검증
        if (jwtUtil.validateToken(token) != null) { 
            Claims claims = jwtUtil.extractClaims(token);
            String userId = claims.getSubject(); // 사용자 ID 가져오기

            // 사용자 정보를 기반으로 UserDetails 객체 생성 (비밀번호는 빈 문자열, 기본 USER 권한)
            UserDetails userDetails = User.withUsername(userId).password("").roles("USER").build();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // SecurityContext에 인증 정보를 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // 토큰이 유효하지 않은 경우 로그를 남길 수 있습니다.
            logger.debug("Invalid JWT token for URI: {}", requestURI);
        }

        // 다음 필터로 요청을 전달
        chain.doFilter(request, response);
    }
}
