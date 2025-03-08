package com.genius.herewe.core.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

import com.genius.herewe.infra.file.domain.FileEnv;
import com.genius.herewe.infra.file.service.FilesStorage;
import com.genius.herewe.infra.file.service.LocalFilesStorage;
import com.genius.herewe.infra.file.service.S3FilesStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppConfig {
	private static final int MAX_PAGE_SIZE = 20;
	private final S3Client s3Client;
	private final Environment env;

	@Bean
	public FilesStorage filesStorage() {
		final String fileMode = env.getProperty("file.mode");
		assert fileMode != null;

		if (fileMode.equals(FileEnv.LOCAL.name())) {
			final String UPLOAD_PATH = env.getProperty("file.local.upload_path");
			return new LocalFilesStorage(UPLOAD_PATH);
		}

		final String bucket = env.getProperty("spring.cloud.aws.s3.bucket");
		final String cloudFrontDomain = env.getProperty("spring.cloud.aws.cloud-front.domain");
		return new S3FilesStorage(s3Client, bucket, cloudFrontDomain);
	}

	@Bean
	public PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
		return resolver -> {
			resolver.setMaxPageSize(MAX_PAGE_SIZE);
			resolver.setOneIndexedParameters(false);
			resolver.setPageParameterName("page");
			resolver.setSizeParameterName("size");
		};
	}
}
