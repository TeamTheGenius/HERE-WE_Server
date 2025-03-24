package com.genius.herewe.business.moment.repository;

import java.util.Optional;

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
}
