package com.genius.herewe.business.crew.repository.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.dto.CrewPreviewResponse;

public interface CrewMemberQueryRepository extends JpaRepository<CrewMember, Long> {
	@Query("SELECT new com.genius.herewe.business.crew.dto.CrewPreviewResponse(c.id, c.name, c.participantCount)"
		+ "FROM Crew c JOIN c.crewMembers cm WHERE cm.user.id = :userId")
	Page<CrewPreviewResponse> findAllJoinCrews(@Param("userId") Long userId, Pageable pageable);
}
