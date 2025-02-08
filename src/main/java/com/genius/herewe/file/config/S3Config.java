package com.genius.herewe.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
	private final String accessKey;
	private final String secretKey;
	private final String region;

	public S3Config(@Value("${cloud.aws.credentials.accessKey}") String accessKey,
		@Value("${cloud.aws.credentials.secretKey}") String secretKey,
		@Value("${cloud.aws.region.static}") String region) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.region = region;
	}

	@Bean
	public S3Client s3Client() {
		AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		return S3Client.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
			.build();
	}
}
