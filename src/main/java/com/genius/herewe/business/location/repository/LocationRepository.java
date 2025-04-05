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

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("""
		UPDATE Location l
		SET l.locationIndex = l.locationIndex - 1
		WHERE l.moment.id = :momentId AND l.locationIndex > :thresholdIndex AND l.moment.version = :momentVersion
		""")
	int bulkDecreaseIndexes(@Param("momentId") Long momentId,
							@Param("thresholdIndex") int thresholdIndex,
							@Param("momentVersion") Long momentVersion);

	@Query("SELECT MAX(l.locationIndex) FROM Location l WHERE l.moment.id = :momentId")
	int findLastIndexForMoment(@Param("momentId") Long momentId);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("""
		UPDATE Location l
		SET l.locationIndex = -l.locationIndex
		WHERE l.moment.id = :momentId AND l.moment.version = :momentVersion
		AND l.locationIndex > :lowerBound AND l.locationIndex <= :upperBound
		""")
	int invertIndexesForDecrement(@Param("momentId") Long momentId,
								  @Param("lowerBound") int lowerBound,
								  @Param("upperBound") int upperBound,
								  @Param("momentVersion") Long momentVersion);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("""
		UPDATE Location l
		SET l.locationIndex = -l.locationIndex
		WHERE l.moment.id = :momentId AND l.moment.version = :momentVersion
		AND l.locationIndex >= :lowerBound AND l.locationIndex < :upperBound
		""")
	int invertIndexesForIncrement(@Param("momentId") Long momentId,
								  @Param("lowerBound") int lowerBound,
								  @Param("upperBound") int upperBound,
								  @Param("momentVersion") Long momentVersion);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("""
		UPDATE Location l
		SET l.locationIndex = (-l.locationIndex - 1)
		WHERE l.moment.id = :momentId AND l.moment.version = :momentVersion
		AND l.locationIndex < -1 AND l.locationIndex >= :lowerThreshold
		""")
	int applyDecrementToInverted(@Param("momentId") Long momentId,
								 @Param("lowerThreshold") int lowerThreshold,
								 @Param("momentVersion") Long momentVersion);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("""
		UPDATE Location l
		SET l.locationIndex = (-l.locationIndex + 1)
		WHERE l.moment.id = :momentId AND l.moment.version = :momentVersion
		AND l.locationIndex < -1 AND l.locationIndex >= :lowerThreshold
		""")
	int applyIncrementToInverted(@Param("momentId") Long momentId,
								 @Param("lowerThreshold") int lowerThreshold,
								 @Param("momentVersion") Long momentVersion);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("""
		UPDATE Location l
		SET l.locationIndex = :newIndex
		WHERE l.moment.id = :momentId AND l.moment.version = :momentVersion
		AND l.locationIndex = :originalIndex
		""")
	int repositionIndex(@Param("momentId") Long momentId,
						@Param("originalIndex") int originalIndex,
						@Param("newIndex") int newIndex,
						@Param("momentVersion") Long momentVersion);
}
