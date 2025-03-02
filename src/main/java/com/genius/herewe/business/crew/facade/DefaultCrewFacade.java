package com.genius.herewe.business.crew.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.domain.CrewRole;
import com.genius.herewe.business.crew.dto.CrewCreateRequest;
import com.genius.herewe.business.crew.dto.CrewModifyRequest;
import com.genius.herewe.business.crew.dto.CrewResponse;
import com.genius.herewe.business.crew.service.CrewMemberService;
import com.genius.herewe.business.crew.service.CrewService;
import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.global.exception.ErrorCode;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultCrewFacade implements CrewFacade {
	private final UserService userService;
	private final CrewService crewService;
	private final CrewMemberService crewMemberService;

	@Override
	@Transactional
	public CrewResponse createCrew(Long userId, CrewCreateRequest request) {
		User user = userService.findById(userId);
		Crew crew = Crew.builder()
			.leaderName(user.getNickname())
			.name(request.name())
			.introduce(request.introduce())
			.participantCount(1)
			.build();

		CrewMember crewMember = CrewMember.createByRole(CrewRole.LEADER);
		crewMember.joinCrew(user, crew);

		Crew savedCrew = crewService.save(crew);
		crewMemberService.save(crewMember);

		return CrewResponse.create(savedCrew);
	}

	@Override
	@Transactional
	public CrewResponse modifyCrew(Long userId, Long crewId, CrewModifyRequest request) {
		Crew crew = crewService.findById(crewId);
		CrewMember crewMember = crewMemberService.find(userId, crew.getId());

		if (crewMember.getRole() != CrewRole.LEADER) {
			throw new BusinessException(ErrorCode.ONLY_LEADER_OPERATION);
		}

		crew.modify(request.name(), request.introduce());
		return CrewResponse.create(crew);
	}

	@Override
	@Transactional
	public void joinCrew(Long userId, Long crewId) {
		User user = userService.findById(userId);
		Crew crew = crewService.findById(crewId);

		Optional<CrewMember> optionalCrewMember = crewMemberService.findOptional(userId, crewId);
		if (optionalCrewMember.isPresent()) {
			throw new BusinessException(ALREADY_JOINED_CREW);
		}

		CrewMember crewMember = CrewMember.createByRole(CrewRole.MEMBER);
		crewMember.joinCrew(user, crew);

		crewMemberService.save(crewMember);
	}
}
