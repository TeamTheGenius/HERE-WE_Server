package com.genius.herewe.core.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.infra.file.domain.FileHolder;
import com.genius.herewe.infra.file.domain.FileType;
import com.genius.herewe.infra.file.domain.Files;
import com.genius.herewe.infra.file.dto.FileResponse;
import com.genius.herewe.infra.file.service.FileHolderFinder;
import com.genius.herewe.infra.file.service.FilesManager;
import com.genius.herewe.core.user.dto.SignupRequest;
import com.genius.herewe.core.user.dto.SignupResponse;
import com.genius.herewe.core.user.facade.UserFacade;
import com.genius.herewe.core.global.response.CommonResponse;
import com.genius.herewe.core.global.response.SingleResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController implements UserApi {
	private final UserFacade userFacade;
	private final FileHolderFinder holderFinder;
	private final FilesManager filesManager;

	@GetMapping("/auth/check-nickname")
	public CommonResponse checkNicknameDuplicated(@RequestParam(value = "nickname") String nickname) {
		userFacade.isNicknameDuplicated(nickname);
		return CommonResponse.ok();
	}

	@PostMapping("/auth/signup")
	public SingleResponse<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
		SignupResponse signupResponse = userFacade.signup(signupRequest);
		return new SingleResponse<>(HttpStatus.CREATED, signupResponse);
	}

	@GetMapping("/auth/profile/{id}")
	public SingleResponse<FileResponse> getProfile(@PathVariable Long id) {
		FileHolder fileHolder = holderFinder.find(id, FileType.PROFILE);
		FileResponse fileResponse = filesManager.convertToFileResponse(fileHolder.getFiles());

		return new SingleResponse<>(HttpStatus.OK, fileResponse);
	}

	@PatchMapping("/auth/profile/{id}")
	public SingleResponse<FileResponse> updateProfile(
		@PathVariable Long id,
		@RequestParam(value = "files") MultipartFile multipartFile
	) {
		FileHolder fileHolder = holderFinder.find(id, FileType.PROFILE);
		Files files = filesManager.updateFile(fileHolder.getFiles(), multipartFile);
		FileResponse fileResponse = filesManager.convertToFileResponse(files);

		return new SingleResponse<>(HttpStatus.OK, fileResponse);
	}
}
