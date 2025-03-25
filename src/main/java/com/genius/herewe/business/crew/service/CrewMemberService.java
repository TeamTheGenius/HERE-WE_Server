package com.genius.herewe.business.crew.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.dto.CrewMemberResponse;
import com.genius.herewe.business.crew.dto.CrewPreviewResponse;
import com.genius.herewe.business.crew.repository.CrewMemberRepository;
import com.genius.herewe.business.crew.repository.query.CrewMemberQueryRepository;
import com.genius.herewe.core.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewMemberService {
	private final CrewMemberRepository crewMemberRepository;
	private final CrewMemberQueryRepository queryRepository;

	@Transactional
	public CrewMember save(CrewMember crewMember) {
		return crewMemberRepository.save(crewMember);
	}

	public CrewMember find(Long userId, Long crewId) {
		return crewMemberRepository.find(userId, crewId)
			.orElseThrow(() -> new BusinessException(CREW_JOIN_INFO_NOT_FOUND));
	}

	public Optional<CrewMember> findOptional(Long userId, Long crewId) {
		return crewMemberRepository.find(userId, crewId);
	}

	public Page<CrewMemberResponse> findAllMembersInCrew(Long crewId, Pageable pageable) {
		return queryRepository.findAllMembersInCrew(crewId, pageable);
	}

	public Page<CrewPreviewResponse> findAllJoinCrews(Long userId, Pageable pageable) {
		return queryRepository.findAllJoinCrews(userId, pageable);
	}

	@Transactional
	public void delete(CrewMember crewMember) {
		crewMemberRepository.delete(crewMember);
		crewMemberRepository.flush();
	}
}
