package com.genius.herewe.core.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.repository.UserRepository;
import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public User findById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
	}

	public Optional<User> findByNickname(String nickname) {
		return userRepository.findByNickname(nickname);
	}
}
