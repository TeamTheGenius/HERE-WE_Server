package com.genius.herewe.business.moment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.moment.domain.MomentMember;

public interface MomentMemberRepository extends JpaRepository<MomentMember, Long> {

	@Query("SELECT m FROM MomentMember m WHERE m.user.id = :userId AND m.moment.id = :momentId")
	Optional<MomentMember> findByJoinInfo(@Param("userId") Long userId, @Param("momentId") Long momentId);
}
