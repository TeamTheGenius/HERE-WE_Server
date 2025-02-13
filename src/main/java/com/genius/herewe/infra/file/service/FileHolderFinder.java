package com.genius.herewe.infra.file.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import org.springframework.stereotype.Service;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.user.repository.UserRepository;
import com.genius.herewe.infra.file.domain.FileHolder;
import com.genius.herewe.infra.file.domain.FileType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileHolderFinder {
	private final UserRepository userRepository;

	public FileHolder find(Long id, FileType fileType) {
		switch (fileType) {
			case PROFILE -> {
				return userRepository.findById(id)
					.orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
			}
		}
		throw new BusinessException(NOT_SUPPORTED_IMAGE_TYPE);
	}
}
