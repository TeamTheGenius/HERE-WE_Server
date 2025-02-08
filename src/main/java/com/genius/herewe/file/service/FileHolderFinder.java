package com.genius.herewe.file.service;

import static com.genius.herewe.util.exception.ErrorCode.*;

import org.springframework.stereotype.Service;

import com.genius.herewe.file.domain.FileHolder;
import com.genius.herewe.file.domain.FileType;
import com.genius.herewe.user.repository.UserRepository;
import com.genius.herewe.util.exception.BusinessException;

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
		throw new BusinessException();
	}
}
