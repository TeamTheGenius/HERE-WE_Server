package com.genius.herewe.security.service;

import static com.genius.herewe.file.domain.FileType.*;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.file.domain.FileEnv;
import com.genius.herewe.file.domain.Files;
import com.genius.herewe.file.dto.FileDTO;
import com.genius.herewe.file.repository.FilesRepository;
import com.genius.herewe.file.service.FilesStorage;
import com.genius.herewe.file.util.FileUtil;
import com.genius.herewe.security.domain.UserPrincipal;
import com.genius.herewe.security.dto.OAuth2UserInfo;
import com.genius.herewe.security.dto.OAuth2UserInfoFactory;
import com.genius.herewe.user.domain.ProviderInfo;
import com.genius.herewe.user.domain.Role;
import com.genius.herewe.user.domain.User;
import com.genius.herewe.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;
	private final FilesRepository filesRepository;
	private final FilesStorage filesStorage;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		ClientRegistration clientRegistration = userRequest.getClientRegistration();

		ProviderInfo providerInfo = ProviderInfo.from(clientRegistration.getRegistrationId());
		Map<String, Object> attributes = oAuth2User.getAttributes();

		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.createUserInfo(providerInfo, attributes);
		String email = oAuth2UserInfo.getEmail();

		User user = getUser(email, providerInfo);
		// 소셜로부터 받은 프로필 사진 저장
		if (user.getFiles() == null) {
			Files files = getFiles(oAuth2UserInfo.getProfileImage(), providerInfo);
			user.setFiles(files);
			userRepository.save(user);
		}

		String userNameAttributeName = clientRegistration.getProviderDetails().getUserInfoEndpoint()
			.getUserNameAttributeName();

		return new UserPrincipal(user, providerInfo, attributes, userNameAttributeName);
	}

	private Files getFiles(String imageUrl, ProviderInfo providerInfo) {
		FileEnv fileEnv = filesStorage.getFileEnvironment();

		if (imageUrl == null || imageUrl.isBlank()) {
			return Files.builder()
				.environment(fileEnv)
				.type(PROFILE)
				.originalName("")
				.storedName("")
				.fileURI("")
				.build();
		}

		MultipartFile multipartFile = FileUtil.downloadFromUrl(imageUrl, providerInfo);
		FileDTO fileDTO = filesStorage.upload(multipartFile, fileEnv, PROFILE);

		Files files = Files.builder()
			.environment(fileEnv)
			.type(PROFILE)
			.originalName(fileDTO.originalName())
			.storedName(fileDTO.storedName())
			.fileURI(fileDTO.fileURI())
			.build();
		files = filesRepository.save(files);
		return files;
	}

	private User getUser(String email, ProviderInfo providerInfo) {
		Optional<User> optionalUser = userRepository.findByOAuth2Info(email, providerInfo);

		if (optionalUser.isEmpty()) {
			User unregistered = User.builder()
				.email(email)
				.role(Role.NOT_REGISTERED)
				.providerInfo(providerInfo)
				.build();
			return userRepository.save(unregistered);
		}
		return optionalUser.get();
	}
}
