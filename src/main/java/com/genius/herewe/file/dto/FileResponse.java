package com.genius.herewe.file.dto;

import com.genius.herewe.file.domain.FileEnv;
import com.genius.herewe.file.domain.Files;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "파일 정보를 담은 응답 객체")
public record FileResponse(
	@Schema(description = "Files 엔티티의 식별자(PK)", example = "1")
	Long fileId,
	@Schema(description = "파일에 접근할 수 있는 정보. LOCAL의 경우 Base64로 인코딩한 문자열, CLOUD의 경우 이미지 URL 제공"
		, example = "{Base64로 인코딩 된 문자열}   or   https://{cloudfront 주소}/{파일 타입}/{파일 이름}")
	String source,
	@Schema(description = "해당 파일이 저장된 환경 정보", example = "LOCAL   or   CLOUD")
	FileEnv fileEnv
) {

	public static FileResponse create(Files files, String source) {
		return FileResponse.builder()
			.fileId(files.getId())
			.source(source)
			.fileEnv(files.getEnvironment())
			.build();
	}
}
