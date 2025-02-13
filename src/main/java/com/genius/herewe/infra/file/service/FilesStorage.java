package com.genius.herewe.infra.file.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.infra.file.domain.FileEnv;
import com.genius.herewe.infra.file.domain.FileType;
import com.genius.herewe.infra.file.dto.FileDTO;

@Service
public interface FilesStorage {
	FileEnv getFileEnvironment();

	String getSource(FileDTO fileDTO);

	FileDTO upload(MultipartFile multipartFile, FileEnv fileEnv, FileType fileType);

	FileDTO update(FileDTO fileDTO, MultipartFile multipartFile);

	void delete(FileDTO fileDTO);
}
