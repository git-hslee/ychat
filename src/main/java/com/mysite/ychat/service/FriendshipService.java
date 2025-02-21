package com.mysite.ychat.service;

import com.mysite.ychat.domain.Friendship;
import com.mysite.ychat.domain.FriendshipStatus;
import com.mysite.ychat.domain.User;
import com.mysite.ychat.repository.FriendshipRepository;
import com.mysite.ychat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mysite.ychat.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @Autowired
    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    // 친구 요청 보내기
    @Transactional
    public Friendship sendFriendRequest(String requesterId, String receiverId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("요청자가 존재하지 않습니다."));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("요청 하는 대상이 존재하지 않습니다."));

        // 자기 자신에게 친구 요청 방지
        if (requester.equals(receiver)) {
            throw new RuntimeException("자기자신은 이미 영원한 친구입니다.");
        }

        // 이미 요청이 있는지 확인 (PENDING 또는 ACCEPTED 상태일 경우 중복 방지)
        Optional<Friendship> existing = friendshipRepository.findByRequesterAndReceiver(requester, receiver);
        if (existing.isPresent() && existing.get().getStatus() != FriendshipStatus.REJECTED) {
            throw new RuntimeException("이미 요청이 존재합니다.");
        }

        // 🔹 반대 방향의 친구 요청이 `PENDING` 상태인지 확인
        Optional<Friendship> reverseRequest = friendshipRepository.findByRequesterAndReceiver(receiver, requester);
        if (reverseRequest.isPresent() && reverseRequest.get().getStatus() == FriendshipStatus.PENDING) {
            // 기존 요청을 ACCEPTED로 변경하여 친구 관계 확정
            Friendship existingFriendship = reverseRequest.get();
            existingFriendship.setStatus(FriendshipStatus.ACCEPTED);
            return friendshipRepository.save(existingFriendship);
        }

        // 새 친구 요청 생성
        Friendship friendship = Friendship.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendshipStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return friendshipRepository.save(friendship);
    }


    // 특정 사용자가 받은 친구 요청 조회 (PENDING 상태만 조회)
    public List<Friendship> getPendingFriendRequestsForUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("조회 대상이 존재하지 않습니다"));
        return friendshipRepository.findAllByReceiverAndStatus(user, FriendshipStatus.PENDING);
    }

    // 친구 요청 수락
    @Transactional
    public Friendship acceptFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("요청자가 존재하지 않아 수락할 수 없습니다."));

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException("이미 처리된 요청입니다.");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    // 친구 요청 거절
    @Transactional
    public Friendship rejectFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("요청자가 존재하지 않아 거절할 수 없습니다."));

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException("이미 처리된 요청입니다.");
        }

        friendship.setStatus(FriendshipStatus.REJECTED);
        return friendshipRepository.save(friendship);
    }
}

