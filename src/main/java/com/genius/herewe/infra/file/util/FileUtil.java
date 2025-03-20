package com.genius.herewe.infra.file.util;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.user.domain.ProviderInfo;
import com.genius.herewe.infra.file.domain.FileEnv;
import com.genius.herewe.infra.file.domain.FileType;
import com.genius.herewe.infra.file.dto.FileDTO;

public class FileUtil {
	private static final List<String> SUPPORT_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

	public static MultipartFile getDefaultProfileImage() {
		try {
			ClassPathResource resource = new ClassPathResource("static/profile/default_profile.png");
			InputStream inputStream = resource.getInputStream();
			byte[] content = inputStream.readAllBytes();

			return new CustomMultipartFile(
				content,
				"defaultProfile",
				"image/png",
				"default_profile.png"
			);
		} catch (IOException e) {
			throw new BusinessException(LOAD_PROFILE_FAILED, e);
		}
	}

	public static MultipartFile downloadFromUrl(String imageUrl, ProviderInfo providerInfo) {
		try {
			URL url = new URL(imageUrl);
			URLConnection connection = url.openConnection();

			// Content-Type 가져오기
			String contentType = connection.getContentType();

			// 파일 이름 추출 (URL의 마지막 부분 사용)
			String filename =
				providerInfo.getProfilePrefix() + UUID.randomUUID() + getExtensionByContentType(contentType);

			// 이미지 데이터 읽기
			try (InputStream inputStream = url.openStream();
				 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				byte[] imageBytes = outputStream.toByteArray();

				return new CustomMultipartFile(
					imageBytes,
					"file",
					contentType,
					filename
				);
			}
		} catch (IOException e) {
			// 다운로드 실패한 경우 기본 이미지 불러오기
			return getDefaultProfileImage();
		}
	}

	public static FileDTO getFileDTO(MultipartFile file, FileEnv env, FileType type, String upload_path) {
		validateFile(file);

		String originalName = file.getOriginalFilename();
		String extension = getExtensionByFilename(originalName);
		String storedName = getStoredName(extension);

		return FileDTO.builder()
			.originalName(originalName)
			.storedName(storedName)
			.fileURI(upload_path + type.getPath() + storedName)
			.environment(env)
			.fileType(type)
			.build();
	}

	private static void validateFile(MultipartFile multipartFile) {
		if (multipartFile.isEmpty()) {
			throw new BusinessException(FILE_INVALID);
		}
		String originalName = multipartFile.getOriginalFilename();
		if (originalName == null || originalName.isEmpty()) {
			throw new BusinessException(FILE_INVALID);
		}
	}

	private static String getStoredName(String extension) {
		String uuid = UUID.randomUUID().toString();
		return uuid + "." + extension;
	}

	private static String getExtensionByFilename(String filename) {
		int splitIndex = filename.lastIndexOf(".");
		if (splitIndex == -1) {
			return "";
		}
		String extension = filename.substring(splitIndex + 1).toLowerCase();
		if (SUPPORT_EXTENSIONS.stream().noneMatch(ex -> ex.equals(extension))) {
			throw new BusinessException(NOT_SUPPORTED_EXTENSION);
		}

		return extension;
	}

	private static String getExtensionByContentType(String contentType) {
		if (contentType == null) {
			return ".jpg";
		}

		switch (contentType.toLowerCase()) {
			case "image/jpeg" -> {
				return ".jpeg";
			}
			case "image/png" -> {
				return ".png";
			}
			case "image/gif" -> {
				return ".gif";
			}
			default -> {
				return ".jpg";
			}
		}
	}
}
