package com.mysite.ychat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) //  CORS 설정 추가
            .csrf(csrf -> csrf.disable()) // 🔥 CSRF 보호 비활성화 (POST 요청 가능하도록 설정)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/signup.html", "/api/users/signup", "/images/**").permitAll() //  인증 없이 접근 허용
                .anyRequest().authenticated()
            )
            .formLogin().disable() // 🔥 기본 로그인 페이지 비활성화 (무한 리디렉트 방지)
            .logout(logout -> logout
                .logoutSuccessUrl("/") // 로그아웃 후 메인 페이지로 이동
                .permitAll()
            );

        return http.build();
    }

    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); //  모든 도메인 허용
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
