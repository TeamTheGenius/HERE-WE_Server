package com.genius.herewe.business.moment.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.moment.domain.Moment;

import jakarta.persistence.LockModeType;

public interface MomentRepository extends JpaRepository<Moment, Long> {
	@Lock(LockModeType.OPTIMISTIC)
	@Query("SELECT m FROM Moment m WHERE m.id = :momentId")
	Optional<Moment> findByIdWithOptimisticLock(@Param("momentId") Long momentId);

	@Query("SELECT m FROM Moment m WHERE m.crew.id = :crewId ORDER BY m.createdAt")
	Page<Moment> findAllInCrewByPaging(@Param("crewId") Long crewId, Pageable pageable);

	@Query("""
		SELECT m
		FROM Moment m
		WHERE m.id IN :momentIds AND m.meetAt >= :currentDate
		ORDER BY m.meetAt ASC
		""")
	List<Moment> findAllJoined(@Param("momentIds") List<Long> momentIds, @Param("currentDate") LocalDateTime now);
}
