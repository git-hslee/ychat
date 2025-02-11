package com.mysite.ychat.controller;

import com.mysite.ychat.domain.User;
import com.mysite.ychat.exception.UserNotFoundException;
import com.mysite.ychat.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    // Constructor-based dependency injection
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create a new user
    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        if (userRepository.existsById(user.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 아이디입니다.");
        }
        
        try {
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
}
