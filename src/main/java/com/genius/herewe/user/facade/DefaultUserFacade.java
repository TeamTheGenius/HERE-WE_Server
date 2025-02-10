package com.genius.herewe.user.facade;

import static com.genius.herewe.util.exception.ErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.file.dto.FileResponse;
import com.genius.herewe.file.service.FilesManager;
import com.genius.herewe.user.domain.Role;
import com.genius.herewe.user.domain.User;
import com.genius.herewe.user.dto.SignupRequest;
import com.genius.herewe.user.dto.SignupResponse;
import com.genius.herewe.user.service.UserService;
import com.genius.herewe.util.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultUserFacade implements UserFacade {
	private final UserService userService;
	private final FilesManager filesManager;

	@Override
	public void isNicknameDuplicated(String nickname) {
		Optional<User> optionalUser = userService.findByNickname(nickname);
		if (optionalUser.isPresent()) {
			throw new BusinessException(NICKNAME_DUPLICATED);
		}
	}

	@Override
	@Transactional
	public SignupResponse signup(SignupRequest signupRequest) {
		User user = userService.findById(signupRequest.userId());
		String nickname = signupRequest.nickname();

		if (user.getRole() != Role.NOT_REGISTERED) {
			throw new BusinessException(ALREADY_REGISTERED);
		}

		isNicknameDuplicated(nickname);
		user.updateNickname(nickname);
		user.updateRole(Role.USER);

		FileResponse fileResponse = filesManager.convertToFileResponse(user.getFiles());
		return new SignupResponse(user.getId(), user.getNickname(), fileResponse);
	}
}
