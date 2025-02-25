package com.mysite.ychat.controller;

import com.mysite.ychat.domain.Friendship;
import com.mysite.ychat.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// 변경: "public" 경로 제거 → 인증이 필요한 엔드포인트로 변경 (SecurityConfig에서 permitAll() 패턴과 겹치지 않도록)
@RequestMapping("/api/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    // 친구 요청 보내기
    // 예시: POST /api/friendships/request?receiverId=yyy
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestParam("receiverId") String receiverId) { // 변경: 매개변수 이름 명시
        String requesterId = getCurrentUserId(); // 현재 로그인한 사용자 ID 가져오기
        Friendship friendship = friendshipService.sendFriendRequest(requesterId, receiverId);
        return ResponseEntity.ok("요청이 성공적으로 전송되었습니다!");
    }

    // 받은 친구 요청 조회 (PENDING 상태만)
    // 예시: GET /api/friendships/requests
    @GetMapping("/requests")
    public ResponseEntity<List<Friendship>> getPendingFriendRequestsForUser() {
        String userId = getCurrentUserId(); // 현재 로그인한 사용자 ID 가져오기
        List<Friendship> friendships = friendshipService.getPendingFriendRequestsForUser(userId);
        return ResponseEntity.ok(friendships);
    }

    // 친구 요청 수락
    // 예시: PATCH /api/friendships/{friendshipId}/accept
    @PatchMapping("/{friendshipId}/accept")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable("friendshipId") Long friendshipId) { // 변경: @PathVariable 이름 명시
        Friendship friendship = friendshipService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok("성공적으로 친구 요청을 수락했습니다.");
    }

    // 친구 요청 거절
    // 예시: PATCH /api/friendships/{friendshipId}/reject
    @PatchMapping("/{friendshipId}/reject")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable("friendshipId") Long friendshipId) { // 변경: @PathVariable 이름 명시
        Friendship friendship = friendshipService.rejectFriendRequest(friendshipId);
        return ResponseEntity.ok("성공적으로 친구 요청을 거부했습니다.");
    }

    // 현재 로그인한 사용자 ID 가져오기
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        return authentication.getName(); // SecurityContext에서 로그인한 사용자 ID 반환
    }
}
