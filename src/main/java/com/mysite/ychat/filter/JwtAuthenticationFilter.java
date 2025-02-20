package com.mysite.ychat.filter;

import com.mysite.ychat.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
    	
    	String requestURI = request.getRequestURI();

        // ✅ 로그인 & 회원가입 요청은 필터를 거치지 않도록 예외 처리
    	if (requestURI.startsWith("/api/public/users/signup") || requestURI.startsWith("/api/public/users/login")) {
    	    chain.doFilter(request, response);
    	    return;
    	}

        
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // "Bearer " 이후의 토큰 추출

        if (jwtUtil.validateToken(token)) { // 🔹 토큰이 유효한지 확인
            Claims claims = jwtUtil.extractClaims(token); // 🔹 클레임 추출
            String userId = claims.getSubject(); // 🔹 사용자 ID 가져오기

            UserDetails userDetails = User.withUsername(userId).password("").roles("USER").build();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

}
