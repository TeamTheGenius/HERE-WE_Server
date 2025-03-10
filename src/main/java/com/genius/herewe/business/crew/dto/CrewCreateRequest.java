package com.genius.herewe.business.crew.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrewCreateRequest(
	@NotNull(message = "이름은 필수 항목입니다")
	@NotBlank(message = "이름은 공백일 수 없습니다")
	String name,

	@Size(max = 1000)
	String introduce
) {
}
