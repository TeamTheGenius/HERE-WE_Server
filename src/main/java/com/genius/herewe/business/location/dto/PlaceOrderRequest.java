package com.genius.herewe.business.location.dto;

import lombok.Builder;

@Builder
public record PlaceOrderRequest(
	int originalIndex,
	int newIndex
) {
}
