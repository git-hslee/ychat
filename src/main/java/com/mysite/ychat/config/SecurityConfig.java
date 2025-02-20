package com.mysite.ychat.config;

import com.mysite.ychat.util.JwtUtil; // JWT 유틸 추가
import com.mysite.ychat.filter.JwtAuthenticationFilter; // JWT 인증 필터 추가
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil; // JWT 유틸 주입

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SecurityFilterChain 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**") // 모든 경로에 보안 정책 적용
            .csrf(csrf -> csrf.disable()) //csrf 비활성화
            // CORS 활성화 및 커스텀 설정 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 세션을 사용하지 않도록 STATELESS 설정 추가
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/public/**").permitAll()
                .anyRequest().authenticated() //다른 api 요청들은 인증을 받아야함
            )
            .formLogin().disable()
            .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CorsConfigurationSource Bean 설정: CORS 관련 설정을 지정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues(); //기본값 허용
        configuration.addAllowedOriginPattern("*"); // 모든 origin 허용
        configuration.addAllowedMethod("*");        // 모든 HTTP 메소드 허용
        configuration.addAllowedHeader("*");        // 모든 헤더 허용
        configuration.setAllowCredentials(true); //쿠기 허용
        configuration.addExposedHeader("Authorization"); // 클라이언트가 Authorization 헤더를 읽을 수 있도록 노출
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
