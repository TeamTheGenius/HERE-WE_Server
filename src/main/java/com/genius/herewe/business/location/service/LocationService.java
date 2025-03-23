package com.genius.herewe.business.location.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.location.domain.Location;
import com.genius.herewe.business.location.repository.LocationRepository;
import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.core.global.exception.BusinessException;

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

	public Location findMeetLocation(Long momentId) {
		return locationRepository.findByLocationIndex(momentId, 1)
			.orElseThrow(() -> new BusinessException(NEED_MEET_PLACE));
	}
}
