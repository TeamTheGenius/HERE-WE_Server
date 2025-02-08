package com.genius.herewe.file.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.file.domain.FileHolder;
import com.genius.herewe.file.domain.FileType;
import com.genius.herewe.file.domain.Files;
import com.genius.herewe.file.dto.FileResponse;
import com.genius.herewe.file.service.FileHolderFinder;
import com.genius.herewe.file.service.FilesManager;
import com.genius.herewe.util.response.CommonResponse;
import com.genius.herewe.util.response.SingleResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FilesController {
	private final FilesManager filesManager;
	private final FileHolderFinder holderFinder;

	@PostMapping("/{id}")
	public SingleResponse<FileResponse> uploadFile(
		@PathVariable Long id,
		@RequestParam("type") String type,
		@RequestParam(value = "files", required = false) MultipartFile multipartFile
	) {
		FileType fileType = FileType.findType(type);
		FileHolder fileHolder = holderFinder.find(id, fileType);

		Files files = filesManager.uploadFile(fileHolder, multipartFile, fileType);
		FileResponse fileResponse = filesManager.convertToFileResponse(files);

		return new SingleResponse<>(HttpStatus.OK, fileResponse);
	}

	@PatchMapping("/{id}")
	public SingleResponse<FileResponse> updateFile(
		@PathVariable Long id,
		@RequestParam("type") String type,
		@RequestParam(value = "files", required = false) MultipartFile multipartFile
	) {
		FileType fileType = FileType.findType(type);
		FileHolder fileHolder = holderFinder.find(id, fileType);

		Files files = filesManager.updateFile(fileHolder.getFiles(), multipartFile);
		FileResponse fileResponse = filesManager.convertToFileResponse(files);

		return new SingleResponse<>(HttpStatus.OK, fileResponse);
	}

	@DeleteMapping("/{id}")
	public CommonResponse deleteFile(
		@PathVariable Long id,
		@RequestParam("type") String type
	) {
		FileType fileType = FileType.findType(type);
		FileHolder fileHolder = holderFinder.find(id, fileType);
		filesManager.deleteFile(fileHolder.getFiles());
		
		return new CommonResponse(HttpStatus.OK);
	}
}
