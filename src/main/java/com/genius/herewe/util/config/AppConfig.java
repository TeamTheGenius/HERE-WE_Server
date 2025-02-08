package com.genius.herewe.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.genius.herewe.file.config.S3Config;
import com.genius.herewe.file.domain.FileEnv;
import com.genius.herewe.file.service.FilesStorage;
import com.genius.herewe.file.service.LocalFilesStorage;
import com.genius.herewe.file.service.S3FilesStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppConfig {
	private final S3Config s3Config;
	private final Environment env;

	@Bean
	public FilesStorage filesStorage() {
		final String fileMode = env.getProperty("file.mode");
		assert fileMode != null;

		if (fileMode.equals(FileEnv.LOCAL.name())) {
			final String UPLOAD_PATH = env.getProperty("file.local.upload_path");
			return new LocalFilesStorage(UPLOAD_PATH);
		}

		final String bucket = env.getProperty("file.cloud.aws.s3.bucket");
		final String cloudFrontDomain = env.getProperty("file.cloud.aws.cloud-front.domain");
		return new S3FilesStorage(s3Config.s3Client(), bucket, cloudFrontDomain);
	}
}
