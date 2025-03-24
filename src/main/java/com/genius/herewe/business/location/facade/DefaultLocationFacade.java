package com.genius.herewe.business.location.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.location.LocationRequest;
import com.genius.herewe.business.location.domain.Location;
import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.business.location.service.LocationService;
import com.genius.herewe.business.moment.domain.Moment;
import com.genius.herewe.business.moment.service.MomentService;
import com.genius.herewe.core.global.exception.BusinessException;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultLocationFacade implements LocationFacade {
	private final MomentService momentService;
	private final LocationService locationService;

	@Override
	@Transactional
	public Place addPlace(Long momentId, LocationRequest locationRequest) {
		try {
			Place place = locationRequest.place();
			int locationIndex = locationRequest.locationIndex();

			Moment moment = momentService.findByIdWithOptimisticLock(momentId);

			locationService.findByIndex(momentId, locationIndex).ifPresent(val -> {
				throw new BusinessException(LOCATION_ALREADY_EXISTS);
			});

			moment.updateLastModifiedTime();
			momentService.save(moment);
			momentService.flushChanges();

			Location location = Location.createFromPlace(place, locationIndex);
			location.addMoment(moment);
			locationService.save(location);

			return place;
		} catch (OptimisticLockException e) {
			throw new BusinessException(CONCURRENT_MODIFICATION_EXCEPTION);
		} catch (DataIntegrityViolationException e) {
			if (e.getMessage().contains("uk_location_index_moment")) {
				throw new BusinessException(LOCATION_ALREADY_EXISTS);
			}
			throw new BusinessException(e);
		}
	}
}
