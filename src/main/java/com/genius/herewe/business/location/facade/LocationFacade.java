package com.genius.herewe.business.location.facade;

import com.genius.herewe.business.location.LocationRequest;
import com.genius.herewe.business.location.dto.PlaceResponse;
import com.genius.herewe.business.location.search.dto.Place;

public interface LocationFacade {
	Place addPlace(Long userId, Long momentId, LocationRequest locationRequest);

	PlaceResponse inquiryAll(Long momentId);

	void deletePlace(Long userId, Long momentId, int locationIndex);
}
