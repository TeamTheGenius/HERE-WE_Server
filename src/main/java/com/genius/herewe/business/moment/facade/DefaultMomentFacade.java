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
		Moment moment = momentService.findById(momentId);

		Optional.ofNullable(momentRequest.momentName()).ifPresent(moment::updateName);
		Optional.ofNullable(momentRequest.capacity()).ifPresent(val -> {
			validateCapacity(val);
			moment.updateCapacity(val);
		});

		LocalDateTime meetAt = momentRequest.meetAt() != null ? momentRequest.meetAt() : moment.getMeetAt();
		LocalDateTime closedAt = momentRequest.closedAt() != null ? momentRequest.closedAt() : moment.getClosedAt();
		if (!meetAt.isAfter(closedAt)) {
			throw new BusinessException(INVALID_MOMENT_DATE);
		}
		moment.updateMeetAt(meetAt);
		moment.updateClosedAt(closedAt);

		Optional.ofNullable(momentRequest.place()).ifPresent(place -> {
			locationService.findMeetLocation(momentId)
				.ifPresentOrElse(val -> val.update(place),
					() -> {
						Location fromPlace = Location.createFromPlace(place, 1);
						fromPlace.addMoment(moment);
					});
		});

		return MomentResponse.createJoined(moment, true);
	}

	private void validateMomentRequest(MomentRequest momentRequest) {
		LocalDateTime now = LocalDateTime.now();

		int capacity = Optional.ofNullable(momentRequest.capacity())
			.orElseThrow(() -> new BusinessException(REQUIRED_FIELD_MISSING));
		LocalDateTime meetAt = Optional.ofNullable(momentRequest.meetAt())
			.orElseThrow(() -> new BusinessException(REQUIRED_FIELD_MISSING));
		LocalDateTime closedAt = Optional.ofNullable(momentRequest.closedAt())
			.orElseThrow(() -> new BusinessException(REQUIRED_FIELD_MISSING));

		validateCapacity(capacity);
		validateDate(now, meetAt, closedAt);
	}

	private void validateCapacity(int capacity) {
		if (capacity < 2) {
			throw new BusinessException(INVALID_MOMENT_CAPACITY);
		}
	}

	private void validateDate(LocalDateTime now, LocalDateTime meetAt, LocalDateTime closedAt) {
		if (!meetAt.isAfter(now) || !closedAt.isAfter(now) || !meetAt.isAfter(closedAt)) {
			throw new BusinessException(INVALID_MOMENT_DATE);
		}
	}
}
