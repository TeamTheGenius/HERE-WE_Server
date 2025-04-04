package com.genius.herewe.business.location.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.location.LocationRequest;
import com.genius.herewe.business.location.domain.Location;
import com.genius.herewe.business.location.dto.LocationInfo;
import com.genius.herewe.business.location.dto.PlaceResponse;
import com.genius.herewe.business.location.handler.RetryHandler;
import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.business.location.service.LocationService;
import com.genius.herewe.business.moment.domain.Moment;
import com.genius.herewe.business.moment.service.MomentMemberService;
import com.genius.herewe.business.moment.service.MomentService;
import com.genius.herewe.core.global.exception.BusinessException;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultLocationFacade implements LocationFacade {
	private final MomentService momentService;
	private final MomentMemberService momentMemberService;
	private final LocationService locationService;
	private final RetryHandler retryHandler;

	@Override
	@Transactional
	public Place addPlace(Long userId, Long momentId, LocationRequest locationRequest) {
		momentMemberService.findByJoinInfo(userId, momentId)
			.orElseThrow(() -> new BusinessException(MOMENT_PARTICIPATION_NOT_FOUND));

		return retryHandler.executeWithRetry(
			() -> executeAddPlace(momentId, locationRequest)
		);
	}

	@Transactional
	public Place executeAddPlace(Long momentId, LocationRequest locationRequest) {
		try {
			Place place = locationRequest.place();

			int lastIndex = locationService.findLastIndexForMoment(momentId);
			int nextIndex = lastIndex + 1;
			if (nextIndex > 100) {
				throw new BusinessException(LOCATION_LIMIT_EXCEEDED);
			}

			Moment moment = momentService.findByIdWithOptimisticLock(momentId);

			Location location = Location.createFromPlace(place, nextIndex);
			location.addMoment(moment);
			locationService.save(location);

			moment.updateLastModifiedTime();
			momentService.save(moment);
			momentService.flushChanges();

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

	@Override
	public PlaceResponse inquiryAll(Long momentId) {
		List<LocationInfo> locationInfos = locationService.findAllInMoment(momentId).stream()
			.map(LocationInfo::create)
			.toList();
		return PlaceResponse.create(momentId, locationInfos);
	}

	@Override
	@Transactional
	public void deletePlace(Long userId, Long momentId, int locationIndex) {
		retryHandler.executeWithRetry(() -> {
			executeDeletePlace(userId, momentId, locationIndex);
			return null;
		});
	}

	@Transactional
	private void executeDeletePlace(Long userId, Long momentId, int locationIndex) {
		momentMemberService.findByJoinInfo(userId, momentId)
			.orElseThrow(() -> new BusinessException(MOMENT_PARTICIPATION_NOT_FOUND));

		Moment moment = momentService.findByIdWithOptimisticLock(momentId);
		int lastIndex = locationService.findLastIndexForMoment(momentId);
		if (1 > locationIndex || locationIndex > lastIndex) {
			throw new BusinessException(INVALID_LOCATION_INDEX);
		}

		Location targetLocation = locationService.findByIndex(momentId, locationIndex)
			.orElseThrow(() -> new BusinessException(CONCURRENT_MODIFICATION_EXCEPTION));
		locationService.delete(targetLocation);

		boolean isLastIndex = locationIndex == lastIndex;
		if (!isLastIndex) {
			int updatedRows = locationService.bulkDecreaseIndexes(momentId, locationIndex, moment.getVersion());
			if (updatedRows == 0) {
				throw new BusinessException(CONCURRENT_MODIFICATION_EXCEPTION);
			}
		}

		moment.updateLastModifiedTime();
		momentService.save(moment);
		momentService.flushChanges();
	}
}
