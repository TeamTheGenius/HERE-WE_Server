package com.genius.herewe.business.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.location.domain.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

	@Query("SELECT l FROM Location l WHERE l.moment.id = :momentId AND l.locationIndex = :index")
	Optional<Location> findByLocationIndex(@Param("momentId") Long momentId, @Param("index") int index);

	@Query("SELECT l FROM Location l WHERE l.moment.id = :momentId ORDER BY l.locationIndex ASC LIMIT 100")
	List<Location> findAllInMoment(@Param("momentId") Long momentId);

	@Query("SELECT l FROM Location l WHERE l.locationIndex = 1 AND l.moment.id IN :momentIds")
	List<Location> findMeetingLocationsByIds(@Param("momentIds") List<Long> momentIds);

	@Modifying
	@Query("""
		UPDATE Location l
		SET l.locationIndex = l.locationIndex - 1
		WHERE l.moment.id = :momentId AND l.locationIndex > :thresholdIndex AND l.moment.version = :momentVersion
		""")
	int bulkDecreaseIndexes(@Param("momentId") Long momentId,
							@Param("thresholdIndex") int thresholdIndex,
							@Param("momentVersion") Long momentVersion);
}
