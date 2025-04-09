package com.genius.herewe.business.location.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Meta(
	@JsonProperty(value = "is_end")
	boolean isEnd,
	@JsonProperty(value = "pageable_count")
	int pageableCount,
	@JsonProperty(value = "total_count")
	int totalCount
) {
}
