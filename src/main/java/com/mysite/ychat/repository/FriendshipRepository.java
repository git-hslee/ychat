package com.mysite.ychat.repository;

import com.mysite.ychat.domain.Friendship;
import com.mysite.ychat.domain.User;
import com.mysite.ychat.domain.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    // 요청한 사용자의 모든 Friendship 목록 조회
    List<Friendship> findAllByRequester(User requester);
    
    // 친구 요청을 받은 사용자의 모든 Friendship 목록 조회
    List<Friendship> findAllByReceiver(User receiver);
    
    // 두 사용자 간의 Friendship이 존재하는지 확인
    Optional<Friendship> findByRequesterAndReceiver(User requester, User receiver);
    
    // 상태를 포함하여 특정 Friendship을 조회
    Optional<Friendship> findByRequesterAndReceiverAndStatus(User requester, User receiver, FriendshipStatus status);
}
