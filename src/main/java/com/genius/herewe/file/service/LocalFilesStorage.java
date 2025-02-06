package com.genius.herewe.file.service;

import static com.genius.herewe.util.exception.ErrorCode.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.file.domain.FileEnv;
import com.genius.herewe.file.domain.FileType;
import com.genius.herewe.file.dto.FileDTO;
import com.genius.herewe.file.util.FileUtil;
import com.genius.herewe.util.exception.BusinessException;

public class LocalFilesStorage implements FilesStorage {
	private final String UPLOAD_PATH;

	public LocalFilesStorage(@Value("${file.local.upload_path}") String UPLOAD_PATH) {
		this.UPLOAD_PATH = UPLOAD_PATH;
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
			File file = new File(fileDTO.fileURI());
			createPath(fileDTO.fileURI());
			multipartFile.transferTo(file);
		} catch (IOException e) {
			throw new BusinessException(FILE_NOT_SAVED);
		}

		return fileDTO;
	}

	private void createPath(String fileURI) {
		File file = new File(fileURI);
		if (!file.exists()) {
			file.mkdirs();
		}
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

	@Override
	public void validateFileExist(FileDTO fileDTO) {

	}
}
