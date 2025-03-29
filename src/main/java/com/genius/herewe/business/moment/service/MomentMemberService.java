package com.genius.herewe.business.moment.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.moment.domain.MomentMember;
import com.genius.herewe.business.moment.repository.MomentMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MomentMemberService {
	private final MomentMemberRepository momentMemberRepository;

	public Optional<MomentMember> findByJoinInfo(Long userId, Long momentId) {
		return momentMemberRepository.findByJoinInfo(userId, momentId);
	}

	@Transactional
	public MomentMember save(MomentMember momentMember) {
		return momentMemberRepository.save(momentMember);
	}

	@Transactional
	public void delete(MomentMember momentMember) {
		momentMemberRepository.delete(momentMember);
	}
}
