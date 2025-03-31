package com.genius.herewe.business.moment.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.moment.domain.Moment;
import com.genius.herewe.business.moment.domain.MomentMember;

public interface MomentMemberRepository extends JpaRepository<MomentMember, Long> {

	@Query("SELECT m FROM MomentMember m WHERE m.user.id = :userId AND m.moment.id = :momentId")
	Optional<MomentMember> findByJoinInfo(@Param("userId") Long userId, @Param("momentId") Long momentId);

	@Query("SELECT mm.moment.id FROM MomentMember mm WHERE mm.moment.id IN :momentIds")
	List<Long> findIdsInMoment(@Param("momentIds") List<Long> momentIds);

	@Query(
		value = """
			SELECT DISTINCT mm.moment
			FROM MomentMember mm JOIN mm.moment m
			WHERE mm.user.id = :userId AND m.meetAt >= :currentDate
			ORDER BY m.meetAt ASC
			""",
		countQuery = """
			SELECT COUNT(DISTINCT mm.moment)
			FROM MomentMember mm JOIN mm.moment m
			WHERE mm.user.id = :userId AND m.meetAt >= :currentDate
			""")
	Page<Moment> findAllJoinedMoments(
		@Param("userId") Long userId,
		@Param("currentDate") LocalDateTime currentDate,
		Pageable pageable);
}
