package com.genius.herewe.business.moment.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.moment.domain.MomentMember;
import com.genius.herewe.business.moment.repository.MomentMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MomentMemberService {
	private final MomentMemberRepository momentMemberRepository;

	public Optional<MomentMember> findByJoinInfo(Long userId, Long momentId) {
		return momentMemberRepository.findByJoinInfo(userId, momentId);
	}
}
