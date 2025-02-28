package com.genius.herewe.business.crew.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.crew.domain.CrewMember;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {

	@Query("select cm from CrewMember cm where cm.userId = :userId and cm.crewId = :crewId")
	Optional<CrewMember> find(@Param("userId") Long userId, @Param("crewId") Long crewId);
}
