package com.genius.herewe.business.location.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.location.domain.Location;
import com.genius.herewe.business.location.repository.LocationRepository;
import com.genius.herewe.business.location.search.dto.Place;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationService {
	private final LocationRepository locationRepository;

	@Transactional
	public Location saveFromPlace(Place place, int index) {
		Location location = Location.createFromPlace(place, index);
		return locationRepository.save(location);
	}

	@Transactional
	public Location save(Location location) {
		return locationRepository.save(location);
	}

	public Optional<Location> findByIndex(Long momentId, int locationIndex) {
		return locationRepository.findByLocationIndex(momentId, locationIndex);
	}

	public List<Location> findAllInMoment(Long momentId) {
		return locationRepository.findAllInMoment(momentId);
	}

	public Optional<Location> findMeetLocation(Long momentId) {
		return locationRepository.findByLocationIndex(momentId, 1);
	}

	public Map<Long, Location> findMeetingLocationsInIds(List<Long> momentIds) {
		List<Location> meetingLocations = locationRepository.findMeetingLocationsByIds(momentIds);
		return meetingLocations.stream()
			.collect(Collectors.toMap(
				location -> location.getMoment().getId(),
				location -> location
			));
	}

	public int findLastIndexForMoment(Long momentId) {
		return locationRepository.findLastIndexForMoment(momentId);
	}

	@Transactional
	public int bulkDecreaseIndexes(Long momentId, int locationIndex, Long momentVersion) {
		return locationRepository.bulkDecreaseIndexes(momentId, locationIndex, momentVersion);
	}

	@Transactional
	public int invertIndexesForDecrement(Long momentId, int lowerBound, int upperBound, Long momentVersion) {
		return locationRepository.invertIndexesForDecrement(momentId, lowerBound, upperBound, momentVersion);
	}

	@Transactional
	public int applyDecrementToInverted(Long momentId, int lowerThreshold, Long momentVersion) {
		return locationRepository.applyDecrementToInverted(momentId, lowerThreshold, momentVersion);
	}

	@Transactional
	public int applyIncrementToInverted(Long momentId, int lowerThreshold, Long momentVersion) {
		return locationRepository.applyIncrementToInverted(momentId, lowerThreshold, momentVersion);
	}

	@Transactional
	public int invertIndexesForIncrement(Long momentId, int lowerBound, int upperBound, Long momentVersion) {
		return locationRepository.invertIndexesForIncrement(momentId, lowerBound, upperBound, momentVersion);
	}

	@Transactional
	public int repositionIndex(Long momentId, int originalIndex, int newIndex, Long momentVersion) {
		return locationRepository.repositionIndex(momentId, originalIndex, newIndex, momentVersion);
	}

	@Transactional
	public void delete(Location location) {
		locationRepository.delete(location);
	}
}
