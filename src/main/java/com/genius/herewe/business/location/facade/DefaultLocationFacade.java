package com.genius.herewe.business.location.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.location.LocationRequest;
import com.genius.herewe.business.location.domain.Location;
import com.genius.herewe.business.location.dto.LocationInfo;
import com.genius.herewe.business.location.dto.PlaceResponse;
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
	private final int MAX_RETRIES = 3;

	private final MomentService momentService;
	private final MomentMemberService momentMemberService;
	private final LocationService locationService;

	@Override
	@Transactional
	public Place addPlace(Long momentId, LocationRequest locationRequest) {
		try {
			Place place = locationRequest.place();
			int locationIndex = locationRequest.locationIndex();
			//TODO: 해당 모먼트에 등록되어 있는 최대 인덱스 값을 확인하고 맞지 않으면 예외 발생 필요
			if (locationIndex > 100) {
				throw new BusinessException(LOCATION_LIMIT_EXCEEDED);
			}

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
		int retryCount = 0;
		while (retryCount < MAX_RETRIES) {
			try {
				executeDeletePlace(userId, momentId, locationIndex);
				return;
			} catch (Exception e) {
				retryCount++;
				if (shouldRetry(e, retryCount)) {
					logRetryAttempt(retryCount, momentId, locationIndex, e);
					applyBackoff(retryCount);
					continue;
				}
				throw translateException(e);
			}
		}
		log.error("모든 재시도 실패");
	}

	private boolean shouldRetry(Exception e, int retryCount) {
		// 최대 재시도 횟수 미만이고 재시도 가능한 예외인 경우
		return retryCount < MAX_RETRIES &&
			(e instanceof DataAccessException ||
				e instanceof OptimisticLockException ||
				(e.getCause() != null && e.getCause() instanceof DataAccessException));
	}

	private void applyBackoff(int retryCount) {
		try {
			long delay = (long)(100 * Math.pow(2, retryCount - 1));
			Thread.sleep(delay);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

	private void logRetryAttempt(int retryCount, Long momentId, int locationIndex, Exception e) {
		log.warn("Retry attempt {}/{} for deleting place: momentId={}, locationIndex={}, error={}",
				 retryCount, MAX_RETRIES, momentId, locationIndex, e.getMessage());
	}

	private RuntimeException translateException(Exception e) {
		if (e instanceof BusinessException) {
			return (BusinessException)e;
		} else if (e instanceof OptimisticLockException || e instanceof DataAccessException) {
			return new BusinessException(CONCURRENT_MODIFICATION_EXCEPTION, e);
		} else {
			return new BusinessException(UNEXPECTED_ERROR, e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void executeDeletePlace(Long userId, Long momentId, int locationIndex) {
		momentMemberService.findByJoinInfo(userId, momentId)
			.orElseThrow(() -> new BusinessException(MOMENT_PARTICIPATION_NOT_FOUND));

		Moment moment = momentService.findByIdWithOptimisticLock(momentId);

		Location targetLocation = locationService.findByIndex(momentId, locationIndex)
			.orElseThrow(() -> new BusinessException(LOCATION_NOT_FOUND));
		locationService.delete(targetLocation);

		int updatedRows = locationService.bulkDecreaseIndexes(momentId, locationIndex, moment.getVersion());
		if (updatedRows == 0) {
			throw new BusinessException(CONCURRENT_MODIFICATION_EXCEPTION);
		}

		moment.updateLastModifiedTime();
		momentService.save(moment);
		momentService.flushChanges();
	}
}
