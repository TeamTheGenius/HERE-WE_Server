package com.genius.herewe.business.moment.facade;

import static com.genius.herewe.business.moment.domain.ParticipantStatus.*;
import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
import com.genius.herewe.business.moment.domain.ParticipantStatus;
import com.genius.herewe.business.moment.dto.MomentIncomingResponse;
import com.genius.herewe.business.moment.dto.MomentMemberResponse;
import com.genius.herewe.business.moment.dto.MomentPreviewResponse;
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
	public Page<MomentIncomingResponse> inquiryIncomingList(Long userId, LocalDateTime now, Pageable pageable) {
		Page<Moment> joinedMoments = momentService.findAllJoinedMoments(userId, now, pageable);

		List<Moment> moments = joinedMoments.getContent();
		List<Long> momentIds = moments.stream().map(Moment::getId).toList();

		Map<Long, Location> locationInfos = locationService.findMeetingLocationsInIds(momentIds);
		Map<Long, Crew> crewInfos = crewService.findAllInMoments(momentIds);

		List<MomentIncomingResponse> incomingResponses = moments.stream().map(moment -> {
			Crew crew = crewInfos.get(moment.getId());
			Location location = locationInfos.get(moment.getId());

			return MomentIncomingResponse.builder()
				.crewId(crew.getId())
				.momentId(moment.getId())
				.crewName(crew.getName())
				.momentName(moment.getName())
				.meetAt(moment.getMeetAt())
				.meetPlaceName(location.getName())
				.build();
		}).toList();

		return new PageImpl<>(incomingResponses, pageable, joinedMoments.getTotalElements());
	}

	@Override
	public Page<MomentPreviewResponse> inquiryList(Long userId, Long crewId, LocalDateTime now, Pageable pageable) {
		Page<Moment> moments = momentService.findAllInCrewByPaging(crewId, pageable);

		List<Long> momentIds = moments.getContent().stream().map(Moment::getId).toList();
		Map<Long, Location> locationInfos = locationService.findMeetingLocationsInIds(momentIds);

		List<Long> momentMemberIds = momentMemberService.findAllInMomentIds(momentIds);
		Set<Long> momentMemberSet = new HashSet<>(momentMemberIds);

		List<MomentPreviewResponse> previewResponses = moments.getContent().stream()
			.map(moment -> {
				boolean isJoined = momentMemberSet.contains(moment.getId());
				ParticipantStatus status = getParticipantStatus(isJoined, moment.getClosedAt(), now);
				String placeName = locationInfos.getOrDefault(moment.getId(), Location.createDummy()).getName();
				return MomentPreviewResponse.create(moment, status, placeName);
			})
			.toList();

		return new PageImpl<>(previewResponses, pageable, moments.getTotalElements());
	}

	private ParticipantStatus getParticipantStatus(boolean isJoined, LocalDateTime closedAt, LocalDateTime now) {
		if (isJoined) {
			return PARTICIPATING;
		}
		if (now.isAfter(closedAt)) {
			return ParticipantStatus.DEADLINE_PASSED;
		}
		return ParticipantStatus.AVAILABLE;
	}

	@Override
	public MomentResponse inquirySingle(User user, Long momentId, LocalDateTime now) {
		Moment moment = momentService.findById(momentId);

		Optional<MomentMember> joinInfo = momentMemberService.findByJoinInfo(user.getId(), momentId);
		boolean isJoined = joinInfo.isPresent();
		boolean isClosed = now.isAfter(moment.getClosedAt());

		Optional<Location> meetLocation = locationService.findMeetLocation(momentId);
		Place place = Place.createFromOptional(meetLocation);

		return MomentResponse.create(moment, place, isJoined, isClosed);
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

		return MomentResponse.create(moment, Place.create(location), true, false);
	}

	@Override
	@Transactional
	public MomentResponse modify(Long momentId, MomentRequest momentRequest, LocalDateTime now) {
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

		boolean isClosed = now.isAfter(moment.getClosedAt());

		return MomentResponse.create(moment, momentRequest.place(), true, isClosed);
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

		return MomentResponse.create(moment, place, true, false);
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
		if (capacity <= participantCount) {
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
	public Slice<MomentMemberResponse> inquiryJoinedMembers(Long momentId, Pageable pageable) {
		return momentMemberService.findAllJoinedUsers(momentId, pageable);
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
		if (capacity < 2 || capacity > 500) {
			throw new BusinessException(INVALID_MOMENT_CAPACITY);
		}
	}

	private void validateDate(LocalDateTime now, LocalDateTime meetAt, LocalDateTime closedAt) {
		if (!meetAt.isAfter(now) || !closedAt.isAfter(now) || !meetAt.isAfter(closedAt)) {
			throw new BusinessException(INVALID_MOMENT_DATE);
		}
	}
}
