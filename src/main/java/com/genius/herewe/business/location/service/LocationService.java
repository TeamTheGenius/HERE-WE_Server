package com.genius.herewe.business.location.service;

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
		Location location = Location.builder()
			.locationIndex(index)
			.name(place.name())
			.address(place.address())
			.roadAddress(place.roadAddress())
			.url(place.placeUrl())
			.x(place.x())
			.y(place.y())
			.phone(place.phone())
			.build();
		return locationRepository.save(location);
	}
}
