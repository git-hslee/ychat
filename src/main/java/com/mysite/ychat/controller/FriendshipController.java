package com.mysite.ychat.controller;

import com.mysite.ychat.domain.Friendship;
import com.mysite.ychat.dto.FriendshipResponseDto;
import com.mysite.ychat.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    // 친구 요청 보내기
    // 예시: POST /api/friendships/request?receiverId=yyy
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestParam String receiverId) {
        String requesterId = getCurrentUserId(); // 🔹 현재 로그인한 사용자 ID 가져오기
        Friendship friendship = friendshipService.sendFriendRequest(requesterId, receiverId);
        return ResponseEntity.ok(new FriendshipResponseDto(friendship)); //return 정보 어떤걸로 할지 생각
    }

    // 받은 친구 요청 조회 (PENDING 상태만)
    // 예시: GET /api/friendships/requests
    @GetMapping("/requests")
    public ResponseEntity<List<FriendshipResponseDto>> getPendingFriendRequestsForUser() {
        String userId = getCurrentUserId(); // 🔹 현재 로그인한 사용자 ID 가져오기
        List<Friendship> friendships = friendshipService.getPendingFriendRequestsForUser(userId);
        List<FriendshipResponseDto> responseDtos = friendships.stream()
                .map(FriendshipResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    // 친구 요청 수락
    // 예시: PATCH /api/friendships/{friendshipId}/accept
    @PatchMapping("/{friendshipId}/accept")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long friendshipId) {
        Friendship friendship = friendshipService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok(new FriendshipResponseDto(friendship));
    }

    // 친구 요청 거절
    // 예시: PATCH /api/friendships/{friendshipId}/reject
    @PatchMapping("/{friendshipId}/reject")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable Long friendshipId) {
        Friendship friendship = friendshipService.rejectFriendRequest(friendshipId);
        return ResponseEntity.ok(new FriendshipResponseDto(friendship));
    }

    // 🔹 현재 로그인한 사용자 ID 가져오기
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        return authentication.getName(); // 🔹 SecurityContext에서 로그인한 사용자 ID 반환
    }
}
