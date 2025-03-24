package com.genius.herewe.business.location.service;

import java.util.Optional;

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

	public Optional<Location> findMeetLocation(Long momentId) {
		return locationRepository.findByLocationIndex(momentId, 1);
	}

	public Optional<Location> findByIndex(Long momentId, int locationIndex) {
		return locationRepository.findByLocationIndex(momentId, locationIndex);
	}
}
