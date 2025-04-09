package com.genius.herewe.business.crew.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.repository.CrewRepository;
import com.genius.herewe.business.crew.repository.dto.CrewMomentPair;
import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewService {
	private final CrewRepository crewRepository;

	public Crew findById(Long crewId) {
		return crewRepository.findById(crewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CREW_NOT_FOUND));
	}

	@Transactional
	public Crew save(Crew crew) {
		return crewRepository.save(crew);
	}

	@Transactional
	public void delete(Crew crew) {
		crewRepository.delete(crew);
	}

	public Map<Long, Crew> findAllInMoments(List<Long> momentIds) {
		List<CrewMomentPair> pairs = crewRepository.findAllInMomentIds(momentIds);
		return pairs.stream().collect(
			Collectors.toMap(
				CrewMomentPair::getMomentId,
				CrewMomentPair::getCrew
			)
		);
	}
}
