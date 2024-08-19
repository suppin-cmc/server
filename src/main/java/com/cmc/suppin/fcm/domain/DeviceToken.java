package com.cmc.suppin.fcm.domain;

import com.cmc.suppin.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, unique = true)
    private String deviceToken;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType; // ANDROID, IOS, OTHER

    @Column(nullable = false)
    private LocalDateTime createdAt;


    public void setMember(Member member) {
        this.member = member;
    }

    public void setToken(String token) {
        this.deviceToken = token;
    }

    public void setCreatedAt(LocalDateTime now) {
        this.createdAt = now;
    }
}
