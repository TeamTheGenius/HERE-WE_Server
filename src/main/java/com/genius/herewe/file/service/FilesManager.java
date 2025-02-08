package com.genius.herewe.file.service;

import static com.genius.herewe.util.exception.ErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.file.domain.FileEnv;
import com.genius.herewe.file.domain.FileHolder;
import com.genius.herewe.file.domain.FileType;
import com.genius.herewe.file.domain.Files;
import com.genius.herewe.file.dto.FileDTO;
import com.genius.herewe.file.dto.FileResponse;
import com.genius.herewe.file.repository.FilesRepository;
import com.genius.herewe.file.util.FileUtil;
import com.genius.herewe.util.exception.BusinessException;

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
		String upload_path = filesStorage.getUploadPath();
		FileDTO fileDTO = FileUtil.getFileDTO(multipartFile, files.getEnvironment(), files.getType(), upload_path);
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
		return FileResponse.createExist(files, source);
	}

	public FileResponse convertToFileResponse(Optional<Files> optionalFiles) {
		return optionalFiles
			.map(files -> {
				String source = filesStorage.getSource(FileDTO.create(files));
				return FileResponse.createExist(files, source);
			})
			.orElseGet(FileResponse::createNotExist);
	}
}
