package com.genius.herewe.infra.file.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static com.genius.herewe.infra.file.domain.FileType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.user.domain.ProviderInfo;
import com.genius.herewe.infra.file.domain.FileEnv;
import com.genius.herewe.infra.file.domain.FileHolder;
import com.genius.herewe.infra.file.domain.FileType;
import com.genius.herewe.infra.file.domain.Files;
import com.genius.herewe.infra.file.dto.FileDTO;
import com.genius.herewe.infra.file.dto.FileResponse;
import com.genius.herewe.infra.file.repository.FilesRepository;
import com.genius.herewe.infra.file.util.FileUtil;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FilesManager {
	private final FilesStorage filesStorage;
	private final FilesRepository filesRepository;

	public Files findFile(Long fileId) {
		return filesRepository.findById(fileId)
			.orElseThrow(() -> new BusinessException(FILE_NOT_EXIST));
	}

	@Transactional
	public Files getProfileFromSocial(String imageUrl, ProviderInfo providerInfo) {
		FileEnv fileEnv = filesStorage.getFileEnvironment();
		MultipartFile multipartFile;

		if (imageUrl == null || imageUrl.isBlank()) {
			multipartFile = FileUtil.getDefaultProfileImage();
		} else {
			multipartFile = FileUtil.downloadFromUrl(imageUrl, providerInfo);
		}

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

	@Transactional
	public Files uploadFile(FileHolder fileHolder, MultipartFile multipartFile, FileType fileType) {
		FileEnv fileEnv = filesStorage.getFileEnvironment();
		FileDTO fileDTO = filesStorage.upload(multipartFile, fileEnv, fileType);

		Files files = Files.builder()
			.environment(fileEnv)
			.type(fileType)
			.originalName(fileDTO.originalName())
			.storedName(fileDTO.storedName())
			.fileURI(fileDTO.fileURI())
			.build();

		fileHolder.setFiles(files);
		return filesRepository.save(files);
	}

	@Transactional
	public Files updateFile(Files files, MultipartFile multipartFile) {
		FileDTO fileDTO = FileDTO.create(files);
		FileDTO updated = filesStorage.update(fileDTO, multipartFile);

		files.updateFiles(updated);
		return files;
	}

	@Transactional
	public void deleteFile(Files files) {
		filesStorage.delete(FileDTO.create(files));
		filesRepository.delete(files);
	}

	public FileResponse convertToFileResponse(Files files) {
		String source = filesStorage.getSource(FileDTO.create(files));
		return FileResponse.create(files, source);
	}
}
