package com.genius.herewe.business.crew.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.repository.CrewMemberRepository;
import com.genius.herewe.core.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewMemberService {
	private final CrewMemberRepository crewMemberRepository;

	@Transactional
	public CrewMember save(CrewMember crewMember) {
		return crewMemberRepository.save(crewMember);
	}

	public CrewMember find(Long userId, Long crewId) {
		return crewMemberRepository.find(userId, crewId)
			.orElseThrow(() -> new BusinessException(CREW_JOIN_INFO_NOT_FOUND));
	}
}
