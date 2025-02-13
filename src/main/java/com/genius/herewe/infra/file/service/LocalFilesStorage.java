package com.genius.herewe.infra.file.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.infra.file.domain.FileEnv;
import com.genius.herewe.infra.file.domain.FileType;
import com.genius.herewe.infra.file.dto.FileDTO;
import com.genius.herewe.infra.file.util.FileUtil;

public class LocalFilesStorage implements FilesStorage {
	private final String UPLOAD_PATH;

	public LocalFilesStorage(@Value("${file.local.upload_path}") String UPLOAD_PATH) {
		this.UPLOAD_PATH = UPLOAD_PATH;
	}

	@Override
	public FileEnv getFileEnvironment() {
		return FileEnv.LOCAL;
	}

	@Override
	public String getSource(FileDTO fileDTO) {
		try {
			UrlResource urlResource = new UrlResource("file:" + fileDTO.fileURI());
			byte[] encode = Base64.getEncoder().encode(urlResource.getContentAsByteArray());
			return new String(encode, StandardCharsets.UTF_8);
		} catch (IOException e) {
			return "";
		}
	}

	@Override
	public FileDTO upload(MultipartFile multipartFile, FileEnv fileEnv, FileType fileType) {
		FileDTO fileDTO = FileUtil.getFileDTO(multipartFile, fileEnv, fileType, UPLOAD_PATH);
		try {
			Path filePath = Paths.get(fileDTO.fileURI());
			Path parentPath = filePath.getParent();

			// 1. 부모 디렉토리 생성
			if (!Files.exists(parentPath)) {
				Files.createDirectories(parentPath);
			}

			// 2. 해당 파일 경로에만 디렉토리가 있는지 확인
			if (Files.exists(filePath) && Files.isDirectory(filePath)) {
				// 이 특정 UUID 파일 경로가 디렉토리로 잘못 생성된 경우에만 삭제
				Files.delete(filePath);
			}

			// 3. 파일 저장
			Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException e) {
			throw new BusinessException(FILE_NOT_SAVED);
		}

		return fileDTO;
	}

	@Override
	public FileDTO update(FileDTO fileDTO, MultipartFile multipartFile) {
		delete(fileDTO);
		FileDTO uploaded = upload(multipartFile, fileDTO.environment(), fileDTO.fileType());
		return uploaded;
	}

	@Override
	public void delete(FileDTO fileDTO) {
		String fileURI = fileDTO.fileURI();
		File targetFile = new File(fileURI);
		if (!targetFile.delete()) {
			throw new BusinessException(FILE_NOT_DELETED);
		}
	}
}
