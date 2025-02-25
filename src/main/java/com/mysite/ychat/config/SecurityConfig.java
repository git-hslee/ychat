package com.mysite.ychat.config;

import com.mysite.ychat.util.JwtUtil;
import com.mysite.ychat.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.http.SessionCreationPolicy;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (JWT 사용 시 필요)
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 X
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll() // 공개 API 경로 허용
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
            )
            .logout(logout -> logout.disable()) // JWT 기반 인증이므로 Spring Security의 기본 로그아웃 비활성화
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // React 앱의 Origin만 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // 필요한 HTTP 메소드만 허용
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // 필요한 헤더만 허용
        configuration.setAllowCredentials(true); // 인증 정보를 포함한 요청 허용
        configuration.setExposedHeaders(List.of("Authorization")); // 클라이언트가 Authorization 헤더 접근 가능

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
