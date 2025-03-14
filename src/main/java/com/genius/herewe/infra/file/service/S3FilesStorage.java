package com.genius.herewe.infra.file.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.infra.file.domain.FileEnv;
import com.genius.herewe.infra.file.domain.FileType;
import com.genius.herewe.infra.file.dto.FileDTO;
import com.genius.herewe.infra.file.util.FileUtil;
import com.genius.herewe.core.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RequiredArgsConstructor
public class S3FilesStorage implements FilesStorage {
	private final S3Client s3Client;
	private final String bucket;
	private final String cloudFrontDomain;

	@Override
	public FileEnv getFileEnvironment() {
		return FileEnv.CLOUD;
	}

	@Override
	public String getSource(FileDTO fileDTO) {
		return cloudFrontDomain + fileDTO.fileURI();
	}

	@Override
	public FileDTO upload(MultipartFile multipartFile, FileEnv fileEnv, FileType fileType) {
		try {
			FileDTO fileDTO = FileUtil.getFileDTO(multipartFile, fileEnv, fileType, "");

			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucket)
				.key(fileDTO.fileURI())
				.contentType(multipartFile.getContentType())
				.build();

			s3Client.putObject(putObjectRequest,
				RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

			return fileDTO;
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public FileDTO update(FileDTO fileDTO, MultipartFile multipartFile) {
		delete(fileDTO);
		FileDTO updated = upload(multipartFile, fileDTO.environment(), fileDTO.fileType());
		return updated;
	}

	@Override
	public void delete(FileDTO fileDTO) {
		try {
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucket)
				.key(fileDTO.fileURI())
				.build();

			s3Client.deleteObject(deleteObjectRequest);
		} catch (SdkClientException e) {
			throw new BusinessException(e);
		}
	}
}
