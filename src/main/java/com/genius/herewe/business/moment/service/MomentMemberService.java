package com.genius.herewe.business.moment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.moment.domain.MomentMember;
import com.genius.herewe.business.moment.dto.MomentMemberResponse;
import com.genius.herewe.business.moment.repository.MomentMemberRepository;
import com.genius.herewe.business.moment.repository.query.MomentMemberQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MomentMemberService {
	private final MomentMemberRepository momentMemberRepository;
	private final MomentMemberQueryRepository queryRepository;

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

	public Slice<MomentMemberResponse> findAllJoinedUsers(Long momentId, Pageable pageable) {
		return queryRepository.findAllJoinedUsers(momentId, pageable);
	}

	public List<Long> findAllInMomentIds(List<Long> momentIds) {
		return momentMemberRepository.findIdsInMoment(momentIds);
	}
}
