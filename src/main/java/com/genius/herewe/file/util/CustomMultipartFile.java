package com.genius.herewe.file.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {
	private final byte[] content;
	private final String name;
	private final String contentType;
	private final String originalFilename;

	public CustomMultipartFile(byte[] content, String name, String contentType, String originalFilename) {
		this.content = content;
		this.name = name;
		this.contentType = contentType;
		this.originalFilename = originalFilename;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getOriginalFilename() {
		return originalFilename;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public boolean isEmpty() {
		return content == null || content.length == 0;
	}

	@Override
	public long getSize() {
		return content.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return content;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		try (FileOutputStream fos = new FileOutputStream(dest)) {
			fos.write(content);
			fos.flush();  // 버퍼에 남은 데이터를 확실히 쓰기 위해
		}
	}
}
