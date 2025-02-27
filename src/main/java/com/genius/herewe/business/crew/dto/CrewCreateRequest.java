package com.genius.herewe.business.crew.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CrewCreateRequest(
	@NotEmpty
	String name,

	@Size(max = 1000)
	String introduce
) {
}
