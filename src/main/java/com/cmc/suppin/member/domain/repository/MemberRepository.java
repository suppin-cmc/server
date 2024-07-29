package com.cmc.suppin.member.domain.repository;

import com.cmc.suppin.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByName(String name);

    Member findByName(String name);
}
