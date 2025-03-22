package com.genius.herewe.business.moment.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.service.CrewService;
import com.genius.herewe.business.location.domain.Location;
import com.genius.herewe.business.location.service.LocationService;
import com.genius.herewe.business.moment.domain.Moment;
import com.genius.herewe.business.moment.domain.MomentMember;
import com.genius.herewe.business.moment.dto.MomentCreateRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;
import com.genius.herewe.business.moment.service.MomentService;
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

	/**
	 * 모먼트 생성 시 설정해야하는 항목들
	 * 1. 모먼트 정보 생성
	 * 1-1. 모먼트랑 crew 연결
	 * 2. Place 정보 받아서 Location 생성 후 저장 & Moment에 연결하기
	 * 3. MomentMember를 통해 참여 정보 저장하기
	 * NOTE: meetAt, capacity, closedAt 유효성 확인하기
	 */
	@Override
	@Transactional
	public MomentResponse createMoment(Long userId, MomentCreateRequest momentCreateRequest) {
		User user = userService.findById(userId);
		Crew crew = crewService.findById(momentCreateRequest.crewId());

		Moment moment = Moment.builder()
			.name(momentCreateRequest.momentName())
			.participantCount(1)
			.capacity(momentCreateRequest.capacity())
			.meetAt(momentCreateRequest.meetAt())
			.closedAt(momentCreateRequest.closedAt())
			.build();
		moment.addCrew(crew);
		momentService.save(moment);

		MomentMember momentMember = MomentMember.create();
		momentMember.joinMoment(user, moment);

		Location location = locationService.saveFromPlace(momentCreateRequest.meetPlace(), 1);
		location.addMoment(moment);

		return MomentResponse.createJoined(moment, true);
	}
}
