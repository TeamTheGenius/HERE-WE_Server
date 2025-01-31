package com.genius.herewe.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.user.domain.User;
import com.genius.herewe.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public Optional<User> findByNickname(String nickname) {
		return userRepository.findByNickname(nickname);
	}
}
