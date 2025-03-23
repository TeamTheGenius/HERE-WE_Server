package com.genius.herewe.business.moment.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.moment.domain.Moment;
import com.genius.herewe.business.moment.repository.MomentRepository;
import com.genius.herewe.core.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MomentService {
	private final MomentRepository momentRepository;

	@Transactional
	public Moment save(Moment moment) {
		return momentRepository.save(moment);
	}

	public Moment findById(Long momentId) {
		return momentRepository.findById(momentId)
			.orElseThrow(() -> new BusinessException(MOMENT_NOT_FOUND));
	}
}
