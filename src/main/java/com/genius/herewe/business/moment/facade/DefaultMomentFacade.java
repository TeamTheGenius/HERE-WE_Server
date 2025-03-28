package com.genius.herewe.business.moment.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.service.CrewMemberService;
import com.genius.herewe.business.crew.service.CrewService;
import com.genius.herewe.business.location.domain.Location;
import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.business.location.service.LocationService;
import com.genius.herewe.business.moment.domain.Moment;
import com.genius.herewe.business.moment.domain.MomentMember;
import com.genius.herewe.business.moment.dto.MomentMemberResponse;
import com.genius.herewe.business.moment.dto.MomentRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;
import com.genius.herewe.business.moment.service.MomentMemberService;
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
	private final CrewMemberService crewMemberService;
	private final MomentService momentService;
	private final MomentMemberService momentMemberService;
	private final LocationService locationService;

	@Override
	public MomentResponse inquirySingle(User user, Long momentId) {
		Moment moment = momentService.findById(momentId);

		Optional<MomentMember> joinInfo = momentMemberService.findByJoinInfo(user.getId(), momentId);
		boolean isJoined = joinInfo.isPresent();

		Optional<Location> meetLocation = locationService.findMeetLocation(momentId);
		Place place = Place.createFromOptional(meetLocation);

		return MomentResponse.create(moment, place, isJoined);
	}

	@Override
	@Transactional
	public MomentResponse create(Long userId, Long crewId, MomentRequest momentRequest) {
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

		return MomentResponse.create(moment, Place.create(location), true);
	}

	@Override
	@Transactional
	public MomentResponse modify(Long momentId, MomentRequest momentRequest) {
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
				.ifPresentOrElse(val -> {
						val.update(place);
					},
					() -> {
						Location fromPlace = Location.createFromPlace(place, 1);
						fromPlace.addMoment(moment);
					});
		});

		return MomentResponse.create(moment, momentRequest.place(), true);
	}

	@Override
	@Transactional
	public void delete(Long momentId) {
		Moment moment = momentService.findById(momentId);
		momentService.delete(moment);
	}

	@Override
	@Transactional
	public MomentResponse join(Long userId, Long momentId, LocalDateTime now) {
		User user = userService.findById(userId);
		Moment moment = momentService.findById(momentId);

		validateJoinCondition(userId, moment, now);

		moment.updateParticipant(1);
		MomentMember momentMember = MomentMember.create();
		momentMember.joinMoment(user, moment);
		momentMemberService.save(momentMember);

		Optional<Location> optionalLocation = locationService.findMeetLocation(momentId);
		Place place = Place.createFromOptional(optionalLocation);

		return MomentResponse.create(moment, place, true);
	}

	private void validateJoinCondition(Long userId, Moment moment, LocalDateTime now) {
		Long crewId = moment.getCrew().getId();
		Optional<CrewMember> crewJoinInfo = crewMemberService.findOptional(userId, crewId);
		if (crewJoinInfo.isEmpty()) {
			throw new BusinessException(CREW_MEMBERSHIP_REQUIRED);
		}
		Optional<MomentMember> momentJoinInfo = momentMemberService.findByJoinInfo(userId, moment.getId());
		if (momentJoinInfo.isPresent()) {
			throw new BusinessException(ALREADY_JOINED_MOMENT);
		}
		LocalDateTime closedAt = moment.getClosedAt();
		if (!closedAt.isAfter(now)) {
			throw new BusinessException(MOMENT_DEADLINE_EXPIRED);
		}
		int capacity = moment.getCapacity();
		int participantCount = moment.getParticipantCount();
		if (capacity == participantCount) {
			throw new BusinessException(MOMENT_CAPACITY_FULL);
		}
	}

	@Override
	@Transactional
	public void quit(Long userId, Long momentId, LocalDateTime now) {
		Moment moment = momentService.findById(momentId);

		LocalDateTime closedAt = moment.getClosedAt();
		if (!closedAt.isAfter(now)) {
			throw new BusinessException(MOMENT_DEADLINE_EXPIRED);
		}

		Optional<MomentMember> joinInfo = momentMemberService.findByJoinInfo(userId, momentId);
		joinInfo.ifPresent(val -> {
			momentMemberService.delete(val);
			moment.updateParticipant(-1);
		});
	}

	@Override
	public List<MomentMemberResponse> inquiryJoinedMembers(Long momentId) {
		List<User> joinedUsers = momentMemberService.findAllJoinedUsers(momentId);
		return joinedUsers.stream()
			.map(user -> MomentMemberResponse.builder()
				.userId(user.getId())
				.name(user.getNickname())
				.build())
			.toList();
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
