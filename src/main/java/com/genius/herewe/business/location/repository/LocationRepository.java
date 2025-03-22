package com.genius.herewe.business.location.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.location.domain.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

	@Query("SELECT l FROM Location l WHERE l.momentId = :momentId AND l.index = :index")
	Optional<Location> findByIndex(@Param("momentId") Long momentId, @Param("index") int index);
}
