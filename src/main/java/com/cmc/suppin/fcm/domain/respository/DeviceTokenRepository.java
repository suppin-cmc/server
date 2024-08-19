package com.cmc.suppin.fcm.domain.respository;

import com.cmc.suppin.fcm.domain.DeviceToken;
import com.cmc.suppin.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByDeviceToken(String deviceToken);

    List<DeviceToken> findAllByMember(Member member);
}

