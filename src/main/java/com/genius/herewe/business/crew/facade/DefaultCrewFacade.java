package com.genius.herewe.business.crew.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.domain.CrewRole;
import com.genius.herewe.business.crew.dto.CrewCreateRequest;
import com.genius.herewe.business.crew.dto.CrewExpelRequest;
import com.genius.herewe.business.crew.dto.CrewMemberResponse;
import com.genius.herewe.business.crew.dto.CrewModifyRequest;
import com.genius.herewe.business.crew.dto.CrewPreviewResponse;
import com.genius.herewe.business.crew.dto.CrewProfileResponse;
import com.genius.herewe.business.crew.dto.CrewResponse;
import com.genius.herewe.business.crew.service.CrewMemberService;
import com.genius.herewe.business.crew.service.CrewService;
import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.service.UserService;
import com.genius.herewe.infra.file.dto.FileDTO;
import com.genius.herewe.infra.file.service.FilesStorage;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultCrewFacade implements CrewFacade {
	private final UserService userService;
	private final CrewService crewService;
	private final CrewMemberService crewMemberService;
	private final FilesStorage filesStorage;

	@Override
	public CrewProfileResponse inquiryCrewProfile(Long userId, Long crewId) {
		User user = userService.findById(userId);
		CrewMember crewMember = crewMemberService.find(userId, crewId);
		return new CrewProfileResponse(user.getNickname(), crewMember.getRole());
	}

	@Override
	public Page<CrewPreviewResponse> inquiryMyCrews(Long userId, Pageable pageable) {
		Page<CrewPreviewResponse> allJoinCrews = crewMemberService.findAllJoinCrews(userId, pageable);

		return allJoinCrews;
	}

	@Override
	public CrewResponse inquiryCrew(Long userId, Long crewId) {
		Crew crew = crewService.findById(crewId);
		CrewMember crewMember = crewMemberService.find(userId, crewId);

		return CrewResponse.create(crew, crewMember.getRole());
	}

	@Override
	public Page<CrewMemberResponse> inquiryMembers(Long crewId, Pageable pageable) {
		return crewMemberService.findAllMembersInCrew(crewId, pageable);
	}

	@Override
	@Transactional
	public CrewPreviewResponse createCrew(Long userId, CrewCreateRequest request) {
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

		return CrewPreviewResponse.create(savedCrew);
	}

	@Override
	@Transactional
	public CrewPreviewResponse modifyCrew(Long userId, Long crewId, CrewModifyRequest request) {
		Crew crew = crewService.findById(crewId);
		CrewMember crewMember = crewMemberService.find(userId, crew.getId());

		if (crewMember.getRole() != CrewRole.LEADER) {
			throw new BusinessException(LEADER_PERMISSION_DENIED);
		}

		crew.modify(request.name(), request.introduce());
		return CrewPreviewResponse.create(crew);
	}

	@Override
	@Transactional
	public void deleteCrew(Long crewId) {
		Crew crew = crewService.findById(crewId);
		if (crew.getFiles() != null) {
			filesStorage.delete(FileDTO.create(crew.getFiles()));
		}
		crewService.delete(crew);
	}

	@Override
	@Transactional
	public void expelCrew(Long userId, CrewExpelRequest expelRequest) {
		User leader = userService.findById(userId);
		Crew crew = crewService.findById(expelRequest.crewId());
		if (crewMemberService.find(leader.getId(), crew.getId()).getRole() != CrewRole.LEADER) {
			throw new BusinessException(LEADER_PERMISSION_DENIED);
		}

		User targetUser = userService.findByNickname(expelRequest.targetName())
			.orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
		CrewMember crewMember = crewMemberService.find(targetUser.getId(), crew.getId());
		if (crewMember.getRole() == CrewRole.LEADER) {
			throw new BusinessException(LEADER_CANNOT_EXPEL);
		}
		crewMemberService.delete(crewMember);
		crew.updateParticipantCount(-1);
	}

	@Override
	@Transactional
	public void quitCrew(Long userId, Long crewId) {
		User user = userService.findById(userId);
		Crew crew = crewService.findById(crewId);

		CrewMember crewMember = crewMemberService.find(userId, crewId);
		if (crewMember.getRole() == CrewRole.LEADER) {
			throw new BusinessException(LEADER_CANNOT_EXPEL);
		}

		crewMemberService.delete(crewMember);
		crew.updateParticipantCount(-1);
	}
}
