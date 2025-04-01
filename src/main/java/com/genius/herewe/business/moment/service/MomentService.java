package com.genius.herewe.business.moment.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.moment.domain.Moment;
import com.genius.herewe.business.moment.repository.MomentMemberRepository;
import com.genius.herewe.business.moment.repository.MomentRepository;
import com.genius.herewe.core.global.exception.BusinessException;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MomentService {
	private final EntityManager entityManager;
	private final MomentRepository momentRepository;
	private final MomentMemberRepository momentMemberRepository;

	@Transactional
	public Moment save(Moment moment) {
		return momentRepository.save(moment);
	}

	public Moment findById(Long momentId) {
		return momentRepository.findById(momentId)
			.orElseThrow(() -> new BusinessException(MOMENT_NOT_FOUND));
	}

	public Moment findByIdWithOptimisticLock(Long momentId) {
		return momentRepository.findByIdWithOptimisticLock(momentId)
			.orElseThrow(() -> new BusinessException(MOMENT_NOT_FOUND));
	}

	public Page<Moment> findAllInCrewByPaging(Long crewId, Pageable pageable) {
		return momentRepository.findAllInCrewByPaging(crewId, pageable);
	}

	@Transactional
	public void delete(Moment moment) {
		momentRepository.delete(moment);
	}

	public void flushChanges() {
		entityManager.flush();
	}

	public Page<Moment> findAllJoinedMoments(Long userId, LocalDateTime now, Pageable pageable) {
		return momentMemberRepository.findAllJoinedMoments(userId, now, pageable);
	}
}
