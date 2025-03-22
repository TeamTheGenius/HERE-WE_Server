package com.genius.herewe.business.location.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.location.domain.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

	@Query("SELECT l FROM Location l WHERE l.moment.id = :momentId AND l.locationIndex = :index")
	Optional<Location> findByLocationIndex(@Param("momentId") Long momentId, @Param("index") int index);
}
