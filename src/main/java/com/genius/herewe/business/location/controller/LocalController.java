package com.genius.herewe.business.location.controller;

import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genius.herewe.business.location.search.client.KakaoSearchClient;
import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.core.global.response.SlicingResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LocalController implements LocalApi {
	private final KakaoSearchClient kakaoSearchClient;

	@GetMapping("/search/location")
	public SlicingResponse<Place> search(@RequestParam("keyword") String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {

		Slice<Place> places = kakaoSearchClient.searchByKeyword(keyword, size, page);

		return new SlicingResponse<>(HttpStatus.OK, places);
	}
}
