package com.genius.herewe.file.util;

import static com.genius.herewe.util.exception.ErrorCode.*;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.file.domain.FileEnv;
import com.genius.herewe.file.domain.FileType;
import com.genius.herewe.file.dto.FileDTO;
import com.genius.herewe.util.exception.BusinessException;

public class FileUtil {
	private static final List<String> SUPPORT_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");

	public static FileDTO getFileDTO(MultipartFile file, FileEnv env, FileType type, String upload_path) {
		validateFile(file);

		String originalName = file.getOriginalFilename();
		String extension = extractExtension(originalName);
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

	private static String extractExtension(String filename) {
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
}
