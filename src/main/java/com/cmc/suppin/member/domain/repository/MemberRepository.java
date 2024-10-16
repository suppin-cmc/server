package com.cmc.suppin.member.domain.repository;

import com.cmc.suppin.global.enums.UserStatus;
import com.cmc.suppin.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByUserIdAndStatusNot(String userId, UserStatus status);

    Boolean existsByEmailAndStatusNot(String email, UserStatus status);

    Optional<Member> findByUserIdAndStatusNot(String userId, UserStatus status);

    Optional<Member> findByIdAndStatusNot(Long id, UserStatus status);

    void deleteByUserId(String userId);
}
