package com.genius.herewe.infra.file.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import org.springframework.stereotype.Service;

import com.genius.herewe.business.crew.repository.CrewRepository;
import com.genius.herewe.business.moment.repository.MomentRepository;
import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.user.repository.UserRepository;
import com.genius.herewe.infra.file.domain.FileHolder;
import com.genius.herewe.infra.file.domain.FileType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileHolderFinder {
	private final UserRepository userRepository;
	private final CrewRepository crewRepository;
	private final MomentRepository momentRepository;

	public FileHolder find(Long id, FileType fileType) {
		switch (fileType) {
			case PROFILE -> {
				return userRepository.findById(id)
					.orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
			}
			case CREW -> {
				return crewRepository.findById(id)
					.orElseThrow(() -> new BusinessException(CREW_NOT_FOUND));
			}
			case MOMENT -> {
				return momentRepository.findById(id)
					.orElseThrow(() -> new BusinessException(MOMENT_NOT_FOUND));
			}
		}
		throw new BusinessException(NOT_SUPPORTED_IMAGE_TYPE);
	}
}
