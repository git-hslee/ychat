package com.mysite.ychat.auth;

import com.mysite.ychat.util.JwtUtil;
import com.mysite.ychat.domain.User;
import com.mysite.ychat.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository; // 🔹 UserRepository 주입

    public AuthController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // 🔹 JWT 토큰 검증 API (userName 반환)
    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "토큰이 없음"));
        }

        token = token.substring(7); // "Bearer " 제거
        String userId = jwtUtil.validateToken(token); // 🔹 JWT 검증 후 userId 반환

        if (userId != null) {
            // 🔹 userId를 기반으로 userName 조회
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                return ResponseEntity.ok().body(Map.of(
                    "message", "토큰 유효",
                    "userName", user.get().getUsername() // 🔹 userName 반환
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자를 찾을 수 없음"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "토큰이 유효하지 않음"));
        }
    }
}
