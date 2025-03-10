package com.genius.herewe.business.crew.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "크루 생성 시 요청 객체")
public record CrewCreateRequest(
	@NotNull(message = "이름은 필수 항목입니다")
	@NotBlank(message = "이름은 공백일 수 없습니다")
	@Schema(description = "크루 이름", example = "눈물나게 맛있는거 먹고싶은 사람들")
	String name,

	@Size(max = 1000)
	@Schema(description = "크루 소개글", example = "수도권의 맛집들을 뽀개는 동아리입니다!")
	String introduce
) {
}
