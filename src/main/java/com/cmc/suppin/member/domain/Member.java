package com.cmc.suppin.member.domain;

import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.fcm.domain.DeviceToken;
import com.cmc.suppin.global.domain.BaseDateTimeEntity;
import com.cmc.suppin.global.enums.UserRole;
import com.cmc.suppin.global.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Event> eventList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DeviceToken> deviceTokenList = new ArrayList<>();

    @Column(columnDefinition = "VARCHAR(30)", nullable = false)
    private String userId;

    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private String name;

    @Column(columnDefinition = "VARCHAR(30)", nullable = false)
    private String email;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String password;

    @Column(columnDefinition = "VARCHAR(13)", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(columnDefinition = "VARCHAR(50)", nullable = false)
    private String userType;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<TermsAgree> termsAgreeList = new ArrayList<>();

    public void addTermsAgree(TermsAgree termsAgree) {
        termsAgree.setMember(this);
        this.termsAgreeList.add(termsAgree);
    }

    public boolean isDeleted() {
        return this.status == UserStatus.DELETED;
    }

    public void delete() {
        this.status = UserStatus.DELETED;
    }

    public void updatePassword(String encode) {
        this.password = encode;
    }

    public void addDeviceToken(DeviceToken deviceToken) {
        deviceToken.setMember(this);
        this.deviceTokenList.add(deviceToken);
    }

    public void removeDeviceToken(DeviceToken deviceToken) {
        this.deviceTokenList.remove(deviceToken);
        deviceToken.setMember(null);
    }
}

