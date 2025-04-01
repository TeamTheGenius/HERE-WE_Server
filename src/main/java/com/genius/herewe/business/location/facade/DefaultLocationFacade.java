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
import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.business.location.service.LocationService;
import com.genius.herewe.business.moment.domain.Moment;
import com.genius.herewe.business.moment.service.MomentMemberService;
import com.genius.herewe.business.moment.service.MomentService;
import com.genius.herewe.core.global.exception.BusinessException;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultLocationFacade implements LocationFacade {
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
		try {
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
		} catch (OptimisticLockException e) {
			throw new BusinessException(CONCURRENT_MODIFICATION_EXCEPTION);
		}
	}
}
