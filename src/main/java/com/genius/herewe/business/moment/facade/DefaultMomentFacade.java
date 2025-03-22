package com.genius.herewe.business.moment.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.service.CrewService;
import com.genius.herewe.business.location.domain.Location;
import com.genius.herewe.business.location.service.LocationService;
import com.genius.herewe.business.moment.domain.Moment;
import com.genius.herewe.business.moment.domain.MomentMember;
import com.genius.herewe.business.moment.dto.MomentRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;
import com.genius.herewe.business.moment.service.MomentService;
import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultMomentFacade implements MomentFacade {
	private final UserService userService;
	private final CrewService crewService;
	private final MomentService momentService;
	private final LocationService locationService;

	@Override
	@Transactional
	public MomentResponse createMoment(Long userId, Long crewId, MomentRequest momentRequest) {
		validateMomentRequest(momentRequest);

		User user = userService.findById(userId);
		Crew crew = crewService.findById(crewId);

		Moment moment = Moment.builder()
			.name(momentRequest.momentName())
			.participantCount(1)
			.capacity(momentRequest.capacity())
			.meetAt(momentRequest.meetAt())
			.closedAt(momentRequest.closedAt())
			.build();
		moment.addCrew(crew);
		momentService.save(moment);

		MomentMember momentMember = MomentMember.create();
		momentMember.joinMoment(user, moment);

		Location location = locationService.saveFromPlace(momentRequest.place(), 1);
		location.addMoment(moment);

		return MomentResponse.createJoined(moment, true);
	}

	@Override
	@Transactional
	public MomentResponse modifyMoment(Long momentId, MomentRequest momentRequest) {
		validateMomentRequest(momentRequest);
		Moment moment = momentService.findById(momentId);

		Optional.ofNullable(momentRequest.momentName()).ifPresent(moment::updateName);
		Optional.ofNullable(momentRequest.meetAt()).ifPresent(moment::updateMeetAt);
		Optional.ofNullable(momentRequest.closedAt()).ifPresent(moment::updateClosedAt);
		Optional.of(momentRequest.capacity()).ifPresent(moment::updateCapacity);

		Optional.ofNullable(momentRequest.place()).ifPresent(place -> {
			Location meetLocation = locationService.findMeetLocation(momentId);
			meetLocation.update(place);
		});

		return MomentResponse.createJoined(moment, true);
	}

	private void validateMomentRequest(MomentRequest momentRequest) {
		LocalDateTime now = LocalDateTime.now();

		int capacity = momentRequest.capacity();
		LocalDateTime meetAt = momentRequest.meetAt();
		LocalDateTime closedAt = momentRequest.closedAt();

		if (capacity < 2) {
			throw new BusinessException(INVALID_MOMENT_CAPACITY);
		}
		if (!meetAt.isAfter(now) || !closedAt.isAfter(now) || !meetAt.isAfter(closedAt)) {
			throw new BusinessException(INVALID_MOMENT_DATE);
		}
	}
}
