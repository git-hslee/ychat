package com.mysite.ychat.controller;

import com.mysite.ychat.domain.User;
import com.mysite.ychat.config.SecurityConfig;
import com.mysite.ychat.exception.UserNotFoundException;
import com.mysite.ychat.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired //의존성 주입 
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; 
    }

    // Create a new user
    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        /* 기본적인 입력 검증 추가 (비밀번호 최소 길이 확인)
        if (user.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호는 최소 6자 이상이어야 합니다.");
        }
        */

        // 중복 체크 (더 간결하게 작성)
        if (userRepository.findById(user.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 아이디입니다.");
        } 
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 사용자명입니다.");
        } 
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 전화번호입니다.");
        }

        // 예외 처리 블록 내부에서 저장 로직 수행 (안정성 증가)
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // 비밀번호 암호화
            userRepository.save(user);
            return ResponseEntity.ok("회원가입 성공");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("데이터 저장 중 오류 발생");
        }
    }


    // Get user by ID
    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());

        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 아이디입니다.");
        }

        User foundUser = existingUser.get();

        //  비밀번호 비교 (BCrypt 사용)
        if (!passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        }

        return ResponseEntity.ok("로그인 성공");
    }

}
