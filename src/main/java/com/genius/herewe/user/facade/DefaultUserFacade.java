package com.genius.herewe.user.facade;

import static com.genius.herewe.util.exception.ErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.user.domain.User;
import com.genius.herewe.user.service.UserService;
import com.genius.herewe.util.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultUserFacade implements UserFacade {
	private final UserService userService;

	@Override
	public void isNicknameDuplicated(String nickname) {
		Optional<User> optionalUser = userService.findByNickname(nickname);
		if (optionalUser.isPresent()) {
			throw new BusinessException(NICKNAME_DUPLICATED);
		}
	}
}
