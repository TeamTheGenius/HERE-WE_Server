package com.genius.herewe.business.crew.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.repository.CrewMemberRepository;

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
}
