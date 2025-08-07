package com.genius.herewe.business.crew.dto;

import jakarta.validation.constraints.NotBlank;

public record CrewLeaderTransferRequest(
	@NotBlank
	String nickname
) {
}
