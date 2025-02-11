package com.mysite.ychat.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(nullable = false, unique = true)
    private String id; //유저 id

    @Column(nullable = false)
    private String username; //유저 이름

    @Column(nullable = false)
    private String password; //유저 pw
    
    @Column(nullable = false, unique = true)
    private String phoneNumber; //유저 핸드폰 번호
    
    @Column(nullable = false)
    private String address; //유저 집주소
}

