package com.genius.herewe.business.crew.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.repository.dto.CrewMomentPair;

public interface CrewRepository extends JpaRepository<Crew, Long> {

	@Query("""
		SELECT m.id as momentId, c as crew
		FROM Moment m
		JOIN m.crew c
		WHERE m.id IN :momentIds
		""")
	List<CrewMomentPair> findAllInMomentIds(List<Long> momentIds);
}
