package com.mysite.ychat.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 친구 요청을 보낸 사용자
    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;

    // 친구 요청을 받은 사용자
    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private User receiver;

    // 친구 요청 상태
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    // 친구 요청 생성 시간
    private LocalDateTime createdAt;
}