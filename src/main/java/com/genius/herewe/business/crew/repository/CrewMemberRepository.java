package com.genius.herewe.business.crew.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.crew.domain.CrewMember;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {

	@Query("SELECT cm FROM CrewMember cm WHERE cm.user.id = :userId AND cm.crew.id = :crewId")
	Optional<CrewMember> find(@Param("userId") Long userId, @Param("crewId") Long crewId);
}
