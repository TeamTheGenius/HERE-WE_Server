package com.genius.herewe.business.moment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.moment.domain.MomentMember;

public interface MomentMemberRepository extends JpaRepository<MomentMember, Long> {

	@Query("SELECT m FROM MomentMember m WHERE m.user.id = :userId AND m.moment.id = :momentId")
	Optional<MomentMember> findByJoinInfo(@Param("userId") Long userId, @Param("momentId") Long momentId);

	@Query("SELECT mm.moment.id FROM MomentMember mm WHERE mm.moment.id IN :momentIds")
	List<Long> findIdsInMoment(@Param("momentIds") List<Long> momentIds);

	@Query(
		value = "SELECT DISTINCT mm.moment.id FROM MomentMember mm WHERE mm.user.id = :userId",
		countQuery = "SELECT COUNT(DISTINCT mm.moment.id) FROM MomentMember mm WHERE mm.user.id = :userId")
	Page<Long> findAllJoinedMomentIds(@Param("userId") Long userId, Pageable pageable);
}
