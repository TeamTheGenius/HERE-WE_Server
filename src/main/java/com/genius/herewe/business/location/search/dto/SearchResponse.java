package com.genius.herewe.business.location.search.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchResponse(
	@JsonProperty(value = "documents")
	List<Place> places,
	Meta meta
) {
}
