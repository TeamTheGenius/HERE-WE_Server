package com.genius.herewe.business.moment.repository.query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.moment.domain.MomentMember;
import com.genius.herewe.business.moment.dto.MomentMemberResponse;

public interface MomentMemberQueryRepository extends JpaRepository<MomentMember, Long> {

	@Query("""
		SELECT new com.genius.herewe.business.moment.dto.MomentMemberResponse(u.id, u.nickname)
		FROM MomentMember mm JOIN mm.user u
		WHERE mm.moment.id = :momentId
		ORDER BY mm.joinedAt ASC, u.nickname ASC
		""")
	Slice<MomentMemberResponse> findAllJoinedUsers(@Param("momentId") Long momentId, Pageable pageable);
}
